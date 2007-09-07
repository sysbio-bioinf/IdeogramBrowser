/*
 * Created on 11.10.2005
 *
 */
package ideogram;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

/**
 * This is a beautiful progress pretending dialog entertaining the waiting user. 
 * @author muellera
 *
 */
@SuppressWarnings("serial")
public class ProgressDialog extends JFrame implements ActionListener, IProgressNotifier {
     
	private static final int APP_WIDTH = 200;
	private static final int APP_HEIGHT = 100;
	private JProgressBar progress;
    private BoundedRangeModel model;
    private Timer timer;
    private JLabel info;
    private JButton cancel;

    private boolean cancelled;
    
    public ProgressDialog()
    {
        super("Ideogram Browser");
        
        getContentPane().setLayout(new GridLayout(3,1,1,1));
        
        model = new DefaultBoundedRangeModel(0,0,0,70);
        progress = new JProgressBar(model);
        //progress.setSize(550,120);
        progress.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        getContentPane().add(progress);
        
        info = new JLabel();
        info.setAlignmentX(0.5f);
        info.setAlignmentY(0.5f);
        info.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        getContentPane().add(info);
         
        cancel = new JButton("cancel");
        cancel.setActionCommand("cancel");
        cancel.addActionListener(this);
            
        getContentPane().add(cancel);
        
                
        addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent event)
                    {
                    }
                });
        

        // center to screensize
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(screensize.width/2-APP_WIDTH/2,screensize.height/2-APP_HEIGHT/2, APP_WIDTH, APP_HEIGHT);
		

        timer = new Timer(100,this);
        start();
        
    }

    public void actionPerformed(ActionEvent e) 
    {
        String cmd = e.getActionCommand();
        if( e.getSource() == timer )
        {
            int v = (model.getValue()+1) % model.getMaximum();
            model.setValue(v);
            return;
        }
        if( cmd.compareToIgnoreCase("cancel") == 0)
        {
            cancel();
        }
    }
    
    public void start()
    {
        cancelled = false;
        timer.start();        
    }
    
    public void cancel()
    {
        cancelled = true;
        info.setText("");
        timer.stop();
        // TODO: notify parent        
    }
    
    public boolean isCancelled()
    {
        return cancelled;
    }
    
    /**
     * Show a progress text in the StartDialog.
     * @param text
     */
    public void setText(String text)
    {
        info.setText(text);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        ProgressDialog dlg = new ProgressDialog();
        
        dlg.setVisible(true);

    }

}
