package Client;


import java.io.*;

//Sinartisi pou xeirizete to I/O apo ti konsola me to xristi. den xreiazete me to gui
public class userInput extends Thread
{
	public void run()
	{
		BufferedReader kin = new BufferedReader(new InputStreamReader(System.in));		
		while(true)
		{
			//otan ginoume logout allazi (allaze prin to gui) 
			//i metavliti logout gia na termatizoun ta alla threads
			if (client.logout)
			{
				return;
			}
			
			//Logout palia ekdosi
			//isos buggy
			try
			{
				String command = kin.readLine();
				if (command.equals("Logout"))
				{
					//client.send(command);
					
					String response = client.read();
					client.logout = true;
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