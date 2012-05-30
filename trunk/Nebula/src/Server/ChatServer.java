package Server;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import Client.iClient;
import Client.fakeClient;


/*
 * This is the main class of serverside
 */
public class ChatServer{
	
	public static void main(String[] args)
	{
		// start a thread to handle the packet broadcasting
		BroadcastThread broadcast = new BroadcastThread();
		
		// start a serversocket for listening client's connection requests
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(10000);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// initialize the network connection
		try
		{

			broadcast.start();
			
			// start an infinite loop
			while(true)
			{
				Socket incoming = serverSocket.accept();
				
				// spawn a thread to handle the request
				SingleClientThread singleClientThread = new SingleClientThread(incoming);
				singleClientThread.start();
			}
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			broadcast.stop();
			
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
