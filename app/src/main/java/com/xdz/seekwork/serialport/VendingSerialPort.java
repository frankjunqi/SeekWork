package com.xdz.seekwork.serialport;

import android.util.Log;

import com.xdz.seekwork.util.LogCat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

/**
 * Card Read Serial Port Util
 * <p>
 * Created by kjh08490 on 2016/11/1.
 */

public class VendingSerialPort {

    private String TAG = VendingSerialPort.class.getSimpleName();
    private static VendingSerialPort portUtil;
    private static SeekerSoftSerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private OnDataReceiveListener onDataReceiveListener = null;
    private OnCmdCallBackListen onCmdCallBackListen = null;

    private ReadThread mReadThread;
    private static boolean isStop = false;
    private String devicePath = "/dev/ttymxc1";// tty02
    private int baudrate = 19200;

    public static final int H1 = 0xEF;
    public static final int H2 = 0xEE;
    public static final int H3 = 0xFE;
    public static final int ACK = 0x00;
    public static final int NAK = 0x15;
    public static final int POLL = 0x56;

    // 出货队列
    private Stack stack = new Stack();

    private byte[] ReceiveData = new byte[1024];
    private int checksum = 0;
    public byte ReSign = 0;// 指令
    private int ReceiveLn = 0;
    private long DataLn = 0;

    // //数据解析状态
    public enum DATASTATE {
        IDEL,
        FIND_H1,
        FIND_H2,
        FIND_ALL_DATA,
        FIND_CHECKSUM,
        FIND_TAIL
    }

    public static VendingSerialPort SingleInit() {
        // 如果停止监听 或者 第一次启动
        if (isStop || null == portUtil) {
            isStop = false;
            portUtil = new VendingSerialPort();
            portUtil.onCreate();
        }
        return portUtil;
    }

    // 往堆栈中压人出货指令
    public VendingSerialPort pushCmdOutShipment(ShipmentObject shipmentObject) {
        stack.push(shipmentObject);
        return this;
    }

    // 获取堆栈中栈顶指令
    public ShipmentObject popCmdOutShipment() {
        if (stack.isEmpty()) {
            return null;
        } else {
            return (ShipmentObject) stack.pop();
        }
    }

    // 根据指令获得数据段长度
    public int GetProLn(int cmd) {
        switch (cmd) {
            case 0x54:
                return 6;//
            case 0x55:
                return 14;//
            case 0x56:
                return 19;//POLL指令
            case 0x58:
                return 50;//签到
            case 0x59:
                return 14;//货道故障（有货无货）信息
            case 0x5A:
                return 14;//	系统配置信息
            case 0x5B:
                return 208;//商品销售汇总信息
            case 0x5C:
                return 11;//出货
            case 0x5D:
                return 16;//机器运行（故障）信息
            case 0x5F:
                return 401;//货道价格信息
            case 0x61:
                return 1;//POS机状态信息
            case 0x62:
                return 401;//货道优惠信息
            case 0x64:
                return 11;// 格子柜的信息
            default:
                return 0;
        }
    }


