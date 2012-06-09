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
                        
                        c.AddMessage(GetNewMessage(buffer));
                        
                        if(c.connected && (c.message.GetMessageType()==12|c.message.GetMessageType()==15|c.message.GetMessageType()==22))
                        {
                                if(c.message.GetMessageType()==12)
                                {
                                        c.mainText.setText(c.mainText.getText() + "\n" + c.message.GetData() + " joined");
                                }
                                else if(c.message.GetMessageType()==15)
                                {
                                        c.mainText.setText(c.mainText.getText() + "\n" + c.message.GetData() + " has left");

                                }
                                else
                                {
                                        if(c.message.GetUserid() == c.GetUserId())
                                        {
                                                c.mainText.setText(c.mainText.getText() + "\n" + " " + "me: " + parseMessage(c.message.GetData().replace("\r\n", "\n")));

                                        }
                                        else
                                        {
                                                c.mainText.setText(c.mainText.getText() + "\n" + " " + paserUsername(c.message.GetData()) + ": " + parseMessage(c.message.GetData().replace("\r\n", "\n")));
                                        }
                                }
                        }
                        System.out.println("Received: " + c.message.toString());
                }
        }
        
        private String parseMessage(String replace) {
                
                String name = replace;
                int end = replace.indexOf(">");
                
                name = name.substring(end+1, replace.length());
                return name;
        }

        private String paserUsername(String getData) {
                String name = getData;
                int from = getData.indexOf("<");
                int end = getData.indexOf(">");
                
                name = name.substring(from+1, end);
                
                return name;
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
