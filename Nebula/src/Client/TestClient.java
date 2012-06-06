package Client;

/*  The java.net package contains the basics needed for network operations. */
import java.net.*;
import java.util.Scanner;
/* The java.io package contains the basics needed for IO operations. */
import java.io.*;

import Basic.Message;

public class TestClient {
	
	public static void main(String[] args) {
	    /** Define a host server */
	    String host = "localhost";
	    /** Define a port */
	    int port = 9999;

	    StringBuffer instr = new StringBuffer();
	    String TimeStamp;
	    System.out.println("SocketClient initialized");
	    try {
	        /** Obtain an address object of the server */
	        InetAddress address = InetAddress.getByName(host);
	        /** Establish a socket connetion */
	        Socket connection = new Socket(address, port);
	        /** Instantiate a BufferedOutputStream object */
	        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

	        while(true)
	        {
	        	if(!connection.isConnected())
	        	{
	        		connection.close();
	        	}
	            /** Instantiate an OutputStreamWriter object with the optional character
	             * encoding.
	             */
	            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
	            OutputStreamWriter osw = new OutputStreamWriter(bos);
	            DataInputStream isr = new DataInputStream(bis);
	            TimeStamp = new java.util.Date().toString();
	            /**Instantiate an InputStreamReader with the optional
	             * character encoding.
	             */
	            Scanner in = new Scanner(System.in);
	            String process = "Calling the Socket Server on "+ host + " port " + port +
	                " at " + TimeStamp +  (char) 13;
	            System.out.print("Please enter message type you want to send: ");
	            int messagetype = Integer.parseInt(in.nextLine());
	            
	            System.out.print("Please enter the Data you want to send: ");
	            
	            String data = in.nextLine();
	            
	            Message m = new Message(1, messagetype, 0, data.length(), data);
	            /** Write across the socket connection and flush the buffer */
	            bos.write(m.Packet2ByteArray());
	            bos.flush();
	            osw.flush();
	            
	            
	            /**Read the socket's InputStream and append to a StringBuffer */
	            Message message = GetNewMessage(isr);
	            
	            System.out.println("Got " + message.GetMessageType() + "Message");
	            
	            if(message.GetMessageType() == 42)
	            {
	            	connection.close();
	            }

	            /** Close the socket connection. */
	            //connection.close();
	            //System.out.println(instr);
	           }
	    }
	          catch (IOException f) {
	            System.out.println("IOException: " + f);
	          }
	          catch (Exception g) {
	            System.out.println("Exception: " + g);
	          }
	        }
	    
	
	private static Message GetNewMessage(DataInputStream input2)
	{
		Message m = new Message();
		DataInputStream d = input2;
		byte[] buffer = new byte[285];
		String b = "";
		try {
			d.read(buffer);
			System.out.println(b.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tt = new String(buffer);
		char[] test = (tt).toCharArray();
		
		m = Message.ByteArrayToMessage(test);
		// read each part of packet from the buffer
		
		return m;
	}

}
