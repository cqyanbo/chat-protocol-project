package Server;

import java.util.*;

public class BroadcastThread extends Thread {
	
	private static ArrayList broadlist = new ArrayList();	// this is the place for storing all the message that would be
	private static ArrayList<User> userlist = new ArrayList<User>();	// store users

	BroadcastThread()
	{
		
	}
	
	public static synchronized void Add(Object o)
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
			ArrayList broad = new ArrayList();
			broad = (ArrayList)broadlist.clone(); // deep copy to local arraylist
			
			for(int i = 0; i<broad.size(); i++)
			{
				//broadcast each message in this arraylist
			}
		}
		
		
		// if it is empty, continue to check the length of waiting list
	}
}
