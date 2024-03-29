package Server;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import Basic.Message;
import Client.iClient;
import Client.fakeClient;


/*
 * This is the main class of serverside
 */
public class ChatServer{
        
        public static int oldestVersion = 1;
        
	    protected DatagramSocket socket = null;
	    protected BufferedReader in = null;
	    
        public static void main(String[] args)
        {
                
                // start a serversocket for listening client's connection requests
                ServerSocket serverSocket = null;
                UserTable.clean();
                // initialize the network connection
                try
                {
                        System.out.println("Start to listen: ");
                        serverSocket = new ServerSocket(9999);
                        // start an infinite loop
                        while(true)
                        {
                                Socket incoming = serverSocket.accept();
                                //incoming.setSoTimeout(600000);
                                System.out.println("connected with " + incoming.getInetAddress());
                                
                                // spawn a thread to handle the request
                                SingleClientThread singleClientThread = new SingleClientThread(incoming);
                                System.out.println("Start to run single thread for this client");
                                singleClientThread.start();
                        }
                }
                catch(Exception e)
                {
                        System.out.println("The server could not be connected now.");
                }
                finally
                {       
                        try {
                                serverSocket.close();
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                }
        }
        
}