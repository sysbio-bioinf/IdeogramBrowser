package ideogram;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ColorTest extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ColorTest()
	{
		super(null);
		//super("ColorTest");
		//setLayout(null);		
		setSize(400,400);
		
		repaint();
	}
	
	public void paint(Graphics g)
	{
		// super.paint(g);
		System.out.println("paint");
		//Graphics2D g2 = (Graphics2D)g;
		float delta_hue = 1.0f/6.0f,
			  delta_sat = 0.1f;
		int width = (int)(getWidth()*delta_hue),
			height = (int) (getHeight()*delta_sat);
		
		g.setPaintMode();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.drawString("Hallo", 0, 0);
		for( float hue=0.0f; hue<=1.0f; hue+=delta_hue )
		{
			int x = (int)(getWidth()*hue);
			for( float sat=0.0f; sat<=1.0f; sat+=delta_sat )
			{
				int y = (int)(getHeight()*sat);
				
				g.setColor(Color.getHSBColor(hue, sat, 1.0f));
				g.fillRect(x, y, width, height);
			}
		}
		
		
		// DefaultColorMapper map = new DefaultColorMapper();
		TableColorMapper map = new TableColorMapper();
		
		map.getColorTable().add( Color.RED );
		map.getColorTable().add( Color.GRAY );
		map.getColorTable().add( Color.BLUE );
		
		
		double delta = 0.1;
		width = (int)(getWidth()*delta);
		for( double value=-1.0; value<=1.0; value+=delta)
		{
			int x = (int)(getWidth()*(1.0+value)*0.5),
				y = 0;
			g.setColor( map.map(value) );
			g.fillRect(x, y, width, height);
			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		JFrame mainFrame = new JFrame("ColorTest");
		mainFrame.setSize(400,400);
		mainFrame.getContentPane().add(new ColorTest());
		mainFrame.setVisible(true);
		
	}

}
