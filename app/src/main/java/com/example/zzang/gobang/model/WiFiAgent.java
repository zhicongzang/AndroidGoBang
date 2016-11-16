package com.example.zzang.gobang.model;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zzc on 11/15/16.
 */

public class WiFiAgent extends BoardAgent {

    private final static int SERVER_PORT  = 22222;
    private String destinationIPAddress;
    private ServerSocket serverSocket;
    private Socket sendSocket;
    private Socket recieveSocket;
    private boolean isListening = false;
    private boolean isAccepting = false;
    private ExecutorService threadPool;


    public WiFiAgent(int piece, String destinationIPAddress ,Observer o) {
        super(piece, o);
        threadPool = Executors.newFixedThreadPool(2);
        this.destinationIPAddress = destinationIPAddress;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        if (isListening) {
            return;
        }
        isListening = true;
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while(isListening) {
                    try {
                        if (isAccepting) {
                            recieveSocket = serverSocket.accept();
                            ObjectInputStream ois = new ObjectInputStream(recieveSocket.getInputStream());
                            Position position = new Position((String) ois.readObject());
                            setNextPosition(position);
                            isAccepting = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void stopListening() {
        if (!isListening) {
            return;
        }
        isListening = false;
        try {
            if(recieveSocket != null) {
                recieveSocket.shutdownOutput();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startAccepting() {
        isAccepting = true;
    }

    public void sendPosition(final Position position) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendSocket = new Socket(destinationIPAddress, SERVER_PORT);
                    ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
                    oos.writeObject(position.toDataString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static String getWIFILocalIpAdress(Context mContext) {
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = formatIpAddress(ipAddress);
        return ip;
    }

    private static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF ) + "." +
                ((ipAdress >> 8 ) & 0xFF) + "." +
                ((ipAdress >> 16 ) & 0xFF) + "." +
                ( ipAdress >> 24 & 0xFF) ;
    }


}
