package ¿¤º£;

import java.io.*;
import java.net.*;

class ClientData implements Serializable
{
	private String state;
	private int thisFloor;
	private String request;
	private String doorState;

	public ClientData(String state, int thisFloor, String request, String doorState)
	{
		this.state = state;
		this.thisFloor = thisFloor;
		this.request = request;
		this.doorState = doorState;
	}

	public String getState()
	{
		return this.state;
	}

	public int getThisFloor()
	{
		return this.thisFloor;
	}

	public String getRequest()
	{
		return this.request;
	}

	public String getDoorState()
	{
		return this.doorState;
	}
}