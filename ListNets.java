import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;

public class ListNets {

    public static void main(String args[]) throws SocketException, UnknownHostException {
        // Enumeration e = NetworkInterface.getNetworkInterfaces();
        // while(e.hasMoreElements())
        // {
        //     NetworkInterface n = (NetworkInterface) e.nextElement();
        //     Enumeration ee = n.getInetAddresses();
        //     while (ee.hasMoreElements())
        //     {
        //         InetAddress i = (InetAddress) ee.nextElement();
        //         System.out.println(i.getHostAddress());
        //     }
        // }
        InetAddress ownIP=InetAddress.getLocalHost();
        System.out.println("IP of my system is := "+ownIP.getHostAddress());
    }     
}  