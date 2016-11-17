import java.io.*;
import java.net.*;
import com.alibaba.fastjson.*;



public class TestWiFiSearching {

    private static final int GAME_PORT = 44444;
    private static final int SEARCH_PORT = 33333;


    public static void main(String[] args) throws Exception {  
        TestWiFiSearching testWiFiSearching = new TestWiFiSearching();
        testWiFiSearching.start();
    }

    private void start()  {
        try {
            InetAddress ownIP=InetAddress.getLocalHost();
            String myIP = ownIP.getHostAddress();
            System.out.println("IP of my system is "+myIP);
            ServerSocket serverSocket = new ServerSocket(GAME_PORT);
            String s = "{\"blackIPAddress\":\""+ myIP +"\",\"blackPlayerName\":\"ZZC\"}";
            System.out.println("----------------------");
            while(true) {
                System.out.println("Waiting...");
                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String toIPAddress = (String) ois.readObject();
                System.out.println("Get IP Address: " + toIPAddress);
                Socket sendSocket = new Socket(toIPAddress, SEARCH_PORT); 
                ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
                oos.writeObject(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    

}