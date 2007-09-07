package ideogram;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * @author mueller
 *
 */
@SuppressWarnings("serial")
public class InfoDialog extends JDialog implements ActionListener
{
    private static final int WIDTH = 460;
    private static final int HEIGHT = 250; 

    public InfoDialog(Frame owner)
	{
		super(owner, "IdeogramBrowser Info", true);
                
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		
		JTextArea	area = new JTextArea() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			/*
            public void paintComponent( Graphics g ) {
                g.setColor(Color.GRAY);
                g.fillRect(0,0,getWidth(),getHeight());
                super.paintComponent(g);
            }
            */		        
		};
        area.setOpaque(true);
		area.setEditable(false);
		area.setText("IdeogramBrowser "+IdeogramMainWindow.VERSION_MAJOR+"."+IdeogramMainWindow.VERSION_MINOR+"."+
				IdeogramMainWindow.VERSION_SUB+
                     "  ("+IdeogramMainWindow.VERSION_DATE+")\n" +
					 "Developed by Hans A. Kestler, Andre Mueller and Matthias Schmid\n" +
					 "University of Ulm, Germany\n" +
					 "\n"+
					 "This product includes software developed by\n" +
					 "the  Apache Software Foundation  (http://www.apache.org/).\n" +
                     "This software is licensed under a Creative Commons Attribution 2.5 License (see cc.html).\n" +
                     "Some rights reserved.\n"+
                     "Please cite the paper:\n" +
                     "Mueller A, Holzmann K, Kestler HA, Visualization of genomic \n" +
                     "aberrations using Affymetrix SNP arrays, Bioinformatics, 23(4):496-497, 2007 "+
                     "\n"+
                     "Further information available at: \n"+
                     "http://www.informatik.uni-ulm.de/ni/staff/HKestler/ideo/"
                     );
        area.setForeground( Color.BLUE );
        area.setFont( area.getFont().deriveFont(Font.BOLD) );
        area.setAutoscrolls(true);
        area.setMargin(new Insets(10,10,10,10));
		cp.add( new JScrollPane(area), BorderLayout.CENTER);
		
		JButton button = new JButton("OK");
		button.addActionListener(this);
		
		getRootPane().setDefaultButton(button);
		
		cp.add(button,BorderLayout.SOUTH);
		
		//center to screensize
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(screensize.width/2-WIDTH/2,screensize.height/2-HEIGHT/2, WIDTH, HEIGHT);
	}
    

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		
		if( cmd.equalsIgnoreCase("ok") )
		{
			setVisible(false);
			dispose();
		}
		
	}
}
