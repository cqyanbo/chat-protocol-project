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
	
	private static ArrayList<Message> broadlist = new ArrayList<Message>();	// this is the place for storing all the message that would be
	private static ArrayList<User> userlist = new ArrayList<User>();	// store users
	
	public static synchronized void AddMessage(Message o)
	{
		broadlist.add(o);
	}
	
	public static synchronized void AddMessageList(ArrayList o)
	{
		broadlist.addAll(o);
	}
	
	public static synchronized ArrayList GetMessageList()
	{
		return broadlist;
	}
	
	public static synchronized void AddUser(User user)
	{
		userlist.add(user);
	}
	
	public static synchronized boolean CheckUser(String username)
	{
		for(User u : userlist)
		{
			if(u.GetUsername() == username.trim())
			{
				return true;
			}
			
		}
		
		return false;
	}
	
	public static synchronized void DeleteUser(User user)
	{
		int index = 0;
		
		for(User u : userlist)
		{
			if(u.GetUsername() == user.GetUsername())
			{
				userlist.remove(index);
				break;
			}
			
			index++;
		}
	}
	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{
	
		// arraylist to store all of the threads from client
		ArrayList<SingleClientThread> threadslist = new ArrayList<SingleClientThread>();
		
		// start a serversocket for listening client's connection requests
		ServerSocket serverSocket = null;

		// initialize the network connection
		try
		{
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
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
