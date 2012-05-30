package Server;

import java.net.*;

public class SingleClientThread extends Thread {
	
	private Socket incoming;
	
	SingleClientThread(Socket _incoming)
	{
		this.incoming = _incoming;
	}
	
	// run method
	public void run()
	{
		// after starting a new thread for new client
		
		// the first step would be grab one message from inputstream
		
		// the second step would be decrypt the packet
		
		// the third step is to parse the header part
		
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

}
