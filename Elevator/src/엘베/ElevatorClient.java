package 엘베;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;
import java.util.*;

/**
* Elevator Client 클래스 파일
*/

public class ElevatorClient extends JFrame implements Runnable
{
	private Image duke;
	private	Image imgPnl;
	private	Font gfont;
	private JPanel pnlOut;
	private JPanel pnlOutLeft;
	private JLabel lblFloor1;
	private JLabel lblFloor2;
	private JLabel lblAction;
	private JPanel pnlOutLeftTop;
	private JPanel pnlOutLeftCenter;
	private JButton elevatorIn;
	private JPanel pnlOutLeftBottom;

	private CardLayout cardLay;
	private JPanel pnlOutRightCard;
	private JPanel pnlOutRightOut;
	private JPanel pnlOutRightIn;
	private JButton btnUp;
	private JButton btnDown;
	private JPanel pnlButtonBox;

	private JButton btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn10, btnOpen;

	private JPanel pnlIn;

	private Socket clientSocket = null;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ServerData serverData;
	private Thread listener;
	private String host;
	private int port;

	private String state = "OUT";
	private int thisFloor = new Random().nextInt(9) + 1;
	private String request;
	private boolean upRequest;
	private boolean downRequest;
	private String doorState = "CLOSE";
	private int clientInElevator = 0;
	private int clientsInFloor = 0;


	public ElevatorClient(String title, String host, int port)
	{
		super(title);
		this.host = host;
		this.port = port;
	}

	public void init()
	{
		duke = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/duke.gif"));

		pnlOut = new JPanel();
		pnlOut.setLayout(new BorderLayout());

		pnlOutLeft = new JPanel();
		pnlOutLeft.setLayout(new BorderLayout());
		lblFloor1 = new JLabel(" Floor : ");
		lblFloor2 = new JLabel();
		lblAction = new JLabel("Stop");

		pnlOutLeftTop = new JPanel();
		pnlOutLeftTop.setLayout(new FlowLayout());
		pnlOutLeftTop.add(lblFloor1);
		pnlOutLeftTop.add(lblFloor2);
		pnlOutLeftTop.add(lblAction);

		pnlOutLeftCenter = new JPanel();
		pnlOutLeftBottom = new JPanel();
		elevatorIn = new JButton("     IN     ");
		elevatorIn.addActionListener(new actionListener());

		pnlOutLeftBottom.add(elevatorIn);

		pnlOutLeft.add(pnlOutLeftTop, BorderLayout.NORTH);
		pnlOutLeft.add(pnlOutLeftBottom, BorderLayout.SOUTH);

		cardLay = new CardLayout();
		pnlOutRightCard = new JPanel();
		pnlOutRightCard.setLayout(cardLay);

		pnlOutRightOut = new JPanel();
		pnlOutRightOut.setLayout(new BorderLayout());
		pnlOutRightCard.add(pnlOutRightOut,"pnlOutRightOut");

		pnlOutRightIn = new JPanel();
		pnlOutRightIn.setLayout(new GridLayout(11,1));

		btn10 = new JButton("10");
		btn9 = new JButton("9");
		btn8 = new JButton("8");
		btn7 = new JButton("7");
		btn6 = new JButton("6");
		btn5 = new JButton("5");
		btn4 = new JButton("4");
		btn3 = new JButton("3");
		btn2 = new JButton("2");
		btn1 = new JButton("1");
		btnOpen = new JButton("OPEN");
		btnOpen.addActionListener(new actionListener());
		btn1.addActionListener(new actionBtnListener());
		btn2.addActionListener(new actionBtnListener());
		btn3.addActionListener(new actionBtnListener());
		btn4.addActionListener(new actionBtnListener());
		btn5.addActionListener(new actionBtnListener());
		btn6.addActionListener(new actionBtnListener());
		btn7.addActionListener(new actionBtnListener());
		btn8.addActionListener(new actionBtnListener());
		btn9.addActionListener(new actionBtnListener());
		btn10.addActionListener(new actionBtnListener());
		pnlOutRightIn.add(btn10);
		pnlOutRightIn.add(btn9);
		pnlOutRightIn.add(btn8);
		pnlOutRightIn.add(btn7);
		pnlOutRightIn.add(btn6);
		pnlOutRightIn.add(btn5);
		pnlOutRightIn.add(btn4);
		pnlOutRightIn.add(btn3);
		pnlOutRightIn.add(btn2);
		pnlOutRightIn.add(btn1);
		pnlOutRightIn.add(btnOpen);
		pnlOutRightCard.add(pnlOutRightIn,"pnlOutRightIn");

		btnUp = new JButton("     UP     ");
		btnDown = new JButton("    DOWN    ");
		btnUp.addActionListener(new actionListener());
		btnDown.addActionListener(new actionListener());
		pnlButtonBox =  new JPanel();
		pnlButtonBox.setLayout(new GridLayout(2,1));
		pnlButtonBox.add(btnUp);
		pnlButtonBox.add(btnDown);
		pnlOutRightOut.add(pnlButtonBox, BorderLayout.SOUTH);

		pnlOut.add(pnlOutLeft, BorderLayout.CENTER);
		pnlOut.add(pnlOutRightCard, BorderLayout.EAST);
		cardLay.show(pnlOutRightCard,"pnlOutRightOut");

		getContentPane().add(pnlOut);
		this.addWindowListener(new clientWindowListener());
	}

