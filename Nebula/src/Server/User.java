package Server;

import java.net.InetAddress;
import java.util.*;

public class User {
	
	private String username = "";
	private InetAddress ipAddress = null;
	private String port = "";
	
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
	
	public void SetPortNumber(String _p)
	{
		this.port = _p;
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
	
	public String GetPortNumber()
	{
		return this.port;
	}
}
