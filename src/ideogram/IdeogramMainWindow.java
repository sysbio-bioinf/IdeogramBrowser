/*
 			* IdeogramBrowser/ideogram/IdeogramMainWindow.java
 * 
 * Created on 09.06.2004
 * 
 */
package ideogram;


import ideogram.db.GeneDB;
import ideogram.db.IdeogramDB;
import ideogram.input.AffymetrixCntReaderModel;
import ideogram.input.CommonFileFilter;
import ideogram.input.CopyNumberMerger;
import ideogram.input.CopyNumberTransformer;
import ideogram.input.DataSlot;
import ideogram.input.RResultReaderModel;
import ideogram.input.RResultTransformer;
import ideogram.input.TabbedTextReaderModel;
import ideogram.input.AffymetrixCntReaderModel.FileVersion;
import ideogram.r.RController;
import ideogram.r.exceptions.JRIVersionException;
import ideogram.r.exceptions.RException;
import ideogram.r.gui.RGuiWindow;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import junit.framework.Assert;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import util.FileFormatException;
import util.MathUtility;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.thoughtworks.xstream.XStream;

/**
 * Shows ideograms of all 24 chromosoms.
 * 
 * @author muellera
 */
public class IdeogramMainWindow extends javax.swing.JFrame
implements ActionListener, ComponentListener, ChangeListener
{
	private static final long	serialVersionUID = 1L;
	private static final int 	APP_WIDTH = 800;
	private static final int 	APP_HEIGHT = 600;
    
    public static final int 	VERSION_MAJOR = 0,
    							VERSION_MINOR = 20,
    							VERSION_SUB   = 3;
    public static final String 	VERSION_DATE = "2007-09-07"; 

    public static final int 	NUM_OF_CHROMOSOMES = 24;
	private static final String OPTIONFILE_NAME = "IdeogramBrowserOptions.xml";

	protected IdeogramDB 		db;
	protected GeneDB 			gene_db;
	protected IdeogramView[]	ideograms;
	protected LinkedList 		markers;
	
	protected int 				currentChromosome;
	protected boolean 			multiMode;
	
	// Window
	protected JTextPane			infoPanel;
	protected JTextPane			headerPanel;
	protected JScrollPane 		scroller;
    protected JSplitPane   		splitter;
    private JPanel 				chr_panel;
    private JPanel				boxer;
    private JTabbedPane			tabs;
    private FilterPanel			filterPanel;
	
	// MENUS
    private JCheckBoxMenuItem	showMarkersMenu;
    private JCheckBoxMenuItem   showLabelsMenu;
    private ButtonGroup			zoomButtons;
    private boolean				dependentWindow;
	private JFileChooser 		dialog;
	
	public JPopupMenu 			popmenu;
	public JMenuItem			removeMarkerItem;
	public JMenuItem			mergeMarkerItem;

	private Vector<DataSlot>	slots;
	
	public AllParameters		parameters;
	private PageFormat			pageFormat;
	
	// serveral often instantiated variables made global
	private IColorMapper		mapper;
	private TableColorMapper	map;
	private MarkerCollection[]	mc;
	private CopyNumberMerger	merger;
	private AffymetrixCntReaderModel data;
	private CopyNumberTransformer cn;
	private RResultTransformer resultTransformer;
	private DataSlot			s;
	private File				lohfile;
	private File				cntfile;
	
	// true only, if no files are imported
	private boolean 			versionModeChangeable;
	
	private class FileCompatibilityException extends Exception {
		public FileCompatibilityException(String string) {
			super(string);
		}
		private static final long serialVersionUID = 1L;
	}
	
	
	
	public IdeogramMainWindow()
	{	
		super("Ideogram Main Application");
		
		// File Version can be changed only if no markers are loaded
		versionModeChangeable = true;
		
		data = new AffymetrixCntReaderModel();
		
		pageFormat = PrinterJob.getPrinterJob().defaultPage();
		
		slots = new Vector<DataSlot>();
        
		// use correct format for double in MainWindow
		Locale.setDefault(new Locale("us"));
		
		// dataModels = new ArrayList<ICopyNumberModel>();
        dependentWindow = false;
        
        addComponentListener(this);
        
        // data base
        db = new IdeogramDB();
		gene_db = new GeneDB();
		
        multiMode = true;
		
		addWindowListener(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent event)
			 	{
					event.getWindow().setVisible(false);
					event.getWindow().dispose();
                    if( ! dependentWindow )
                        System.exit(0);
				 }
			});
		
		// center window
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(screensize.width/2-APP_WIDTH/2,screensize.height/2-APP_HEIGHT/2, APP_WIDTH, APP_HEIGHT);
		
		// add one panel per chromosome
		chr_panel = new JPanel();
		chr_panel.setBackground(Color.WHITE);
		chr_panel.setOpaque(true);
		chr_panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		
        // load options or build new optionset
        parameters = new AllParameters();
        try
        {
        	parameters.assign( loadStandardOptionFile() );
        }
        catch (FileNotFoundException e) 
        {
        	//e.printStackTrace();
        	MainApp.getLogger().throwing(getClass().getName(),"IdeogramMainWindow", e);
        	//System.out.println("Using default options, option file '"+OPTIONFILE_NAME+"' not found.");
        	System.out.println("Using default options.");
        }
        catch (Exception e) 
        {
        	e.printStackTrace();
        	MainApp.getLogger().throwing(getClass().getName(),"IdeogramMainWindow", e);
        }
		
		ideograms = new IdeogramView[NUM_OF_CHROMOSOMES];
		int n2 = ideograms.length/2;
		for(int i=0; i<ideograms.length; ++i)
		{
			ideograms[i] = new IdeogramView();
            ideograms[i].setDataModel(slots);
			ideograms[i].setChromosome(i+1);
			ideograms[i].setIdeogramDB(db);
			ideograms[i].setGeneDB(gene_db);
            ideograms[i].setMainWindow(this);
            ideograms[i].addSelectionChangedListener(this);

			c.gridx = i % n2;
			c.gridy = i / n2;            
			chr_panel.add(ideograms[i],c);
		}

        // setup scroller
		scroller =
			new JScrollPane(chr_panel,
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroller.setFocusable(false);
        
        // disable all key strokes
        scroller.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,null);
        
		// setup info panel
		infoPanel = new JTextPane();
        infoPanel.setContentType("text/html");
        infoPanel.setEditable(false);
        infoPanel.setEnabled(true);
        // click on gene name opens browser window linked to ncbi.org
        infoPanel.addHyperlinkListener(new HyperlinkListener(){
        	public void hyperlinkUpdate(HyperlinkEvent e) {
        		if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED)		
        			BrowserControl.displayURL( e.getURL().toString() );
        		}
        	}
        );
        
        // setup header info panel
        headerPanel = new JTextPane();
        headerPanel.setContentType("text/html");
        headerPanel.setEditable(false);
        headerPanel.setEnabled(true);
        
        // set up ScrollPanes
        JScrollPane infoScroller = new JScrollPane(infoPanel);
        infoScroller.setAlignmentY(Component.TOP_ALIGNMENT);
        
        JScrollPane headerScroller = new JScrollPane(headerPanel);
        headerScroller.setAlignmentY(Component.TOP_ALIGNMENT);
        
        // setup tabbed pane
        tabs = new JTabbedPane();
        tabs.addTab("Info",infoScroller);
        tabs.addTab("Header",headerScroller);
        tabs.setAlignmentY(Component.TOP_ALIGNMENT);
             
        // setup filter dialog
        filterPanel = new FilterPanel();
        filterPanel.setParameters( parameters );
        filterPanel.addActionListener( this );
        filterPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        
        // setup boxer
		boxer = new JPanel();
		boxer.setLayout(new BoxLayout(boxer,BoxLayout.LINE_AXIS));
		boxer.add(filterPanel);
		boxer.add(tabs);

		// setup splitter
		splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scroller,boxer);
		getContentPane().add(splitter);
		
		// load icon
		Image image = getImageResource("images/icon.png");
		if(image != null)
		{
			setIconImage(image);
		}
		
		// add menu bar
		JMenuBar menubar = new JMenuBar();
		menubar.add(createFileMenu());
		menubar.add(createViewMenu());
		menubar.add(createOptionsMenu());
		// [Ferdinand Hofherr]
		menubar.add(createRMenu());
		menubar.add(createHelpMenu());
		setJMenuBar(menubar);
		
		// add File Chooser restricted on *.cnt and *.txt files
		dialog = new JFileChooser();
        //CommonFileFilter filter1  = new CommonFileFilter("Affymetrix Copy Number Analysis Tool File (*.cn.cnt, *.loh.cnt)");
        CommonFileFilter filter2 = new CommonFileFilter("Affymetrix Copy Number Analysis Tool File (*.cnt)");
        CommonFileFilter filter3 = new CommonFileFilter("Tab Delimited Text File (*.txt)");
        CommonFileFilter filter4 = new CommonFileFilter("RResult file (*.RResult)");
        dialog.setAcceptAllFileFilterUsed(false);
        //filter1.addExtension("cn.cnt");
        //filter1.addExtension("loh.cnt");
        filter2.addExtension("cnt");
        filter3.addExtension("txt");
        filter4.addExtension("RResult");

        dialog.addChoosableFileFilter(filter3);
        //dialog.addChoosableFileFilter(filter1);
        dialog.addChoosableFileFilter(filter2);
        dialog.addChoosableFileFilter(filter4);
        dialog.setFileFilter(filter2);
                
        // setup popup menu
        popmenu= new JPopupMenu();
        JMenuItem menuitem= new JMenuItem("Zoom In");
        menuitem.addActionListener(this);
        popmenu.add(menuitem);
        menuitem= new JMenuItem("Zoom Out");
        menuitem.addActionListener(this);
        popmenu.add(menuitem);
        menuitem= new JMenuItem("Show All");
        menuitem.addActionListener(this);
        popmenu.add(menuitem);
        popmenu.addSeparator();
