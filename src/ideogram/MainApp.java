/*
 * Created on 07.06.2004
 */
package ideogram;

import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import java.util.logging.*;

/**
 * Main application object. Will be accessed with JNI from C++.
 * 
 * @author muellera
 *
 */
public class MainApp
{
	private static MainApp instance;
	private static Logger logger = null;
	private static Level loggingLevel = Level.INFO;
	
	private IdeogramMainWindow window = null;
	

	private MainApp()
	{
	}

	public static Logger getLogger()
	{
		if(logger == null)
		{
			logger = Logger.getLogger("ideogram.MainApp");	
			logger.setLevel(loggingLevel);
			Handler ch = new ConsoleHandler();
			logger.addHandler(ch);
			ch.setLevel(loggingLevel);
			
			/*try 
			{
				Handler fh = new FileHandler("mainApp.log");
				logger.addHandler(fh);
				fh.setLevel(loggingLevel);
			}
			catch(IOException e)
			{
				logger.throwing("MainApp","initialize",e);
			}
			catch(Exception e)
			{
				logger.throwing("MainApp","initialize",e);				
			}*/
			
			logger.info("logging started");
		}
		return logger;
	}
	
	public static MainApp getInstance()
	{
		getLogger().entering("MainApp","getInstance");		
		if( instance == null )
		{
			instance = new MainApp();
		}
		getLogger().exiting("MainApp","getInstance");
		return instance;
	}
	
	public static IdeogramMainWindow getWindow()
	{
		return getInstance().window; 
	}	
	
	/**
	 * <b>JNI callable</b>. Open the main application window.
	 *
	 */
	private void open()
	{
		logger.entering("MainApp","open");
		try
		{            
			SwingUtilities.invokeAndWait( 
				new Runnable()
				{
					public void run()
					{
						if( window == null )
						{
							window = IdeogramMainWindow.createAndShowGUI();
						}
                        if( window != null )
                        {
                            window.setDependentWindow(true);
                            window.setVisible(true);
                        }
					}
				});
		}
		catch(InterruptedException e)
		{
			getLogger().throwing("MainApp","open",e);			
		}
		catch(InvocationTargetException e)
		{
			getLogger().throwing("MainApp","open",e);
		}
		logger.exiting("MainApp","open");
	}
	
	/**
	 * Closes the Main Application Window and waits until the window is closed.
	 *
	 */
	public void close()
	{
		getLogger().entering("MainApp","close");
		if( window == null )
			return;

		try 
		{
			SwingUtilities.invokeAndWait( 
			new Runnable()
			{
				public void run()
				{
					window.dispose();
				}
			});			
		}
		catch(InterruptedException e)
		{
			getLogger().throwing("MainApp","close",e);
		} 
		catch (InvocationTargetException e)
		{
			getLogger().throwing("MainApp","close",e);
		}		
		getLogger().exiting("MainApp","close");
	}


	public void addMarkers(int chromosome, MarkerCollection M)
	{
		getLogger().entering("MainApp","addMarkers");
		if( chromosome < 1 || chromosome > 24)
			throw new IllegalArgumentException("chromosome out of range");
		if( window == null )
			return;
				
		SwingUtilities.invokeLater( new run_addMarkers(chromosome,M) );
		
		/*try
		{
			FileWriter fw = new FileWriter("c:\\tmp\\addMarkers.dat");
		
			LinkedList markers = M.getMarkers();
			for(int i = 0; i < markers.size(); i++){
				
				Marker m = (Marker) markers.get(i);
				
				String fstring = Integer.toString(chromosome)+ " " + Long.toString(m.interval.from) +
									" " + Long.toString(m.interval.to) + "\n";
				
				
				
				fw.write(fstring);
				
			}
			
			fw.close();

		}
		catch(IOException e)
		{
			
			getLogger().throwing("MainApp","addMarkers",e);
			
		}*/

		
		getLogger().exiting("MainApp","addMarkers");
	}
	
	private class run_addMarkers implements Runnable
	{
		private int chromosome;
		private MarkerCollection markers;
		
		run_addMarkers(int chromosome,MarkerCollection markers)
		{
			this.chromosome = chromosome;
			this.markers = markers;
		}
		
		public void run()
		{
			window.addMarkers(chromosome,markers);
		}
	}
	
	public void clearMarkers()
	{
		getLogger().entering("MainApp","clearMarkers");
		
		if( window == null )
			return;
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				
				window.clearMarkers();
			}
		});
		
		getLogger().exiting("MainApp","clearMarkers");
	}
	
	public void repaint()
	{
		if( window == null )
			return;
			
		try 
		{
			SwingUtilities.invokeAndWait( 
			new Runnable()
			{
				public void run()
				{
					window.repaint();
				}
			});			
		}
		catch(InterruptedException e)
		{
			getLogger().throwing("MainApp","repaint",e);
		} 
		catch (InvocationTargetException e)
		{
			getLogger().throwing("MainApp","repaint",e);
		}				
	}
	
	public void setConsensusMode(boolean mode)
	{
		
		window.setConsensusMode( mode );
		
	
	}
	
	public static void main(String[] args)
	{
        MainApp app = getInstance();
		getLogger().entering("MainApp","main");
        if( app != null )
        {
            app.open();
        }
		getLogger().exiting("MainApp","main");
	}
}
