package Server;

import java.net.Socket;
import java.util.ArrayList;

public class SocketList {
	// store all of active sockets for broadcast
	private static ArrayList<Socket> socketlist = new ArrayList<Socket>();
	
	public synchronized static void AddSocketToList(Socket _socket)
	{
		synchronized(socketlist)
		{
			socketlist.add(_socket);
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
	
	public static void DeleteSocket(Socket _socket)
	{
		synchronized(socketlist)
		{
			socketlist.remove(_socket);
		}
	}
}
