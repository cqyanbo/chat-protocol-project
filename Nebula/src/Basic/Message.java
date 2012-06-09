package Basic;

import java.util.Arrays;
import java.util.BitSet;
import Server.DFAState.DFASTATE;

public class Message {
	
	private int offset = 0;	  // the current offset of this packet
	private int length = 285; // the whole length of packet
	private String os = "";	// store the name of operating system that is running the server
	private DFASTATE state;

	private int version;
	private int MessageType; // this should be a hex string
	private int UserID;
	private int MessageLength = 0;
	private int Reserved = 0;
	private String Data = "";
	private String ALGO =  "";
	private Security security = null; 
	
	
	// optional field
	private String ACK_MessageType = "";
	private boolean ACK = false;
	private long Checksum;
	
	public Message()
	{
		
	}
	
	public Message(int _version, int _messagetype, int _userid, int _messagelength, String _data) throws Exception
	{
		String osName = System.getProperty("os.name");
		
		if(osName.indexOf("Windows") >= 0)
			os = "Windows";
		else if(osName.indexOf("Linux") >= 0)
			os = "Linux";
		else
			os = "Mac";
		
			this.SetVersion(_version);
			this.SetData(_data);
			if(_data != null)
				SetMessageLength(_data.length());
			else
				SetMessageLength(0);
			this.SetMessageType(_messagetype);
			this.SetUserid(_userid);
	}
	
	public Message(int _version, String _messagetype, int _userid, int _messagelength, String _data) throws Exception
	{
			this.SetVersion(_version);
			this.SetData(_data);
			if(_data != null)
				SetMessageLength(_data.length());
			else
				SetMessageLength(0);
			this.SetMessageType(Integer.parseInt(_messagetype, 16));
			this.SetUserid(_userid);
	}
	
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
	
	
	public void SetMessageType(int _messagetype) throws Exception
	{	
		if(_messagetype <= 0x11111111 )
			this.MessageType = _messagetype;
		else
			throw new IllegalStateException();
	}
	
	public void SetUserid(int _userid) throws Exception
	{
		String userid = this.toBinary(_userid, 16);
		
		if(userid.length() == 16)
			this.UserID = _userid;
		else
			throw new IllegalStateException();
	}
	
	public void SetMessageLength(int _messagelength) throws Exception
	{
		if(Data.length() < 236)
			this.MessageLength = _messagelength;
		else
			throw new IllegalStateException();
	}
	
	public void SetData(String _data) throws Exception
	{
		if(_data != null)
        {
                if(_data.length() <= 245 || _data == null)
                        this.Data = _data;
                else
                        throw new IllegalStateException();
        }
        else
        {
                _data = null;
        }
	}
	
	
	/*
	 * GET
	 */
	public int GetVersion()
	{
		return this.version;
	}
	
	public int GetMessageType()
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
	
	// convert int to char[]
	private char[] IntToCharArray(long a)
	{
		String tmp = Integer.toBinaryString((int) a);
		char[] ca = tmp.toCharArray();
		if(ca.length > length)
		{
			System.out.println("wrong");
		}
		else if(ca.length < length)
		{
			char[] tmp1 = new char[length-ca.length];
			for(int i = 0; i < tmp1.length; i++)
			{
				tmp1[i] = '0';
			}
			
			ca = concat(tmp1, ca);
		}
		
		
		return ca;
	}
	
	private char[] concat(char[] header, char[] cs) {
		  char[] result = Arrays.copyOf(header, header.length + cs.length);
		  System.arraycopy(cs, 0, result, header.length, cs.length);
		  return result;
		}
	
	/*
	 * Methods
	 */
	
	// store the how packet as byte array
	public byte[] Packet2ByteArray()
	{

		char[] header = this.IntToCharArray(version, 4);
		for(int i = 0; i < header.length; i++)
			System.out.print(header[i]);
		System.out.println();
		header = concat(header, this.IntToCharArray(this.MessageType, 8));
		header = concat(header, this.IntToCharArray(this.UserID, 16));
		header = concat(header, this.IntToCharArray(this.Reserved, 4));
		header = concat(header, this.IntToCharArray(this.MessageLength, 8));
		
		
		String data = this.Data;
		
		header = concat(header, data.toCharArray());
		String tmp = new String(header);
		byte[] result = tmp.getBytes();
		return tmp.getBytes();
	}
	
	private char[] IntToCharArray(int a, int i) {
		String tmp = Integer.toBinaryString(a);
		//System.out.println("a is " + a + " tmp is " + tmp + " has " + tmp.length() + " length");
		char[] ca = tmp.toCharArray();
		if(ca.length > i)
		{
			System.out.println("wrong");
		}
		else if(ca.length < i)
		{
			//System.out.println("length - ca = " + (length - ca.length));
			char[] tmp1 = new char[i-ca.length];
			for(int i1 = 0; i1 < tmp1.length; i1++)
			{
				tmp1[i1] = '0';
			}
			
			ca = concat(tmp1, ca);
		}
		
		return ca;
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
	
	private static int CharArrayToInt(char[] array)
	{
		return Integer.parseInt(new String(array), 2);
	}
	
	private static int CharArrayToInt(char[] array, int from, int end)
	{
		char[] tmp = new char[end - from + 1];
		for(int i = from; i <= end; i++)
		{
			tmp[i-from] = array[i];
		}
		
		//System.out.println("Trying to convert: " + new String(tmp));
		
		return Integer.parseInt(new String(tmp), 2);
	}
	
	private static String CharArrayToString(char[] array, int from, int end)
	{
		char[] tmp = new char[end - from + 1];
		for(int i = from; i <= end; i++)
		{
			tmp[i-from] = array[i];
		}
		
		return new String(tmp);
	}
	
	// convert a byte array to message object
	public static Message ByteArrayToMessage(char[] packet)
	{
		char[] tmp = packet;
		Message m = new Message();
		try {
			m.SetVersion(CharArrayToInt(tmp, 0, 3));
			//System.out.println("version: " + m.GetVersion());
			m.SetMessageType(CharArrayToInt(tmp, 4, 11));
			//System.out.println("Message Type: " + m.GetMessageType());
			m.SetUserid(CharArrayToInt(tmp, 12, 27));
			//System.out.println("User id: " + m.GetUserid());
			m.SetMessageLength(CharArrayToInt(tmp, 32, 39));
			//System.out.println("Message Length: " + m.GetMessageLength());
			m.SetData(CharArrayToString(tmp, 40, packet.length-1).replace("\r\n", "\n"));
			//System.out.println("Data: " + m.GetData());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		
		return m;
	}
	
	public String toString()
	{
		String tmp = "Version=" + this.version + 
		" Message Type = " + this.MessageType + 
		" Userid = " + this.UserID + 
		" Message Length = " + this.length + 
		" Data: " + this.Data;
		
		return tmp;
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
		byte[] a = null;
		try {
			m.SetData("Test");
			m.SetMessageLength(m.GetData().length());
			m.SetMessageType(12);
			m.SetVersion(1);
			m.SetUserid(123);
			
			a = m.Packet2ByteArray();
			
			System.out.println(new String(a));
			System.out.println(new String((new String(a)).toCharArray()));
			Message test = Message.ByteArrayToMessage((new String(a)).toCharArray());
			System.out.println(test.MessageType);
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
