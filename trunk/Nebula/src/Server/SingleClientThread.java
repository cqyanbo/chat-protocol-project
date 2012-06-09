package Server;

import java.io.*;
import java.net.*;
import java.security.Key;
import java.util.*;

import javax.xml.parsers.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


import Server.DFAState.DFASTATE;

import Basic.Message;
import Basic.Security;

public class SingleClientThread extends Thread {
        
        private Socket incoming;
        private int Userid;
        private String username = "";
        private DataInputStream input = null;
        private DataOutputStream output = null;
        private BufferedReader in = null;
        private byte[] sharedKey = null;
        private DFAState state = new DFAState();        // store the current state of this single conversation
        private static final String ALGO = "AES";
        private Security security = new Security();
        private int version = ChatServer.oldestVersion;

        SingleClientThread(Socket _incoming) throws IOException
        {
                this.incoming = _incoming;
                try {
                        input = new DataInputStream(incoming.getInputStream());
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
                                                Send(new Message(version, 0xE1, this.Userid, 0, null));
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
                                        
                                        try {
                                                Send(new Message(version, 42, this.Userid, 0, null));
                                                //incoming.close();
                                        } catch (Exception e1) {
                                                // TODO Auto-generated catch block
                                                this.interrupt();
                                        }
                                        
                                        if(state.GetState() == DFASTATE.CONNECTED)
                                        {
                                                // only under the connected state, the server need to broadcast message, without sending to the disconnecting client
                                                m = new Message();
                                                
                                                try {
                                                        m.SetVersion(version);
                                                        m.SetMessageType(15);
                                                        m.SetUserid(this.Userid);
                                                        m.SetMessageLength(username.length());
                                                        m.SetData(username);
                                                } catch (Exception e) {
                                                        // if failed...disconnect directly
                                                        this.DeleteAndClose();
                                                        break;
                                                }
                                                
                                                // give this message to broadcast thread
                                                BroadCastMessage(m);
                                                this.DeleteAndClose();
                                                System.out.println("Threads in the list: \n" + ThreadList.ToString());
                                        }
                                        
                                        
                                        System.out.println(this.getName() + " has been " + this.isInterrupted());
                                        
                                        break;
                                }
                                else
                                {
                                        try {
                                                System.out.println("Enter Message: " + message.toString());
                                                DFA(message);
                                                System.out.println("Out Message: " + message.toString());
                                                message = null;
                                        } catch (Exception e) {
                                                // if the message type is not acceptable, send out E1
                                                try {
                                                        Send(new Message(version, 0xE1, 0, 0, null));
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
                                Send(new Message(version, 0xE1, this.Userid, e.getMessage().length(), e.getMessage()));
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
                                        // Version Check:
                                        Message hello = null;
                                        if(m.GetVersion() < ChatServer.oldestVersion)
                                        {
                                                version = m.GetVersion();
                                                // send S_HELLO to the client
                                                hello = new Message(version, 02, this.Userid, 0, null);
                                        }
                                        else
                                        {
                                                version = ChatServer.oldestVersion;
                                                // send S_HELLO to the client
                                                hello = new Message(ChatServer.oldestVersion, 02, this.Userid, 0, null);
                                        }
                                        
                                        
                                        // then, set the state to S_HELLO_SENT
                                        if(Send(hello))
                                        {
                                                state.SetState(DFASTATE.HELLO_S_SENT);
                                                System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
                                        }
                                        else
                                        {
                                                Message close = new Message(version, 42, this.Userid, 0, null);
                                                Send(close); // do not need to care if this message would be received by client, because the client would disconnect automatically when timeout
                                                incoming.close();
                                                break;
                                        }
                                }
                                else
                                {
                                        // if the message type is not correct, send out E2
                                        Send(new Message(version, 0xE2, 0, 0, null));
                                        Send(new Message(version, 42, this.Userid, 0, null));
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
                                        Message digest = new Message(version, 53, this.Userid, DataField.length(), DataField);
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
                                        Send(new Message(version, 0xE2, 0, 0, null));
                                        Send(new Message(version, 42, this.Userid, 0, null));

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
                                        Send(new Message(version, 51, 0, 0, null));
                                        
                                        state.SetState(DFASTATE.WAIT_FOR_C_AUTH);
                                        System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
                                }
                                else
                                {
                                        // if the message type is not correct, send out E2
                                        Send(new Message(version, 0xE2, 0, 0, null));
                                        // disconnect
                                        incoming.close();
                                        break;
                                }
                                break;
                        case WAIT_FOR_C_AUTH:
                                System.out.println("===========In the state: " + state.GetState());
                                System.out.println(m.toString());
                                // the current state is WAIT_FOR_C_AUTH, the server side is expecting to receive client's digest and public key
                                if(m.GetMessageType() == 52)
                                {
                                        boolean passed = AnalyzeDigest(m);
                                        
                                        if(passed)
                                        {
                                                Send(new Message(version, 55, 0, 0, null));
                                                                                                
                                                state.SetState(DFASTATE.WAIT_FOR_KEY);
                                                System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
                                        }
                                        else
                                        {
                                                // if the authentication did not passed, send NAK message back and disconnect
                                                Send(new Message(version, 56, 0, 0, null));
                                                
                                                // disconnect
                                                incoming.close();
                                                break;
                                        }
                                }
                                else
                                {
                                        // if the message is not C_AUTH, send out E2
                                        Send(new Message(version, 0xE2, 0, 0, null));
                                }
                                break;
                        case WAIT_FOR_KEY:
                                System.out.println("===========In the state: " + state.GetState());
                                // the current is WAIT_FOR_KEY, if the message received is the key message from client, go to next state, or send NAK message, and indicate the message desired
                                if(m.GetMessageType() == 54)
                                {       
                                        System.out.println(m.toString());
                                        this.sharedKey = m.GetData().getBytes();
                                        boolean sent = Send(new Message(version, 57, 0, 0, null));
                                        

                                        state.SetState(DFASTATE.SECURED);
                                        System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());

                                }
                                else
                                {
                                        // if the message type is not correct, send out E2
                                        Send(new Message(version, 0xE2, 0, 0, null));
                                }
                                break;
                        case SECURED:
                                System.out.println("===========In the state: " + state.GetState());
                                System.out.println(m.toString());
                                // the current state is secured, which means the authentication stage has finished.
                                // Then ask the user to provide the username.
                                if(m.GetMessageType() == 11)
                                {
                                        
                                        if(UserTable.CheckUserName(m.GetData()))
                                        {
                                                // if there is the same username, request a new username by sending Duplicate User message
                                                Send(new Message(version, 13, 0, 0, null));
                                                System.out.println("The Thread-"+this.getName()+"'s state is: " + state.GetState());
                                        }
                                        else
                                        {
                                                // if this username is unique
                                                UserTable.AddUserId(this.Userid, m.GetData().trim().replace("\r\n", "\n"));
                                                
                                                Send(new Message(version, 12, this.Userid, m.GetData().length(), m.GetData().trim()));
                                                this.username = m.GetData().trim();
                                                // give this message to broadcast thread
                                                BroadCastMessage(new Message(version, 12, this.Userid, m.GetData().length(), m.GetData().trim()));
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
                                        Send(new Message(version, Integer.toString(0xE2, 16), 0, 0, null));
                                }
                                break;
                        case CONNECTED:
                                // the current state is connected. The server is expecting the normal chat message
                                if(m.GetMessageType() == 21)
                                {
                                        // add the message into the broadcast array in the broadcast thread
                                        BroadCastMessage(new Message(version, 22, (int)m.GetUserid(), m.GetMessageLength(), m.GetData()));
                                }
                                else
                                {
                                        // if the message type is not correct, send out E2
                                        Send(new Message(version, 0xE2, 0, 0, null));
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
                security.getpublicRSAkey();
                return "Server_publickKey";
        }

        private String GetDigest() {

                InetAddress addr = null;
                try {
                        addr = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

            // Get IP Address
            byte[] ipAddr = addr.getAddress();

                // using client's ip address to build the user digest
                byte[] digest = security.GenerateDigest(new String(ipAddr));
                return new String(digest);
        }

        private boolean AnalyzeDigest(Message m) {
                // TODO Auto-generated method stub
                //String XMLdata = m.GetData();
                //String d = ParseXML(XMLdata, "digest");
                //String p = ParseXML(XMLdata, "publickey");
                
                //TODO: digest check
                return true;
        }
        
        private void BroadCastMessage(Message message)
        {
                ArrayList<SingleClientThread> threads = (ArrayList<SingleClientThread>) ThreadList.GetThreadsList().clone();
                
                if(threads.size() > 0)
                {
                        for(int i = 0; i< threads.size(); i++)
                        {
                                if(threads.get(i).incoming.isConnected())
                                {
                                        // if the thread is alive and is not the message sender
                                        System.out.println("Broad message to: " + threads.get(i).getName());
                                        threads.get(i).Send(message);
                                        System.out.println("Broadcasted");
                                }
                                else if(threads.get(i).incoming.isClosed())
                                {
                                        ThreadList.DeleteFromThreadList(threads.get(i));
                                        try {
                                                Send(new Message(version, 15, threads.get(i).Userid, threads.get(i).username.length(), threads.get(i).username));
                                        } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                        }
                                }
                                
                        }
                }
                
                System.out.println("Has published all messages in the broadcast list");
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