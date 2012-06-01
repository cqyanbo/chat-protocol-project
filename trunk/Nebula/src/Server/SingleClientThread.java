package Server;

import java.io.*;
import java.net.*;

import Server.DFAState.DFASTATE;

import Basic.Message;

public class SingleClientThread extends Thread {
	
	private Socket incoming;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	private DFAState state = new DFAState();	// store the current state of this single conversation
	
	private int tmp = 0; // index of times of sending digest to client
	
	SingleClientThread(Socket _incoming)
	{
		this.incoming = _incoming;
	}
	
	// run method
	public void run()
	{
		// after starting a new thread for new client
		byte[] buffer = new byte[300];
		// the first step would be grab one message from inputstream
		try {
			input = new DataInputStream(incoming.getInputStream());
			output = new DataOutputStream(incoming.getOutputStream());
		} catch (IOException e) {
			// TODO: send back an error message
			e.printStackTrace();
		}
		
		/*
		 * grab the fix length of byte stream out of input stream
		 */
		while(true)
		{
			Message message = GetNewMessage(input, buffer);
			if(message.GetMessageType() != "CLOSED")
				DFA(message);
			else
				break;
		}

	}
	
	private Message GetNewMessage(InputStream input, byte[] buffer)
	{
		byte[] b = buffer;
		
		try {
			int tmp = input.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return DecryptMessage(buffer);
	}
	
	private Message DecryptMessage(byte[] buffer)
	{
		Message m = new Message();
		
		return m;
	}
	
	private boolean SendMessage(String MessageType)
	{
		return true;
	}
	
	private void DFA(Message m)
	{
		switch (state.GetState()){
			case DISCONNECTED:
				//the current state is disconnected, the message type should be C_HELLO, or disconnect the connection
				if(m.GetMessageType()=="C_HELLO")
				{
					// send S_HELLO to the client
					boolean sent = SendMessage("S_HELLO");
					
					// then, set the state to S_HELLO_SENT
					if(sent)
					{
						state.SetState(DFASTATE.HELLO_S_SENT);
					}
					else
					{
						// disconnect
					}
				}
				else
				{
					// disconnect
				}
				break;
			case HELLO_S_SENT:
				// the current state is HELLO_S_SENT, the message type should be DIGEST_REQUEST message, or disconnect the connection
				if(m.GetMessageType() == "DIGEST_REQUEST")
				{
					// send server's digest and public key to client
					boolean sent = SendMessage("S_AUTH");
					
					if(sent)
					{
						state.SetState(DFASTATE.WAIT_FOR_ACK);
					}
					else
					{
						// disconnect
					}
				}
				else
				{
					// disconnect
				}
				break;
			case WAIT_FOR_ACK:
				// the current state is WAIT_FOR_ACK, the server is waiting for the authentication from the client side
				if(m.GetMessageType() == "Auth_ACK")
				{
					// send the digest request message to client
					boolean sent = SendMessage("DIGEST_REQUEST");
					
					if(sent)
					{
						state.SetState(DFASTATE.WAIT_FOR_C_AUTH);
					}
					else
					{
						// disconnect
					}
				}
				else
				{
					// disconnect
				}
				break;
			case WAIT_FOR_C_AUTH:
				// the current state is WAIT_FOR_C_AUTH
				if(m.GetMessageType() == "C_AUTH")
				{
					tmp = 0; // if get the correct message, set this index to 0
					
					boolean passed = AnalyzeDigest(m);
					
					if(passed)
					{
						boolean sent = SendMessage("AUTH_ACK");
						
						int index = 0;
						
						// if sending message failed, resend five times. If still could not sent, disconnect
						while(!sent)
						{
							sent = SendMessage("AUTH_ACK");
							index++;
							
							if(index > 5)
							{
								// disconnect
							}
						}
						
						state.SetState(DFASTATE.WAIT_FOR_KEY);
					}
					else
					{
						// if the authentication did not passed, send NAK message back and disconnect
						boolean sent = SendMessage("AUTH_NAK");
						
						// disconnect
					}
				}
				else
				{
					// if the message is not C_AUTH, resend server digest and public key
					boolean sent = SendMessage("DIGEST_REQUEST");
					tmp++;
					
					// if send more than 5 times
					if(tmp > 5)
					{
						// disconnect
					}
				}
				break;
			case WAIT_FOR_KEY:
				// the current is WAIT_FOR_KEY, if the message received is the key message from client, go to next state, or send NAK message, and indicate the message desired
				if(m.GetMessageType() == "SHARED_KEY")
				{
					
					boolean sent = SendMessage("KEY_ACK");
					
					if(sent)
					{
						tmp = 0;
						// go to next state
						state.SetState(DFASTATE.SECURED);
					}
					else
					{
						// try five times, then disconnect
						boolean b = false;
						int index = 0;
						while(!b)
						{
							// send KEY_ACK message, and indicate the server wants the username
							b = SendMessage("KEY_ACK");
							index++;
							if(index > 5)
							{
								// disconnect
							}
						}
					}
				}
				else
				{
					// if the message type is not valid, send the NAK message with indicating the desired message
					boolean sent = SendMessage("NAK");
					tmp++;
					if(tmp > 10)
					{
						// disconnect
					}
				}
				break;
			case SECURED:
				// the current state is secured, which means the authentication stage has finished.
				// Then ask the user to provide the username.
				if(m.GetMessageType() == "USERNAME")
				{
					if(BroadcastThread.CheckUser(m.GetData().toString()))
					{
						// if there is the same username, request a new username
						boolean sent = SendMessage("NAK_username");
					}
					else
					{
						User user = new User();
						user.SetIpAddress(incoming.getInetAddress());
						user.SetPortNumber(incoming.getPort());
						user.SetUsername(m.GetData().toString());
						
						BroadcastThread.AddUser(user);
						
						// then the client and server connected
						state.SetState(DFASTATE.CONNECTED);
					}
				}
				else
				{
					// if the message is not username message
					// ask for username again
					// try five times, then disconnect
					boolean b = false;
					int index = 0;
					while(!b)
					{
						// send KEY_ACK message, and indicate the server wants the username
						b = SendMessage("KEY_ACK");
						index++;
						if(index > 5)
						{
							// disconnect
						}
					}
				}
				break;
			case CONNECTED:
				// the current state is connected. The server is expecting the normal chat message
				if(m.GetMessageType() == "Normal")
				{
					BroadcastThread.AddMessage(m);
				}
				else if(m.GetMessageType() == "REQUEST_CLOSED")
				{
					// disconnect
				}
				break;
			default:
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

	private boolean AnalyzeDigest(Message m) {
		// TODO Auto-generated method stub
		return false;
	}

}
