package 엘베;
import java.io.*;
import java.net.*;
import java.util.*;

/**
* Elevator Server 클래스파일
*/
public class ElevatorServer implements Runnable
{
	private boolean serverPowerOn = true;
	private ServerSocket serverSocket = null;
	private Socket newSocket = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	private int port;
	private Thread listener;
	private ServerData serverdata;

	protected static Vector clients = new Vector();

	public ElevatorServer(int port)
	{
		this.port = port;
		System.out.println("ElevatorServer Start.");
	}

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
		startServer(port);
	}

	public void startServer(int port)
	{
		try
		{
			serverSocket = new ServerSocket(port);

			while (serverPowerOn)
			{
				newSocket = serverSocket.accept();
				oos = new ObjectOutputStream(newSocket.getOutputStream());
				ois = new ObjectInputStream(newSocket.getInputStream());
				System.out.println("Connection Established from : "+ newSocket.getInetAddress().getHostAddress());

				try
				{
					ClientData clientData = (ClientData)ois.readObject();
					int floor = clientData.getThisFloor();
					oos.writeObject(new ServerData(Elevator.getState(), Elevator.getAction(), Elevator.getThisFloor(),
					Elevator.getClientInElevator(), Elevator.getClientInFloor()));

					(new ClientHandler(newSocket, oos, ois, floor)).start();
				}
				catch (IOException ex)
				{
					System.out.println("Client Connect Error!");
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ServerSocket Error");
		}
	}

	public static void main(String[] args)
	{
		if (args.length != 1) throw new IllegalArgumentException ("Syntax: java ElevatorServer port");

		ElevatorServer elevatorServer = new ElevatorServer(Integer.parseInt(args[0]));
		elevatorServer.start();

		Elevator elevator = new Elevator();
		elevator.init(10);
	}
}

