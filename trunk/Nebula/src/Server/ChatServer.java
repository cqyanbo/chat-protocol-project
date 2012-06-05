package Server;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import Basic.Message;
import Client.iClient;
import Client.fakeClient;


/*
 * This is the main class of serverside
 */
public class ChatServer{

	// store all of active sockets for broadcast
	private static ArrayList<Socket> socketlist = new ArrayList<Socket>();
	
	// arraylist to store all of the threads from client
	private static ArrayList<Thread> threadslist = new ArrayList<Thread>();
	
	public synchronized static void AddThreadToList(SingleClientThread _thread)
	{
		synchronized(threadslist)
		{
			threadslist.add(_thread);
		}
	}
	
	public synchronized static void DeleteFromThreadList(SingleClientThread _thread)
	{
		synchronized(threadslist)
		{
			threadslist.remove(_thread);
		}
	}
	
	public synchronized static void AddSocketToList(Socket _socket)
	{
		synchronized(socketlist)
		{
			socketlist.add(_socket);
		}
	}
	
	public synchronized static ArrayList<Thread> GetThreadsList()
	{
		synchronized(threadslist)
		{
			@SuppressWarnings("unchecked")
			ArrayList<Thread> tmp = (ArrayList<Thread>) threadslist.clone();
			return tmp;
		}
	}
	
	public synchronized static Thread GetThread(int index)
	{
		synchronized(threadslist)
		{
			return threadslist.get(index);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public synchronized static ArrayList<Socket> GetSocketList()
	{
		synchronized(socketlist)
		{
			ArrayList<Socket> tmp = (ArrayList<Socket>)socketlist.clone();
			return tmp;
		}
	}
	
	public static void main(String[] args)
	{
		
		// start a serversocket for listening client's connection requests
		ServerSocket serverSocket = null;
		
		BroadcastThread broadcast = new BroadcastThread();
		broadcast.start();
		System.out.println("broadcast thread has begun");
		threadslist.add(0, broadcast);
		// initialize the network connection
		try
		{
			System.out.println("Start to listen: ");
			serverSocket = new ServerSocket(9999);
			System.out.println("There are " + threadslist.size() + " threads: " + threadslist.get(0).getName());
			// start an infinite loop
			while(true)
			{
				Socket incoming = serverSocket.accept();
				System.out.println("connected with " + incoming.getInetAddress());
				
				// spawn a thread to handle the request
				SingleClientThread singleClientThread = new SingleClientThread(incoming);
				System.out.println("Start to run single thread for this client");
				singleClientThread.start();
			}
		}
		catch(Exception e)
		{
			System.out.println("The server could not be connected now.");
		}
		finally
		{	
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
