package Basic;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Server.DFAState;
import Server.DFAState.DFASTATE;

import org.json.simple.*;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message {
	
	private int offset = 0;	  // the current offset of this packet
	private int length = 285; // the whole length of packet
	
	private DFASTATE state;

	private int version;
	private String MessageType = ""; // this should be a hex string
	private long UserID;
	private int MessageLength = 0;
	private int Reserved = 0;
	private String Data = "";
	
	
	// optional field
	private String ACK_MessageType = "";
	private boolean ACK = false;
	private long Checksum;
	
	/*
	 * SET
	 */
	public void SetVersion(int _version) throws Exception
	{
		if(_version <= 15)
			this.version = _version;
		else
			throw new IllegalStateException();
	}
	
	public void SetMessageType(String _messagetype) throws Exception
	{
		/*DFASTATE[] dfa = DFASTATE.values();
		boolean a = false;
		for(int i = 0; i< dfa.length; i++)
		{
			if(dfa[i].name().equals(_messagetype))
			{
				a = true;
			}
		}*/
		if(_messagetype.length() <= 16)
			this.MessageType = _messagetype;
		else
			throw new IllegalStateException();
	}
	
	public void SetMessageType(int _messagetype) throws Exception
	{	
		String hs = this.toBinary(_messagetype, 8);
		if(hs.length() <= 16 )
			this.MessageType = hs;
		else
			throw new IllegalStateException();
	}
	
	public void SetUserid(long _userid) throws Exception
	{
		String userid = this.toBinary(_userid, 16);
		
		if(userid.length() == 16)
			this.UserID = _userid;
		else
			throw new IllegalStateException();
	}
	
	public void SetMessageLength() throws Exception
	{
		if(fromByteArray(Data.getBytes()).length() < 2244)
			this.MessageLength = fromByteArray(Data.getBytes()).length();
		else
			throw new IllegalStateException();
	}
	
	public void SetData(String _data) throws Exception
	{
		if(_data.length() <= 140)
			this.Data = _data;
		else
			throw new IllegalStateException();
	}
	
	
	/*
	 * GET
	 */
	public int GetVersion()
	{
		return this.version;
	}
	
	public String GetMessageType()
	{
		return this.MessageType;
	}
	
	public long GetUserid()
	{
		return this.UserID;
	}
	
	public int GetMessageLength()
	{
		return this.MessageLength;
	}
	
	public String GetData()
	{
		return this.Data;
	}
	
	
	/*
	 * Convert
	 */
	
	public static boolean[] StrToBool(String input){
        boolean[] output=Binstr16ToBool(BinstrToBinstr16(StrToBinstr(input)));
        return output;
    }
	
	 private static String StrToBinstr(String str) {
	        char[] strChar=str.toCharArray();
	        String result="";
	        for(int i=0;i<strChar.length;i++){
	            result +=Integer.toBinaryString(strChar[i])+ " ";
	        }
	        return result;
	    }
	 
	 public static String BoolToStr(boolean[] input){
	        String output=BinstrToStr(Binstr16ToBinstr(BoolToBinstr16(input)));
	        return output;
	    }
	 
	 private static String BoolToBinstr16(boolean[] input){ 
	        StringBuffer output=new StringBuffer();
	        for(int i=0;i<input.length;i++){
	            if(input[i])
	                output.append('1');
	            else
	                output.append('0');
	            if((i+1)%16==0)
	                output.append(' ');           
	        }
	        output.append(' ');
	        return output.toString();
	    }
	 
	 private static char BinstrToChar(String binStr){
	        int[] temp=BinstrToIntArray(binStr);
	        int sum=0;   
	        for(int i=0; i<temp.length;i++){
	            sum +=temp[temp.length-1-i]<<i;
	        }   
	        return (char)sum;
	    }
	 
	 private static int[] BinstrToIntArray(String binStr) {       
	        char[] temp=binStr.toCharArray();
	        int[] result=new int[temp.length];   
	        for(int i=0;i<temp.length;i++) {
	            result[i]=temp[i]-48;
	        }
	        return result;
	    }
	 
	 private static String BinstrToStr(String binStr) {
	        String[] tempStr=StrToStrArray(binStr);
	        char[] tempChar=new char[tempStr.length];
	        for(int i=0;i<tempStr.length;i++) {
	            tempChar[i]=BinstrToChar(tempStr[i]);
	        }
	        return String.valueOf(tempChar);
	    }
	 
	  private static String Binstr16ToBinstr(String input){
	        StringBuffer output=new StringBuffer();
	        String[] tempStr=StrToStrArray(input);
	        for(int i=0;i<tempStr.length;i++){
	            for(int j=0;j<16;j++){
	                if(tempStr[i].charAt(j)=='1'){
	                    output.append(tempStr[i].substring(j)+" ");
	                    break;
	                }
	                if(j==15&&tempStr[i].charAt(j)=='0')
	                    output.append("0"+" ");
	            }
	        }
	        return output.toString();
	    }   
	 
	 private static String BinstrToBinstr16(String input){
	        StringBuffer output=new StringBuffer();
	        String[] tempStr=StrToStrArray(input);
	        for(int i=0;i<tempStr.length;i++){
	            for(int j=16-tempStr[i].length();j>0;j--)
	                output.append('0');
	            output.append(tempStr[i]+" ");
	        }
	        return output.toString();
	    }
	 
	 private static String[] StrToStrArray(String str) {
	        return str.split(" ");
	    }
	 
	 private static boolean[] Binstr16ToBool(String input){
	        String[] tempStr=StrToStrArray(input);
	        boolean[] output=new boolean[tempStr.length*16];
	        for(int i=0,j=0;i<input.length();i++,j++)
	            if(input.charAt(i)=='1')
	                output[j]=true;
	            else if(input.charAt(i)=='0')
	                output[j]=false;
	            else
	                j--;
	        return output;
	    }
	
	// convert from bitset to int[]
	private int[] bits2Ints(BitSet bs) {
	    int[] temp = new int[bs.size() / 32];

	    for (int i = 0; i < temp.length; i++)
	      for (int j = 0; j < 32; j++)
	        if (bs.get(i * 32 + j))
	          temp[i] |= 1 << j;

	    return temp;
	  }
	
	// convert from int to binary string
	private String toBinary(long _userid, int NumberOfBits) {
	    StringBuffer sb = new StringBuffer();

	    for (int i = 0; i < NumberOfBits; i++) {
	      sb.append(((_userid & 1) == 1) ? '1' : '0');
	      _userid >>= 1;
	    }
	    String tmp = sb.reverse().toString();
	    return tmp;
	}
	
	// convert binary string into BitSet
	private static BitSet String2BitSet(String binary, int limitation) throws Exception
	{
		if(binary.length()/8 > limitation)
		{
			throw new IndexOutOfBoundsException();
		}
		
		BitSet bs = new BitSet();
		int index = 0;
		//System.out.println("The length of binary = " + binary.length());
		for(int i = 0; i<binary.length(); i++)
		{
			index++;
			char tmp = binary.charAt(i);
			if(tmp == '1')
				bs.set(i, true);
			else
				bs.set(i, false);
		}
		//System.out.println("The length of bs = " + bs.size() + " index = " + index);
		return bs;
	}
	
	// Returns a bitset containing the values in bytes.
	// The byte-ordering of bytes must be big-endian which means the most significant bit is in element 0.
	public static BitSet fromByteArray(byte[] bytes) {
	    BitSet bits = new BitSet();
	    for (int i=0; i<bytes.length*8; i++) {
	        if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
	            bits.set(i);
	        }
	    }
	    return bits;
	}

	// Returns a byte array of at least length 1.
	// The most significant bit in the result is guaranteed not to be a 1
	// (since BitSet does not support sign extension).
	// The byte-ordering of the result is big-endian which means the most significant bit is in element 0.
	// The bit at index 0 of the bit set is assumed to be the least significant bit.
	public static byte[] toByteArray(BitSet bits) {
	    byte[] bytes = new byte[bits.length()/8+1];
	    for (int i=0; i<bits.length(); i++) {
	        if (bits.get(i)) {
	            bytes[bytes.length-i/8-1] |= 1<<(i%8);
	        }
	    }
	    return bytes;
	}
	
	// convert bitset to long
	private long BitSetToLong(BitSet bits) {
		long value = 0;
	    for (int i = 0; i < bits.length(); ++i) {
	      value += bits.get(i) ? (1L << i) : 0L;
	    }
	    return value;
	}
	
	// convert long to int
	private int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	
	// convert string to binary string
	private String StringToBinary(String s)
	{
		s += "\r\n";
		  byte[] bytes = s.getBytes();
		  StringBuilder binary = new StringBuilder();
		  for (byte b : bytes)
		  {
		     int val = b;
		     for (int i = 0; i < 8; i++)
		     {
		        binary.append((val & 128) == 0 ? 0 : 1);
		        val <<= 1;
		     }
		  }
		  
		  return binary.toString();
	}
	
	/*
	 * Methods
	 */
	
	// store the how packet as byte array
	public String Packet2ByteArray()
	{
		Map jsonObject=new LinkedHashMap();		
		jsonObject.put("version", new Integer(this.version));
		jsonObject.put("messagetype", new String(this.MessageType));
		jsonObject.put("userid", this.UserID);
		jsonObject.put("reserved", this.Reserved);
		jsonObject.put("messagelength", this.MessageLength);
		jsonObject.put("data", this.Data);
		
		return JSONValue.toJSONString(jsonObject).toString();
	}
	
	public static String toText(String s){
		
		String s2 = "";   
		char nextChar;
		int length = s.length()/8;
		for(int i = 0; i < length; i++) //this is a little tricky.  we want [0, 7], [9, 16], etc
		{
		     nextChar = (char)Integer.parseInt(s.substring(i, i+8), 2);
		     s2 += nextChar;
		}
		
		return s2;
    }
	
	// convert a byte array to message object
	public Message ByteArrayToMessage(String packet)
	{
		 Message m = new Message();
		 JSONParser parser = new JSONParser();
		  ContainerFactory containerFactory = new ContainerFactory(){
		    public List creatArrayContainer() {
		      return new LinkedList();
		    }

		    public Map createObjectContainer() {
		      return new LinkedHashMap();
		    }
		                        
		  };
		  
		  try{
			    Map json = (Map)parser.parse(packet, containerFactory);
			    Iterator iter = json.entrySet().iterator();
			    while(iter.hasNext()){
			      Map.Entry entry = (Map.Entry)iter.next();
			      if(entry.getKey().toString() == "version")
			      {
			    	  try {
						m.SetVersion(Integer.parseInt(entry.getValue().toString()));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			      }
				else if(entry.getKey().toString() == "messagetype")
				{
					try {
						m.SetMessageType(entry.getValue().toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(entry.getKey().toString() == "userid")
				{
					try {
						m.SetUserid(Long.valueOf(entry.getValue().toString()));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(entry.getKey().toString() == "data")
				{
					try {
						m.SetData(entry.getValue().toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			      
			    }
			                        
			  }
			  catch(ParseException pe){
			    System.out.println(pe);
			  }
		
			  try {
				m.SetMessageLength();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return m;
	}
	
	public static char[] toCharArray(BitSet bs)
    {     
		//System.out.println(bs.length());
       int length = bs.length();     
       char[] arr = new char[length];     
       for(int i = 0; i < length; i++)
       {         
         arr[i] = bs.get(i) ? '1' : '0';     
       }  
      return arr; 
    }

	
	private byte[] combineByteArrays(byte[] one, byte[] two)
	{
		byte[] combined = new byte[one.length + two.length];

		for (int i = 0; i < combined.length; ++i)
		{
		    combined[i] = i < one.length ? one[i] : two[i - one.length];
		}
		
		return combined;
	}
	
	public static void main(String[] args)
	{
		Message m = new Message();
		String a = "";
		try {
			m.SetData("Test");
			m.SetMessageLength();
			m.SetMessageType(12);
			m.SetVersion(1);
			m.SetUserid(123);
			
			a = m.Packet2ByteArray();
			
			System.out.println("==" + a);
			System.out.println(a.length());
			
			
			Message test = m.ByteArrayToMessage(a);
			System.out.println(test.MessageType);
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
