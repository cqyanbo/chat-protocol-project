package Server;

import java.util.Hashtable;

public class UserTable {
	
	// table for storing userid and username pair
	private static Hashtable<Integer, String> userTable = new Hashtable<Integer, String>();
	
	
	public synchronized static void clean()
	{
		userTable.clear();
	}
	
	public synchronized static int GetNewUserId()
	{

			for(int i = 0; i < userTable.size(); i++)
			{
				if(!userTable.containsKey(i+1))
					return i+1;
			}
			
			return userTable.size();
		
	}
	
	public synchronized static void DeleteUserId(int Id)
	{

			userTable.remove(Id);
		
	}
	
	public synchronized static void AddUserId(int userid, String username)
	{

			userTable.put(userid, username.trim().toLowerCase());
		
	}
	
	public synchronized static boolean CheckUserName(String username)
	{
		if(username.trim() == "" || username.trim() == null)
		{
			return true;
		}
			System.out.println("CheckUsername:" + userTable.toString());
			System.out.println("checking: " + username.toLowerCase() + ": " + userTable.contains(username.toLowerCase()));
			return userTable.containsValue(username.trim());
			
	}

}
