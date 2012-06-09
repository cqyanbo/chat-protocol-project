package Client;

import java.io.*;
import java.net.*;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;   

import Basic.Message;
import Basic.Security;
import Client.ClientState.CLIENTSTATE;

import java.awt.event.*;  
import java.awt.*;   


class client extends JFrame
{
	static boolean connected; 
	static boolean logout; 
	static Socket cSocket; 
	static DataOutputStream out; 
	static DataInputStream in;
	static userInput uinput; 
	static readFromServer sinput; 
	static Message message = null;
	
	private int Version = 1;
	private String username = "";
	private int userid;
	private Security security = new Security();
	private final String ALGO = "AES";
	private final byte[] keyValue = 
	    new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't',
	'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };
	
	private ClientState clientstate = new ClientState();

 	public int GetUserId()
 	{
 		return userid;
 	}
 	
	void run()
	{
		
		setTitle("Simple Java Chat - Disconnected");
		connected = false; 
		
		//Gui Stuff

		jScrollPane1 = new javax.swing.JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inputText = new javax.swing.JTextArea();
        sendButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainText = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        jMenuBar1 = new javax.swing.JMenuBar();
    
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
		jMenuItem2 = new javax.swing.JMenuItem();
		jMenuItem3 = new javax.swing.JMenuItem();
        
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					Send(new Message(Version, 41, userid, 0, null));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		
        inputText.setColumns(20);
        inputText.setRows(5);
        jScrollPane1.setViewportView(inputText);
		
        sendButton.setText("Send");
        
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });
        
        inputText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputTextKeyReleased(evt);
            }
        });
        
        inputText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputTextKeyReleased(evt);
            }
        });
        
        mainText.setColumns(20);
        mainText.setEditable(false);
        mainText.setRows(5);
        mainText.setLineWrap(true);
        
        inputText.setLineWrap(true);
        jScrollPane2.setViewportView(mainText);

		jScrollPane3.setViewportView(nickList);
		//Ta menus
        jMenu1.setText("Commands");
		jMenu2.setText("Help");
        jMenuItem1.setText("Connect");
        jMenuItem2.setText("Disconnect");
        jMenuItem3.setText("Usage");
		jMenuItem2.setEnabled(false);
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
					jMenuItem1ActionPerformed(evt);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed3(evt);
            }
        });
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
					jMenuItem1ActionPerformed2(evt);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        jMenu1.add(jMenuItem1);
		jMenu1.add(jMenuItem2);
		jMenu2.add(jMenuItem3);
		
		jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

		make();
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		mainText.setFont(new Font("Serif", Font.ITALIC, 16));
	}
	//pure netbeans
	void make()
	{
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sendButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        pack();
        
        
        setVisible(true);
	}
	
	static boolean enter;
	
    private void inputTextKeyReleased(java.awt.event.KeyEvent evt) 
    {

        if(evt.getKeyCode() == 10)
        {
        	if(inputText.getText().length() >= 220)
        	{
        		setTitle("could not enter more than 223 characters!");
        	}
        	else
        	{
	           	if (enter)
	    		{
	    			try {
						sendInput(inputText.getText());
						setTitle("Connected");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			enter = false;
	    		}
	    		else
	    		{
	    			
	    			enter = true;
	    		}
        	}
        }
    }
    private void jMenuItem1ActionPerformed2(java.awt.event.ActionEvent evt) throws Exception 
    {
    	// send out message type 41 for requiring disconnect
    	Send(new Message(Version, 41, 0, 0, null));
    	this.connected = false;
		while(true)
		{
			if(message.GetMessageType() == 42)
			{
				//cSocket.close();
				System.exit(0);
			}
			else if(!this.isActive())
			{
				// set a timer
				System.exit(0);
			}
		}
    }
    
    //Help stuff
    //Actions - Disconnect
    private void jMenuItem1ActionPerformed3(java.awt.event.ActionEvent evt) 
    {
    	String s;
    	s = "GROUP 7";
    	JOptionPane.showMessageDialog( this, s,"About", JOptionPane.INFORMATION_MESSAGE );
    }
    
    static String server;

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) throws Exception 
    {
    	
    	setTitle("Connecting ...");
    	logout = false;
		
		sinput = new readFromServer(this);
		

		uinput = new userInput();
		
		cSocket = null;
		out = null;
		in = null;

		boolean error;
		error = false;
				
		server = JOptionPane.showInputDialog("Connect to: ", "localhost");
		try 
		{
			//connect to server
			cSocket = new Socket(server,9999);
			
			//get the I/O stream from socket
			out = new DataOutputStream(cSocket.getOutputStream());
			in = new DataInputStream(cSocket.getInputStream());

			
			//start two threads. One for getting user input, another for getting messages sent from server side
			uinput.start();
			sinput.start();
			
			// once connect with server, switch DISCONNECTED state to HELLO_C_SENT state
			Send(new Message(Version, 01, 0, 0, null));
			clientstate.SetState(CLIENTSTATE.HELLO_C_SENT);
		}
		catch(UnknownHostException e)
		{
			//Se periptosi Error epikoinonias me to server (UnknownHost)
			JOptionPane.showMessageDialog( this, "Den ine efikti i epikoinonia me to server","ERROR", JOptionPane.ERROR_MESSAGE );
			System.out.println("Host Error" + e);
			setTitle("Simple Java Chat - Cannot Connect Please Try another server");
			error = true;
		}
		catch (IOException e)
		{
			System.out.println("IOException");
		}
		
		if (!error)
		{
			while(message == null)
			{
				System.out.println("waiting for message1");
			}
			
			if(message.GetMessageType() == 42)
			{
				cSocket.close();
			}
			else
			{
				System.out.println("Start CLIENT DFA");
				DFA();

				if(this.connected)
				{
					this.setTitle("Connected");
				}
			}
		}
    }

	private void DFA() throws Exception {
		
		while(message != null && clientstate.GetState() != CLIENTSTATE.CONNECTED)
		{				
			System.out.println("Enter Message: " + message.toString());

			switch(clientstate.GetState())
			{
			case HELLO_C_SENT:
				if(message.GetMessageType() == 02)
				{
					// Version Check
					if(this.Version > message.GetVersion())
						this.Version = message.GetVersion();
					// if get S_HELLO from server, send digest request
					Send(new Message(this.Version, 51, 0, 0, null));
					clientstate.SetState(CLIENTSTATE.WAIT_FOR_S_AUTH);
					System.out.println("Get " + message.GetMessageType() + " go to " + CLIENTSTATE.WAIT_FOR_S_AUTH);
				}
				break;
			case WAIT_FOR_S_AUTH:
				if(message.GetMessageType() == 53)
				{
					// if get server's digest and public key
					boolean pass = DigestCheck(message);
					
					if(pass)
					{
						// send passed
						Send(new Message(Version, 55, 0, 0, null));
						
						// go to next state
						clientstate.SetState(CLIENTSTATE.SERVER_AUTHENTICATED);
						System.out.println("Get " + message.GetMessageType() + " go to " + clientstate.GetState());
					}
					else
					{
						// disconnect directly without informing the server
						cSocket.close();
						
					}
				}
				else
				{
					// if the message is not server's digest, keep waiting until timeout
				}
				break;
			case SERVER_AUTHENTICATED:
				//under this state, the client is expecting the digest require message from the server
				if(message.GetMessageType() == 51)
				{
					// after sending the S_AUTH passed message
					// the client should send it's own digest and public key to server
					String digest = "Digest" + "client_publickey";
					Send(new Message(Version, 52, 0, digest.length(), digest));
					clientstate.SetState(CLIENTSTATE.WAIT_FOR_ACK2);
					System.out.println("Get " + message.GetMessageType() + " go to " + clientstate.GetState());
				}
				else
				{
					//if the received message is not digest require message, wait until timeout
					
				}
				break;
			case WAIT_FOR_ACK2:
				//under this state, the client is waiting for the result of digest checking in server side
				if(message.GetMessageType() == 55)
				{
					System.out.println(message.toString());
					// the digest check passed, the client would send out a shared key for furthre encryption
					
					// send this key to the server
					Send(new Message(Version, 54, 0, new String(keyValue).length(), new String(keyValue)));
					
					// then go to the next state
					clientstate.SetState(CLIENTSTATE.WAIT_FOR_ACK3);
					System.out.println("Get " + message.GetMessageType() + " go to " + clientstate.GetState());
				}
				else if(message.GetMessageType() == 56)
				{
					// the digest check did not pass, disconnect
					cSocket.close();
				}
				else
				{
					// other message type, wait until time out
				}
				break;
			case WAIT_FOR_ACK3:
				System.out.println("===========In the state: " + clientstate.GetState());
				System.out.println(message.toString());
				// under this state, the client is waiting for the key received ACK message from the serer side
				if(message.GetMessageType() == 57)
				{
					nick = null;
			       	nick = JOptionPane.showInputDialog(null, "Please Set Up Your User Name: ");
					Send(new Message(Version, 11, 0, nick.length(), nick));
					// key received correctly, go to the SECURED state
					clientstate.SetState(CLIENTSTATE.SECURED);
					System.out.println("Get " + message.GetMessageType() + " go to " + clientstate.GetState());

				}
				else
				{
					// in any other situation, ignore
					//cSocket.close();
					//System.exit(0);
				}
				break;
			case SECURED:
				System.out.println("===========In the state: " + clientstate.GetState());
				// under this state, the client is waiting for the result of username check from server side
				if(message.GetMessageType() == 13)
				{
					// did not passed
					// send the username message to server for setting up username				
					nick = null;
			       	nick = JOptionPane.showInputDialog(null, "Please Set Up Your User Name: ");
					Send(new Message(Version, 11, 0, nick.length(), nick));
					System.out.println("Get " + message.GetMessageType() + " go to " + clientstate.GetState());

				}
				else if(message.GetMessageType() == 12)
				{
					// passed, will get user id from this message
					this.username = nick;
					this.userid = (int) message.GetUserid();
					this.connected = true;
					clientstate.SetState(CLIENTSTATE.CONNECTED);
					System.out.println("Get " + message.GetMessageType() + " go to " + clientstate.GetState());

					this.jMenuItem1.setEnabled(false);
					this.jMenuItem2.setEnabled(true);
				}
				else
				{
					//System.out.println(message.toString());
					// any other situation, wait until timeout
					//System.exit(0);
				}
				break;
			default:
				break;
				
			}
			
			//SetMessageNull();
		}
		
		
	}
	
	public synchronized void SetMessageNull()
	{
		message = null;
	}
	
	public synchronized void AddMessage(Message m)
	{
		message = m;
	}
	
	private boolean DigestCheck(Message message2) {
		 byte[] digest = ParseXML(message2.GetData(), "digest").getBytes();
		 byte[] publicKey = ParseXML(message2.GetData(), "publickey").getBytes();
		 byte[] checkMD5 = security.GenerateDigest(cSocket.getInetAddress().toString());
		 boolean check = false;
		 try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			check = md.isEqual(digest, checkMD5);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return true;
	}
	// send message to user
	public static boolean Send(Message m){
		try {
			System.out.println("Send: " + m.GetMessageType() + " Data: " + m.GetData());
			out.write(m.Packet2ByteArray());
			out.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {
		try {
			sendInput(inputText.getText()+"\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
    }

	static String read()
	{
		
		String s = null;
		try 
		{
			s = in.readLine();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		
		return s;
	}
	
	
	static String replace(String str, String pattern, String replace) 
	{
  	  	int s = 0;
  	  	int e = 0;
  	  	StringBuffer result = new StringBuffer();
    	while ((e = str.indexOf(pattern, s)) >= 0) 
    	{
    		result.append(str.substring(s, e));
       	    result.append(replace);
       	    s = e+pattern.length();
    	}
    	result.append(str.substring(s));
    	return result.toString();
    }
  
    void sendInput(String userInput) throws Exception
    {
    	if (!connected)
    	{
    		JOptionPane.showMessageDialog( this, "Not connected! Actions - Connect","Error", JOptionPane.ERROR_MESSAGE );
    		inputText.setText("");
    	}
    	else if(inputText.getText().equals("") || inputText.getText().equals("\n") ||  inputText.getText()== null  )
    	{
    		inputText.setText("");
    	}
    	else
    	{
    		// message delineate
  		  	String sendText = "<"+this.username+">"+replace(userInput,"\n","\r\n");
  		  	Send(new Message(Version, 21, this.userid, sendText.length(), sendText));
    	    inputText.setText("");
    	    //this.mainText.setText(mainText.getText() + "\n" + "me: " + sendText);
    	}
    }
    
    private String ParseXML(String xMLdata, String string) {
		String tmp = "";

		tmp = xMLdata;
	
		int i = tmp.indexOf("<"+ string + ">");
		int end = tmp.indexOf("</" + string + ">");
		String t = tmp.substring(i+string.length()+2, end);
		
		return t;
	}
    
    public static String nick;
	private javax.swing.JTextArea inputText;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    static public javax.swing.JMenuItem jMenuItem1;
    static public javax.swing.JMenuItem jMenuItem2;
    static public javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    public static javax.swing.JTextArea mainText;
    private javax.swing.JList nickList;
    private javax.swing.JButton sendButton;

}