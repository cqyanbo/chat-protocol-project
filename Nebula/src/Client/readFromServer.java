package Client;

import java.io.*;
import java.util.*;
import javax.swing.*;
import Basic.Message;

public class readFromServer extends Thread
{
	client c;
	//constructor
	readFromServer(client cc)
	{
		c = cc;
	}
	
	public void run()
	{
		while(true)
		{
			byte[] buffer = new byte[285];

			try
			{
				int a = -1;
				while(a < 0)
					a = c.in.read(buffer);
			}
			catch(IOException e)
			{
				// if read the wrong message, do not do anything
			}
			
			c.AddMessage(GetNewMessage(buffer));
			String m = c.message.GetData();
			
			if(c.os == "Linux")
  		  		m = "\n" + m.replace("\r\n","\n");
  		  	if(c.os == "Windows")
  		  		m = "\r\n" + m.replace("\r\n","\r\n");
  		  	else
  		  		m = "\n" + m.replace("\r\n","\n");
			
			if(c.connected && (c.message.GetMessageType()==12|c.message.GetMessageType()==15|c.message.GetMessageType()==22))
			{
				if(c.message.GetMessageType()==12)
				{
					c.mainText.setText(c.mainText.getText() + m + " joined");
				}
				else if(c.message.GetMessageType()==15)
				{
					c.mainText.setText(c.mainText.getText() + m + " has left");

				}
				else
				{
					if(c.message.GetUserid() == c.GetUserId())
					{
						c.mainText.setText(c.mainText.getText() + " " + "me: " + parseMessage(m));

					}
					else
					{
						c.mainText.setText(c.mainText.getText() + " " + paserUsername(m) + ": " + parseMessage(m));
					}
				}
			}
			System.out.println("Received: " + c.message.toString());
		}
	}
	
	private String parseMessage(String replace) {
		
		String name = replace;
		int end = replace.indexOf(">");
		
		name = name.substring(end+1, replace.length());
		return name;
	}

	private String paserUsername(String getData) {
		String name = getData;
		int from = getData.indexOf("<");
		int end = getData.indexOf(">");
		
		name = name.substring(from+1, end);
		
		return name;
	}

	private Message GetNewMessage(byte[] buffer)
	{
		Message m = new Message();
		String tt = new String(buffer);
		char[] test = (tt).toCharArray();
		
		try
		{
			m = Message.ByteArrayToMessage(test);
			// read each part of packet from the buffer
		}
		catch(Exception e)
		{
			// if could not parse the message correctly, send E1 message with the exception message
		}
		
		return m;
	}
	
}