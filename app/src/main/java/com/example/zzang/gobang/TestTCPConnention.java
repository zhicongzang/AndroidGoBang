package com.example.zzang.gobang;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.zzang.gobang.model.BoardAgent;
import com.example.zzang.gobang.model.ChessType;
import com.example.zzang.gobang.model.Position;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observer;
import java.util.Random;

/**
 * Created by zzc on 11/13/16.
 */

// 22222 => 22222
// Android: 10.0.2.2:22222 => PC: Localhost:22222
//


public class TestTCPConnention extends BoardAgent {

    private static final int SERVER_PORT = 22222;

    private ServerSocket serverSocket;
    private boolean isListening = false;
    private Socket socket;

    private ChessType chessType;


    public TestTCPConnention(int piece, Observer o) {
        super(piece, o);

        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startListening() {
        if (isListening) {
            return;
        }
        isListening = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = serverSocket.accept();
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    Position position = new Position((String) ois.readObject());
                    Log.d("WiFi", "position: " + position.toString());
                    setNextPosition(position);
                    stopListening();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopListening() {
        if (!isListening) {
            return;
        }
        isListening = false;
    }
    public void close() {
        stopListening();
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void testRandomNextPosition() {
        startListening();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                Random random = new Random();
                int col = random.nextInt(14);
                int row = random.nextInt(14);
                Position position = new Position(col,row);
                try {
                    socket = new Socket("10.0.2.2", 22222);
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(position.toDataString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    //    public static String getLocAddress() {
//        String ipaddress = "";
//
//        try {
//            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
//            while (en.hasMoreElements()) {
//                NetworkInterface networks = en.nextElement();
//                Enumeration<InetAddress> address = networks.getInetAddresses();
//                while (address.hasMoreElements()) {
//                    InetAddress ip = address.nextElement();
//                    if (!ip.isLoopbackAddress() && ip instanceof Inet4Address) {
//                        ipaddress = ip.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//        return ipaddress;
//    }

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
