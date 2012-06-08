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
			
			client.message = GetNewMessage(buffer);
			
			if(client.connected && (client.message.GetMessageType()==12|client.message.GetMessageType()==15|client.message.GetMessageType()==22))
			{
				if(client.message.GetMessageType()==12)
				{
					client.mainText.setText(client.mainText.getText() + "\n" + " new connection: " + client.message.GetUserid());
				}
				else if(client.message.GetMessageType()==15)
				{
					client.mainText.setText(client.mainText.getText() + "\n" + " user: " + client.message.GetUserid() + " has left");

				}
				else
				{
					client.mainText.setText(client.mainText.getText() + "\n" + ": " + client.message.GetUserid() + " " + client.message.GetData().replace("\r\n", "\n"));
				}
			}
			System.out.println("Received: " + client.message.toString());
		}
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