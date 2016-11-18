import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by ZZANG on 11/17/16.
 */

public class TestWiFi {
    private static final int GAME_PORT = 44444;
    private static final int SEARCH_PORT = 33333;
    private static final int PLAYING_PORT = 22222;


    public static void main(String[] args) throws Exception {
        TestWiFi testWiFi = new TestWiFi();
        testWiFi.start();
    }

    private void start()  {
        try {
            String ip;
            Scanner sc = new Scanner(System.in);
            System.out.println("IP address to Test: ");
            ip = sc.nextLine();
            InetAddress ownIP=InetAddress.getLocalHost();
            String myIP = ownIP.getHostAddress();
            System.out.println("IP of my system is "+myIP);
            String s = "{\"blackIPAddress\":\""+ myIP +"\",\"blackPlayerName\":\"ZZC\"}";
            System.out.println("----------------------");
            new ServerThread(GAME_PORT) {
                @Override
                void toDo() {}
            }.start();
            new ServerThread(SEARCH_PORT) {
                @Override
                void toDo() {}
            }.start();
            new ServerThread(PLAYING_PORT) {
                @Override
                void toDo() {}
            }.start();

            while(true) {
            	sc = new Scanner(System.in);
            	System.out.println("Request: ");
                String request = sc.nextLine();
                System.out.println("Send Request To Port: ");
                int port = sc.nextInt();
                if (request != null || request.length() > 0) {
                	try {
                		Socket socket = new Socket(ip, port);
                		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                		oos.writeObject(request);
                		System.out.println("Request send successfully.");
                	} catch (Exception e) {
            			e.printStackTrace();
        			}
                	
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract class ServerThread extends Thread {
        private int port;

        public ServerThread(int port) {
            this.port = port;
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
                    System.out.println("Server Port: " + port + "    From IP: " + socket.getInetAddress().getHostAddress());
                    System.out.println("Info: " + info);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        abstract void toDo();
    }
}
