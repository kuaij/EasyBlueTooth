package com.xiaok.superbluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.WriteAbortedException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;


public class SuperBluetooth {

    public SuperBluetooth(){
        pinList = new ArrayList<>();
        macAddress = null;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private BluetoothAdapter mAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice defaultDevice;

    private String macAddress;

    private List<String> pinList;

    private enum BLUETOOTH_STATE {
        BLUETOOTH_CONNECTING,
        BLUETOOTH_CONNECTED,
        BLUETOOTH_DISCONNECTING,
        BLUETOOTH_DISCONNECTED
    }

    private final int CONNECTION_SUCCESS = 0;
    private final int CONNECTION_FAILED = 1;
    private final int CONNECTION_UNKNOWED = 2;
    private final int CONNECTION_SOCKET_ERROR = 3;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CONNECTION_SUCCESS:
                    //连接成功
                    break;
                case CONNECTION_FAILED:
                    //连接失败
                    break;
                case CONNECTION_SOCKET_ERROR:
                    //Socket异常
                    break;
                case CONNECTION_UNKNOWED:
                    //未知错误
                    break;
                default:break;
            }
        }
    };

    /**
     * 获取蓝牙当前状态
     * @return True/False
     */
    private boolean isBluetoothOpen(){
        return true;
    }

    /**
     * 通过设定的Mac地址建立与设备之间的连接
     * @return 连接是否成功，True表示成功建立连接
     */
    boolean connectWithMAC(){
        defaultDevice = mAdapter.getRemoteDevice(macAddress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Method method;
                Message msg = Message.obtain();
                try {
                    method = defaultDevice.getClass().getMethod("createRfcommSocket",new Class[]{int.class});
                    socket = (BluetoothSocket) method.invoke(defaultDevice, 1);
                } catch (Exception e) {
                    Log.e("TAG", e.toString());
                    msg.what = CONNECTION_UNKNOWED;
                }
                try {
                    if (socket != null){
                        socket.connect();
                        msg.what = CONNECTION_SUCCESS;
                    }
                    msg.what = CONNECTION_SOCKET_ERROR;

                } catch (Exception e) {
                    msg.what = CONNECTION_FAILED;
                }
                handler.sendMessage(msg);

            }
        });
        return true;
    }

    /**
     * 通过程序内定义好的一个或多个PIN与蓝牙设备进行连接，若均连接失败，则由用户手动输入
     * @return
     */
    private boolean connectWithoutPin(){
        return true;
    }

    /**
     * 设定将要连接设备的mac地址，将通过此Mac地址直接建立连接
     * @param macAddress:设定的mac地址
     * @return:
     */
    public void setMacAddress(String macAddress){
        this.macAddress = macAddress;
    }

    /**
     * 设定程序自动连接时所使用的PIN，允许设定多个
     * @param pins:设定的PIN
     * @return:当前集合中PIN的数目
     */
    private int setPin(String...pins){
        pinList.clear();
        Collections.addAll(pinList,pins);
        if (pinList.size() != 0){
            setBluetoothPin(pinList.get(0));
            return pinList.size();
        }
        return -1;
    }

    private void setBluetoothPin(String pinStr){
        try {
            ClsUtils.setPin(defaultDevice.getClass(), defaultDevice, pinStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前手机的蓝牙状态，正在连接、已连接、正在断开、已断开
     * @return BLUETOOTH_STATE
     */
    private BLUETOOTH_STATE getBluetoothState(){
        return BLUETOOTH_STATE.BLUETOOTH_DISCONNECTED;
    }


    public InputStream getInputStream(){
        if (socket != null){
            try {
                return socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void writeDate(String date){
        if (socket != null){
            try {
                byte[] st = date.getBytes();
                OutputStream os = socket.getOutputStream();
                os.write(st);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
