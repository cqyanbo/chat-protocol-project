package Server;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Basic.Message;

public class BroadcastThread extends Thread {
	
	private ArrayList<Message> broadlist = new ArrayList<Message>();	// this is the place for storing all the message that would be
	private Message m;

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
	
	public synchronized void AddMessage(Message o)
	{
		System.out.println(this.getName() + " is adding broadcast message");

		broadlist.add(o);

		
		System.out.print("There are: ");
		
		for(int i = 0; i < broadlist.size(); i++)
		{
			System.out.println(broadlist.get(i).GetData());
		}
	}
	
	public synchronized void AddMessageList(ArrayList<Message> o)
	{

		broadlist.addAll(o);

	}
	
	public synchronized ArrayList<Message> GetMessageList()
	{
		return broadlist;
	}
	
	// run method
	public void run()
	{
		// check if the waiting list is empty.
		while(true)
		{
			//System.out.println("waiting for broadcasting");
			if(broadlist.size() > 0)
			{
				System.out.println("There are " + broadlist.size() + " messages needed to be broadcasted.");
				System.out.println("inside the broadcast");
				// if it is not empty, start to broadcast message one by one
				ArrayList<Message> broad = new ArrayList<Message>();
				broad = (ArrayList<Message>)broadlist.clone(); // deep copy to local arraylist
				
				// delete messages in the broadlist
				broadlist.clear();
				System.out.println("There are " + broad.size() + " messages in broad");
				System.out.println("There are " + broadlist.size() + " messages in broadlist");

				for(int i = 0; i<broad.size(); i++)
				{
					//broadcast each message in this arraylist
					System.out.println("broadcasting message: \n" + broad.get(i).toString());
					BroadCastMessage((Message)broad.get(i));
					
				}
			}
		}
	}
	
	private void BroadCastMessage(Message message)
	{
		ArrayList<SingleClientThread> threads = (ArrayList<SingleClientThread>) ThreadList.GetThreadsList().clone();
		
		System.out.print("There are: ");
		
		for(int i = 0; i < broadlist.size(); i++)
		{
			System.out.println(broadlist.get(i).GetData());
		}
		while(threads.size() > 0)
		{
			for(int i = 1; i< threads.size(); i++)
			{
				// if the thread is alive and is not the message sender
				System.out.println("Broad message to: " + threads.get(i).getName());
				threads.get(i).Send(message);
				System.out.println("Broadcasted");
				
			}
		}
		
		System.out.println("Has published all messages in the broadcast list");
	}
	
}
