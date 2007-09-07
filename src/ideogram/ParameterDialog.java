package ideogram;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

/*
 * VennMaster//ParameterDialog.java
 * 
 * Created on 01.07.2004
 * 
 */

/**
 * @author muellera
 */
public class ParameterDialog extends JDialog
implements java.awt.event.ActionListener, java.awt.event.KeyListener, PropertyChangeListener//, ItemListener
{
    private static final long serialVersionUID = 1L;
    
    public static final int	INVALID = 0,
							OK_OPTION = 1,
							CANCEL_OPTION = 2;
	private int state;
	

    private JTabbedPane         tabbed_pane;
    
    //////////////////////////////////////////////////////////////////////////
    // Global parameters
    private JPanel             glob_panel;
    private JSpinner 			glob_log2ratio_lower,
    							glob_log2ratio_upper,
    							glob_gsa_lower,
    							glob_gsa_upper,
    							glob_spa_lower,
    							glob_spa_upper,
    							glob_loh_upper,
    							glob_min_length,
    							glob_group_lim;
    
    

	LinkedList					fields;
	
	private boolean	checking;
	private boolean	initialized;
	
	public ParameterDialog(Frame owner)
	{
		super(owner,"IdeogramBrowser Parameters",true);
		
		initialized = false;
		checking = false;
		
		state = INVALID;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
		
		tabbed_pane = new JTabbedPane();
		
//		SpinnerModel floatFormat = new SpinnerNumberModel(0,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.05);
		
		fields = new LinkedList();
		
        JPanel panel = null;

        /////////////////////////////////////////////////////////////////////////////////
        // Filter Value PANEL
        glob_panel = new JPanel();
        panel = glob_panel;
        panel.setLayout(new GridLayout(10,3));
        
        panel.add(new JLabel("Value"));
        panel.add(new JLabel("Lower Bound"));
        panel.add(new JLabel("Upper Bound"));
        
        panel.add(new JLabel("log2ratio"));
        glob_log2ratio_lower = new JSpinner(new SpinnerNumberModel(0,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.05));
        glob_log2ratio_upper = new JSpinner(new SpinnerNumberModel(0,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.05));
        fields.add(glob_log2ratio_lower);
        fields.add(glob_log2ratio_upper);
        panel.add(glob_log2ratio_lower);
        panel.add(glob_log2ratio_upper);
        
        panel.add(new JLabel("gsa"));
        glob_gsa_lower = new JSpinner(new SpinnerNumberModel(0,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.05));
        glob_gsa_upper = new JSpinner(new SpinnerNumberModel(0,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.05));
        fields.add(glob_gsa_lower);
        fields.add(glob_gsa_upper);
        panel.add(glob_gsa_lower);
        panel.add(glob_gsa_upper);
        
        panel.add(new JLabel("spa"));
        glob_spa_lower = new JSpinner(new SpinnerNumberModel(0,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.05));
        glob_spa_upper = new JSpinner(new SpinnerNumberModel(0,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.05));
        fields.add(glob_spa_lower);
        fields.add(glob_spa_upper);
        panel.add(glob_spa_lower);
        panel.add(glob_spa_upper);

        panel.add(new JLabel("loh"));
        glob_loh_upper = new JSpinner(new SpinnerNumberModel(0,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.05));
        fields.add(glob_loh_upper);
        JSpinner glob_loh_lower = new JSpinner(new SpinnerNumberModel(0,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.05));
        glob_loh_lower.setVisible(false);
        panel.add(glob_loh_lower);
        panel.add(glob_loh_upper);
        
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        
        panel.add(new JLabel("Min length"));
        glob_min_length = new JSpinner(new SpinnerNumberModel(0,0,Double.POSITIVE_INFINITY,0.5));
        fields.add(glob_min_length);
        panel.add(glob_min_length);
        panel.add(new JLabel());

        panel.add(new JLabel("Group limit"));
        glob_group_lim = new JSpinner(new SpinnerNumberModel(1,1,99999,1));
        fields.add(glob_group_lim);
        panel.add(glob_group_lim);
        panel.add(new JLabel());
        
        panel = new JPanel(new BorderLayout());
        panel.add(glob_panel, BorderLayout.NORTH);
        tabbed_pane.addTab("Filter Values",panel);
        
		addListeners();
		getContentPane().add(tabbed_pane,BorderLayout.CENTER);
		
		// add buttons
		panel = new JPanel();
		JButton button = new JButton("OK");
		button.addActionListener(this);
		button.setActionCommand("ok"); 
		panel.add(button);
		button = new JButton("Cancel");
		button.addActionListener(this);
		button.setActionCommand("cancel"); 		
		panel.add(button);
		
		getContentPane().add(panel,BorderLayout.SOUTH); 
		
		setSize(350,350);
		
		initialized = true;
	}
	
	/**
	 * 
	 */
	private void addListeners()
	{
		Iterator iter = fields.iterator();
		
		while(iter.hasNext())
		{
			JComponent comp = (JComponent)iter.next();
			comp.addKeyListener(this);
			comp.addPropertyChangeListener(this);
		}
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		if( cmd.equalsIgnoreCase("ok"))
		{
			processOkAction();
			return;
		}
		
		if( cmd.equalsIgnoreCase("cancel"))
		{
			processCancelAction();
			return;
		}
	}
	
	protected void processOkAction()
	{
		// commit all text fields
		Iterator iter = fields.iterator();		
		while(iter.hasNext())
		{
			Object obj = iter.next();
			if(obj instanceof JFormattedTextField)
			{
				JFormattedTextField text = (JFormattedTextField)obj;
				if(text.isEditValid()) 
				{
					try
					{
						text.commitEdit();
					}
					catch(ParseException e)
					{
					}
				}
			}
		}		
		state = OK_OPTION;
		dispose();		
	}
	
	protected void processCancelAction()
	{
		state = CANCEL_OPTION;
		dispose();		
	}
	
	public int getState()
	{
		return state;
	}
	
	public void setParameters(AllParameters parameters)
	{        
        // Filter Value Parameters
		
        glob_log2ratio_lower.setValue(new Double(parameters.log2ratio_lower));
        glob_log2ratio_upper.setValue(new Double(parameters.log2ratio_upper));
        glob_gsa_lower.setValue(new Double(parameters.gsa_lower));
        glob_gsa_upper.setValue(new Double(parameters.gsa_upper));
        glob_spa_lower.setValue(new Double(parameters.spa_lower));
        glob_spa_upper.setValue(new Double(parameters.spa_upper));
        glob_loh_upper.setValue(new Double(parameters.loh_upper));
        glob_min_length.setValue(new Double(parameters.min_length));
        glob_group_lim.setValue(new Integer(parameters.group_lim));
	}
	
	public AllParameters getParameters()
	{
		AllParameters param = new AllParameters();
        
        // Filter Values
        if( glob_log2ratio_lower.getValue() != null )
            param.log2ratio_lower = ((Number)glob_log2ratio_lower.getValue()).floatValue();

        if( glob_log2ratio_upper.getValue() != null )
            param.log2ratio_upper = ((Number)glob_log2ratio_upper.getValue()).floatValue();

        if( glob_gsa_lower.getValue() != null )
            param.gsa_lower = ((Number)glob_gsa_lower.getValue()).floatValue();

        if( glob_gsa_upper.getValue() != null )
            param.gsa_upper = ((Number)glob_gsa_upper.getValue()).floatValue();
        
        if( glob_spa_lower.getValue() != null )
            param.spa_lower = ((Number)glob_spa_lower.getValue()).floatValue();

        if( glob_spa_upper.getValue() != null )
            param.spa_upper = ((Number)glob_spa_upper.getValue()).floatValue();

        if( glob_loh_upper.getValue() != null )
            param.loh_upper = ((Number)glob_loh_upper.getValue()).floatValue();
        
        if( glob_min_length.getValue() != null )
            param.min_length = ((Number)glob_min_length.getValue()).floatValue();
        
        if( glob_group_lim.getValue() != null )
            param.group_lim = ((Number)glob_group_lim.getValue()).intValue();
        
		return param;
	}

	public void keyPressed(KeyEvent e)
	{
		switch( e.getKeyCode() )
		{
			case KeyEvent.VK_ENTER:
				processOkAction();
				break;
				
			case KeyEvent.VK_ESCAPE:
				processCancelAction();
				break;
				
			default:
				// nothing
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e)
	{
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e)
	{
		
		
	}

	public synchronized void check()
	{
		if( ! initialized || checking )       // changed from && ! checking
			return;
        
        if( !isVisible() )
            return;

		checking = true;
        
		AllParameters params = getParameters();
		params.check();
		setParameters(params);
        
		checking = false;
	}


	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		check();
	}


//    public void itemStateChanged(ItemEvent e) 
//    {
//        if( e.getSource() == opt_optimizer )
//        {
//            check(); 
//        }       
//    }
}
