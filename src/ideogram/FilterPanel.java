package ideogram;

import ideogram.input.AffymetrixCntReaderModel.FileVersion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.junit.Assert;

/**
 * Small panel in the IdeogramMainWindow for controlling the parameters.
 * 
 * @author mueller
 *
 */
@SuppressWarnings("serial")
public class FilterPanel extends JPanel implements ActionListener, MouseListener
{
	private LinkedList<ActionListener>				actionListeners;
	private AllParameters							parameters;
	private HashMap<AllParameters.FieldA, Integer>	fieldToIndexA;
	private HashMap<AllParameters.FieldB, Integer>	fieldToIndexB;
	private HashMap<Integer, AllParameters.FieldA>	indexToFieldA;
	private HashMap<Integer, AllParameters.FieldB>	indexToFieldB;	
	
	// GUI Fields
    private JSpinner	lower_spinner;
	private JSpinner 	upper_spinner;
	private JSpinner	max_pValue;
	private JSpinner	group_lim;
	private JSpinner	min_length;
	private JSpinner	consensus_thresh;
	private JComboBox 	fieldComboBoxA, fieldComboBoxB;
	private JCheckBox 	consensus_mode;

	private JComboBox	filterComboBox;
	private JCheckBox	diffMode;
	private JCheckBox	condensed_mode;
	private JCheckBox	showProfileLines_mode;
	private JCheckBox	invertColors;
	
	// Small indicators showing current color for CNState
	private JPanel 		doubleloss;
	private JPanel 		loss;
	private JPanel 		gain;
	private JPanel 		amplification;
	
	// popup menu for color selection
	private JPopupMenu 	popup;
	private JMenuItem 	changeColor;
	private JMenuItem 	restoreColor;
	
