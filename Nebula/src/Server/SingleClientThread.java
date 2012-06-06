package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


import Server.DFAState.DFASTATE;

import Basic.Message;

public class SingleClientThread extends Thread {
	
	private Socket incoming;
	private int Userid;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	private BufferedReader in = null;
	private String sharedKey = "";
	private DFAState state = new DFAState();	// store the current state of this single conversation
	private User user = new User();	// store the current user for this thread
	
	SingleClientThread(Socket _incoming) throws IOException
	{
		this.incoming = _incoming;
		try {
			input = new DataInputStream(incoming.getInputStream());
			in = new BufferedReader(new InputStreamReader(input));
			output = new DataOutputStream(incoming.getOutputStream());
			System.out.println("Get I/O Stream Correctly");
			System.out.println("Threads in the list: \n" + ThreadList.ToString());
		} catch (IOException e) {
			// stop this socket
			incoming.close();
		}
		
	}
	
	public int GetUserid()
	{
		return this.Userid;
	}
	
	// run method
	public void run()
	{
		// assign a unique userid for this user
		// only add the new userid into usertable when the username is set up
		this.Userid = UserTable.GetNewUserId();
		
		// after starting a new thread for new client
		System.out.println("Inside of thread");
		/*
		 * grab the fix length of byte stream out of input stream
		 */
		if(!incoming.isClosed())
		{
			while(true)
			{
				byte[] buffer = new byte[285];
				
				if(!this.isAlive())
				{
					this.DeleteAndClose();
					break;
				}
				
				if(incoming.isClosed() || !incoming.isConnected())
				{
					this.DeleteAndClose();
					break;
				}
				try {
					int a = -1;
					
					while(a < 0)
						a = input.read(buffer);
					
				} catch (IOException e1) {
					// if the server could not understand this message type and has exception, send E1: invalid message
					try
					{
						Send(new Message(1, 0xE1, this.Userid, 0, null));
					}
					catch(Exception e)
					{
						// if could not send the error message correctly, just disconnect directly
						this.DeleteAndClose();
						break;
					}
				}
				
				Message message = GetNewMessage(buffer);
	
				if(message.GetMessageType() == 41)
				{
					// send ACK message 42, and broadcast client closed message 15
					Message m = new Message();
					
					try
					{
						m.SetVersion(1);
						m.SetMessageType(42);
						m.SetUserid(this.user.GetUserid());
						m.SetMessageLength(0);
						m.SetData(null);
					}catch (Exception e) {
						// if failed...disconnect directly
						this.DeleteAndClose();
						break;
					}
					
					Send(m);
					
					// then broadcast message, without sending to the disconnecting client
					m = new Message();
					
					try {
						m.SetVersion(1);
						m.SetMessageType(15);
						m.SetUserid(this.user.GetUserid());
						m.SetMessageLength(0);
						m.SetData(null);
					} catch (Exception e) {
						// if failed...disconnect directly
						this.DeleteAndClose();
						break;
					}
					
					// give this message to broadcast thread
					BroadcastThread broadcast = new BroadcastThread(m);
					broadcast.start();
					this.DeleteAndClose();
					System.out.println("Threads in the list: \n" + ThreadList.ToString());
					break;
				}
				else
				{
					try {
						DFA(message);
					} catch (Exception e) {
						// if the message type is not acceptable, send out E1
						try {
							Send(new Message(1, 0xE1, 0, 0, null));
						} catch (Exception e1) {
							break;
						}
					}
				}
			}
		}

	}

