/*
 * File:	RGuiWindow.java
 * Created: 01.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RGuiWindow extends JFrame implements Observer {

    private JTextArea rConsole; // Displays R's console output.
    private JLabel statusLabel; // Displays status information.
    private Container contentPane;
    
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
        
        // Register with RMainLoopModel.
        RController.getInstance().getRMainLoopModel().addObserver(this);
        
        contentPane.add(createRStatusPanel(), BorderLayout.NORTH);
        contentPane.add(createRPackageInterface(), BorderLayout.CENTER);
        contentPane.add(createRConsolePanel(), BorderLayout.SOUTH);
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setSize(800, 600);
    }

    private JPanel createRStatusPanel() {
        JPanel ret = new JPanel();
        ret.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, 
                Color.LIGHT_GRAY));
        ret.setBackground(Color.WHITE);
        ret.add(statusLabel);
        return ret;
    }
    
    private JPanel createRPackageInterface() {
        JPanel ret = new JPanel();
        ret.setLayout(new BoxLayout(ret, BoxLayout.PAGE_AXIS));
        ret.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel p;
        JLabel l;
        JComboBox cb;
        
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        l = new JLabel("Select R package:");
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(5,0)));
        cb = new JComboBox(new String[] {"GLAD"});
        p.add(cb);
        p.add(Box.createHorizontalGlue());
        ret.add(p);
        
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
        
        rConsole.setBorder(BorderFactory.createLoweredBevelBorder());
        ret.add(rConsole);
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
                statusLabel.setText("Waiting for R");
            }
            else {
                statusLabel.setText("R has nothing to do at the moment");
            }
            
        }
    }
}
