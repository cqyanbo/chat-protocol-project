package Server;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Basic.Message;

public class BroadcastThread extends Thread {
	
	private static ArrayList<Message> broadlist = new ArrayList<Message>();	// this is the place for storing all the message that would be
	private Message m;
	private static Lock lock = new ReentrantLock();

	BroadcastThread(Message _m)
	{
		System.out.println("inside the broadcast");
		this.m = _m;
		AddMessage(m);
	}
	
	BroadcastThread()
	{
		broadlist.clear();
	}
	
	public void AddMessage(Message o)
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
	
	public void AddMessageList(ArrayList<Message> o)
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
	
	public ArrayList<Message> GetMessageList()
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
		ArrayList<SingleClientThread> threads = (ArrayList<SingleClientThread>) ThreadList.GetThreadsList().clone();
		while(threads.size() > 0)
		{
			for(int i = 1; i< threads.size(); i++)
			{
				// if the thread is alive and is not the message sender
				if(threads.get(i).isAlive() && threads.get(i).GetUserid() == message.GetUserid())
				{
					threads.get(i).Send(message);
				}
			}
		}
		
		System.out.println("Has published all messages in the broadcast list");
	}
	
}
