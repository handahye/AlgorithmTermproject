package 엘베;

import java.io.*;
import java.net.*;
import java.util.*;

/**
* Elevator 작동을 전담하는 클래스 파일
*/

class MoveElevatorThread implements Runnable
{
	private ClientData clientData;
	protected Thread listener;

	public synchronized void start()
	{
		if (listener == null)
		{
			listener = new Thread(this);
			listener.start();
		}
	}

	public void run()
	{
		move();
		stop();
	}

	public synchronized void stop()
	{
		System.out.println("ServerThread Stop!");
        listener = null;
	}

	/**
	* 엘레베이터를 호출한 층을 반환
	*/
	public int callElevator()
	{
		for (int i=1; i<=10; i++)
		{
			RequestFloorInfo requestFloorInfo = (RequestFloorInfo)Elevator.getRequestFloor().elementAt(i);
			boolean request = requestFloorInfo.getRequest();
			boolean upRequest = requestFloorInfo.getUpRequest();
			boolean downRequest = requestFloorInfo.getDownRequest();
			if (request == true || upRequest == true || downRequest == true)
			{
				return i;
			}

		}
		return 0;
	}

	/**
	*Elevator 동작 메소드
	*/
	public void move()
	{
		if (Elevator.getState() == "CLOSE")
		{
			Elevator.broadcast();
			for (int i=1; i<=10; i++)
			{
					RequestFloorInfo requestFloorInfo = (RequestFloorInfo)Elevator.getRequestFloor().elementAt(i);
					boolean request = requestFloorInfo.getRequest();
					boolean upRequest = requestFloorInfo.getUpRequest();
					boolean downRequest = requestFloorInfo.getDownRequest();
			}
			if (Elevator.getAction() == "STOP")
			{
				int desFloor = callElevator();
				if (desFloor != 0)
				{
					if(desFloor > Elevator.getThisFloor())
					{
						Elevator.setAction("UP");
						Elevator.setThisFloor(Elevator.getThisFloor() + 1);
					}
					else if(desFloor < Elevator.getThisFloor())
					{
						Elevator.setAction("DOWN");
						Elevator.setThisFloor(Elevator.getThisFloor() - 1);
					}
					else if (desFloor == Elevator.getThisFloor())
					{
						moveStop();
						setRequest(false, Elevator.getThisFloor());
						setUpRequest(false, Elevator.getThisFloor());
						setDownRequest(false, Elevator.getThisFloor());
					}
				}
			}
			else
			{
				if (Elevator.getAction() == "UP")
				{
					String isUpFloor = whatAction();
					if (getRequest(Elevator.getThisFloor()) == true || getUpRequest(Elevator.getThisFloor()) == true)
					{
						if (isUpFloor != "STOP")
							moveStop();
						setRequest(false, Elevator.getThisFloor());
						setUpRequest(false, Elevator.getThisFloor());
					}
					if (isUpFloor == "STOP")
					{
						Elevator.setAction("STOP");
						setDownRequest(false, Elevator.getThisFloor());
						moveStop();
					}
					else if (isUpFloor == "UP")
					{
						Elevator.setThisFloor(Elevator.getThisFloor() + 1);
					}
					else if (isUpFloor == "DOWN")
					{
						Elevator.setAction("DOWN");
					}
				}
				else if (Elevator.getAction() == "DOWN")
				{
					String isDownFloor = whatAction();
					if (getRequest(Elevator.getThisFloor()) == true || getDownRequest(Elevator.getThisFloor()) == true)
					{
						if (isDownFloor != "STOP")
							moveStop();
						setRequest(false, Elevator.getThisFloor());
						setDownRequest(false, Elevator.getThisFloor());
					}
					if (isDownFloor == "STOP")
					{
						Elevator.setAction("STOP");
						setUpRequest(false, Elevator.getThisFloor());
						moveStop();
					}
					else if (isDownFloor == "DOWN")
					{
						Elevator.setThisFloor(Elevator.getThisFloor() - 1);
					}
					else if (isDownFloor == "UP")
					{
						Elevator.setAction("UP");
					}
				}
			}
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
			}

			if (Elevator.getAction() != "STOP" || callElevator() != 0)
			{
				move();
			}
			return;
		}
	}

	/**
	*각층의 request 관리를 위한 자료구조(Vector) 다루는 메소드들
	*/
	public boolean getRequest(int thisFloor)
	{
		return ((RequestFloorInfo)Elevator.getRequestFloor().elementAt(thisFloor)).getRequest();
	}

	public void setRequest(boolean value, int thisFloor)
	{
		((RequestFloorInfo)Elevator.getRequestFloor().elementAt(thisFloor)).setRequest(value);
	}

	public boolean getUpRequest(int thisFloor)
	{
		return ((RequestFloorInfo)Elevator.getRequestFloor().elementAt(thisFloor)).getUpRequest();
	}

	public void setUpRequest(boolean value, int thisFloor)
	{
		((RequestFloorInfo)Elevator.getRequestFloor().elementAt(thisFloor)).setUpRequest(value);
	}

	public boolean getDownRequest(int thisFloor)
	{
		return ((RequestFloorInfo)Elevator.getRequestFloor().elementAt(thisFloor)).getDownRequest();
	}

	public void setDownRequest(boolean value, int thisFloor)
	{
		((RequestFloorInfo)Elevator.getRequestFloor().elementAt(thisFloor)).setDownRequest(value);
	}

	/**
	*Elevator를 한 층식 move 시킨 후 다음동작 결정위한 메서드
	*/
	public String whatAction()
	{
		if (Elevator.getAction() == "UP")
		{
			for (int i=Elevator.getThisFloor()+1; i<=10; i++)
			{
				RequestFloorInfo requestFloorInfo = (RequestFloorInfo)Elevator.getRequestFloor().elementAt(i);
				boolean request = requestFloorInfo.getRequest();
				boolean upRequest = requestFloorInfo.getUpRequest();
				boolean downRequest = requestFloorInfo.getDownRequest();
				if (request == true || upRequest == true || downRequest == true)
				{
					return "UP";
				}
			}
			for (int i=Elevator.getThisFloor()-1; i>=1; i--)
			{
				RequestFloorInfo requestFloorInfo = (RequestFloorInfo)Elevator.getRequestFloor().elementAt(i);
				boolean request = requestFloorInfo.getRequest();
				boolean upRequest = requestFloorInfo.getUpRequest();
				boolean downRequest = requestFloorInfo.getDownRequest();
				if (request == true || upRequest == true || downRequest == true)
				{
					return "DOWN";
				}
			}
		}
		else if (Elevator.getAction() == "DOWN")
		{
			for (int i=Elevator.getThisFloor()-1; i>=1; i--)
			{
				RequestFloorInfo requestFloorInfo = (RequestFloorInfo)Elevator.getRequestFloor().elementAt(i);
				boolean request = requestFloorInfo.getRequest();
				boolean upRequest = requestFloorInfo.getUpRequest();
				boolean downRequest = requestFloorInfo.getDownRequest();
				if (request == true || upRequest == true || downRequest == true)
				{
					return "DOWN";
				}
			}
			for (int i=Elevator.getThisFloor()+1; i<=10; i++)
			{
				RequestFloorInfo requestFloorInfo = (RequestFloorInfo)Elevator.getRequestFloor().elementAt(i);
				boolean request = requestFloorInfo.getRequest();
				boolean upRequest = requestFloorInfo.getUpRequest();
				boolean downRequest = requestFloorInfo.getDownRequest();
				if (request == true || upRequest == true || downRequest == true)
				{
					return "UP";
				}
			}
		}

		return "STOP";

	}

	/**
	* request 가 있는 층에서 정지된 후 호출되는 메서드
	*/
	public void moveStop()
	{
		Elevator.setState("OPEN");
		Elevator.broadcast();
		try
		{
			Thread.sleep(4000);
		}
		catch (InterruptedException e)
		{}
		Elevator.setState("CLOSE");
		Elevator.broadcast();
	}
}