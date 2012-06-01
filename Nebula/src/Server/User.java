package Server;

import java.net.InetAddress;
import java.util.*;

public class User {
	
	private String username = "";
	private InetAddress ipAddress = null;
	private int port;
	
	/*
	 * SET
	 */
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
