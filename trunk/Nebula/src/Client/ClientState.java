package Client;

import Server.DFAState.DFASTATE;

public class ClientState {
	
	private CLIENTSTATE currentState;
	
	public enum CLIENTSTATE {
		DISCONNECTED, HELLO_C_SENT,WAIT_FOR_ACK2, WAIT_FOR_ACK3, WAIT_FOR_S_AUTH,
		SERVER_AUTHENTICATED, WAIT_FOR_DUP_CHECK, WAIT_FOR_S_CLOSE, SECURED, CONNECTED
	}
	
	public ClientState()
	{
		currentState = CLIENTSTATE.DISCONNECTED;
	}
	
	public void SetState(CLIENTSTATE _currentstate)
	{
		this.currentState = _currentstate;
	}
	
	public CLIENTSTATE GetState()
	{
		return this.currentState;
	}
	

	
}