	private boolean openDoorRequested = false;
	private boolean closeDoorRequested = false;

	public boolean isDoorOpening()
	{
		return width > 0 && openDoorRequested && !closeDoorRequested;
	}

	public boolean isDoorClosing()
	{
		return width < 180 && closeDoorRequested && !openDoorRequested;
	}

	public void paint(Graphics g)
	{
		Image offScreenImage = createImage(getSize().width, getSize().height);
		draw(offScreenImage.getGraphics());
		g.drawImage(offScreenImage, 0, 0, this);
 	}

 	public void draw(Graphics g)
 	{
		super.paint(g);
		if (state == "OUT")
		{
			drawImgPnl(g);
			drawFonts(g);
		}
		drawElevatorBg(g);
		drawMan(g);
		drawDoor(g);
		if (isDoorOpening())
			openDoor();
		if (isDoorClosing())
			closeDoor();
		drawButton(g);
 	}

	public void drawImgPnl(Graphics g)
	{
		imgPnl = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/inImg.gif"));
		g.drawImage(imgPnl, 402, 22, this);
	}

	public void drawButton(Graphics g)
	{
		if (upRequest == true)
		{
			btnUp.setForeground(Color.red);
		}
		else
		{
			btnUp.setForeground(Color.black);
		}
		if (downRequest == true)
		{
			btnDown.setForeground(Color.red);
		}
		else
		{
			btnDown.setForeground(Color.black);
		}

	}

	public void drawElevatorBg(Graphics g)
	{
		if (state == "OUT")
		{
			g.setColor(Color.white);
			g.fillRect(20, 70, 360 , 280);
			g.setColor(Color.gray);
			g.drawRect(80, 130, 240, 160);
			g.drawLine(20, 70, 80, 130);
			g.drawLine(380, 70, 320, 130);
			g.drawLine(20, 350, 80, 290);
			g.drawLine(380, 350, 320, 290);
		}
		else if (state == "IN")
		{
			g.setColor(Color.white);
			g.fillRect(20, 70, 360 , 280);
			g.setColor(Color.gray);
			g.drawLine(20, 290, 380, 290);
		}
	}

	public void drawMan(Graphics g)
	{
		int x = 100;
		int gap = 50;
		if (state == "OUT")
		{
			for (int i=0; i < clientInElevator; i++)
			{
				g.drawImage(duke, x + gap * i, 270, this);
			}
		}
		else if (state == "IN")
		{
			for (int i=0; i < clientsInFloor; i++)
			{
				g.drawImage(duke, x + gap * i, 270, this);
			}
		}

	}

	public void drawFonts(Graphics g)
	{
		gfont = new Font("Sansserif", Font.ITALIC, 22);
		g.setFont(gfont);
		g.setColor(Color.black);
		g.drawString(String.valueOf(thisFloor) + " F ", 440,200);
	}

	private int x = 20;
	private int x2 = 200;
	private int y = 70;
	private int step = 40;
	private int width = 180;
	private int width2 = 180;
	private int height= 280;

	public void drawDoor(Graphics g)
	{
		g.setColor(Color.gray);
		g.fill3DRect(x, y, width, height,true);
		g.fill3DRect(x2, y, width2, height,true);
	}

	public void openDoor()
	{
		if (isDoorOpening()) {
			width = width - step;
			x2 = x2 + step;
			width2 = width2 - step;
			repaint();
		}
	}

	public void closeDoor()
	{
		if (isDoorClosing()) {
			width = width + step;
			x2 = x2 - step;
			width2 = width2 + step;
			repaint();
		}
	}

