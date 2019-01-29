package 엘베;

import java.io.*;
import java.net.*;
import java.util.*;

/**
* 새로운 클라이언트 접속시 생성되는 ClientHandler 클래스 파일
*/
class ClientHandler implements Runnable
{
	protected Socket socket = null;
	protected ObjectOutputStream oos = null;
	protected ObjectInputStream ois = null;
	private ClientData clientData;
	protected Thread listener;
	private MoveElevatorThread moveElevatorThread;

	private String state = "OUT";
	private int thisFloor;

	public ClientHandler(Socket socket, ObjectOutputStream oos, ObjectInputStream ois, int floor)
	{
		this.socket = socket;
		this.oos = oos;
		this.ois = ois;
		this.thisFloor = floor;
		Elevator.setClientInFloor(floor, 1);
		moveElevatorThread = new MoveElevatorThread();
		Elevator.broadcast();

	}

	public synchronized void start()
	{
		if (listener == null)
		{
			listener = new Thread(this);
			listener.start();
		}

	}

	public synchronized void stop()
	{
        if (listener != Thread.currentThread())
        {
			listener.interrupt();
        }
		try
		{
			oos.close();
			ois.close();
			socket.close();
		}
		catch (Exception e)
		{
			System.out.println("Client Handler Stop!");
		}
        if (state == "OUT")
        {
        	Elevator.setClientInFloor(thisFloor, -1);
        }
        else if (state == "IN")
        {
        	Elevator.setClientInElevator(-1);
        }
        ElevatorServer.clients.removeElement(this);
        Elevator.broadcast();
        listener = null;
	}

	public void run()
	{
		ElevatorServer.clients.addElement(this);
		while(!Thread.interrupted())
		{
			try
			{
				clientData = (ClientData)ois.readObject();
				requestClient(clientData);
			}
			catch (Exception ex)
			{
				stop();
			}
		}
	}

	/**
	* Client 로부터 받은 ClientData 처리 메서드
	*/
	public void requestClient(ClientData clientData)
	{
		String stateClient = clientData.getState();
		thisFloor = clientData.getThisFloor();
		String requestClient = clientData.getRequest();

		if (stateClient.equals("OUT"))
		{
			if (requestClient.equals("UP"))
			{
				RequestFloorInfo requestFloorInfo = (RequestFloorInfo)Elevator.getRequestFloor().elementAt(thisFloor);
				requestFloorInfo.setUpRequest(true);
			}
			else if (requestClient.equals("DOWN"))
			{
				RequestFloorInfo requestFloorInfo = (RequestFloorInfo)Elevator.getRequestFloor().elementAt(thisFloor);
				requestFloorInfo.setDownRequest(true);
			}
		}
		else if (stateClient.equals("IN"))
		{
			int requestFloor = Integer.parseInt(requestClient);
			RequestFloorInfo requestFloorInfo = (RequestFloorInfo)Elevator.getRequestFloor().elementAt(requestFloor);
			requestFloorInfo.setRequest(true);
		}
		else if (stateClient.equals("tryIn"))
		{
			this.state = "IN";
			Elevator.setClientInFloor(thisFloor, -1);
			Elevator.setClientInElevator(1);
			Elevator.broadcast();
		}
		else if (stateClient.equals("tryOut"))
		{
			this.state = "OUT";
			Elevator.setClientInFloor(thisFloor, 1);
			Elevator.setClientInElevator(-1);
			Elevator.broadcast();
		}

		if (Elevator.getAction() == "STOP")
		{
			moveElevatorThread.start();
		}
	}
}