	public FilterPanel()
	{
		super(new GridBagLayout());

		parameters = new AllParameters();
				
		actionListeners = new LinkedList<ActionListener>();
					
        // input field
        condensed_mode = new JCheckBox("Condensed Mode");
        condensed_mode.addActionListener(this);
        
        showProfileLines_mode = new JCheckBox("Show Lines");
        showProfileLines_mode .addActionListener(this);
        
        invertColors = new JCheckBox("Inv.");
        invertColors.addActionListener(this);
        
        lower_spinner = new JSpinner(new SpinnerNumberModel(parameters.log2ratio_lower, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.05f));
        upper_spinner = new JSpinner(new SpinnerNumberModel(parameters.log2ratio_upper, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.05f));
        max_pValue = new JSpinner(new SpinnerNumberModel(parameters.pValue_upper, 0.0, 20.0, 1.0));
        max_pValue.setToolTipText("p-Values are logarithmized (base 10) and inverted.");
        
        group_lim = new JSpinner(new SpinnerNumberModel(parameters.group_lim, 1, 99999, 1));
        group_lim.setToolTipText("Minimum number of adjacent SNPs to form a group.");
        min_length = new JSpinner(new SpinnerNumberModel(parameters.min_length, 0, Double.POSITIVE_INFINITY, 0.1));
        min_length.setToolTipText("Minimum length (in million bases) of a contigous group.");
        
        consensus_mode = new JCheckBox("Consensus Mode");
        consensus_mode.addActionListener(this);
        consensus_thresh = new JSpinner(new SpinnerNumberModel(parameters.consensus_threshold, 0, 100, 1));
        consensus_thresh.setToolTipText("Minimum number of samples (min 1) which must be of one category (gain/loss or LOH) to count as consensus region.");
        
        // fieldComboBox
        fieldComboBoxA = new JComboBox();
        fieldToIndexA = new HashMap<AllParameters.FieldA,Integer>();
        indexToFieldA = new HashMap<Integer, AllParameters.FieldA>();
        
        fieldComboBoxA.addItem( AllParameters.FieldA.LOGRATIO.toString() );
        fieldToIndexA.put(AllParameters.FieldA.LOGRATIO, 0);
        indexToFieldA.put(0, AllParameters.FieldA.LOGRATIO);
        
        fieldComboBoxA.addItem( AllParameters.FieldA.LOH.toString() );
        fieldToIndexA.put(AllParameters.FieldA.LOH, 1);
        indexToFieldA.put(1, AllParameters.FieldA.LOH);
        
        fieldComboBoxA.addItem( AllParameters.FieldA.GSA.toString() );
        fieldToIndexA.put(AllParameters.FieldA.GSA, 2);
        indexToFieldA.put(2, AllParameters.FieldA.GSA);
        
        fieldComboBoxA.addItem( AllParameters.FieldA.SPA.toString() );
        fieldToIndexA.put(AllParameters.FieldA.SPA, 3);
        indexToFieldA.put(3, AllParameters.FieldA.SPA);
        
        fieldComboBoxA.setVisible(false);
        fieldComboBoxA.addActionListener(this);
     

        fieldComboBoxB = new JComboBox();
        fieldToIndexB = new HashMap<AllParameters.FieldB,Integer>();
        indexToFieldB = new HashMap<Integer, AllParameters.FieldB>();
        
        fieldComboBoxB.addItem( "CNState" );
        fieldToIndexB.put(AllParameters.FieldB.CNSTATE, 0);
        indexToFieldB.put(0, AllParameters.FieldB.CNSTATE);        
        
        fieldComboBoxB.addItem( "Log2Ratio" );
        fieldToIndexB.put(AllParameters.FieldB.LOGRATIO, 1);
        indexToFieldB.put(1, AllParameters.FieldB.LOGRATIO);

        fieldComboBoxB.addItem( "HmmMedianLog2Ratio" );
        fieldToIndexB.put(AllParameters.FieldB.HMMMEDIANLOG2RATIO,2);        
        indexToFieldB.put(2, AllParameters.FieldB.HMMMEDIANLOG2RATIO);
        
        fieldComboBoxB.addItem( "NegLog10PValue" );
        fieldToIndexB.put(AllParameters.FieldB.NEGLOG10PVALUE,3);
        indexToFieldB.put(3, AllParameters.FieldB.NEGLOG10PVALUE);
          
        fieldComboBoxB.addItem( "LOHProb" );
        fieldToIndexB.put(AllParameters.FieldB.LOHPROB,4);
        indexToFieldB.put(4, AllParameters.FieldB.LOHPROB);
        
        fieldComboBoxB.addActionListener(this);
        

        
        //
        filterComboBox = new JComboBox();
        filterComboBox.addItem( "no filtering" );
        filterComboBox.addItem( "pre-filtering");
        filterComboBox.addItem( "post-filtering");
        filterComboBox.addActionListener(this);
        filterComboBox.setToolTipText("Filtering method before merging or after merging multiple profiles.");
        
        diffMode = new JCheckBox("Diff. Mode");
        diffMode.addActionListener(this);
        diffMode.setToolTipText("Diff. Mode Off: Resolution Enhancement/Diff. Mode On: Reliability Enhancement");

        // ok button
        JButton ok_button = new JButton("OK");
        ok_button.addActionListener(this);
           
        // initialize color indicators
        Dimension d = new Dimension(13,10);
        doubleloss = new JPanel();
        doubleloss.setPreferredSize(d);
        doubleloss.addMouseListener(this);
        loss = new JPanel();
        loss.setPreferredSize(d);
        loss.addMouseListener(this);
        gain = new JPanel();
        gain.setPreferredSize(d);
        gain.addMouseListener(this);
        amplification = new JPanel();
        amplification.setPreferredSize(d);
        amplification.addMouseListener(this);
        
        // setup popup menu
        popup = new JPopupMenu();
        changeColor = new JMenuItem("Change Color");
        changeColor.addActionListener(this);
        restoreColor = new JMenuItem("Restore Default Colors");
        restoreColor.addActionListener(this);
        popup.add(changeColor);
        popup.add(restoreColor);
        
        GridBagConstraints c = new GridBagConstraints();
        
        // setup grid layout
        int y = 0;
        
        c.ipadx = 0;
        c.ipady = 5;
        c.insets = new Insets(0,3,2,3);
        c.weighty = 0;
        c.gridy = y++;

        c.gridx = 0;
        c.weightx = 1.0;
		add(doubleloss,c);
		c.gridx = 1;
		c.weightx = 1.0;
		add(new JLabel("D.Loss"),c);
		c.gridx = 2;
		c.weightx = 1.0;
		add(loss,c);
		c.gridx = 3;
		c.weightx = 1.0;
        add(new JLabel("S.Loss"),c);
        c.gridx = 4;
        c.weightx = 1.0;
        add(invertColors,c);
        c.gridx = 5;
        c.weightx = 1.0;
        add(gain,c);
        c.gridx = 6;
        c.weightx = 1.0;
        add(new JLabel("Gain"),c);   
        c.gridx = 7;
        c.weightx = 1.0;
        add(amplification,c);
        c.gridx = 8;
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(new JLabel("Amp."),c);

        
        c.ipadx = 0;
        c.ipady = 5;
        c.insets = new Insets(0,3,2,3);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.gridx = 0;
        c.weightx = 1;
        c.gridy = y++;
        add(condensed_mode,c);
        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;	//end row
        add(showProfileLines_mode,c);
       
      
        //
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipadx = 0;
        c.ipady = 5;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE; //end row
        c.gridy = y;
        add(new JLabel("Value :"),c);

      
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.ipadx = 0;
        c.ipady = 0;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y;
        add(fieldComboBoxA,c);
        add(fieldComboBoxB,c);
        ++y;
        
        //
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipadx = 0;
        c.ipady = 5;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE; //end row
        c.gridy = y;
        add(new JLabel("Lower bound :"),c);

        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.ipadx = 44;
        c.ipady = 0;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y;   
        add(lower_spinner,c);
        ++y;
        
        //
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipadx = 0;
        c.ipady = 5;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE; //end row
        c.gridy = y;
        add(new JLabel("Upper bound :"),c);
        
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.ipadx = 44;
        c.ipady = 0;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y; 
        add(upper_spinner,c);
        ++y;
        
        //
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipadx = 0;
        c.ipady = 5;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE; //end row
        c.gridy = y;
        add(new JLabel("p-Value:"),c);
        
        
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.ipadx = 44;
        c.ipady = 0;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y; 
        add(max_pValue,c);
        ++y;        
        
        //
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipadx = 0;
        c.ipady = 5;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y;
        add(diffMode,c);
        
        
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.ipadx = 44;
        c.ipady = 0;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y; 
        add(filterComboBox,c);
        ++y;
                
        //
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0,5,2,5);
        c.ipadx = 0;
        c.ipady = 5;
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE; //end row
        c.gridy = y;
        add(new JLabel("Min  length :"),c);

        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.ipadx = 55;
        c.ipady = 0;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y;         
        add(min_length,c);
        ++y;
        
