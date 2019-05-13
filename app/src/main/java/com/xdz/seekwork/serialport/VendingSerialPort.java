package com.xdz.seekwork.serialport;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

/**
 * Card Read Serial Port Util
 * <p>
 */

public class VendingSerialPort {

    private String TAG = VendingSerialPort.class.getSimpleName();
    private static VendingSerialPort portUtil;
    private static SeekerSoftSerialPort mSerialPort;

    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private OnDataReceiveListener onDataReceiveListener = null;

    private ReadThread mReadThread;
    private static boolean isStop = false;
    private String devicePath = "/dev/ttyS3";// tty02
    private int baudrate = 9600;


    public static VendingSerialPort SingleInit() {
        // 如果停止监听 或者 第一次启动
        if (isStop || null == portUtil) {
            isStop = false;
            portUtil = new VendingSerialPort();
            portUtil.onCreate();

            // 初始化是否成功
            if (mSerialPort == null) {
                portUtil = null;
            }
        }
        return portUtil;
    }

    // 出货
    public void commadTakeOut(ShipmentCommad shipmentCommad) {
        ShipmentCommad shipmentObject = shipmentCommad;

        if (shipmentObject != null) {
            if (!shipmentCommad.isGEZI()) {
                // 螺纹
                // 起始码 字节码 地址码 功能码 排序 Yn行 Xn列 转数 卸货时间S CRC低 CRC高 停止码
                byte[] sendData = new byte[12];
                sendData[0] = (byte) 0xFF;
                sendData[1] = (byte) 0x0C;
                sendData[2] = (byte) 0x00;
                sendData[3] = (byte) 0xA3;// A3
                sendData[4] = (byte) shipmentObject.containerNum;
                sendData[5] = (byte) shipmentObject.getProHang();
                sendData[6] = (byte) shipmentObject.getProLie();
                sendData[7] = 0x01;
                sendData[8] = 0;
                sendData[9] = 0;
                sendData[10] = 0;
                sendData[11] = (byte) 0xFE;
                // 此处需要重新根据出货成功标识进行判断是否成功出货的回调
                sendBuffer(crcA3(sendData));
            } else {
                // 格子柜门
                byte[] sendData = new byte[10];
                sendData[0] = (byte) 0xFF;
                sendData[1] = (byte) 0x0A;
                sendData[2] = (byte) 0x01;
                sendData[3] = (byte) 0xA2;
                sendData[4] = (byte) shipmentObject.containerNum;
                sendData[5] = (byte) shipmentObject.getProHang();
                sendData[6] = (byte) shipmentObject.getProLie();
                sendData[7] = 0;
                sendData[8] = 0;
                sendData[9] = (byte) 0xFE;
                sendBuffer(crcA2(sendData));
            }
        } else {
            return;
        }
    }


    public static int CRC16(byte[] Buf, int len) {
        int CRC;
        int i, j;
        CRC = 0xffff;
        for (i = 0; i < len; i++) {
            CRC = CRC ^ (Buf[i] & 0xff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x01) == 1) {
                    CRC = (CRC >> 1) ^ 0xA001;
                } else {
                    CRC = CRC >> 1;
                }
            }
        }
        return CRC;
    }

    // FF0C00A30B01010103AAB7FE
    private byte[] crcA3(byte[] data) {
        int wcrc = CRC16(data, 9);
        int up = wcrc >> 8;
        int low = wcrc & 0x00ff;
        data[9] = (byte) low;
        data[10] = (byte) up;
        return data;
    }

    private byte[] crcA2(byte[] data) {
        int wcrc = CRC16(data, 7);
        int up = wcrc >> 8;
        int low = wcrc & 0x00ff;
        data[7] = (byte) low;
        data[8] = (byte) up;
        return data;
    }

    /**
     * 数据回调接口
     */
    public interface OnDataReceiveListener {

        void onDataReceiveString(String ResultStr);

    }

    public VendingSerialPort setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
        return this;
    }

    /**
     * 初始化串口信息，并启动此串口的read来自vmc的信号数据
     * 在启动程序的时候就做此初始化操作。
     */
    private void onCreate() {
        try {
            mSerialPort = new SeekerSoftSerialPort(new File(devicePath), baudrate, 0);
            if (mSerialPort.getOpenSerialResult()) {
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
                mReadThread = new ReadThread();
                mReadThread.start();
            } else {
                mSerialPort = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Init Serial Port Failed");
            mSerialPort = null;
        }
    }


    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            // 起始码 字节码 地址码 功能码 排序 Yn行 Xn列 转数 卸货时间S 开关状态 掉货状态 CRC低 CRC高 停止码
            byte[] backData = new byte[14];
            while (!isStop && !isInterrupted()) {
                // 串口开启，做读取数据
                int size = 1;
                try {
                    if (mInputStream == null) {
                        return;
                    }
                    size = mInputStream.read(backData);
                    String hexStr = bytesToHexString(backData);
                    Log.e("TAG", "byte to str= " + hexStr + ",size=" + size);
                    // 默认以 "0xFE" 结束读取
                    if (hexStr.contains("FE")) {
                        if (null != onDataReceiveListener) {
                            onDataReceiveListener.onDataReceiveString(hexStr);
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
            }
        }
    }


    /**
     * 数组转换成十六进制字符串
     *
     * @return HexString
     */
    public String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    /**
     * 关闭串口，在程序退出的时候，在调用这个串口的关闭
     */
    public void closeSerialPort() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
            portUtil = null;
        }
    }

    /**
     * 发送指令到串口
     *
     * @param mBuffer 原始命令的二进制流
     * @return
     */
    public boolean sendBuffer(byte[] mBuffer) {
        boolean result = true;
        byte[] mBufferTemp = new byte[mBuffer.length];
        System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
        //注意：我得项目中需要在每次发送后面加\r\n，大家根据项目项目做修改，也可以去掉，直接发送mBuffer
        Log.e("TAG", "send com = " + bytesToHexString(mBuffer));

        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBufferTemp);
            } else {
                result = false;
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    public static int isOdd(int num) {
        return num & 1;
    }

    /**
     * 16进制转成byte
     *
     * @param inHex 原始数据
     * @return
     */
    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 16进制转成byte[]
     *
     * @param inHex 原始数据字符串
     * @return
     */
    public static byte[] HexToByteArr(String inHex) {
        byte[] result;
        int hexlen = inHex.length();
        if (isOdd(hexlen) == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

}
