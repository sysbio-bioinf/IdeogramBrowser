package ideogram.view;

import ideogram.db.IdeogramDB;
import ideogram.input.GenomicMarker;
import ideogram.input.MemoryGenomicMarkerModel;
import ideogram.tree.Interval;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

public class ChromosomalPanel extends JPanel implements  KeyEventDispatcher, VetoableChangeListener, AdjustmentListener, ActionListener, FocusListener
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private JScrollPane scroller;
	private JPanel		 scrollPanel;
	private IdeogramPanel ideoPanel;
//	private IdeogramDB	ideogramDB;
	private JComboBox	zoomSelector;
	private JLabel		headerLabel, basepairLabel;
	private boolean	inUpdate;
//	private byte	chromosome;

	public ChromosomalPanel()
	{
		this(null,(byte)1);
	}
	
	public ChromosomalPanel(IdeogramDB ideogramDB,byte chromosome)
	{	
//		this.ideogramDB = ideogramDB;
//		this.chromosome = chromosome; 
		
		setFocusable(true);
		setPreferredSize(new Dimension(250,500));
		
		Vector<String> zoomSteps = new Vector<String>();
		zoomSteps.add("250M");
		zoomSteps.add("50M");
		zoomSteps.add("10M");
		zoomSteps.add("2M");
		zoomSteps.add("500k");
		zoomSteps.add("50k");
		zoomSteps.add("10k");
		
		basepairLabel = new JLabel("0 - 250M");
		basepairLabel.setForeground(new Color(0,0x66,0));
		basepairLabel.setToolTipText("Basepair Range");
		
		zoomSelector = new JComboBox(zoomSteps);
		zoomSelector.setEditable(true);
		zoomSelector.setToolTipText("Field of View");
				
		ideoPanel = new IdeogramPanel(ideogramDB,chromosome);
				
		// add marker panels
		MemoryGenomicMarkerModel model1 = new MemoryGenomicMarkerModel();
		long delta = 500000;
		Random rnd = new Random(173);
		for( long pos = 0; pos < 100000000; pos+=delta )
		{
			delta = 100000 + rnd.nextInt(500000);
			model1.add(new GenomicMarker(new Interval(pos,pos+delta/2)));
		}

		MemoryGenomicMarkerModel  model2 = new MemoryGenomicMarkerModel();
		for( long pos = 0; pos < 100000000; pos+=delta )
		{
			delta = 100000 + rnd.nextInt(500000);
			model2.add(new GenomicMarker(new Interval(pos,pos+delta/2)));
		}
		
		

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		
		// create the scroll panel
		scrollPanel = new JPanel( new GridBagLayout() );

		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 100;
		
//		MarkerView view;
//		scrollPanel.add( view = new MarkerView(model1),c );
		
		++c.gridx;
		c.weightx = 0;
		c.weighty = 100;		
		scrollPanel.add( ideoPanel,c );
		c.weightx = 0;
		c.weighty = 100;		
		++c.gridx;
//		scrollPanel.add( view = new MarkerView(model2),c );
		
		scrollPanel.setPreferredSize(new Dimension(200,500));
		scrollPanel.setBackground(Color.WHITE);
		scrollPanel.setOpaque(true);
		scrollPanel.setBorder(null);
		
		// scrollPanel.setMinimumSize(new Dimension(50,200));
		//scrollPanel.setMinimumSize(new Dimension(100,500));
		//scrollPanel.setMaximumSize(new Dimension(100,500));

	
		scroller = new JScrollPane(scrollPanel);
		//scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setFocusable(true);
		scroller.setWheelScrollingEnabled(true);
		scroller.getVerticalScrollBar().setUnitIncrement(50);
		scroller.getVerticalScrollBar().addAdjustmentListener(this);
		scroller.setBorder(null);
		
		setLayout(new GridBagLayout());
			
		c.anchor = GridBagConstraints.NORTH;
		c.gridwidth = 1;
		c.gridheight = 1;		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(headerLabel = new JLabel("Chr. "+chromosome),c);
		
		++c.gridy;
		c.weighty = 100;
		c.weightx = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		add(scroller,c);
		

		++c.gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		c.weighty = 0;
		add(basepairLabel,c);
		
		++c.gridy;
		c.anchor = GridBagConstraints.SOUTH;
		c.weighty = 0;
		add(zoomSelector,c);
		
		
		
		addComponentListener( new ComponentListener() {
			public void componentHidden(ComponentEvent arg0)	{	}
			public void componentMoved(ComponentEvent arg0)		{	}
			public void componentResized(ComponentEvent arg0)	{ updateLabels();		}
			public void componentShown(ComponentEvent arg0) 	{ updateLabels();		}
		});
		
		addMouseListener( new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				requestFocusInWindow();
			}
		});
		
		zoomSelector.addActionListener( this );
		addFocusListener(this);
		
		// react on keyboard events
		//KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		
		
		//view.enableInputMethods(false);

		getInputMap().put(
				KeyStroke.getKeyStroke("+"), "zoomIn");

		getActionMap().put("zoomIn", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
            	System.out.println("zoomIn");
            	zoomIn(+1);
		    }
        });
	
		
		//scroller.addPropertyChangeListener(arg0)
	}

	protected void updateLabels()
	{
		if( inUpdate )
			return;
		
		inUpdate = true;
		try {
			Interval view = getViewInterval();
			basepairLabel.setText(formatInterval(view));
			zoomSelector.setSelectedItem(formatBaseCount(view.getLength()));
		}
		finally
		{
			inUpdate = false;
		}
	}
	
	public Interval getViewInterval()
	{
		java.awt.Rectangle rect = scroller.getViewport().getViewRect();
		return new Interval(ideoPanel.convertYCoordToBase(rect.y),
				ideoPanel.convertYCoordToBase(rect.y+rect.height));
	}
	
	/**
	 * Sets the view to the given Interval
	 * @param I
	 */
	public void setViewInterval(Interval I)
	{
		Dimension dim = new Dimension( scrollPanel.getWidth(),
									   ideoPanel.convertBaseToYCoord(ideoPanel.getViewLength()) );
		
		
		System.out.println(I+ ":"+dim);
		
		scrollPanel.setPreferredSize(dim);
		scrollPanel.setSize(dim);
		
		int y1 = ideoPanel.convertBaseToYCoord(I.from);
//			y2 = ideoPanel.convertBaseToYCoord(I.to);
		
		scroller.getViewport().setViewPosition(new Point(0,y1));
		
		updateLabels();
	}
	
	public String formatBaseCount( long count )
	{
		long S;
		String sym = "";
		if( count < 100000 )
		{
			S = 1;
		}
		else
		{
			if( count < 10000000 )
			{
				S = 1000;
				sym = "K";
			}
			else
			{
				S = 1000000;
				sym = "M";
			}
		}
		NumberFormat nf = DecimalFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
	
		return nf.format((float)Math.round((1000*count)/S)/1000.0)+sym;
	}
	
	public String formatInterval( Interval i )
	{
		long L = i.getLength(), S;
		String sym = "";
		if( L < 100000 )
		{
			S = 1;
		}
		else
		{
			if( L < 10000000 )
			{
				S = 1000;
				sym = "K";
			}
			else
			{
				S = 1000000;
				sym = "M";
			}
		}
		NumberFormat nf = DecimalFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
	
		String str;
	
		str = nf.format((float)Math.round((1000*i.from)/S)/1000.0)+sym;
		str += " - ";
		str += nf.format((float)Math.round((1000*i.to)/S)/1000.0)+sym;
		
		return str;
	}

	private void zoomIn(int i)
	{
		int height = scrollPanel.getHeight();
		
		if( i > 0 )
			height *= 1.5;
		else
			height /= 1.5;
		
		Dimension dim = new Dimension( scrollPanel.getWidth(),  height );
		scrollPanel.setPreferredSize(dim);
		scrollPanel.setSize(dim);
		updateLabels();
	}


	public boolean dispatchKeyEvent(KeyEvent event)
	{
		char key = event.getKeyChar();
//		int mod = event.getModifiers();
//		boolean ctrl = (mod & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK,
//			alt =  (mod & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK;

		switch( key )
		{
			case '+':
				zoomIn(+1);
				return true;
			case '-':
				zoomIn(-1);
				return true;
				
		}
		return false;
	}
	


	public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException
	{
		System.out.println("event "+event.getPropertyName());
		updateLabels();
	}

	public void adjustmentValueChanged(AdjustmentEvent event)
	{
		updateLabels();
		
	}
	
	public long parseBasePairs( String str )
	{
		long pos = 0;
		// TODO
		
		return pos;
	}

	public void actionPerformed(ActionEvent event)
	{
		if( inUpdate )
			return;
		String cmd = event.getActionCommand();
		System.out.println("action "+event.getActionCommand());

		if( event.getSource() == zoomSelector )
		{
//			Interval view = getViewInterval();
			
			if( cmd.equalsIgnoreCase("comboBoxChanged") )
			{
				setViewInterval(new Interval(0,ideoPanel.getViewLength()));
				return;
			}
			if( cmd.equalsIgnoreCase("comboBoxEdited") )
			{
				return;
			}
		}
	}

	public void focusGained(FocusEvent arg0)
	{
		headerLabel.setForeground(Color.WHITE);
		headerLabel.setBackground(Color.BLUE);
		headerLabel.setOpaque(true);
		setBorder(BorderFactory.createLineBorder(Color.BLUE));
	}

	public void focusLost(FocusEvent arg0)
	{
		headerLabel.setBackground(getBackground());
		headerLabel.setForeground(getForeground());
		headerLabel.setOpaque(false);
		setBorder(null);
	}
}
