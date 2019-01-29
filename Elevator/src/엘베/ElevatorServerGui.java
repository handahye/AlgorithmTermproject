package 엘베;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;

/**
* 단순히 서버측의 상태 표현을 위한 클래스 파일
* 서버에서 상태변화가 있을 때마다 repaint 된다.
*/

class ElevatorServerGui extends JFrame
{
	Image duke, main;
	Font gfont;

	public ElevatorServerGui(String title)
	{
		super(title);
		this.addWindowListener(new serverWindowListener());
		this.setSize(500,550);
		this.show();
		duke = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/duke.gif"));
		main = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/main.gif"));
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
		drawBg(g);
		drawElevatorState(g);
		drawFloor(g);
		drawElevator(g);
	}

	public void drawElevatorState(Graphics g)
	{
		String action = Elevator.getAction();
		String state = Elevator.getState();
		gfont = new Font("Sansserif", Font.PLAIN, 12);
		g.setFont(gfont);
		g.setColor(Color.black);
		g.drawString("Elevator Action : " + action, 25, 300);
		g.drawString("Elevator State : " + state, 25, 330);

	}

	public void drawElevator(Graphics g)
	{
		int gap = 47;
		int floor = Elevator.getThisFloor();
		int y = gap * floor;
		g.setColor(Color.gray);
		g.fill3DRect(380, 520 - y, 80 , 47, true);
		int num = Elevator.getClientInElevator();
		if (num != 0)
		{
			g.drawImage(duke, 380, 521 - y, this);
			gfont = new Font("Sansserif", Font.ITALIC, 16);
			g.setFont(gfont);
			g.setColor(Color.white);
			g.drawString(String.valueOf(num), 440, 536 - y);
			g.setColor(Color.gray);
		}
	}

	public void drawBg(Graphics g)
	{
		g.setColor(Color.white);
		g.fillRect(170, 50, 210, 470);
		g.drawImage(main, 20, 60, this);
	}

	public void drawFloor(Graphics g)
	{
		int gap = 47;
		g.setColor(Color.white);

		int[] clientInFloor = Elevator.getClientInFloor();
		g.setColor(Color.gray);
		for (int i = 1; i <= 10; i++)
		{
			g.drawRect(170, 520 - gap * i, 210 , 47);
			gfont = new Font("Sansserif", Font.ITALIC, 16);
			g.setFont(gfont);
			g.drawString(String.valueOf(i) + " F", 200, 520 - gap * i + 25);
			if (clientInFloor[i] != 0)
				drawMan(g, i ,clientInFloor[i]);
		}
	}

	public void drawMan(Graphics g, int floor, int num)
	{
		int gap = 47;
		g.drawImage(duke, 305, 521 - gap * floor, this);
		gfont = new Font("Sansserif", Font.ITALIC, 16);
		g.setFont(gfont);
		g.setColor(Color.red);
		g.drawString(String.valueOf(num), 365, 538 - gap * floor);
		g.setColor(Color.gray);
	}

	class serverWindowListener implements WindowListener
	{
		public void windowClosing (WindowEvent event)
		{
			dispose();
			System.exit(0);
		}
		public void windowOpened (WindowEvent event){}
		public void windowClosed (WindowEvent event) {}
		public void windowIconified (WindowEvent event) {}
		public void windowDeiconified (WindowEvent event) {}
		public void windowActivated (WindowEvent event) {}
		public void windowDeactivated (WindowEvent event) {}
	}
}