	private void DeleteAndClose() {
		// delete the socket from socket list
		SocketList.DeleteSocket(incoming);
		
		// delete this thread from thread list
		ThreadList.DeleteFromThreadList(this);
		
		// delete this user from user list
		UserTable.DeleteUserId(Userid);
		
		try {
			incoming.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// send message to user
	public boolean Send(Message m){
		
		try {
			this.output.write(m.Packet2ByteArray());
			output.flush();
			return true;
		} catch (IOException e) {
			return false;
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
			try {
				Send(new Message(1, 0xE1, this.Userid, e.getMessage().length(), e.getMessage()));
			} catch (Exception e1) {
				this.DeleteAndClose();
			}
		}
		
		return m;
	}
	
	private void DFA(Message m) throws Exception
	{
		//System.out.println("The Thread-"+this.getId()+"'s state is: " + state.GetState());
		//System.out.println("Inside the DFA loop");
		//System.out.println("The current message is: " + m.GetMessageType());
		switch (state.GetState()){
			case DISCONNECTED:
				//the current state is disconnected, the message type should be C_HELLO, or disconnect the connection
				if(m.GetMessageType() == 01)
				{	
					// send S_HELLO to the client
					Message hello = new Message(1, 02, this.Userid, 0, null);

					// then, set the state to S_HELLO_SENT
					if(Send(hello))
					{
						state.SetState(DFASTATE.HELLO_S_SENT);
						System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
					}
					else
					{
						Message close = new Message(1, 42, this.Userid, 0, null);
						Send(close); // do not need to care if this message would be received by client, because the client would disconnect automatically when timeout
						incoming.close();
						break;
					}
				}
				else
				{
					// if the message type is not correct, send out E2
					Send(new Message(1, 0xE2, 0, 0, null));
					// disconnect
					incoming.close();
					break;
				}
				break;
			case HELLO_S_SENT:
				// the current state is HELLO_S_SENT, the message type should be DIGEST_REQUEST message, or disconnect the connection
				if(m.GetMessageType() == 51)
				{
					// send server's digest and public key to client
					// TODO: get digest and public key from security method
					String DataField = null;
					try {
						DataField = XMLBuilder();
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					}
					Message digest = new Message(1, 53, this.Userid, DataField.length(), DataField);
					boolean sent = Send(digest);
					
					if(sent)
					{
						state.SetState(DFASTATE.WAIT_FOR_ACK);
						System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
					}
					else
					{
						// disconnect
						incoming.close();
						break;
					}
				}
				else
				{
					// if the message type is not correct, send out E2
					Send(new Message(1, 0xE2, 0, 0, null));
					
					// disconnect
					incoming.close();
					break;
				}
				break;
			case WAIT_FOR_ACK:
				// the current state is WAIT_FOR_ACK, the server is waiting for the authentication from the client side
				if(m.GetMessageType() == 55)
				{	
					// send the digest request message to client
					boolean sent = Send(new Message(1, 51, 0, 0, null));
					
					if(sent)
					{
						state.SetState(DFASTATE.WAIT_FOR_C_AUTH);
						System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
					}
				}
				else
				{
					// if the message type is not correct, send out E2
					Send(new Message(1, 0xE2, 0, 0, null));
					// disconnect
					incoming.close();
					break;
				}
				break;
			case WAIT_FOR_C_AUTH:
				// the current state is WAIT_FOR_C_AUTH, the server side is expecting to receive client's digest and public key
				if(m.GetMessageType() == 52)
				{
					boolean passed = AnalyzeDigest(m);
					
					if(passed)
					{
						Send(new Message(1, 55, 0, 0, null));
												
						state.SetState(DFASTATE.WAIT_FOR_KEY);
						System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
					}
					else
					{
						// if the authentication did not passed, send NAK message back and disconnect
						Send(new Message(1, 56, 0, 0, null));
						
						// disconnect
						incoming.close();
						break;
					}
				}
				else
				{
					// if the message is not C_AUTH, send out E2
					Send(new Message(1, 0xE2, 0, 0, null));
				}
				break;
			case WAIT_FOR_KEY:
				// the current is WAIT_FOR_KEY, if the message received is the key message from client, go to next state, or send NAK message, and indicate the message desired
				if(m.GetMessageType() == 54)
				{	
					this.sharedKey = m.GetData().toString().trim().replace("\r\n", "\n");
					boolean sent = Send(new Message(1, 57, 0, 0, null));
					
					if(sent)
					{
						// go to next state
						state.SetState(DFASTATE.SECURED);
						System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
					}
					else
					{
						// try five times, then disconnect
						boolean b = false;
						int index = 0;
						if(!b)
						{
							// send KEY_ACK message, and indicate the server wants the username
							b = Send(new Message(1, 57, 0, 0, null));
							// go to next state
							state.SetState(DFASTATE.SECURED);
							System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
							index++;
							if(index > 5)
							{
								// disconnect
								incoming.close();
								break;
							}
						}
					}
				}
				else
				{
					// if the message type is not correct, send out E2
					Send(new Message(1, 0xE2, 0, 0, null));
				}
				break;
			case SECURED:
				// the current state is secured, which means the authentication stage has finished.
				// Then ask the user to provide the username.
				if(m.GetMessageType() == 11)
				{
					
					if(UserTable.CheckUserName(m.GetData().toString()))
					{
						// if there is the same username, request a new username by sending Duplicate User message
						Send(new Message(1, 13, 0, 0, null));
						System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
					}
					else
					{
						// if this username is unique
						UserTable.AddUserId(this.Userid, m.GetData().trim().replace("\r\n", "\n"));
						
						// add the Username connected message to broadcast thread, the broadcast thread will broadcast it to all other connecting clients
						ThreadList.GetBroadThread().AddMessage(new Message(1, 12, this.Userid, m.GetData().length(), m.GetData().trim()));
						
						// then the client and server connected
						state.SetState(DFASTATE.CONNECTED);
						System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());

						// when connected, add this thread and socket into according lists
						SocketList.AddSocketToList(incoming);
						ThreadList.AddThreadToList(this);
						System.out.println("Threads in the list: \n" + ThreadList.ToString());
					}
				}
				else
				{
					// if the message is not username message
					// if the message type is not correct, send out E2
					Send(new Message(1, 0xE2, 0, 0, null));
				}
				break;
			case CONNECTED:
				// the current state is connected. The server is expecting the normal chat message
				if(m.GetMessageType() == 21)
				{
					// add the message into the broadcast array in the broadcast thread
					ThreadList.GetBroadThread().AddMessage(new Message(1, 22, (int)m.GetUserid(), m.GetMessageLength(), m.GetData()));
				}
				else
				{
					// if the message type is not correct, send out E2
					Send(new Message(1, 0xE2, 0, 0, null));
				}
				break;
			default:
				System.exit(0);
				break;
				
		}
				
	}

	private String XMLBuilder() throws ParserConfigurationException {
		String digest = GetDigest();
		String publicKey = GetPublicKey();
		
		String tmp = "<digest>" + digest + "</digest>" + "<publickey>" + publicKey + "</publickey>";
        
		return tmp;//doc.toString();
	}
	

	private String GetPublicKey() {
		// TODO Auto-generated method stub
		return "publickKey";
	}

	private String GetDigest() {
		// TODO Auto-generated method stub
		return "Digest";
	}

	private boolean AnalyzeDigest(Message m) {
		// TODO Auto-generated method stub
		String XMLdata = m.GetData();
		String d = ParseXML(XMLdata, "digest");
		String p = ParseXML(XMLdata, "publickey");
		
		//TODO: digest check
		return true;
	}

	private String ParseXML(String xMLdata, String string) {
		String tmp = "";

		tmp = xMLdata;
	
		int i = tmp.indexOf("<"+ string + ">");
		int end = tmp.indexOf("</" + string + ">");
		String t = tmp.substring(i+string.length()+2, end);
		
		return t;
	}
}
