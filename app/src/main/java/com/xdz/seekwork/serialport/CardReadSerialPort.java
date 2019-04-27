package com.xdz.seekwork.serialport;

import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Card Read Serial Port Util
 * <p>
 */

public class CardReadSerialPort {

    private String TAG = CardReadSerialPort.class.getSimpleName();

    // serial port JNI object
    private static CardReadSerialPort portUtil;
    private static SeekerSoftSerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    // serial port about read thread listen for listen the data from the serial port device
    private OnDataReceiveListener onDataReceiveListener = null;

    // serial port read thrad & running flag
    private ReadThread mReadThread;

    // serial port thread interrupt and close serial port:
    // true : close ; false : open
    private static boolean isStop = false;

    // device & baudrate
    private String devicePath = "/dev/ttyS0";
    // tty02--- ttymxc1   ; ttyo3---ttymxc2  ;  tty04---ttymxc3  ;  tty05---ttymxc4  ;
    //   ICCard is OK        ICCard is Ok       ICCrad is not BAD     ICCard is OK
    // tty06---ttyES0  ; tty07---ttyES1 ;
    //  ICCard is OK     ICCard is BAD(IDCardReadSerialPortUtil: length=1; regionStart=0; regionLength=-1)

    // tty02--- ttymxc1   ; ttyo3---ttymxc2  ;  tty04---ttymxc3  ;  tty05---ttymxc4  ;
    //   IDCard is OK        IDCard is Ok       IDCrad is not BAD     IDCard is OK
    // tty06---ttyES0  ; tty07---ttyES1 ;
    //  IDCard is OK     IDCard is BAD(IDCardReadSerialPortUtil: length=1; regionStart=0; regionLength=-1)

    private int baudrate = 9600;

    public interface OnDataReceiveListener {
        void onDataReceiveString(String IDNUM);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }


    public static CardReadSerialPort SingleInit() {
        // 如果停止监听 或者 第一次启动
        if (isStop || null == portUtil) {
            isStop = false;
            portUtil = new CardReadSerialPort();
            portUtil.onCreate();
        }
        return portUtil;
    }


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


    private class ReadThread extends Thread {

        @Override
        public void run() {
            byte[] backData = new byte[14];
            while (!isStop && !isInterrupted()) {
                // 串口开启，做读取数据
                int size = 1;
                try {
                    if (mInputStream == null) {
                        return;
                    }
                    size = mInputStream.read(backData);
                    String IDNUM = new String(backData, 0, size);
                    Log.e("test", "idnum = " + IDNUM);
                    // 默认以 "\r\n" 结束读取
                    if (IDNUM.contains("\r\n")) {
                        if (null != onDataReceiveListener) {
                            onDataReceiveListener.onDataReceiveString(getStringIn(IDNUM));
                            backData = new byte[14];
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
     * 16进制转成byte
     *
     * @param inHex 原始数据
     * @return
     */
    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public String getStringIn(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}
