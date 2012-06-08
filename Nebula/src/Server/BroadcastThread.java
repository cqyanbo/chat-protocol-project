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
		System.out.println("Broadcasting: " + m.toString());
		BroadCastMessage(m);
	}
	
	private void BroadCastMessage(Message message)
	{
		ArrayList<SingleClientThread> threads = (ArrayList<SingleClientThread>) ThreadList.GetThreadsList().clone();
		
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