    // 工控回复vmc，在poll命令条件下
    private void Answer(byte id, byte code, int containerNum, int proMum, int objectId) {
        byte[] sendData = new byte[200];
        byte checksum = 0;
        switch (code) {
            case 0x02://设置VMC的系统时间
                break;
            case 0x03://通知VMC出货。
                try {
                    LogCat.e("<<<  VMC Out Product");
                    sendData[0] = POLL;
                    sendData[1] = code;//交易码
                    sendData[2] = (byte) containerNum;//货柜编号
                    sendData[3] = (byte) proMum;//货道编号
                    sendData[4] = 0;//变价出货
                    sendData[5] = 0;//售卖金额
                    sendData[6] = 0;//售卖金额
                    sendData[7] = 0;//售卖金额
                    sendData[8] = 0;//售卖金额
                    sendData[9] = 1;//支付方式
                    sendData[10] = 0;//流水号
                    sendData[11] = 0;//流水号
                    sendData[12] = (byte) objectId;//流水号
                    for (int i = 0; i < 13; i++) {
                        checksum ^= sendData[i];
                    }
                    sendData[13] = checksum;
                    // 此处需要重新根据出货成功标识进行判断是否成功出货的回调
                    sendBuffer(sendData);
                } catch (Exception e) {
                    ACKorNAK(id, ACK);
                }
                break;
            case 0x04://设置货道的价格。
                break;
            case 0x05://制冷、加热等系统配置信息
                break;
            case 0x06://清除销售统计
                break;
            case 0x07://获取销售信息。
                break;
            case 0x08://开始设置货道价格。通知VMC要开始设置货道的价格。 不支持此功能可以忽略此帧，回复ACK即可。
                break;
            case 0x09://设置货道价格完成。通知VMC货道价格设置完成。不支持此功能可以忽略此帧，回复ACK后在一定时间内需时上报货道价格信息。
                break;
            case 0x0a://设置货道优惠信息。
                break;
            case 0x0b://设置交易发起和结束。
                break;
            //交易的发起仅仅是告诉VMC，用户通过UI上选中了相应的货道。
            //交易的结束也是告诉VMC，用户购买结束。
            default:
                break;
        }
    }

    // 工控机回复ACK或者NAK
    private void ACKorNAK(byte id, int is) {
        byte[] data = new byte[3];
        data[0] = id;
        data[1] = (byte) is;
        data[2] = (byte) (is ^ byteToInt(id));
        LogCat.e("<<<  ACKorNAK " + bytesToHexString(data));
        sendBuffer(data);
    }

    // 工控机收到的VMC命令
    private void CMD(byte id) {
        switch (id) {
            case 0x54://货柜连接信息
                LogCat.e("<<<  0x54 货柜连接信息");
                ACKorNAK(id, ACK);
                break;
            case 0x55://货道设置信息
                LogCat.e("<<<  0x55 货道设置信息");
                ACKorNAK(id, ACK);
                break;
            case 0x56://POLL指令
                LogCat.e("<<<  0x56 POLL指令");
                ShipmentObject shipmentObject = popCmdOutShipment();
                if (shipmentObject != null) {
                    Answer(id, (byte) 0x03, shipmentObject.containerNum, shipmentObject.proNum, shipmentObject.objectId);
                } else {
                    ACKorNAK(id, ACK);
                }
                break;
            case 0x58:// 签到
                LogCat.e("<<<  0x58 签到");
                ACKorNAK(id, ACK);
                break;
            case 0x59://货道故障（有货无货）信息
                LogCat.e("<<<  0x59 货道故障（有货无货）信息");
                ACKorNAK(id, ACK);
                break;
            case 0x5A://系统配置信息
                LogCat.e("<<<  0x5A 系统配置信息");
                ACKorNAK(id, ACK);
                break;
            case 0x5B://商品销售汇总信息
                LogCat.e("<<<  0x5B 商品销售汇总信息");
                ACKorNAK(id, ACK);
                break;
            case 0x5C://出货
                ACKorNAK(id, ACK);
                LogCat.e("<<<  0x5C 出货" + (ReceiveData.length >= 8 ? ReceiveData[7] : ""));
                // 判断出货是否成功
                if (ReceiveData.length >= 8 && (byte) 0 == ReceiveData[7]) {
                    LogCat.e("<<<  0x5C 出货 success");
                    onCmdCallBackListen.onCmdCallBack(true);
                } else {
                    LogCat.e("<<<  0x5C 出货 failed");
                    onCmdCallBackListen.onCmdCallBack(false);
                }
                break;
            case 0x5D://机器运行（故障）信息
                LogCat.e("<<<  0x5D 机器运行（故障）信息");
                ACKorNAK(id, ACK);
                break;
            case 0x5F://货道价格信息
                LogCat.e("<<<  0x5F 货道价格信息");
                ACKorNAK(id, ACK);
                break;
            case 0x61://POS机状态信息
                LogCat.e("<<<  0x61 POS机状态信息");
                ACKorNAK(id, ACK);
                break;
            case 0x62://货道优惠信息
                LogCat.e("<<<  0x62 货道优惠信息");
                ACKorNAK(id, ACK);
                break;
            case 0x64://格子的状态
                LogCat.e("<<<  0x64 格子的状态");
                ACKorNAK(id, ACK);
                break;
            default:
                LogCat.e("<<<  ERROR DEFAULT ");
                ACKorNAK(id, ACK);
                break;
        }
    }

