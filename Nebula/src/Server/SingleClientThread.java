package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import Server.DFAState.DFASTATE;

import Basic.Message;

public class SingleClientThread extends Thread {
	
	private Timer timer = new Timer();
	private Socket incoming;
	private int Userid;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	private BufferedReader in = null;
	private String sharedKey = "";
	private DFAState state = new DFAState();	// store the current state of this single conversation
	private User user = new User();	// store the current user for this thread
	
	
	private int tmp = 0; // index of times of sending digest to client
	
	SingleClientThread(Socket _incoming)
	{
		this.incoming = _incoming;
		try {
			input = new DataInputStream(incoming.getInputStream());
			in = new BufferedReader(new InputStreamReader(input));
			output = new DataOutputStream(incoming.getOutputStream());
			System.out.println("Get I/O Stream Correctly");
		} catch (IOException e) {
			// stop this thread
			this.stop();
		}
		
	}
	
	public int GetUserid()
	{
		return this.Userid;
	}
	
	// run method
	@SuppressWarnings("deprecation")
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
					break;
				}
				
				if(incoming.isClosed() || !incoming.isConnected())
				{
					break;
				}
				try {
					int a = -1;
					
					//while(a < 0)
					a = input.read(buffer);
					
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
						// TODO: handle exception
					}
					
					Send(m);
					ThreadList.DeleteFromThreadList(this);
					ThreadList.GetBroadThread().DeleteUser(user);
					SocketList.DeleteSocket(incoming);
					
					// then broadcast message, without sending to the disconnecting client
					m = new Message();
					
					try {
						m.SetVersion(1);
						m.SetMessageType(15);
						m.SetUserid(this.user.GetUserid());
						m.SetMessageLength(0);
						m.SetData(null);
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					// give this message to broadcast thread
					BroadcastThread broadcast = new BroadcastThread(m);
					broadcast.start();
					try {
						incoming.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					// delete this user from user table
					UserTable.DeleteUserId(Userid);
					
					// delete this thread from thread list
					ThreadList.DeleteFromThreadList(this);
					
					// delete this socket from socket list
					SocketList.DeleteSocket(incoming);
					
					break;
				}
				else
				{
					try {
						DFA(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
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
		
		m = Message.ByteArrayToMessage(test);
		// read each part of packet from the buffer
		
		return m;
	}
	
	@SuppressWarnings("deprecation")
	private void DFA(Message m) throws IOException
	{
		System.out.println("The state is: " + state.GetState());
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
						System.out.println("The state is: " + state.GetState());
					}
					else
					{
						Message close = new Message(1, 42, this.Userid, 0, null);
						Send(close); // do not need to care if this message would be received by client, because the client would disconnect automatically when timeout
						incoming.close();
						this.destroy();
					}
				}
				else
				{
					// disconnect
					incoming.close();
					this.equals(null); // stop this thread;
				}
				break;
			case HELLO_S_SENT:
				// the current state is HELLO_S_SENT, the message type should be DIGEST_REQUEST message, or disconnect the connection
				if(m.GetMessageType() == 51)
				{
					// send server's digest and public key to client
					// TODO: get digest and public key from security method
					String Digest = "digest";
					String PublicKey = "publick";
					String DataField = XMLBuilder(Digest, PublicKey);
					Message digest = new Message(1, 53, this.Userid, DataField.length(), DataField);
					boolean sent = Send(digest);
					
					if(sent)
					{
						state.SetState(DFASTATE.WAIT_FOR_ACK);
						System.out.println("The state is: " + state.GetState());
					}
					else
					{
						// disconnect
						incoming.close();
					}
				}
				else
				{
					// disconnect
					incoming.close();
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
						System.out.println("The state is: " + state.GetState());
					}
					else
					{
						// if did not send successfully, 
					}
				}
				else
				{
					// disconnect
					incoming.close();
				}
				break;
			case WAIT_FOR_C_AUTH:
				// the current state is WAIT_FOR_C_AUTH
				if(m.GetMessageType() == 52)
				{
					boolean passed = AnalyzeDigest(m);
					
					if(passed)
					{
						boolean sent = Send(new Message(1, 55, 0, 0, null));
												
						state.SetState(DFASTATE.WAIT_FOR_KEY);
						System.out.println("The state is: " + state.GetState());
					}
					else
					{
						// if the authentication did not passed, send NAK message back and disconnect
						Send(new Message(1, 56, 0, 0, null));
						
						// disconnect
						incoming.close();
					}
				}
				else
				{
					// if the message is not C_AUTH, resend server digest and public key
					boolean sent = Send(new Message(1, 51, 0, 0, null));
					tmp++;
					
					// if send more than 5 times
					if(tmp >= 5)
					{
						// disconnect
						incoming.close();
						this.destroy();
					}
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
						System.out.println("The state is: " + state.GetState());
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
							System.out.println("The state is: " + state.GetState());
							index++;
							if(index > 5)
							{
								// disconnect
								incoming.close();
								this.destroy();
							}
						}
					}
				}
				else
				{
					// if the message type is not valid, send the Key Request message indicating the desired message and go back the former state
					boolean sent = Send(new Message(1, 51, 0, 0, null));
					state.SetState(DFASTATE.WAIT_FOR_C_AUTH);
					System.out.println("The state is: " + state.GetState());
				}
				break;
			case SECURED:
				// the current state is secured, which means the authentication stage has finished.
				// Then ask the user to provide the username.
				if(m.GetMessageType() == 57)
				{
					
					if(UserTable.CheckUserName(m.GetData().toString()))
					{
						// if there is the same username, request a new username
						boolean sent = Send(new Message(1, 57, 0, 0, null));
						System.out.println("The state is: " + state.GetState());
					}
					else
					{
						// if this username is unique
						UserTable.AddUserId(this.Userid, m.GetData().trim().replace("\r\n", "\n"));
						
						// then the client and server connected
						state.SetState(DFASTATE.CONNECTED);
						System.out.println("The state is: " + state.GetState());

						// when connected, add this thread and socket into according lists
						SocketList.AddSocketToList(incoming);
						ThreadList.AddThreadToList(this);
					}
				}
				else
				{
					// if the message is not username message
					// ask for username again
					// try five times, then disconnect
				}
				break;
			case CONNECTED:
				// the current state is connected. The server is expecting the normal chat message
				if(m.GetMessageType() == 21)
				{
					// if get the message, stop the timer
					timer.cancel();
					
					// add the message into the broadcast array in the broadcast thread
					ThreadList.GetBroadThread().AddMessage(new Message(1, 22, (int)m.GetUserid(), m.GetMessageLength(), m.GetData()));
				}
				else
				{
					// if the message is not the normal chat message, just ignore
				}
				break;
			default:
				System.exit(0);
				break;
				
		}
				
		// at the very beginning, the state of DFA is disconnected
		
		// 1. has to be C_Hello message, if not C_Hello, close the connection
		
		// 2. build S_Hello, and send back to client side
		
		// 3. waiting for next message from client, if timeout, close the connection
		//    if get the Auth_request message, send digest and own public key; if not the Auth_request, wait until the timeout, then close the connection
		
		// 4. server wait until get the Auth_passed message from client, sends back ACK message indicating requiring client's authentication information message
		//    setting up a time for waiting, if timeout, close the connection
		
		// 5. if get client authentication message, analyze it and sends back auth_passed message, if information is correct, and indicating requiring a shared key.
		//    if get other kinds of messages, keep sending ACK message with requiring client's authentication information message, until get the correct message, or attend to the limitation of attempt number.
		
		// 6. if get the shared key, store it and send back ACK with username_require information,
		//   if not the shared key message, keep sending ACK with username_require information, or close connection when arrive the attempt number.
		
		// 7. after receiving username message from client, check if the username has been registered, if yes, send NAK with username_require information,
		//   if no, send ACK with normal message requiring.
		
		// 8. put a message with new user indication into the first place of waiting list, and add new user information into user list for current group
		
		// 9. waiting for normal message from client, if get correct one, send into waiting list, if not correct, send NAK message indicating a new message require
		//    if client did not send any messages in amount of time, close the connection, and put a user leaving message into the first place of waiting list.
		//    Then, delete the user information from user list of current group
		
		// 10. when get a disconnect message, 
	}

	private String XMLBuilder(String digest, String publicKey) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean AnalyzeDigest(Message m) {
		// TODO Auto-generated method stub
		return true;
	}

}
