import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by ZZANG on 11/17/16.
 */

public class TestWiFi {
    private static final int GAME_PORT = 44444;
    private static final int SEARCH_PORT = 33333;
    private static final int PLAYING_PORT = 22222;
    private static final int DEBUG_PORT = 55555;



    public static void main(String[] args) throws Exception {
        TestWiFi testWiFi = new TestWiFi();
        testWiFi.start();
    }

    private void start()  {
        try {
            Scanner sc = new Scanner(System.in);
//            System.out.println("IP address to Test: ");
//            ip = sc.nextLine();
            InetAddress ownIP=InetAddress.getLocalHost();
            final String myIP = ownIP.getHostAddress();
            System.out.println("IP of my system is "+myIP);
            String s = "{\"blackIPAddress\":\""+ myIP +"\",\"blackPlayerName\":\"ZZC\"}";
            System.out.println("----------------------");
            new ServerThread(myIP,GAME_PORT) {
                @Override
                void toDo(String serverIP) {}
            }.start();
            new ServerThread(myIP,SEARCH_PORT) {
                @Override
                void toDo(String serverIP) {}
            }.start();
            new ServerThread(myIP,PLAYING_PORT) {
                @Override
                void toDo(String serverIP) {}
            }.start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket("127.0.0.1", DEBUG_PORT);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(myIP);
                        System.out.println("Debug Mode Active!");
                    } catch (IOException e) {
                        System.out.println("Debug Mode Inactive!");
                    }
                }
            }).start();


            while(true) {
                System.out.println("Send Request To Port: ");
                int port = sc.nextInt();
                System.out.println("Request: ");
                String request = sc.nextLine();
                Socket socket = new Socket(ip, port);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(request);
                System.out.println("Request send successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract class ServerThread extends Thread {
        private int port;
        private String myIP;
        private String toIP;

        public ServerThread(String myIP, int port) {
            this.myIP = myIP;
            this.port = port;
            toIP = myIP;
        }

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    String info = (String) ois.readObject();
                    String fromIP = socket.getInetAddress().getHostAddress();
                    if(!fromIP.equals(myIP)&&!fromIP.equals("127.0.0.1")) {
                        toIP = fromIP;
                    }
                    System.out.println("Server Port: " + port + "    From IP: " + fromIP);
                    System.out.println("Info: " + info);
                    String[] strings = info.split("@@@@");
                    if (strings.length > 1) {
                        String ip = strings[0].equals("127.0.0.1") ? toIP : strings[0];
                        Socket sendSocket = new Socket(ip, port);
                        ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
                        oos.writeObject(strings[1]);
                    } else {
                        Socket sendSocket = new Socket("127.0.0.1", port);
                        ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
                        oos.writeObject(info);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        abstract void toDo(String serverIP);
    }
}
