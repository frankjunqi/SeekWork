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
    private SeekerSoftSerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    // serial port about read thread listen for listen the data from the serial port device
    private OnDataReceiveListener onDataReceiveListener = null;

    // serial port read thrad & running flag
    private ReadThread mReadThread;

    // serial port thread interrupt and close serial port:
    // true : close ; false : open
    private boolean isStop = false;

    // device & baudrate
    private String devicePath = "/dev/ttymxc3";
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


    public CardReadSerialPort() {
        onCreate();
    }

    /**
     * 打开 Card Serial
     */
    private void onCreate() {
        try {
            //  打开 Card Serial
            mSerialPort = new SeekerSoftSerialPort(new File(devicePath), baudrate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
        } catch (Exception e) {
            Log.e(TAG, "Init Card Serial Open Port Failed");
            mSerialPort = null;
        }
        // 开启 串口 读取 线程
        mReadThread = new ReadThread();
        mReadThread.start();
    }

    /**
     * 关闭串口
     */
    public void closeReadSerial() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }


    private class ReadThread extends Thread {

        @Override
        public void run() {
            String IDNUM = "";
            while (!isStop && !isInterrupted()) {
                // 串口开启，做读取数据
                int size = 1;
                try {
                    if (mInputStream == null) {
                        return;
                    }
                    byte[] buffer = new byte[1];
                    size = mInputStream.read(buffer);
                    IDNUM = IDNUM + new String(buffer, 0, size);
                    Log.e("test", "idnum = " + IDNUM);
                    // 默认以 "\r\n" 结束读取
                    if (IDNUM.endsWith("\r\n")) {
                        if (null != onDataReceiveListener) {
                            onDataReceiveListener.onDataReceiveString(getStringIn(IDNUM));
                            IDNUM = "";
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
