package Server;

import java.util.*;

import Basic.Message;

public class BroadcastThread extends Thread {
	
	private static ArrayList<Message> broadlist = new ArrayList<Message>();	// this is the place for storing all the message that would be
	private static ArrayList<User> userlist = new ArrayList<User>();	// store users

	BroadcastThread()
	{
		
	}
	
	public static synchronized void Add(Message o)
	{
		broadlist.add(o);
	}
	
	public static synchronized void AddList(ArrayList o)
	{
		broadlist.addAll(o);
	}
	
	public static synchronized ArrayList GetList()
	{
		return broadlist;
	}
	
	// run method
	public void run()
	{
		// check if the waiting list is empty.
		if(broadlist.size() > 0)
		{
			// if it is not empty, start to broadcast message one by one
			ArrayList<Message> broad = new ArrayList<Message>();
			broad = (ArrayList)broadlist.clone(); // deep copy to local arraylist
			
			for(int i = 0; i<broad.size(); i++)
			{
				//broadcast each message in this arraylist
				BroadCastMessage((Message)broad.get(i));
				
			}
		}
		
		
		// if it is empty, continue to check the length of waiting list
	}
	
	private void BroadCastMessage(Message message)
	{
		
	}
	
}
