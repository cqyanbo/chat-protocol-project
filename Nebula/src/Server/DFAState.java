package Server;

public class DFAState {
	
	private DFASTATE currentState;
	
	public enum DFASTATE {
		DISCONNECTED, HELLO_S_SENT,WAIT_FOR_ACK, WAIT_FOR_C_AUTH,
		WAIT_FOR_KEY, SECURED, CONNECTED
	}
	
	public DFAState()
	{
		currentState = DFASTATE.DISCONNECTED;
	}
	
	public void SetState(DFASTATE _currentstate)
	{
		this.currentState = _currentstate;
	}
	
	public DFASTATE GetState()
	{
		return this.currentState;
	}
	
}
