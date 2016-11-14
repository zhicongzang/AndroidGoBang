
import java.io.ObjectOutputStream;  
import java.net.Socket; 
import java.io.Serializable; 
import java.util.Scanner;
public class TestWiFi {  
    public static void main(String[] args) throws Exception {  
    	TestWiFi testWiFi = new TestWiFi();
 		testWiFi.start();
    }

	private void start()  {
		try {
			Socket socket = new Socket("127.0.0.1", 22222);  
	    	int col = 0;
	    	int row = 0;

	        while(true) {
	        	Scanner sc = new Scanner(System.in);
	        	System.out.println("Col: ");
	        	col = sc.nextInt();
	        	System.out.println("Row: ");
	        	row = sc.nextInt();
	        	ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());  
		        oos.writeObject((new Position(col,row)).toDataString()); 
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	} 

}