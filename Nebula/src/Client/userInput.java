package Client;


import java.io.*;

public class userInput extends Thread
{
	public void run()
	{
		BufferedReader kin = new BufferedReader(new InputStreamReader(System.in));		
		while(true)
		{
			if (!client.connected)
			{
				return;
			}
			
			try
			{
				String command = kin.readLine();
				if (command.equals("Logout"))
				{
					//client.send(command);
					
					String response = client.read();
					return;
				}
				else
				{
					//client.send(command);
				}
			}
			catch (Exception e)
			{
				
			}
		}
	}
}