    /**
     * 数据回调接口
     */
    public interface OnDataReceiveListener {

        void onDataReceiveString(String IDNUM);

        void onDataReceiveBuffer(byte[] buffer, int size);
    }

    public interface OnCmdCallBackListen {
        void onCmdCallBack(boolean isSuccess);
    }

    public VendingSerialPort setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
        return this;
    }

    public VendingSerialPort setOnCmdCallBackListen(OnCmdCallBackListen onCmdCallBackListen) {
        this.onCmdCallBackListen = onCmdCallBackListen;
        return this;
    }

    /**
     * 初始化串口信息，并启动此串口的read来自vmc的信号数据
     * 在启动程序的时候就做此初始化操作。
     */
    private void onCreate() {
        try {
            mSerialPort = new SeekerSoftSerialPort(new File(devicePath), baudrate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (Exception e) {
            Log.e(TAG, "Init Serial Port Failed");
            mSerialPort = null;
        }
    }


    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isStop && !isInterrupted()) {
                try {
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[1];
                    int size = mInputStream.read(buffer);
                    if (Protocal(buffer[0])) {
                        CMD(ReSign);
                    }
                    // 回传数据给业务
                    if (onDataReceiveListener != null) {
                        onDataReceiveListener.onDataReceiveString(bytesToHexString(buffer));
                        // 实时传出buffer,让业务进行处理。什么时候开始,什么时候结束
                        onDataReceiveListener.onDataReceiveBuffer(buffer, size);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
            }
        }
    }


    // 串口数据状态格式签名是否正确
    DATASTATE state = DATASTATE.IDEL;

    // 校验数据是否格式签名正确
    public boolean Protocal(byte ch) {
        ReceiveData[ReceiveLn++] = ch;
        //System.out.print(byteToInt(ch));
        int byteCh = byteToInt(ch);
        if ((state == DATASTATE.IDEL) && (byteCh == H1))//0xEF
        {
            LogCat.e("<<< H1");
            state = DATASTATE.FIND_H1;
        } else if ((state == DATASTATE.FIND_H1) && (byteCh == H2))//0xEE
        {
            LogCat.e("<<< H2");
            state = DATASTATE.FIND_H2;
        } else if ((state == DATASTATE.FIND_H2) && (byteCh == H3))//0xFE
        {
            state = DATASTATE.FIND_ALL_DATA;
            ReSign = ReceiveData[ReceiveLn - 4];//获取指令
            //checksum=ReSign+0xDB;
            checksum = ReSign ^ 0xff;
            // qDebug()<<"ReSign:"<<ReSign;
            DataLn = GetProLn(ReSign);//获取数据段长度
            // qDebug()<<"DataLn:"<<DataLn;
            ReceiveLn = 0;

            byte[] byteResign = new byte[1];
            byteResign[0] = ReSign;
            LogCat.e("<<< H3 " + bytesToHexString(byteResign) + " ," + DataLn);
        } else if (state == DATASTATE.FIND_ALL_DATA)//开始接收数据
        {
            if (ReceiveLn == DataLn) {
                state = DATASTATE.FIND_CHECKSUM;
            }
        } else if (state == DATASTATE.FIND_CHECKSUM) {
            for (int i = 0; i < DataLn; i++) {
                checksum ^= ReceiveData[i];
            }
            if (checksum == byteCh) {// pass
                LogCat.e("<<< OK");
                return true;
            } else {
                state = DATASTATE.IDEL;
                return false;
            }
        } else {
            state = DATASTATE.IDEL;
        }
        return false;
    }

    /**
     * 数组转换成十六进制字符串
     *
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
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
     * @param cmd 　应该是原始指令的字符串
     * @return
     */
    public boolean sendCmds(String cmd) {
        boolean result = true;
        byte[] mBuffer = cmd.getBytes();
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
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
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBufferTemp);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
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
