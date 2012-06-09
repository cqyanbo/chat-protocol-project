package Client;

import java.io.BufferedReader;
import java.net.DatagramSocket;
import java.net.SocketException;

class clientRunner 
{
	public static void main(String args[])
	{
	    DatagramSocket socket = null;
	    BufferedReader in = null;
	    
        try {
			socket = new DatagramSocket(4445);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		client c = new client();
		c.run();
	}    
} 