	/**
	* 서버로부터 받은 ServerData 처리 메서드
	*/
	public void process(ServerData serverData)
	{
		int currentFloor = serverData.getThisFloor();
		String currentAction = serverData.getAction();
		String currentState = serverData.getState();
		clientInElevator = serverData.getClientInElevator();
		clientsInFloor = serverData.getClientInFloor(currentFloor);

		lblFloor2.setText(String.valueOf(currentFloor));
		lblAction.setText(currentAction);
		if (currentFloor == thisFloor)
		{
			if (currentState.equals("OPEN"))
			{
				repaint();
				if (currentAction.equals("STOP"))
				{
					upRequest = false;
					downRequest = false;
				}
				else
				{
					if (currentAction.equals("UP"))
					{
						upRequest = false;
					}
					else if (currentAction.equals("DOWN"))
					{
						downRequest = false;
					}
				}

				requestDoorOpening();
				openDoor();
				doorState = "OPEN";
			}
		}
		if (currentState.equals("CLOSE"))
		{
			if (doorState == "OPEN")
			{
				requestDoorClosing();
				closeDoor();
				doorState = "CLOSE";
			}
		}

		if (state == "IN")
		{
			thisFloor = currentFloor;
		}

	}

	public void start()
	{
		init();
		if (listener == null)
		{
			try
			{
				//clientSocket = new Socket("127.0.0.1", port);
				clientSocket = new Socket(host, port);
				oos = new ObjectOutputStream(clientSocket.getOutputStream());
				ois = new ObjectInputStream(clientSocket.getInputStream());
				broadcast();
			}
			catch (IOException e)
			{
				stop();
			}
			listener = new Thread (this);
			listener.start();
		}
	}

	public void stop()
	{
        if (listener != Thread.currentThread())
        {
			listener.interrupt();
        }
		try
		{
			oos.close();
			ois.close();
			clientSocket.close();
		}
		catch (Exception e)
		{
			System.out.println("ClientThread Stop!");
		}
        listener = null;
        dispose();
        System.exit(0);
	}

	public void run()
	{
		try
		{
			while(!Thread.interrupted())
			{
				serverData = (ServerData)ois.readObject();
				process(serverData);
			}
		}
		catch (Exception e)
		{
			System.out.println("Client Thread Error!");
			stop();
		}
	}

	public static void main(String[] args)
	{
		if ((args.length != 1) || (args[0].indexOf (':') < 0)) throw new IllegalArgumentException ("Syntax: java ElevatorClient host:port");

		int idx = args[0].indexOf (':');
		String host = args[0].substring (0, idx);
		int port = Integer.parseInt (args[0].substring (idx + 1));

		ElevatorClient elevatorClient = new ElevatorClient("ElevatorClient", host, port);
		elevatorClient.start();
		elevatorClient.setSize(500,400);
		elevatorClient.show();
	}

	private void pressUpButton()
	{
		this.request = "UP";
		this.upRequest = true;
		broadcast();
	}

	private void pressDownButton()
	{
		this.request = "DOWN";
		this.downRequest = true;
		broadcast();
	}

	private void tryIn()
	{
		if (doorState == "OPEN")
		{
			if (clientInElevator < 5)
			{
				state = "tryIn";
				broadcast();
				state = "IN";
				cardLay.show(pnlOutRightCard,"pnlOutRightIn");
				elevatorIn.setText("     OUT     ");
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Over Weight!", "Notice", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private void tryOut()
	{
		if (doorState == "OPEN")
		{
			state = "tryOut";
			broadcast();
			state = "OUT";
			cardLay.show(pnlOutRightCard,"pnlOutRightOut");
			elevatorIn.setText("     IN     ");
		}
	}

	private void broadcast()
	{
		try
		{
			oos.writeObject(new ClientData(state, thisFloor, request, doorState));
		}
		catch (Exception ex)
		{
		}
	}

	protected void requestDoorOpening()
	{
		this.openDoorRequested = true;
		this.closeDoorRequested = false;
	}
	protected void requestDoorClosing()
	{
		this.closeDoorRequested = true;
		this.openDoorRequested = false;
	}

	class actionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (((JButton)e.getSource()).getText() == "     UP     ")
			{
				pressUpButton();
				repaint();
			}
			else if (((JButton)e.getSource()).getText() == "    DOWN    ")
			{
				pressDownButton();
				repaint();
			}
			else if (((JButton)e.getSource()).getText() == "     IN     ")
			{
				tryIn();
				repaint();
			}
			else if (((JButton)e.getSource()).getText() == "     OUT     ")
			{
				tryOut();
				repaint();
			}
			else if (((JButton)e.getSource()).getText() == "OPEN")
			{
				request = String.valueOf(thisFloor);
				broadcast();
			}
		}
	}

	class actionBtnListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			request = ((JButton)e.getSource()).getText();
			broadcast();
		}
	}

	class clientWindowListener implements WindowListener
	{
		public void windowClosing (WindowEvent event)
		{
			stop();
		}
		public void windowOpened (WindowEvent event){}
		public void windowClosed (WindowEvent event) {}
		public void windowIconified (WindowEvent event) {}
		public void windowDeiconified (WindowEvent event) {}
		public void windowActivated (WindowEvent event) {}
		public void windowDeactivated (WindowEvent event) {}
	}
}