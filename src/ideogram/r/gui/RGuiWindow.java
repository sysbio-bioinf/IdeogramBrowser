/*
 * File:	RGuiWindow.java
 * Created: 01.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;


import ideogram.r.RController;
import ideogram.r.RException;
import ideogram.r.RMainLoopModel;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
// TODO THREAD SAFETY!!
// TODO Comment
/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RGuiWindow extends JFrame 
implements Observer, ActionListener, MessageDisplay {

    private enum ActionCommands {
        LIB_SEL_COMBO ("libselcombo");
        
        private final String cmd;

        ActionCommands(String cmd) {
            this.cmd = cmd;
        }
        
        String accCmd() {return cmd;}
    };
    
    private JTextArea rConsole; // Displays R's console output.
    private JLabel statusLabel; // Displays status information.
    private JPanel libSpecInterfPan; // Library specific interface.
    private Container contentPane;
    private RLibraryWrapperReader reader;
    private JSplitPane splitPane;
    
    /**
     * INSERT DOCUMENTATION HERE!
     *
     * @throws HeadlessException
     */
    public RGuiWindow() throws HeadlessException {
        super("R");
        rConsole = new JTextArea(); rConsole.setEditable(false);
        statusLabel = new JLabel();
        contentPane = getContentPane();
        reader = new RLibraryWrapperReader(this);
        libSpecInterfPan = new JPanel();
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        
        //contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        
        // Register with RMainLoopModel.
        RController.getInstance().getRMainLoopModel().addObserver(this);
        
        
        JPanel p = new JPanel();
        p.add(createRStatusPanel());
        p.add(Box.createRigidArea(new Dimension(0,5)));
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        p.add(createCommonRLibraryInterface());
        contentPane.add(p, BorderLayout.NORTH);
        
        splitPane.setTopComponent(libSpecInterfPan);
        setLibSpecInterf(reader.createInputPanel());
                
        splitPane.setBottomComponent(createRConsolePanel());
        
        contentPane.add(splitPane, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setSize(810, 600);
    }

    private JPanel createRStatusPanel() {
        JPanel ret = new JPanel();
        ret.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, 
                Color.LIGHT_GRAY));
        ret.setBackground(Color.WHITE);
        ret.add(statusLabel);
        return ret;
    }

    private JPanel createCommonRLibraryInterface() {
        JPanel ret;
        JLabel l;
        JComboBox cb;
        
        ret = new JPanel();
        ret.setLayout(new BoxLayout(ret, BoxLayout.LINE_AXIS));
        l = new JLabel("Select R library:");
        ret.add(l);
        
        ret.add(Box.createRigidArea(new Dimension(5,0)));
        
        cb = new JComboBox(RController.getInstance().listLibraryWrappers());
        cb.setSelectedIndex(-1); // Assure, that initially nothing is selected.
        cb.setActionCommand(ActionCommands.LIB_SEL_COMBO.accCmd());
        cb.addActionListener(this);
        ret.add(cb);
        
        ret.add(Box.createHorizontalGlue());
        return ret;
    }
    
    
    /*
     * Create the panel containing the R console.
     */
    private JPanel createRConsolePanel() {
        JPanel ret = new JPanel();
        ret.setLayout(new BoxLayout(ret, BoxLayout.PAGE_AXIS));
        ret.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel p;
        
        JLabel lab = new JLabel("R console:");
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        p.add(lab);
        p.add(Box.createHorizontalGlue());
        ret.add(p);
        
        ret.add(Box.createRigidArea(new Dimension(0,5)));
        
        //rConsole.setBorder(BorderFactory.createLoweredBevelBorder());
        rConsole.setMaximumSize(new Dimension(800, 300));
        ret.add(new JScrollPane(rConsole));
        return ret;
    }
    
    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        if (o instanceof RMainLoopModel) {
            RMainLoopModel model = (RMainLoopModel) o;
            rConsole.setText(model.getRConsole());
            
            if (model.isBusy()) {
                setMessage("Waiting for R");
            }
            else {
                setDefaultMessage();
            }
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String accCmd = e.getActionCommand();
        if (ActionCommands.LIB_SEL_COMBO.accCmd().compareTo(accCmd) == 0) {
            String selLib = (String)((JComboBox)e.getSource()).getSelectedItem();
            try {
                RLibraryWrapper wr = 
                    RController.getInstance().loadRLibraryWrapper(selLib);
                reader.setBuilder(new StandardInterfaceBuilder(this, wr));
                setLibSpecInterf(reader.createInputPanel(wr));
            } catch (IllegalArgumentException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (RException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
     * @see ideogram.r.MessageDisplay#setDefaultMessage()
     */
    public void setDefaultMessage() {
        statusLabel.setText("R has nothing to do!");
    }

    /* (non-Javadoc)
     * @see ideogram.r.MessageDisplay#setMessage(java.lang.String)
     */
    public void setMessage(String msg) {
        statusLabel.setText(msg);
    }

    private void setLibSpecInterf(Component cmp) {
        libSpecInterfPan.removeAll();
        libSpecInterfPan.add(cmp);
        libSpecInterfPan.setPreferredSize(cmp.getPreferredSize());
        this.validate();
    }
}
