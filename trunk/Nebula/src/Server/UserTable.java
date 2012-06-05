package Server;

import java.util.Hashtable;

public class UserTable {
	
	// table for storing userid and username pair
	private static Hashtable userTable = new Hashtable();
	
	public static int GetNewUserId()
	{
		synchronized(userTable)
		{
			return userTable.size();
		}
	}
	
	public static void AddUserId(int userid, String username)
	{
		synchronized(userTable)
		{
			userTable.put(userid, username);
		}
	}
	
	public static boolean CheckUserName(String username)
	{
		synchronized(userTable)
		{
			if(userTable.contains(username.replace("\r\n", "\n")))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}

}
