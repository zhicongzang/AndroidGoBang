import java.io.*;
import java.net.*;



public class TestServer {

    private static final int SERVER_PORT = 22222;

    public static void main(String[] args) throws Exception {  
        TestServer testServer = new TestServer();
        testServer.start();
    }

    private void start()  {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            while(true) {
                System.out.println("Waiting...");
                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String position = (String) ois.readObject();
                Socket sendSocket = new Socket("127.0.0.1", 22222); 
                ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
                oos.writeObject(position);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}