        //
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0,5,2,5);
        c.ipadx = 0;
        c.ipady = 5;
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE; 
        c.gridy = y;
        add(new JLabel("Group limit :"),c);

        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.ipadx = 0;
        c.ipady = 0;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y;         
        add(group_lim,c);
        ++y;
        
        //
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0,5,2,5);
        c.ipadx = 0;
        c.ipady = 5;
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y;
        add(consensus_mode,c);
               
        
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.ipadx = 0;
        c.ipady = 0;
        c.insets = new Insets(0,5,2,5);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y;         
        add(consensus_thresh,c);
        ++y;
        
        //
        c.weighty = 0;
        c.anchor=GridBagConstraints.LINE_END;
        c.ipadx = 33;
        c.ipady = 0;
        c.insets = new Insets(2,5,1,5);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = y;
        add(ok_button,c);
        
        ++y;
        
        setAlignmentY(Component.TOP_ALIGNMENT);
        setMaximumSize(new Dimension(220, 330));
		      
        updateGui();
	}

	protected void fireActionPerformed()
	{
		ActionEvent e = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"ok");
		
		for( ActionListener listener : actionListeners )
		{
			listener.actionPerformed(e);
		}
	}
	
	public void addActionListener( ActionListener listener )
	{
		if( listener != null )
		{
			if( !actionListeners.contains(listener) ) 
				actionListeners.add( listener );
		}
	}
	
	public void removeActionListener( ActionListener listener )
	{
		actionListeners.remove( listener ); 
	}


	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
	
		
        // ok button on filter dialog
        if( cmd.equalsIgnoreCase("ok") )
        {
        	updateFromFields();
        	parameters.check();
        	updateGui();
        	fireActionPerformed();

        	return;
        }

        if (e.getSource() == invertColors ) {
        	parameters.setInvertColors(invertColors.isSelected());
        	updateGui();
    		fireActionPerformed();
        }
        
        if( cmd.equalsIgnoreCase("restore default colors")) {
       		restoreDefaultColors();
           	fireActionPerformed();
        }
        
        // change colors for panels
        if( cmd.equalsIgnoreCase("change color doubleloss")) {
        	Color c = JColorChooser.showDialog(this, "Select color", doubleloss.getBackground());
        	if(c!=null) {
        		doubleloss.setBackground(c);
            	updateFromFields();
            	fireActionPerformed();
        	}
        }
        if( cmd.equalsIgnoreCase("change color loss")) {
        	Color c = JColorChooser.showDialog(this, "Select color", loss.getBackground());
        	if(c!=null) {
        		loss.setBackground(c);
            	updateFromFields();
              	fireActionPerformed();
        	}
        }
        if( cmd.equalsIgnoreCase("change color gain")) {
        	Color c = JColorChooser.showDialog(this, "Select color", gain.getBackground());
        	if(c!=null) {
        		gain.setBackground(c);
            	updateFromFields();
              	fireActionPerformed();
        	}
        }
        if( cmd.equalsIgnoreCase("change color amplification")) {
        	Color c = JColorChooser.showDialog(this, "Select color", amplification.getBackground());
        	if(c!=null) {
        		amplification.setBackground(c);
            	updateFromFields();
              	fireActionPerformed();
        	}
        }
        
        
        if( e.getSource() == condensed_mode || e.getSource() == showProfileLines_mode )
        {
        	updateFromFields();
    		updateGui();
    		
    		fireActionPerformed();
       		return;
        }
        
        // combobox on filter dialog for 1.0 values
        if( e.getSource() == fieldComboBoxA )
        {
    		//String fieldName = (String)fieldComboBoxA.getSelectedItem();
    		//llParameters.Field field = AllParameters.findFieldByName( fieldName );
        	
        	int index = fieldComboBoxA.getSelectedIndex();
        	AllParameters.FieldA field;
        	field = indexToFieldA.get(index);
        	
        	if( field == AllParameters.FieldA.UNKNOWN )
        	{
        		field = AllParameters.FieldA.LOGRATIO;
        	}
        	if( parameters.fieldA != field )
        	{
	    		parameters.fieldA = field;
	    		updateGui();
	    		return;
        	}
        }
        
        // combobox on filter dialog for 1.1 values
        if( e.getSource() == fieldComboBoxB )
        {
    		//String fieldName = (String)fieldComboBoxB.getSelectedItem();
    		//AllParameters.Field field = AllParameters.findFieldByName( fieldName );
        	
        	int index = fieldComboBoxB.getSelectedIndex();
        	AllParameters.FieldB field;
        	field = indexToFieldB.get(index);
        	
        	if( field == AllParameters.FieldB.UNKNOWN )
        	{
        		field = AllParameters.FieldB.CNSTATE;
        	}
        	if( parameters.fieldB != field )
        	{
	    		parameters.fieldB = field;
	    		updateGui();
	    		return;
        	}
        }
         
        if( e.getSource() == consensus_mode )
        {
        	parameters.consensus_mode = consensus_mode.isSelected();
        	updateGui();
        	return;
        }
        
        if( e.getSource() == filterComboBox )
        {
        	int i = filterComboBox.getSelectedIndex();
        	switch( i )
        	{
        		case 0:	// no filtering
        			parameters.preFilter = false;
        			parameters.postFilter = false;
        			break;
        		case 1: // pre-filtering 
        			parameters.preFilter = true;
        			parameters.postFilter = false;        			
        			break;
        		case 2: // post-filtering
        			parameters.preFilter = false;
        			parameters.postFilter = true;        			
        			break;
        	}
        	updateGui();
        	return;
        }
        
	}

	private void updateFromFields()
	{
		try {
			if( min_length.getValue() != null )
				parameters.min_length = Float.parseFloat(min_length.getValue().toString());
		}
		catch( ClassCastException e ) {	
			e.printStackTrace();}

		try {
			if( group_lim.getValue() != null )
				parameters.group_lim = ((Integer)group_lim.getValue()).intValue();
		}
		catch( ClassCastException e ) {
			e.printStackTrace();
		}
		try {
			if( lower_spinner.getValue() != null ) 
				//parameters.setBounds(  ((Float)lower_spinner.getValue()).floatValue(), ((Float)upper_spinner.getValue()).floatValue() );
				parameters.setBounds(  Float.parseFloat(lower_spinner.getValue().toString()), Float.parseFloat(upper_spinner.getValue().toString()) );
		}
		catch( ClassCastException e ) {	
			e.printStackTrace();
		}

		try {
			if( max_pValue.getValue() != null )
				parameters.pValue_upper = ((Float)max_pValue.getValue()).floatValue();
		}
		catch( ClassCastException e ) {
			e.printStackTrace();
		}
		
		try {
			if( consensus_thresh.getValue() != null )
				parameters.consensus_threshold = ((Integer)(consensus_thresh.getValue())).intValue();
		}
		catch( ClassCastException e ) {
			e.printStackTrace();
		}
		
		parameters.consensus_mode = consensus_mode.isSelected();
		parameters.condensed_mode = condensed_mode.isSelected();
		parameters.show_profile_lines = showProfileLines_mode.isSelected();
		parameters.invertColors = invertColors.isSelected();
		
		parameters.setColor(0, doubleloss.getBackground());
		parameters.setColor(1, loss.getBackground());
		parameters.setColor(3, gain.getBackground());
		parameters.setColor(4, amplification.getBackground());
		
	}

	public void updateGui()
	{
		
		parameters.check();
				
		float[] bounds = parameters.getBounds();
		Assert.assertNotNull(bounds);
		
		fieldComboBoxA.setSelectedIndex(fieldToIndexA.get(parameters.fieldA));
		fieldComboBoxB.setSelectedIndex(fieldToIndexB.get(parameters.fieldB));
		
        lower_spinner.setValue( bounds[0] );
    	upper_spinner.setValue( bounds[1] );
		lower_spinner.setVisible(true);
		upper_spinner.setVisible(true);	
		
		switch (parameters.versionLoaded) {
		case V1_0:
			// for LOH mode hide lower_spinner
	    	if( parameters.fieldA == AllParameters.FieldA.LOH ) 
	    		lower_spinner.setVisible(false);		
			fieldComboBoxA.setVisible(true);
			fieldComboBoxB.setVisible(false);
		break;
		default:
	    	// for CNState hide Bounds
	    	if(parameters.fieldB == AllParameters.FieldB.CNSTATE) {
	    		lower_spinner.setVisible(false);
	    		upper_spinner.setVisible(false);
	    	// for LOH modes hide spinners
	    	} else if (parameters.fieldB == AllParameters.FieldB.LOHPROB) {
	    		lower_spinner.setVisible(false);	    		
	    	}
	    		
			fieldComboBoxA.setVisible(false);
			fieldComboBoxB.setVisible(true);
		}
    	    	
    	boolean filtering = parameters.preFilter || parameters.postFilter;
    	
    	if( !parameters.preFilter && !parameters.postFilter )
    		filterComboBox.setSelectedIndex( 0 );
    	if( parameters.preFilter && !parameters.postFilter )
    		filterComboBox.setSelectedIndex( 1 );
    	if( !parameters.preFilter && parameters.postFilter )
    		filterComboBox.setSelectedIndex( 2 );
    	
    	min_length.setValue( parameters.min_length );
    	min_length.setEnabled( filtering );
    	
    	group_lim.setValue( parameters.group_lim );
    	group_lim.setEnabled( filtering );
    	
    	consensus_thresh.setValue( parameters.consensus_threshold );
    	consensus_thresh.setEnabled( parameters.consensus_mode );
    	
    	condensed_mode.setSelected( parameters.condensed_mode );
    	showProfileLines_mode.setSelected( parameters.show_profile_lines );
    	invertColors.setSelected( parameters.invertColors );
    	
    	max_pValue.setValue( parameters.pValue_upper );
    	boolean p = (parameters.fieldA == AllParameters.FieldA.GSA || parameters.fieldA == AllParameters.FieldA.SPA);
    	p = p && (parameters.versionLoaded==FileVersion.V1_0);
    	// hide p-value if field =/= gsa or spa
    	max_pValue.setVisible(p);
    	
		doubleloss.setBackground(parameters.getColor(0, false));
        loss.setBackground(parameters.getColor(1, false));
        gain.setBackground(parameters.getColor(3, false));
        amplification.setBackground(parameters.getColor(4, false));
	}
	
	private void restoreDefaultColors() {
		parameters.restoreDefaultColors();
		updateGui();
	}
	
	public AllParameters getParameters()
	{
		return parameters;
	}

	public void setParameters(AllParameters parameters)
	{	
		this.parameters.assign( parameters );
		updateGui();
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() || e.isControlDown()) {
			JPanel panel = null;
			try {
				panel = (JPanel) e.getComponent();
			} catch (ClassCastException c) {
				c.printStackTrace();
			}
			if(panel!=null) {
				if(panel==doubleloss) changeColor.setActionCommand("change color doubleloss");
				if(panel==loss) changeColor.setActionCommand("change color loss");
				if(panel==gain) changeColor.setActionCommand("change color gain");
				if(panel==amplification) changeColor.setActionCommand("change color amplification");
				popup.show(panel,e.getX(),e.getY());
			}
		}
	}
}
