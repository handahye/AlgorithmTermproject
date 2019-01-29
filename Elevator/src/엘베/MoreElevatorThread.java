package ����;

import java.io.*;
import java.net.*;
import java.util.*;

/**
* Elevator �۵��� �����ϴ� Ŭ���� ����
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
	* ���������͸� ȣ���� ���� ��ȯ
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
	*Elevator ���� �޼ҵ�
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
	*������ request ������ ���� �ڷᱸ��(Vector) �ٷ�� �޼ҵ��
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
	*Elevator�� �� ���� move ��Ų �� �������� �������� �޼���
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
	* request �� �ִ� ������ ������ �� ȣ��Ǵ� �޼���
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