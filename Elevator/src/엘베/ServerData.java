package ¿¤º£;

import java.io.*;
import java.util.*;

class ServerData implements Serializable
{
	private String state;
	private String action;
	private int thisFloor;
	private Vector requestFloor;
	private int clientInElevator;
	private int[] clientInFloor;

	public ServerData(String state, String action, int thisFloor, int clientInElevator, int[] clientInFloor)
	{
		this.state = state;
		this.action = action;
		this.thisFloor = thisFloor;
		this.clientInElevator = clientInElevator;
		this.clientInFloor = clientInFloor;
	}

	public String getState()
	{
		return this.state;
	}

	public String getAction()
	{
		return this.action;
	}

	public int getThisFloor()
	{
		return this.thisFloor;
	}

	public int getClientInElevator()
	{
		return this.clientInElevator;
	}

	public int[] getClientInFloor()
	{
		return this.clientInFloor;
	}

	public int getClientInFloor(int floor)
	{
		return this.clientInFloor[floor];
	}
}