//        JMenuItem menuitem3= new JMenuItem("Select Next Marker");
//        menuitem3.addActionListener(this);
//        popmen.add(menuitem3);
//        JMenuItem menuitem4= new JMenuItem("Select Previous Marker");
//        menuitem4.addActionListener(this);
//        popmen.add(menuitem4);
//        popmen.addSeparator();
        removeMarkerItem = new JMenuItem("Remove Marker File");
        removeMarkerItem.addActionListener(this);
        popmenu.add(removeMarkerItem);
        mergeMarkerItem = new JMenuItem("Merge Marker File");
        mergeMarkerItem.addActionListener(this);
        popmenu.add(mergeMarkerItem);
           
        // display current data in info panel
        updateInfoPanels(ideograms[currentChromosome]);

        pack();
        setOptimumSize();
	}
    
    public void setDependentWindow( boolean dep )
    {
        dependentWindow = dep;
    }
	
	private JMenu createFileMenu()
	{
		// MENU: FILE
		JMenu menu = new JMenu("File");
		menu.setMnemonic('F');
		
		JMenuItem item;

        // new
        item = new JMenuItem("New",'N');
        item.addActionListener(this);
        menu.add(item);
        
        // load markers
        item = new JMenuItem("Load Markers",'L');
        item.addActionListener(this);
        menu.add(item);
        
		// save
		item = new JMenuItem("Save",'S');
		item.addActionListener(this);
		menu.add(item);
		
		item = new JMenuItem("Export Markerlist");
		item.addActionListener(this);
		menu.add(item);
		
		// print
		item = new JMenuItem("Printer Settings");
		item.addActionListener(this);
		menu.add(item);
		
		item = new JMenuItem("Print single chromosome");
		item.addActionListener(this);
		menu.add(item);
		
		item = new JMenuItem("Print all chromosomes");
		item.addActionListener(this);
		menu.add(item);
		
		// exit		
		item = new JMenuItem("Exit",'X');
		item.addActionListener(this);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,KeyEvent.ALT_MASK));
		menu.add(item);
		
		return menu;
	}
	
	private JMenu createViewMenu()
	{
        JMenuItem mitem;
        
		// MENU: View
		JMenu menu = new JMenu("View");
		menu.setMnemonic('V');
        		
        mitem = new JMenuItem("Reset view");
        mitem.setMnemonic('R');
        mitem.addActionListener(this);
        menu.add(mitem);
        
		
		showMarkersMenu = new JCheckBoxMenuItem("Show markers");
		showMarkersMenu.addActionListener(this);
		menu.add(showMarkersMenu );
        showMarkersMenu.setSelected( getShowMarkers() );
        
        showLabelsMenu = new JCheckBoxMenuItem("Show labels");
        showLabelsMenu.addActionListener(this);
        menu.add(showLabelsMenu );
        showLabelsMenu.setSelected( getShowLabels() );
              
        // Zoom Menu
        JMenu  sub = new JMenu("Zoom");
        zoomButtons = new ButtonGroup();
        JRadioButtonMenuItem but;
        
        but = new JRadioButtonMenuItem("50%");
        but.setActionCommand("zoom:50");
        but.addActionListener(this);
        zoomButtons.add(but);
        sub.add(but);
        
        but = new JRadioButtonMenuItem("75%");
        but.setActionCommand("zoom:75");
        but.addActionListener(this);        
        zoomButtons.add(but);
        sub.add(but);
        
        but = new JRadioButtonMenuItem("100%");
        but.setActionCommand("zoom:100");
        but.addActionListener(this);        
        zoomButtons.add(but);
        sub.add(but);
        zoomButtons.setSelected(but.getModel(),true);
        
        but = new JRadioButtonMenuItem("150%");
        but.setActionCommand("zoom:150");
        but.addActionListener(this);        
        zoomButtons.add(but);
        sub.add(but);
        
        but = new JRadioButtonMenuItem("200%");
        but.setActionCommand("zoom:200");
        but.addActionListener(this);        
        zoomButtons.add(but);
        sub.add(but);
        
        but = new JRadioButtonMenuItem("300%");
        but.setActionCommand("zoom:300");
        but.addActionListener(this);        
        zoomButtons.add(but);
        sub.add(but);
        
        menu.add( sub );
        		
		return menu;
	}
	
	private JMenu createOptionsMenu()
	{
		// MENU: FILE
		JMenu menu = new JMenu("Options");
		menu.setMnemonic('O');
				
        JMenuItem item;
        item = new JMenuItem("Save Options",'s');
        item.addActionListener(this);
        menu.add(item);
        
        item = new JMenuItem("Load Options",'l');
        item.addActionListener(this);
        menu.add(item);
        
		return menu;
	}
	
	/*
	 * [Ferdinand Hofherr]
	 */
	private JMenu createRMenu() {
	    //MENU: R
	    JMenu menu = new JMenu("R");
	    menu.setMnemonic('R');
	    
	    menu.addActionListener(this);
	    JMenuItem item = new JMenuItem("Connect to R");
	    item.addActionListener(this);
	    menu.add(item);
	    
	    return menu;
	}
	
	private JMenu createHelpMenu()
	{
		// MENU: FILE
		JMenu menu = new JMenu("Help");
		menu.setMnemonic('H');
		
		JMenuItem menuItemOptions = new JMenuItem("About",'A');
		menuItemOptions.addActionListener(this);
		menu.add(menuItemOptions );

		return menu;
	}
	
	public void showErrorDialog( String message )
	{
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE );
	}
	
	public void showInfoDialog( String message )
	{
		JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE );
	}
	
	public void actionPerformed(ActionEvent event)
	{
        if( ! isVisible() )
            return;
        
		String cmd = event.getActionCommand();
		
        // file menu
        if( cmd.equalsIgnoreCase("new"))
        {
            clear();
            updateInfoPanels(ideograms[0]);
        	// if no more markers are present, version can be changed
        	versionModeChangeable = true;
            return;
        }
        
        if( cmd.equalsIgnoreCase("load markers"))
        {
            actionLoadMarkers();
            return;
        }
        
		if( cmd.equalsIgnoreCase("save"))
		{
			actionFileSave();
			return;
		}
		
		if( cmd.equalsIgnoreCase("printer settings"))
		{
			actionFilePrinterSettings();
			return;
		}
		
		if( cmd.equalsIgnoreCase("print single chromosome"))
		{
			actionFilePrintSingle();
			return;
		}
		
		if( cmd.equalsIgnoreCase("Print all chromosomes"))
		{
			actionFilePrintAll();
			return;
		}
		
		/*if( cmd.equalsIgnoreCase("Print file header"))
		{
			actionFilePrintHeader();
			return;
		}*/	
		

		if( cmd.equalsIgnoreCase("exit") )
		{
			actionFileExit();
			return;
		}
				
        if( cmd.equalsIgnoreCase("reset view" ))
        {
            resetView();
            
            return;
        }
        
		if( cmd.equalsIgnoreCase("show markers" ))
		{
			setShowMarkers( ! getShowMarkers() );
			showMarkersMenu.setSelected( getShowMarkers() );
			return;
		}
        
        if( cmd.equalsIgnoreCase("show labels" ))
        {
            setShowLabels( ! getShowLabels() );
            showLabelsMenu.setSelected( getShowLabels() );
            return;
        }
        
        if( cmd.startsWith("zoom:") )
        { // zoom command
            int     zoom = Integer.parseInt(cmd.substring(5));
            if( zoom > 0 )
            {
                setZoom( zoom );
            }
            return;
        }

        if( cmd.equalsIgnoreCase("load options" ))
        {
        	actionOptionsLoadOptions();
            return;
        }
        
        if( cmd.equalsIgnoreCase("save options" ))
        {
        	actionOptionsSaveOptions();
            return;
        }
        
        // from popupmenu
        if( cmd.equalsIgnoreCase("zoom in"))
        {
        	((IdeogramView)((JPopupMenu)((JMenuItem)event.getSource()).getParent()).getInvoker()).zoom(-1);
        	return;
        }
        
        if( cmd.equalsIgnoreCase("zoom out"))
        {
        	((IdeogramView)((JPopupMenu)((JMenuItem)event.getSource()).getParent()).getInvoker()).zoom(1);
        	return;
        }

        if( cmd.equalsIgnoreCase("show all"))
        {
        	((IdeogramView)((JPopupMenu)((JMenuItem)event.getSource()).getParent()).getInvoker()).resetView();
        	return;
        }
       
        if( cmd.equalsIgnoreCase("remove marker file"))
        {
        	int idx = ((IdeogramView)((JPopupMenu)((JMenuItem)event.getSource()).getParent()).getInvoker()).getSelectedSampleIndex();
        	if( idx>=0 && idx < slots.size() )
        	{
        		slots.get(idx).clear();
        		slots.remove(idx);
	        	infoPanel.setText("<html><body>\n </body></html>\n");
	        	
	        	// if no more markers are present, version can be changed
	        	if (slots.size()==0) versionModeChangeable = true;
	        	update();
        	}
        	return;
        }

        if( cmd.equalsIgnoreCase("merge marker file"))
        {
        	int idx = ((IdeogramView)((JPopupMenu)((JMenuItem)event.getSource()).getParent()).getInvoker()).getSelectedSampleIndex();
        	
        	dialog.setSelectedFiles(null);
        	dialog.setMultiSelectionEnabled(false);
        	dialog.setSelectedFile(null);
            if( dialog.showOpenDialog(this) != JFileChooser.APPROVE_OPTION )
                return;
            
            tryToLoad(dialog.getSelectedFile(), idx);

        	return;
        }
        
        if( cmd.equalsIgnoreCase("about") )
        {
        	InfoDialog info = new InfoDialog(this);
        	info.setVisible(true);
        	return;
        }
        
        // filter panel
        if( cmd.equalsIgnoreCase("ok") )
        {
        	parameters.assign( filterPanel.getParameters() );
        	setCondensedMode( parameters.condensed_mode );
        	setShowProfileLines( parameters.show_profile_lines );
        	update();
        	validate();
        	return;
        }
        
        if (cmd.equalsIgnoreCase("export markerlist")) {
        	exportMarkerList();
        	return;
        }
        
        /*
         * [Ferdinand Hofherr]: R Menu
         */
        if (cmd.equalsIgnoreCase("Connect to R")) {
            try {
                RController.checkVersion();
                new RGuiWindow();
                RController.getInstance().startEngine();
            } catch (JRIVersionException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            } catch (RException e) {
                e.printStackTrace();
            }
            return;
        }

        MainApp.getLogger().logp(Level.INFO, getClass().getName(), "actionPerformed", "unhandled action: "+cmd);
	}

	private void exportMarkerList() {
    	if(currentChromosome<1) {
    		showErrorDialog("There is no sample selected.\nPlease select a sample first.");
    	} else if (ideograms[currentChromosome-1].getSelectedSampleIndex()<0) {
    		showErrorDialog("There is no sample selected.\nPlease select a sample first.");
    	} else {
    		
    		JFileChooser dialog = new JFileChooser();
    		CommonFileFilter filter;
    		
    		dialog.setAcceptAllFileFilterUsed(false);		
    		filter = new CommonFileFilter("Colon Seperated Values (*.csv)");
    		filter.addExtension("csv");
    		dialog.addChoosableFileFilter(filter);

    		if( dialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
    		{
    			File file = dialog.getSelectedFile();
     	        if( file.getName().lastIndexOf(".") == -1 )
    	        {
    				file = new File( file.getAbsolutePath() + ".csv" ); 
    			}
    			
    			if( file.exists() )
    			{
    				// overwrite file??
    				int res = JOptionPane.showConfirmDialog(this,"File '"+ file.getName().toString() +"'already exists! Do you want to replace the existing file?");
    				if( res != JOptionPane.OK_OPTION )
    					return;
    			}
    			
    			FileWriter writer;
    			String path = file.getAbsolutePath();
    			try {
    				writer = new FileWriter(path);
    			}
    			catch (IOException e) {
    				showErrorDialog("Cannot open file\r\n"+path);
					e.printStackTrace();
					return;
				}
    			if( writer == null ) {
    				showErrorDialog("Cannot open file\r\n"+path);
    				return;
    			}

    			int sample = ideograms[currentChromosome-1].getSelectedSampleIndex();
    			
				try {
					
					// write to file!
					writer.write("IdeogramBrowser Markerset\n");
				
					if(sample>=slots.size()) {
						// consensus mode
						writer.write("Consensus Mode\n");
					} else {
						DataSlot sl = slots.get(sample);
						if(sl.getChipType()!=null) {
							writer.write("Original Filename(s):\n");
							for(String str : sl.getFileName()) {
								writer.write(str+"\n");
							}
						}
				
						if(sl.getChipType()!=null) {
							writer.write("Chiptype: ");
							for(String str : sl.getChipType()) {
								writer.write(str+" ");
							}
							writer.write("\n");
						}
					}
					// filter settings
					writer.write("Filter Settings: ");
					writer.write(parameters.getFieldName());
					if(parameters.getOptions()!=null)
						writer.write(" "+parameters.getOptions()+"\n");
					else writer.write("\n");
					
				} catch (IOException e) {
					showErrorDialog("I/O Exception while writing to\r\n"+path);
					e.printStackTrace();
				}
				String val;
				try {
					writer.write("\nChromosome;ProbeSet;From;To;Value\n");
					for(int i=0;i<NUM_OF_CHROMOSOMES;i++) {

						String chromosome = ""+(i+1);
						if(chromosome.equals("23")) chromosome="X";
						if(chromosome.equals("24")) chromosome="Y";
    					MarkerCollection M = ideograms[i].getMarkers(sample);
    					for( Marker marker : M.markers ) {
    						String infoobj = marker.getInfo().toString();
    						String [] infos = infoobj.split("[\n]", 2);
    						switch(marker.value) {
    							case 0: val = "double loss"; break;
    							case 1: val = "loss"; break;
    							case 2: val = "normal"; break;
    							case 3: val = "gain"; break;
    							case 4: val = "amplification"; break;
    							default: val = "unknown";
    						}
    						if(parameters.getFieldName().equals("LOHPROB")) {			// TODO change "loh" to loh-value
    							writer.write(chromosome+";"+infos[0]+";"+marker.interval.from+";"+marker.interval.to+";loh\n");
    						} else {
    							writer.write(chromosome+";"+infos[0]+";"+marker.interval.from+";"+marker.interval.to+";"+val+"\n");
    						}
    					}
					}
					//showInfoDialog("Export complete.");
				} catch (IOException e) {
					showErrorDialog("I/O Exception while writing to\r\n"+path);
					e.printStackTrace();
				}
    			
    			try {
					writer.close();
				} catch (IOException e) {
					showErrorDialog("Could not close file\r\n+"+path);
					e.printStackTrace();
				}
    		}
    	}		
	}

	private void tryToLoad(File file, int target) {
		if (!file.exists()) {
			showErrorDialog("File '" + file.getName().toString()
					+ "' does not exist!");
			return;
		}
		if (!file.canRead()) {
			showErrorDialog("File '" + file.getName().toString()
					+ "' cannot be opened since it has no read permissions!");
			return;
		}

		String type = "";

		try {
			waitCursor();
			if (file.getName().endsWith(".cn.cnt")) {
				//parameters.fieldB = FieldB.CNSTATE;
				// does matching loh file exist?
				lohfile = new File(file.getAbsolutePath().subSequence(0,
						file.getAbsolutePath().length() - 7)
						+ ".loh.cnt");
				if (lohfile.exists()) {
					type = "CN.CNT + LOH.CNT";
					importCNTFile(file, lohfile, target);
				} 
				else {
					type = "CNT";
					importCNTFile(file, target);
				}
			} 
			else if (file.getName().endsWith(".loh.cnt")) {
				//parameters.fieldB = FieldB.LOHPROB;
				// does matching cnt file exist?
				cntfile = new File(file.getAbsolutePath().subSequence(0,
						file.getAbsolutePath().length() - 8)
						+ ".cn.cnt");
				if (cntfile.exists()) {
					type = "CN.CNT + LOH.CNT";
					importCNTFile(cntfile, file, target);
				} else {
					type = "CNT";
					importCNTFile(file, target);
				}
			} 
			else if (file.getName().endsWith(".cnt")) {
				type = "CNT";
				importCNTFile(file, target);
			}
			// TXT File format.
			else if (file.getName().endsWith(".txt") && target == -1) {
				type = "TXT";
				importTXTFile(file);
			} 
			else if (file.getName().endsWith(".txt") && target != -1){
				showInfoDialog("Action not possible for TXT files");
				return;
			}
			//RResult file format
			else if (file.getName().endsWith(".RResult")) {
			    type = "RResult";
			    importRResultFile(file, target);
			}
			// EXTEND HERE FOR NEW FILE FORMATS:
			// TODO
		}
		catch (FileFormatException e) {
			MainApp.getLogger().throwing(getClass().getName(), "actionPerformed", e);
			showErrorDialog("Wrong file format of " + type + " file '"
					+ file.getName() + "'\r\n" + e.getMessage());
			return;
		} 
		catch (IOException e) {
			MainApp.getLogger().throwing(getClass().getName(), "actionPerformed", e);
			showErrorDialog("I/O error while importing " + type + " file '"
					+ file.getName() + "'\r\n" + e.getMessage());
			return;
		} 
		catch (Exception e) {
			e.printStackTrace();
			MainApp.getLogger().throwing(getClass().getName(), "actionPerformed", e);
			showErrorDialog("Exception while importing " + type + "file '"
					+ file.getName() + "'\r\n" + e.getMessage());
			return;
		} 
		finally {
			normalCursor();
		}

		update();
		// updateInfoPanels( ideograms[target] );
		repaint();

	}

	public void resetView()
	{
		for(int i=0; i<ideograms.length; ++i )
		{
		    if(ideograms[i] != null )
		        ideograms[i].resetView();
		}
	}
	
	private void setCondensedMode(boolean condensed_mode)
	{
		for( int i=0; i<ideograms.length; ++i )
			ideograms[i].setCondensedMode( condensed_mode );
	}
	
	private void setShowProfileLines(boolean profile_lines)
	{
		for( int i=0; i<ideograms.length; ++i )
			ideograms[i].setShowProfileLines( profile_lines );
	}
	
	/**
	 * Updates all marker collections
	 *
	 */
	private void update	()
	{
		waitCursor();
		try {
			collectGarbage();
			rebuildMarkerCollection();
			updateInfoPanels(ideograms[currentChromosome]);
		}
		finally {
			normalCursor();
		}
	}
	
	private void rebuildMarkerCollection()
	{
		if( parameters.fieldA == AllParameters.FieldA.LOH  && parameters.versionLoaded==FileVersion.V1_0 || (parameters.fieldB == AllParameters.FieldB.LOHPROB)&& parameters.versionLoaded!=FileVersion.V1_0)
		{	// choose a different color scheme for the LOH mode
			map = new TableColorMapper();
			
			map.getColorTable().add( new Color(0.5f,0.5f,1.0f)  );
			map.getColorTable().add( Color.BLUE );
			map.getColorTable().add( Color.MAGENTA );
			mapper = map;
		} else
		{
	        mapper = new DefaultColorMapper();
		}
		
		clearMarkers();
		
    	for(int j=0; j<slots.size();++j)
    	{
    		//mc=null;
     		mc = slots.get(j).convertToMarkerCollection(mapper);
     		
            Assert.assertNotNull(mc);
            Assert.assertEquals(mc.length,ideograms.length);	// one collection per chromosome
            for( int i=0; i<mc.length; ++i )
            {
            	ideograms[i].setDataModel( slots );
                Assert.assertNotNull(mc[i]);
                ideograms[i].addMarkers( mc[i] );
            }
    	}
    	
    	// consensus region
    	if( parameters.consensus_mode )
    	{
        	merger = new CopyNumberMerger();
        	merger.setDifferenceMode( true );
        	int threshold = MathUtility.restrict(parameters.consensus_threshold, 1, slots.size());
        	merger.setThreshold(threshold);
        	parameters.consensus_threshold = threshold;
        	filterPanel.setParameters(parameters);
        	filterPanel.updateGui();
        	
        	for( DataSlot slot : slots )
        	{
        		merger.add(slot);
        	}
        	
     		mc = merger.convertToMarkerCollection(mapper);
     		
     		Assert.assertNotNull(mc);
            Assert.assertEquals(mc.length,ideograms.length);	// one collection per chromosome
            LinkedList<String> l = new LinkedList<String>();
            l.add("Consensus Region");
            for( int i=0; i<mc.length; ++i )
            {
                Assert.assertNotNull(mc[i]);
    			mc[i].setName(l);
    			mc[i].setColor(Color.MAGENTA);
                
                ideograms[i].addMarkers( mc[i] );
            	ideograms[i].setConsensusMode(true);
            }
            

    	}
    	else
    	{
    		for( int i=0; i<ideograms.length; ++i ) {
    			ideograms[i].setConsensusMode(false);
    		}
    	}
    	validate();
	}

	private void setZoom(int zoom) 
    {
		if( zoom < 20 )
			zoom = 20;
		if( zoom > 500 )
			zoom = 500;
		
        double z = (double)zoom/100;
        
        for( int i=0; i<ideograms.length; ++i )
        {
        	ideograms[i].setZoomFactor( z,z );
        }
        validate();
    }
	
    protected void actionOptionsLoadOptions()
    {
        JFileChooser dialog = new JFileChooser();
        CommonFileFilter filter;
        
        dialog.setAcceptAllFileFilterUsed(false);
        
        filter = new CommonFileFilter("IdeogramBrowser Options (.xml)");
        filter.addExtension("xml");
        
        dialog.addChoosableFileFilter(filter);
                        
        if( dialog.showOpenDialog(this) != JFileChooser.APPROVE_OPTION )
        {
            return;
        }
        
        File file = dialog.getSelectedFile(); 
        if( ! file.exists() )
        {
        	showErrorDialog("The chosen file does not exist!");
            return;
        }
        
        if( ! file.canRead() )
        {
        	showErrorDialog("The file '"+file.getPath()+"' has no read privileges!");
            return;
        }
                    
        // open input stream
        FileReader fs = null;

        try 
        {
            fs = new FileReader(file);
            if( fs == null )
            {
            	showErrorDialog( "Cannot open file\r\n"+file.getPath() );
                return;
            }
            
            // ObjectInputStream os = new ObjectInputStream(fs);
            //XMLDecoder os = new XMLDecoder(new BufferedInputStream(fs));
            
            
            AllParameters param = null;

            XStream xstream = new XStream();
            ObjectInputStream os = xstream.createObjectInputStream(fs);
            
            param = (AllParameters)os.readObject();
            
            os.close();
            fs.close();
            
            
            if( param != null )
            {
                parameters.assign( param );
        		filterPanel.setParameters( param );
            }
            else
            {
            	showErrorDialog( "Cannot read configuration from file\r\n"+file.getPath() );
            }

            os.close();
            fs.close();
        }
        catch(FileNotFoundException e)
        {
        	showErrorDialog( "Cannot open configuration file\r\n"+file.getPath()+"\r\n"+e.getMessage() );
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            showErrorDialog( "I/O error while opening configuration file\r\n"+file.getPath()+"\r\n"+e.getMessage() );
        }
        catch (ClassNotFoundException e) 
        {
            e.printStackTrace();
            showErrorDialog("Wrong format of configuration file \r\n"+file.getPath() +"\r\n"+e.getMessage() );
        }
        update();
    }
    
	
    protected void actionOptionsSaveOptions()
    {
        JFileChooser dialog = new JFileChooser();
        CommonFileFilter filter;
        
        dialog.setAcceptAllFileFilterUsed(false);
        
        filter = new CommonFileFilter("IdeogramBrowser Options (*.xml)");
        filter.addExtension("xml");
        
        dialog.addChoosableFileFilter(filter);
                        
        if( dialog.showSaveDialog(this) != JFileChooser.APPROVE_OPTION )
        {
            return;
        }
        
        File file = dialog.getSelectedFile();

        if( file.getName().lastIndexOf(".") == -1 )
        {
            file = new File(file.getAbsolutePath()+".xml");
        }
                
        if( file.exists() )
        {
            // overwrite file??
            int res = JOptionPane.showConfirmDialog(this,"File '"+ file.getName().toString() +"'already exists! Do you want to replace the existing file?");
            if( res != JOptionPane.OK_OPTION )
                return;
        }

        // open output stream

        FileWriter fs = null;
        try 
        {
            fs = new FileWriter(file);
            if( fs == null )
            {
                showErrorDialog( "Cannot open file\r\n"+file.getAbsolutePath() );
                return;
            }
            
            //ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(fs));
            //XMLEncoder os = new XMLEncoder(new BufferedOutputStream(fs));
            //           
            XStream xstream = new XStream();
            ObjectOutputStream os = xstream.createObjectOutputStream(fs,"doc");
            
            os.writeObject( parameters );
            
            os.close();
            fs.close();
        }
        catch(FileNotFoundException e)
        {
        	showErrorDialog( "Cannot open file\r\n"+file.getAbsolutePath() +"\r\n" + e.getMessage() );
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            showErrorDialog( "I/O error while writing file\r\n"+file.getAbsolutePath()+"\r\n"+e.getMessage() );
        }
    }

    
    // +++++++++++++++++++++ MENU ACTIONS +++++++++++++++++++++
    protected void actionLoadMarkers()
    {
    	dialog.setMultiSelectionEnabled(true);
        if( dialog.showOpenDialog(this) != JFileChooser.APPROVE_OPTION ) {
            return;
        }
        
        File[] files = dialog.getSelectedFiles();
        
        
        for( int i=0; i<files.length; ++i )
        {
            tryToLoad(files[i], -1);
        	
        }
    }

	/**
     * Sets the current mouse cursor to a normal cursor. 
     *
     */
	protected void normalCursor() {
		Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		 setCursor(normalCursor);
	}

	/**
	 * Sets the current mouse cursor to a waiting cursor (e.g. hourglass).
	 * {@see #normalCursor()}
	 */
	protected void waitCursor() {
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
	}
    
	/*
	 * http://itextdocs.lowagie.com/tutorial/
	 */
	public void writePDFFile( OutputStream os ) throws DocumentException
	{
	    
        // step 1: creation of a document-object
		com.lowagie.text.Document document = new com.lowagie.text.Document();
        
        // step 2: creation of the writer
        PdfWriter writer = PdfWriter.getInstance(document, os);
        
        // step 3: we open the document
        document.open();
        
        // step 4: we grab the ContentByte and do some stuff with it

        // we create a fontMapper and read all the fonts in the font directory
        DefaultFontMapper mapper = new DefaultFontMapper();
        FontFactory.registerDirectories();
        //mapper.insertDirectory("c:\\windows\\fonts");
        
        PdfContentByte cb = writer.getDirectContent();
        
        IdeogramViewPrintingHelper helper = new IdeogramViewPrintingHelper(ideograms);
        
        Dimension dim = helper.getDim();
        
        PdfTemplate tp = cb.createTemplate(dim.width, dim.height);
        Graphics2D g2 = tp.createGraphics(dim.width, dim.height, mapper);
        Rectangle r = document.getPageSize();
        
        double scale = Math.min(r.width()/dim.width,r.height()/dim.height);
        g2.scale(scale,scale);
        helper.directPaintAll(g2);     
        g2.dispose();
        cb.addTemplate(tp, 0, 0);
        
        
        /*
        for( int i=0; i<ideograms.length; ++i )
        {
        	int w = ideograms[i].getWidth(),
        		h = ideograms[i].getHeight();
        	
            PdfTemplate tp = cb.createTemplate(w, h);
            Graphics2D g2 = tp.createGraphics(w, h, mapper);
            Rectangle r = document.getPageSize();
            g2.scale(r.width()/2, r.height()/2);
            
            ideograms[i].paintBackgroundSelections(g2);
            ideograms[i].directPaint(g2); //, w, h);
            
            g2.dispose();
            cb.addTemplate(tp, 0, 0);
            if( i+1 < ideograms.length )
            	document.newPage();  
        }
        */
        
        document.close();
	}
	    
    /**
     * Writes the current Venn diagram to an SVG file.
     * 
     * @param os
     * @throws UnsupportedEncodingException 
     * @throws SVGGraphics2DIOException 
     */
	public void writeSVGFile(OutputStream os) throws UnsupportedEncodingException, SVGGraphics2DIOException
    {
        // Get a DOMImplementation
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document
        org.w3c.dom.Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
                
        IdeogramViewPrintingHelper helper = new IdeogramViewPrintingHelper(ideograms); 
        Dimension dim = helper.getDim();
        svgGenerator.setSVGCanvasSize( dim );
        helper.directPaintAll( svgGenerator );
        
        // Finally, stream out SVG to the standard output using UTF-8
        // character to byte encoding
        boolean useCSS = true; // we want to use CSS style attribute
        Writer out;
        out = new OutputStreamWriter(os, "UTF-8");
        svgGenerator.stream(out, useCSS);
    }
    

	protected void actionFileSave()
	{
		JFileChooser dialog = new JFileChooser();
		CommonFileFilter filter;
		
		dialog.setAcceptAllFileFilterUsed(false);		
		filter = new CommonFileFilter("JPEG Image (.jpg,.jpeg)");
		filter.addExtension("jpg");
		filter.addExtension("jpeg");
		dialog.addChoosableFileFilter(filter);
		
		filter = new CommonFileFilter("SVG Image (.svg)");
		filter.addExtension("svg");
		dialog.addChoosableFileFilter(filter);

		filter = new CommonFileFilter("Portable Document Format (.pdf)");
		filter.addExtension("pdf");
		dialog.addChoosableFileFilter(filter);
		
		if( dialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
		{
			File file = dialog.getSelectedFile();
			
			String ext = new String();
	        if( file.getName().lastIndexOf(".") == -1 )
	        {
				int idx=-1;
				for(int i=0; idx<dialog.getChoosableFileFilters().length; ++i)
				{
					if( dialog.getChoosableFileFilters()[i] == dialog.getFileFilter() )
					{
						idx = i;
						break;
					}
				}
				switch(idx)
				{
					case 0:
						ext = "jpg";
						break;
					case 1:
						ext = "svg";
						break;
					case 2:
						ext = "pdf";
						break;
						
					default:
						ext = "jpg";
				}
				file = new File( file.getAbsolutePath() + "." + ext ); 
			}
			
			if( file.exists() )
			{
				// overwrite file??
				int res = JOptionPane.showConfirmDialog(this,"File '"+ file.getName().toString() +"'already exists! Do you want to replace the existing file?");
				if( res != JOptionPane.OK_OPTION )
					return;
			}
			
			// open output stream
			FileOutputStream os;
			String path = file.getAbsolutePath();
			try 
			{
				os = new FileOutputStream(path);
			}
			catch(FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(	this,
												"Cannot open file\r\n"+path,
												"Error",
												JOptionPane.ERROR_MESSAGE);
				return;
			}
			if( os == null )
			{
				JOptionPane.showMessageDialog(	this,
						"Cannot open file\r\n"+path,
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
						
			if( ext.compareToIgnoreCase("jpg") == 0 || ext.compareToIgnoreCase("jpeg") == 0 )
			{
				// jpeg output
				IdeogramViewPrintingHelper helper = new IdeogramViewPrintingHelper(ideograms); 
		        
		        Dimension dim = helper.getDim();
				BufferedImage image = new BufferedImage(dim.width,dim.height, BufferedImage.TYPE_INT_RGB);
				
				Graphics g = image.createGraphics();
				g.setColor(Color.WHITE); 
				g.fillRect(0,0,image.getWidth(),image.getHeight());				
		        helper.directPaintAll( g );
		        g.dispose();
				
				try
				{
					JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
					JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
					param.setQuality(1.0f, false);
					encoder.setJPEGEncodeParam(param);
					encoder.encode(image);
					os.close();
				}
				catch(IOException e)
				{
					JOptionPane.showConfirmDialog(this,"Error while writing file\r\n"+path);
					return;					
				}
				return;
			}
			
			if( ext.compareToIgnoreCase("svg") == 0 )
			{
	           try {
                    writeSVGFile(os);
                    os.close();
                }
                catch (UnsupportedEncodingException e) 
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(  this,
                            "Cannot write file \r\n"+path,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    
                    return;
                }                
                catch (SVGGraphics2DIOException e) 
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(  this,
                            "Error while creating SVG file\r\n"+path+"\r\n"+
                            e.getLocalizedMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);                 
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(  this,
                            "Error while creating SVG file\r\n"+path+"\r\n"+
                            e.getLocalizedMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);                                     
                }
                return;
			}
			
			if( ext.compareToIgnoreCase("pdf") == 0 )
			{
				
				try
				{
					writePDFFile(os);
				}
				catch( DocumentException e )
				{
					e.printStackTrace();
					showErrorDialog("Error while creating PDF file\n"+path+"\n"+e.getMessage());
				}
				catch( Exception e )
				{
					e.printStackTrace();
					showErrorDialog("Exception while creating PDF file\n"+path+"\n"+e.getMessage());					
				}
				
				try
				{
					os.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
					showErrorDialog("I/O Exception while closing file\n"+path+"\n"+e.getMessage());					
				}
			}
		}
	}
	
	protected void actionFilePrinterSettings()
	{
		pageFormat = PrinterJob.getPrinterJob().pageDialog( pageFormat );
	}


	protected void actionFilePrintSingle()
	{		
		IdeogramView view = getSelectedView();
		if( view == null )
		{
			showInfoDialog("A chromosome has to be selected!");
			return;
		}
		
		PrinterJob job = PrinterJob.getPrinterJob();
		if( job.printDialog() )
		{
			job.setPrintable(view, pageFormat);
			try
			{
				job.print();
			}
			catch( PrinterException e )
			{
				showErrorDialog("Printer Exception :\n"+e.getMessage());
			}
		}
	}
	
	protected void actionFilePrintAll()
	{		
		PrinterJob job = PrinterJob.getPrinterJob();
		if( job.printDialog() )
		{
			job.setPrintable(new IdeogramViewPrintingHelper(ideograms), pageFormat);
			try
			{
				job.print();
			}
			catch( PrinterException e )
			{
				showErrorDialog("Printer Exception :\n"+e.getMessage());
			}
		}
	}
	
	/*protected void actionFilePrintHeader() {	
		// headerPanel valiD?
		//if(headerPanel)
		PrinterJob job = PrinterJob.getPrinterJob();
		if( job.printDialog() )
		{
			job.setPrintable(new TextPanePrinter(headerPanel),pageFormat);
			try
			{
				job.print();
			}
			catch( PrinterException e )
			{
				showErrorDialog("Printer Exception :\n"+e.getMessage());
			}
		}
	}*/
	
	
	public IdeogramView getSelectedView()
	{
		for( int i=0; i<ideograms.length; ++i )
			if( ideograms[i].isActive() )
				return ideograms[i];
		return null;
	}

	
	protected void actionFileExit()
	{
		setVisible(false);
		dispose();
        if( ! dependentWindow )
            System.exit(0);
	}
	
    public boolean getMultiMode()
    {
        return multiMode;
    }
    
	public void setMultiMode(boolean multiMode)
	{
        // TODO: fix this: when switching forth and back from multi to single mode every
        // previsouly selected IdeogramBean disappears (probably a 'parent component' problem).
        
        /*
        if( this.multiMode != multiMode )
        {
    		this.multiMode = multiMode;
    		if( multiMode )
    		{
                splitter.setTopComponent(scroller);
    		}
    		else
    		{
                IdeogramBean id = ideograms[currentChromosome - 1];
                splitter.setTopComponent(id);
                id.setSize(scroller.getSize());
    		}
        }
        */
	}
	
	public void setCurrentChromosome(int chromosome)
	{
		if(chromosome >= 1 && chromosome <= NUM_OF_CHROMOSOMES)
		{
			currentChromosome = chromosome;
			setMultiMode( false ); 
		}
	}
		
	public IdeogramDB getIdeogramDB()
	{
		return db;
	}
	
	/**
     * @return The gene data base.
     */
    private GeneDB getGeneDB() 
    {
        return gene_db;
    }
	
	
	/**
	 * Adds a MarkerCollection to an ideogram.
	 * 
	 * @param chromosome Must be in the range 1..24 (23=X,24=Y)
	 * @param M MarkerCollection
	 */
	public void addMarkers(int chromosome, MarkerCollection M)
	{
		ideograms[chromosome-1].addMarkers(M);
	}
	
	/**
	 * Removes all markers from the given chromosome
	 * 
	 * @param chromosome Chromosome number in the range 1..24 .
	 */
	public void clearMarkers(int chromosome)
	{
		ideograms[chromosome-1].clearMarkers();
	}
	
	/**
	 * Removes all markers from the all chromosomes.
	 * 
	 */
	public void clearMarkers()
	{		
		for(int i=0;i<ideograms.length;++i)
		{
			ideograms[i].clearMarkers();
		}
	}
	
	public void clear()
	{
		if( !slots.isEmpty() )
		{
			clearMarkers();
			for( DataSlot slot : slots )
			{
				slot.clear();
			}
			slots.clear();

			mapper = null;
			map = null;
			mc = null;

			//merger.clear();
			merger = null;

			/*
			 * TODO Upon loading new CNT files the clear() method will never
			 * be called. The cn variable only points to the most recent 
			 * CopyNumberTransformer. I think that it would be better, if the 
			 * variables cn and resultTransformers were local variables instead
			 * of instance fields!
			 * [Ferdinand Hofherr] 
			 */
			cn.clear();
			cn = null;
			// TODO dito
			resultTransformer.clear();
			resultTransformer = null;

			s.clear();
			s = null;

			collectGarbage();
		}
		validate();
	}

	private void collectGarbage() {
		// collect garbage
		Runtime.getRuntime().gc();
	}

	/**
     * 
     * @return True if chromosomal labels (bands) are shown in the diagrams.
	 */
    public boolean getShowLabels()
    {
        if( ideograms != null && ideograms.length > 0 )
        {
            return ideograms[0].getShowLabels();
        }
        return false;
    }

    /**
     * Sets the visibility of the chromosomal labels for all ideograms.
     * 
     * @param show
     */    
	public void setShowLabels(boolean show)
	{
		for(int i=0;i<ideograms.length;++i)
		{
			ideograms[i].setShowLabels(show);		
		}
	}
	
	public boolean getShowMarkers()
	{
		if( ideograms != null && ideograms.length > 0 )
		{
			return ideograms[0].getShowMarkers();
		}
		return false;
	}
	
	public void setShowMarkers(boolean show)
	{
		for(int i=0;i<ideograms.length;++i)
		{
			ideograms[i].setShowMarkers(show);		
		}
	}
    
    public static void setLookAndFeel(Component frame)
    {        
        // switch to windows look and feel
        try
        {       
            String plaf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            UIManager.setLookAndFeel(plaf);
            SwingUtilities.updateComponentTreeUI(frame);
        }
        catch(UnsupportedLookAndFeelException e)
        {
            MainApp.getLogger().throwing("IdeogramMainWindow","createAndShowGUI",e);
        }
        catch(ClassNotFoundException e )
        {
            MainApp.getLogger().throwing("IdeogramMainWindow","createAndShowGUI",e);
        }
        catch(InstantiationException e)
        {
            MainApp.getLogger().throwing("IdeogramMainWindow","createAndShowGUI",e);
        }
        catch(IllegalAccessException e)
        {
            MainApp.getLogger().throwing("IdeogramMainWindow","createAndShowGUI",e);
        }
    }

    /**
     * Static method trying to initialize a new Ideogram window (including
     * the databases)
     * 
     * @return An ideogram window or null if an error occured or the user
     * pressed cancel.
     */
	public static IdeogramMainWindow createAndShowGUI()
	{
        // Progress dialog
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setVisible(true);
        progressDialog.setText("starting ...");

		IdeogramMainWindow frame = new IdeogramMainWindow();
		
        boolean success = true;
		frame.setShowLabels(false);
        
        progressDialog.setText("loading ideogram ...");
		success &= frame.loadIdeogramDatabase(progressDialog);

        if( success )
        {
            //success &= frame.loadGeneDatabase(startDialog);
            frame.loadGeneDatabase(progressDialog);
        }
        progressDialog.setText("");
        progressDialog.dispose();
        
        if( ! success )
        {
        	
        	JOptionPane.showMessageDialog(null, "Fatal error: Could not find gene/ideogram database!", "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            frame = null;
        }
        else
        {
            frame.pack();
            frame.setSize(APP_WIDTH,APP_HEIGHT);
        }
		
		return frame;
	}

	/**
	 * Loads the ideogram database from the jar archive (folder data/ideogram.csv).
	 * @param notifier 
	 * 
	 * @return True on success.
	 */	
	public boolean loadIdeogramDatabase(IProgressNotifier notifier)
	{
		try
		{
			InputStream stream;
			String name;
			
			name = "data/ideogram";
			stream = getClass().getResourceAsStream(name);
			if( stream == null )
			{
				name = "data/ideogram.gz";
				stream = getClass().getResourceAsStream(name);
				if( stream != null )
				{
				    stream = new GZIPInputStream(stream);
				}
			}
			
			if( stream == null )
			{
				MainApp.getLogger().warning("cannot find resource name '"+name+"'");
				return false;
			}
			
			if( ! getIdeogramDB().read(new InputStreamReader(stream),notifier) )
                return false;
		}
		catch(Exception e)
		{
			MainApp.getLogger().throwing("MainApp","loadDatabase",e);
			return false;
		}
        return true;
    }
    
    /**
     * Loads the NCBI gene database (seq_gene.md)
     * @param notifier 
     * @return true on success
     */
    public boolean loadGeneDatabase(IProgressNotifier notifier)
    {
		try
		{
			InputStream stream;
			String name;
			
			name = "data/seq_gene.md";
			stream = getClass().getResourceAsStream(name);
			if( stream == null )
			{
				name = "data/seq_gene.md.gz";
				stream = getClass().getResourceAsStream(name);
				if( stream != null )
				{
				    stream = new GZIPInputStream(stream);
				}
			}
			
			if( stream == null )
			{
				MainApp.getLogger().warning("cannot find resource name '"+name+"'");
				return false;
			}
			
			if( ! getGeneDB().read(new InputStreamReader(stream),notifier) )
                return false;
		}
		catch(Exception e)
		{
            e.printStackTrace();
			MainApp.getLogger().throwing("MainApp","loadDatabase",e);
			return false;		    
		}
		
		return true;
	}
	
    public void setConsensusMode(boolean mode)
	{
		for(int i=0;i<ideograms.length;++i)
		{
			ideograms[i].setConsensusMode(mode);
		}
	}
	
	/**
	 * Finds an image resource with the given path.
	 * @param name Path to the image resource.
	 * @return The image resource.
	 */
	protected java.awt.Image getImageResource(String name)
	{
		Image img = null;
		try
		{
			java.net.URL url = getClass().getResource(name);
			if( url == null )
			{
				MainApp.getLogger().warning("cannot find resource name '"+name+"'");
				return null;
			}
			img = getToolkit().createImage(url);
			if( img == null )
			{
				MainApp.getLogger().warning("cannot create image with url '"+url.toString()+"'");
				return null;
			}
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(img,0);
			try
			{
				mt.waitForAll();
			}
			catch(InterruptedException e)
			{
				//
			}
		}
		catch(Exception e)
		{
			MainApp.getLogger().throwing("MainApp","getImageResource",e);
		}
		return img;
	}
	
	protected ImageIcon createImageIcon(String path, String description) 
	{
		java.net.URL imgURL = MainApp.class.getResource(path);
		if (imgURL != null) 
		{
			return new ImageIcon(imgURL, description);
		} else 
		{
			MainApp.getLogger().warning("Couldn't find file: " + path);
			return null;
		}
	}
    
    public void componentResized(ComponentEvent event)
    {
    	setOptimumSize();
    }
    
    public void setOptimumSize()
    {
    	double v = Double.NaN;
    	if(filterPanel!=null) v = 1.0 - (double)filterPanel.getMaximumSize().getHeight()/getHeight();
        if(!Double.isNaN(v)) splitter.setDividerLocation(v);   
    }

    public void componentMoved(ComponentEvent e) {        
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }
	
    private void updateInfoPanel(IdeogramView source) {
		StringBuffer buf = new StringBuffer();
		buf.append("free memory = "
				+ Math.round(100.0 * (double) Runtime.getRuntime()
				.freeMemory() / 1024.0 / 1024.0) / 100.0 + " MB\n");
		if (source != null) {
			buf.append(source.getSelectionInfo());
		}

		infoPanel.setText(buf.toString());
		infoPanel.moveCaretPosition(1);
		infoPanel.setSelectionStart(0);
		infoPanel.setSelectionEnd(0);
	}
    
    private void updateHeaderPanel( IdeogramView source ) {
    	StringBuffer buf = new StringBuffer();
    	if(source != null) {
    		buf.append(source.getHeaderInfo());
    	}
    	headerPanel.setText(buf.toString());
    	headerPanel.moveCaretPosition(1);
    	headerPanel.setSelectionStart(0);
    	headerPanel.setSelectionEnd(0);
    }
    
    private void updateInfoPanels( IdeogramView source )
    {
    	updateInfoPanel( source );
    	updateHeaderPanel( source );
    }
    
    /**
     * If a selection changed in one of the ideograms
     */
    public void stateChanged( ChangeEvent e) 
    {
        if( e.getSource() instanceof IdeogramView )
        {
            IdeogramView ideo = (IdeogramView)e.getSource();
            updateInfoPanels(ideo);
            
            // set all other ideograms to be not active
            for(int i=0; i<ideograms.length; ++i )
            {
                ideograms[i].setActive( ideograms[i] == ideo );
            }
            return;
        }        
    }
    
    public void importCNTFile(File file, int target) throws FileFormatException, IOException, FileCompatibilityException
    {
    	importCNTFile(file, null, target);
    }
    
    public void importCNTFile(File file) throws FileFormatException, IOException, FileCompatibilityException
    {
    	importCNTFile(file, null, -1);
    }

    public void importCNTFile(File file, File lohFile) throws FileFormatException, IOException, FileCompatibilityException
    {
    	importCNTFile(file, lohFile, -1);
    }
    
    public void importCNTFile(File cnFile, File lohFile, int target) throws FileFormatException, IOException, FileCompatibilityException
    {
    	// set hourglass
    	waitCursor();

    	data = new AffymetrixCntReaderModel();
    	
    	if(lohFile==null) { 
    		data.loadFromFile(cnFile);
    	}
    	else {
    		data.loadFromFile(cnFile, lohFile);
    	}

   	    // check whether file is compatible with others loaded  
		setVersionMode(data.getVersion());

		cn = new CopyNumberTransformer(data, parameters);

		if(target != -1) {
			s = slots.get(target);
		}
		else s = new DataSlot( parameters );

		// if there is still no exception, add model to dataslot
    	s.addModel( cn );
    	// target not existing, add new dataslot
    	if ( target==-1) slots.add( s );
    	// there are one ore more files loaded, so version mode cannot be changed any more
    	versionModeChangeable = false;
    	
    	// set arrow cursor
		normalCursor();
    }
    
    /**
     * TODO INSERT DOCUMENTATION HERE!
     *
     * @param file
     * @param target
     * @throws FileFormatException
     * @throws IOException
     */
    public void importRResultFile(File file, int target) throws FileFormatException, IOException {
        waitCursor();
        
        RResultReaderModel model = new RResultReaderModel();
        model.loadFromFile(file);
        resultTransformer = new RResultTransformer(model, parameters);
        if (target != -1) {
            s = slots.get(target);
        } 
        else {
            s = new DataSlot(parameters);
            slots.add(s);
        }
        s.addModel(resultTransformer);
        
        normalCursor();
    }
    
    public void importTXTFile(File file) throws FileFormatException, IOException
    {
        TabbedTextReaderModel model = new TabbedTextReaderModel();
        
        model.loadFromFile(file);
        
    	DataSlot s = new DataSlot(parameters);
    	s.addModel( model );
    	slots.add( s );
    }
    
    public AllParameters loadStandardOptionFile() throws IOException, ClassNotFoundException 
    {
    	File optionfile = new File(OPTIONFILE_NAME);
        FileReader fs = null;

        fs = new FileReader(optionfile);
     
        // ObjectInputStream os = new ObjectInputStream(fs);
        //XMLDecoder os = new XMLDecoder(new BufferedInputStream(fs));
        
        
        AllParameters param = null;

        XStream xstream = new XStream();
        ObjectInputStream os = xstream.createObjectInputStream(fs);
        
        param = (AllParameters)os.readObject();
        
        os.close();
        fs.close();

        return param;
    }
    
	public FileVersion getVersionMode() {
		return parameters.versionLoaded;
	}

	public void setVersionMode(FileVersion versionMode)
			throws FileCompatibilityException {
		if (!versionMode.equals(getVersionMode())) {
			if (versionModeChangeable) {
				parameters.setVersionLoaded(versionMode);
				filterPanel.setParameters(parameters);
			} else {
				throw new FileCompatibilityException("Only files of the same version can be imported together.\n" +
					"To import this file, remove all other markers first.");
			}
		}
	}
	
    public static void main(String args[])
    {
        IdeogramMainWindow ideo = createAndShowGUI();
        if( ideo != null )
        {
            ideo.setVisible(true);
        }
        else
        {
            System.exit(-1);
        }
    }
}

