package Basic;
import org.json.simple.*;

public abstract class Message {
	
	private String version = "";
	private String MessageType = "";
	private String UserID = "";
	private int MessageLength;
	private JSONObject Data = null;
	
	
	// optional field
	private String ACK_MessageType = "";
	private boolean ACK = false;
	private long Checksum;
	
	
	/*
	 * SET
	 */
	public void SetVersion(String _version)
	{
		this.version = _version;
	}
	
	public void SetMessageType(String _messagetype)
	{
		this.MessageType = _messagetype;
	}
	
	public void SetUserid(String _userid)
	{
		this.UserID = _userid;
	}
	
	public void SetMessageLength(int _length)
	{
		this.MessageLength = _length;
	}
	
	public void SetData(JSONObject _data)
	{
		this.Data = _data;
	}
	
	public void SetACKMessage(String _ackmessage)
	{
		this.ACK_MessageType = _ackmessage;
	}
	
	public void SetACK(boolean _ack)
	{
		this.ACK = _ack;
	}
	
	public void SetCheckSum(long _checksum)
	{
		this.Checksum = _checksum;
	}
	
	
	/*
	 * GET
	 */
	public String GetVersion()
	{
		return this.version;
	}
	
	public String GetMessageType()
	{
		return this.MessageType;
	}
	
	public String GetUserid()
	{
		return this.UserID;
	}
	
	public int GetMessageLength()
	{
		return this.MessageLength;
	}
	
	public JSONObject GetData()
	{
		return this.Data;
	}
	
	public String GetACKMessage()
	{
		return this.ACK_MessageType;
	}
	
	public boolean GetACK()
	{
		return this.ACK;
	}
	
	public long SetCheckSum()
	{
		return this.Checksum;
	}
	
	

}
