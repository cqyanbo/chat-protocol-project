package Server;

import java.util.ArrayList;

public class ThreadList {
	// arraylist to store all of the threads from client
	private static ArrayList<Thread> threadslist = new ArrayList<Thread>();
	
	private static BroadcastThread broadthread = new BroadcastThread();
	
	public static void AddBroadThread(BroadcastThread _broadcastThread)
	{
		synchronized(broadthread)
		{
			broadthread = _broadcastThread;
		}
	}
	
	public static BroadcastThread GetBroadThread()
	{
		synchronized(broadthread)
		{
			return broadthread;
		}
	}
	
	public static void AddThreadToList(SingleClientThread _thread)
	{
		synchronized(threadslist)
		{
			threadslist.add(_thread);
		}
	}
	
	public static void DeleteFromThreadList(SingleClientThread _thread)
	{
		synchronized(threadslist)
		{
			threadslist.remove(_thread);
		}
	}
	
	
	
	public static ArrayList<Thread> GetThreadsList()
	{
		synchronized(threadslist)
		{
			@SuppressWarnings("unchecked")
			ArrayList<Thread> tmp = (ArrayList<Thread>) threadslist.clone();
			return tmp;
		}
	}
	
	public static Thread GetThread(Thread _thread)
	{
		synchronized(threadslist)
		{
			if(threadslist.contains(_thread))
				return (Thread)threadslist.get(threadslist.indexOf(_thread));
			else
				return null;
		}
		
	}

}
