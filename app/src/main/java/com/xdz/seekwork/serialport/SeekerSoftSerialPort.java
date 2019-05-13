package com.xdz.seekwork.serialport;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * about: SerialPort
 * <p>
 */

public class SeekerSoftSerialPort {

    static {
        System.loadLibrary("serial_port");
    }

    private static final String TAG = "SeekerSoftSerialPort";

    /**
     * DO not remove or rename the field mFd: it is used by native method close()
     */
    private FileDescriptor mFd;

    private FileInputStream mFileInputStream;

    private FileOutputStream mFileOutputStream;

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    private native static FileDescriptor open(String path, int baudrate, int flag);

    public native void close();

    public SeekerSoftSerialPort(File device, int baudrate, int flag) throws SecurityException, IOException {
        // check access permission
        if (device != null && (!device.canRead() || !device.canWrite())) {
            try {
                // Missing read/wirte permission, try to chomod the file
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chomd 777 " + device.getAbsolutePath() + "\n" + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    Log.e(TAG, "chomd 777 Is Failed, throw the securityException");
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, flag);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }

        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);

    }

    public boolean getOpenSerialResult() {
        if (mFd == null) {
            return false;
        }
        return true;
    }

}
