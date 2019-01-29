package 엘베;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Enumeration;

/**
* Elevator 의 정보를 갖는 클래스 파일
*/
class Elevator
{
	private static int MAXFLOOR = 10;
	private static int thisFloor = 1;
	private static String state = "CLOSE";
	private static String action = "STOP";
	protected static Vector requestFloor;
	private static int clientInElevator = 0;
	private static int[] clientInFloor;

	private static ElevatorServerGui elevatorServerGui;


	public static synchronized void broadcast()
	{
		synchronized (ElevatorServer.clients)
		{
			elevatorServerGui.repaint();
			Enumeration en = ElevatorServer.clients.elements();
			int[] clientInFloor = new int[11];
			int[] temp = Elevator.getClientInFloor();
			for (int i=1 ; i<=10; i++)
			{
				clientInFloor[i] = temp[i];
			}
			while (en.hasMoreElements())
			{
				ClientHandler clients = (ClientHandler)en.nextElement();
				try
				{
					clients.oos.writeObject(new ServerData(Elevator.getState(), Elevator.getAction(),
					Elevator.getThisFloor(), Elevator.getClientInElevator(), clientInFloor));
				}
				catch (IOException e)
				{
				}
			}
		}
	}
	public static void init(int init)
	{
		clientInFloor = new int[MAXFLOOR + 1];
		requestFloor = new Vector();
		for (int i=0; i<=MAXFLOOR; i++)
		{
			requestFloor.addElement(new RequestFloorInfo());
			clientInFloor[i] = 0;
		}
		elevatorServerGui = new ElevatorServerGui("ElevatorServer");

	}

	public static String getState()
	{
		return state;
	}

	public static String getAction()
	{
		return action;
	}

	public static int getThisFloor()
	{
		return thisFloor;
	}

	public static int getClientInElevator()
	{
		return clientInElevator;
	}

	public static int[] getClientInFloor()
	{
		return clientInFloor;
	}

	public static Vector getRequestFloor()
	{
		return requestFloor;
	}

	public static void setState(String value)
	{
		state = value;
	}

	public static void setThisFloor(int value)
	{
		thisFloor =  value;
	}

	public static void setAction(String value)
	{
		action = value;
	}

	public static void setClientInFloor(int floor, int value)
	{
		int num = clientInFloor[floor];
		num = num + value;
		clientInFloor[floor] = num;
	}

	public static void setClientInElevator(int value)
	{
		clientInElevator += value;
	}


}
