package ideogram.view;

import ideogram.IProgressNotifier;
import ideogram.MainApp;
import ideogram.db.IdeogramDB;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.swing.*;

import util.GlobalConfig;

public class ChromosomalPanelTest extends JFrame {

	private static final long serialVersionUID = 1L;

	private IdeogramDB db;

	public ChromosomalPanelTest() {
		super("test");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		db = loadIdeogramDatabase(null);

		JPanel scrollPanel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.VERTICAL;

		for (byte i = 1; i <= GlobalConfig.getInstance().getChromosomeCount(); ++i) {
			c.gridx = (int) i - 1;
			scrollPanel.add(new ChromosomalPanel(db, i));
		}

		JScrollPane scroller = new JScrollPane(scrollPanel);

		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		getContentPane().add(scroller);
		pack();
		setPreferredSize(new Dimension(800, 400));
	}

	public IdeogramDB loadIdeogramDatabase(IProgressNotifier notifier) {
		IdeogramDB ideogramDB = null;
		try {
			InputStream stream;
			String name;

			name = "../data/ideogram";
			stream = getClass().getResourceAsStream(name);
			if (stream == null) {
				name = "../data/ideogram.gz";
				stream = getClass().getResourceAsStream(name);
				if (stream != null) {
					stream = new GZIPInputStream(stream);
				}
			}

			if (stream == null) {
				MainApp.getLogger().warning(
						"cannot find resource name '" + name + "'");
				return null;
			}

			ideogramDB = new IdeogramDB();
			if (!ideogramDB.read(new InputStreamReader(stream), notifier))
				return null;
		} catch (Exception e) {
			MainApp.getLogger().throwing("MainApp", "loadDatabase", e);
			return null;
		}
		return ideogramDB;
	}

	public static void main(String[] argv) {
		JFrame panel = new ChromosomalPanelTest();
		panel.pack();
		panel.setVisible(true);
		System.out.println(Integer.MAX_VALUE);
	}
}
