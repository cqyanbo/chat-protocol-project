package Server;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Basic.Message;

public class BroadcastThread extends Thread {
	
	private static ArrayList<Message> broadlist = new ArrayList<Message>();	// this is the place for storing all the message that would be
	private static ArrayList<User> userlist = new ArrayList<User>();	// store users
	private Message m;
	private ArrayList<Thread> clientsThreads = ChatServer.GetThreadsList();
	private static Lock lock = new ReentrantLock();

	BroadcastThread( Message _m)
	{
		System.out.println("inside the broadcast");
		this.m = _m;
		BroadcastThread.AddMessage(m);
	}
	
	BroadcastThread()
	{
		
	}
	
	public static void AddMessage(Message o)
	{
		lock.lock();
		try
		{
			broadlist.add(o);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public static void AddMessageList(ArrayList<Message> o)
	{
		lock.lock();
		try
		{
			broadlist.addAll(o);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public static ArrayList<Message> GetMessageList()
	{
		lock.lock();
		try
		{
			return broadlist;
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public static void AddUser(User user)
	{
		lock.lock();
		try
		{
			userlist.add(user);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public static boolean CheckUser(String username)
	{
		lock.lock();
		try
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
		finally
		{
			lock.unlock();
		}
	}
	
	public static void DeleteUser(User user)
	{
		lock.lock();
		
		int index = 0;
		
		try
		{
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
		finally
		{
			lock.unlock();
		}
	}
	
	// run method
	public void run()
	{
		System.out.println("inside the ttbroadcast");
		// check if the waiting list is empty.
		while(true)
		{
			//System.out.println("waiting for broadcasting");
			while(broadlist.size() > 0)
			{
				System.out.println("inside the broadcast");
				// if it is not empty, start to broadcast message one by one
				ArrayList<Message> broad = new ArrayList<Message>();
				broad = (ArrayList<Message>)broadlist.clone(); // deep copy to local arraylist
				
				// delete messages in the broadlist
				broadlist.clear();
				
				for(int i = 0; i<broad.size(); i++)
				{
					//broadcast each message in this arraylist
					BroadCastMessage((Message)broad.get(i));
					
				}
			}
		}
	}
	
	private void BroadCastMessage(Message message)
	{
		while(clientsThreads.size() > 0)
		{
			for(int i = 1; i< clientsThreads.size(); i++)
			{
				((SingleClientThread)clientsThreads.get(i)).Send(m);
			}
		}
	}
	
}
