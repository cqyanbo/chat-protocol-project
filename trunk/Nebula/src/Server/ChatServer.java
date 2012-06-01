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
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{
		// arraylist to store all of the threads from client
		ArrayList<SingleClientThread> threadslist = new ArrayList<SingleClientThread>();
		
		// start a thread to handle the packet broadcasting
		BroadcastThread broadcast = new BroadcastThread();
		
		// start a serversocket for listening client's connection requests
		ServerSocket serverSocket = null;

		// initialize the network connection
		try
		{
			// start a thread for handling message broadcast
			broadcast.start();
			
			serverSocket = new ServerSocket(7000);
			// start an infinite loop
			while(true)
			{
				Socket incoming = serverSocket.accept();
				
				// spawn a thread to handle the request
				SingleClientThread singleClientThread = new SingleClientThread(incoming);
				threadslist.add(singleClientThread);	// store this thread to threads list
				singleClientThread.start();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
