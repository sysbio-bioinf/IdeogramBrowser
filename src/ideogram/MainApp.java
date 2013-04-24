/*
 * Created on 07.06.2004
 */
package ideogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import util.GlobalConfig;

/**
 * Main application object. Will be accessed with JNI from C++.
 * 
 * @author muellera
 * 
 */
public class MainApp {

	private static MainApp instance;
	private static Logger logger = null;
	private static Level loggingLevel = Level.INFO;

	private IdeogramMainWindow window = null;

	private MainApp() {
	}

	public static Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger("ideogram.MainApp");
			logger.setLevel(loggingLevel);
			Handler ch = new ConsoleHandler();
			logger.addHandler(ch);
			ch.setLevel(loggingLevel);

			/*
			 * try { Handler fh = new FileHandler("mainApp.log");
			 * logger.addHandler(fh); fh.setLevel(loggingLevel); }
			 * catch(IOException e) { logger.throwing("MainApp","initialize",e);
			 * } catch(Exception e) { logger.throwing("MainApp","initialize",e);
			 * }
			 */

			logger.info("logging started");
		}
		return logger;
	}

	public static MainApp getInstance() {
		getLogger().entering("MainApp", "getInstance");
		if (instance == null) {
			instance = new MainApp();
		}
		getLogger().exiting("MainApp", "getInstance");
		return instance;
	}

	public static IdeogramMainWindow getWindow() {
		return getInstance().window;
	}

	/**
	 * <b>JNI callable</b>. Open the main application window.
	 * 
	 */
	private void open() {
		logger.entering("MainApp", "open");
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					if (window == null) {
						window = IdeogramMainWindow.createAndShowGUI();
					}
					if (window != null) {
						window.setDependentWindow(true);
						window.setVisible(true);
					}
				}
			});
		} catch (InterruptedException e) {
			getLogger().throwing("MainApp", "open", e);
		} catch (InvocationTargetException e) {
			getLogger().throwing("MainApp", "open", e);
		}
		logger.exiting("MainApp", "open");
	}

	/**
	 * Closes the Main Application Window and waits until the window is closed.
	 * 
	 */
	public void close() {
		getLogger().entering("MainApp", "close");
		if (window == null)
			return;

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					window.dispose();
				}
			});
		} catch (InterruptedException e) {
			getLogger().throwing("MainApp", "close", e);
		} catch (InvocationTargetException e) {
			getLogger().throwing("MainApp", "close", e);
		}
		getLogger().exiting("MainApp", "close");
	}

	public void addMarkers(int chromosome, MarkerCollection M) {
		getLogger().entering("MainApp", "addMarkers");
		if (chromosome < 1
				|| chromosome > GlobalConfig.getInstance().getChromosomeCount())
			throw new IllegalArgumentException("chromosome out of range");
		if (window == null)
			return;

		SwingUtilities.invokeLater(new run_addMarkers(chromosome, M));

		/*
		 * try { FileWriter fw = new FileWriter("c:\\tmp\\addMarkers.dat");
		 * 
		 * LinkedList markers = M.getMarkers(); for(int i = 0; i <
		 * markers.size(); i++){
		 * 
		 * Marker m = (Marker) markers.get(i);
		 * 
		 * String fstring = Integer.toString(chromosome)+ " " +
		 * Long.toString(m.interval.from) + " " + Long.toString(m.interval.to) +
		 * "\n";
		 * 
		 * 
		 * 
		 * fw.write(fstring);
		 * 
		 * }
		 * 
		 * fw.close();
		 * 
		 * } catch(IOException e) {
		 * 
		 * getLogger().throwing("MainApp","addMarkers",e);
		 * 
		 * }
		 */

		getLogger().exiting("MainApp", "addMarkers");
	}

	private class run_addMarkers implements Runnable {
		private int chromosome;
		private MarkerCollection markers;

		run_addMarkers(int chromosome, MarkerCollection markers) {
			this.chromosome = chromosome;
			this.markers = markers;
		}

		public void run() {
			window.addMarkers(chromosome, markers);
		}
	}

	public void clearMarkers() {
		getLogger().entering("MainApp", "clearMarkers");

		if (window == null)
			return;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				window.clearMarkers();
			}
		});

		getLogger().exiting("MainApp", "clearMarkers");
	}

	public void repaint() {
		if (window == null)
			return;

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					window.repaint();
				}
			});
		} catch (InterruptedException e) {
			getLogger().throwing("MainApp", "repaint", e);
		} catch (InvocationTargetException e) {
			getLogger().throwing("MainApp", "repaint", e);
		}
	}

	public void setConsensusMode(boolean mode) {

		window.setConsensusMode(mode);

	}

	public static void main(String[] args) {
		MainApp app = getInstance();
		getLogger().entering("MainApp", "main");

		// read arguments
		try {
			readDataDir(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		if (app != null) {
			app.open();
		}
		getLogger().exiting("MainApp", "main");
	}

	private static void readDataDir(String[] args) throws Exception {

		String speciesString;
		if (args.length > 0) {
			speciesString = args[0];
		} else {
			speciesString = "homo_sapiens";
		}

		// put datadir into global config
		GlobalConfig.getInstance().setSpeciesString(speciesString);
		readConfigFile(speciesString);

	}

	private static void readConfigFile(String species) throws Exception {

		String path = "/ideogram/data/ideogram/" + species;
		String name = path + "/config";
		
		
		
		
		
		InputStream stream = Class.class.getResourceAsStream(name);
//		System.out.println(name);
		if (stream == null) {
			throw new Exception("no config file found at " + name
					+ ". Invalid species String");
		}

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));

		String line;
		int lineNumber = 0;

		while ((line = reader.readLine()) != null) {
			lineNumber++;
			line = line.trim();
			if (line.startsWith("#")) {
				continue;
			}
			String[] splitline = line.split("=");
			if (splitline.length != 2) {
				throw new Exception("invalid config format at line "
						+ lineNumber);
			}
			String key = splitline[0];
			String value = splitline[1];
			if (key.equals("total.chromosome.count")) {
				try {
					GlobalConfig.getInstance().setChromosomeCount(
							Byte.parseByte(value));
				} catch (NumberFormatException e) {
					throw new Exception(
							"invalid \"total.chromosome.count\" value at line "
									+ lineNumber);
				}
			} else if (key.equals("gene.reference.string")) {
				GlobalConfig.getInstance().setGeneReferenceString(value);
			} else {
				System.setProperty(splitline[0], splitline[1]);
			}
		}

	}
}
