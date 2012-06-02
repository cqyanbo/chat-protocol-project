package Basic;
import java.net.URL;
import java.util.BitSet;
import Server.DFAState;
import Server.DFAState.DFASTATE;

import org.json.simple.*;

public class Message {
	
	private int offset = 0;	  // the current offset of this packet
	private int length = 285; // the whole length of packet
	
	private DFASTATE state;

	private int version;
	private String MessageType = ""; // this should be a hex string
	private long UserID;
	private int MessageLength;
	private BitSet Reserved = new BitSet(4);
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
		DFASTATE[] dfa = DFASTATE.values();
		boolean a = false;
		for(int i = 0; i< dfa.length; i++)
		{
			if(dfa[i].name().equals(_messagetype))
			{
				a = true;
			}
		}
		if(_messagetype.length() == 16 && a)
			this.MessageType = _messagetype;
		else
			throw new IllegalStateException();
	}
	
	public void SetMessageType(int _messagetype) throws Exception
	{
		DFASTATE[] dfa = DFASTATE.values();
		boolean a = false;
		for(int i = 0; i< dfa.length; i++)
		{
			if(dfa[i].name().equals(Integer.toHexString(_messagetype)))
			{
				a = true;
			}
		}
		if(Integer.toHexString(_messagetype).length() == 16 && a)
			this.MessageType = Integer.toHexString(_messagetype);
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
		if(fromByteArray(Data.getBytes()).length() > 2244)
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

	    return sb.reverse().toString();
	}
	
	// convert binary string into BitSet
	private BitSet String2BitSet(String binary, int limitation) throws Exception
	{
		if(binary.length() != limitation)
		{
			throw new IndexOutOfBoundsException();
		}
		BitSet bs = new BitSet();
		
		for(int i = 0; i<binary.length(); i++)
		{
			char tmp = binary.charAt(i);
			if(tmp == '1')
				bs.set(i, true);
			else
				bs.set(i, false);
		}
		
		return bs;
	}
	
	// converet byte array to bitset
	private BitSet fromByteArray(byte[] bytes) {
	    BitSet bits = new BitSet();
	    for (int i=0; i<bytes.length*8; i++) {
	        if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
	            bits.set(i);
	        }
	    }
	    return bits;
	}
	
	// convert bitset to byte array
	private byte[] toByteArray(BitSet bits) {
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
	
	// converet long to int
	private int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	
	/*
	 * Methods
	 */
	
	// store the how packet as byte array
	public byte[] Packet2ByteArray()
	{
		byte[] packet = new byte[285];
		try {
			byte[] _version = toByteArray(String2BitSet(this.toBinary(version, 4), 4));
			byte[] _messagetype = toByteArray(String2BitSet(this.MessageType, 16));
			packet = combineByteArrays(packet, _version);
			packet = combineByteArrays(packet, _messagetype);
			byte[] _userid = this.toBinary(this.UserID, 16).getBytes();
			packet = combineByteArrays(packet, _userid);
			byte[] _reserved = toByteArray(this.Reserved);
			packet = combineByteArrays(packet, _reserved);
			byte[] _messagelength = toByteArray(String2BitSet(toBinary(this.MessageLength, 8), 8));
			packet = combineByteArrays(packet, _messagelength);
			byte[] _data = Data.getBytes();
			packet = combineByteArrays(packet, _data);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return packet;
	}
	
	// convert a byte array to message object
	public Message ByteArrayToMessage(byte[] packet)
	{
		BitSet bs = fromByteArray(packet);
		//Message m = new Message();
		this.version = safeLongToInt(BitSetToLong(bs.get(0, 3)));
		this.MessageType = new String(toByteArray(bs.get(4, 11)));
		this.UserID = BitSetToLong(bs.get(12, 27));
		this.Reserved = bs.get(28, 31);
		this.MessageLength = safeLongToInt(BitSetToLong(bs.get(32, 39)));
		this.Data = new String(toByteArray(bs.get(40, bs.length() - 1)));
		
		return this;
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
	
}
