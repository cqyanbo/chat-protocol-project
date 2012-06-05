package Server;

import java.net.InetAddress;
import java.util.*;

public class User {
	
	private String username = "";
	private InetAddress ipAddress = null;
	private int port;
	private int Userid;
	
	/*
	 * SET
	 */
	public void SetUserid()
	{
		//build a random integer
	}
	
	public void SetUsername(String _username)
	{
		this.username = _username;
	}
	
	public void SetIpAddress(InetAddress _ip)
	{
		this.ipAddress = _ip;
	}
	
	public void SetPortNumber(int i)
	{
		this.port = i;
	}
	
	/*
	 * GET
	 */
	public int GetUserid()
	{
		return this.Userid;
	}
	
	public String GetUsername()
	{
		return this.username;
	}
	
	public InetAddress GetIpAddress()
	{
		return this.ipAddress;
	}
	
	public int GetPortNumber()
	{
		return this.port;
	}
}
