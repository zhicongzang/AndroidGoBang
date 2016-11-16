import java.io.*;
import java.net.*;



public class TestWiFiSearching {

    private static final int SERVER_PORT = 33333;

    public static void main(String[] args) throws Exception {  
        TestWiFiSearching testWiFiSearching = new TestWiFiSearching();
        testWiFiSearching.start();
    }

    private void start()  {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            while(true) {
                System.out.println("Waiting...");
                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String toIPAddress = (String) ois.readObject();
                System.out.println("Get IP Address: " + toIPAddress);
                Socket sendSocket = new Socket(toIPAddress, SERVER_PORT); 
                ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
                WiFiGame game = new WiFiGame("ZZC", 
                                                "ZZC's Game",
                                                InetAddress.getLocalHost().getHostAddress());
                System.out.println(game.toDataString());
                oos.writeObject(game.toDataString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}