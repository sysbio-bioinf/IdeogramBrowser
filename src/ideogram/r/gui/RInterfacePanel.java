/*
 * File: RInterfacePanel.java Created: 11.12.2007 Author: Ferdinand Hofherr
 * <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.FileTypeRecord;
import ideogram.r.RController;
import ideogram.r.RDataSetWrapper;
import ideogram.r.exceptions.RException;
import ideogram.r.exceptions.UnsupportedFileTypeException;
import ideogram.r.rlibwrappers.RAnalysisWrapper;
import ideogram.r.rlibwrappers.RFileParser;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.rosuda.JRI.Rengine;

/**
 * Stub class for R interface panels.
 * 
 * @author Ferdinand Hofherr
 */
public class RInterfacePanel extends JPanel implements ActionListener {

    private static Logger logger = Logger.getLogger(RInterfacePanel.class
            .getName());

    // Action commands.
    private static final String SAMPLE_DATA = "sample data";
    private static final String SAMPLE_DATA_SEL = "sample data sel";
    private static final String OTHER_DATA = "other data";
    private static final String SAMPLE_DATA_CARD = "Card displaying sample data";
    private static final String OTHER_DATA_CARD = "Card displaying other data";
    private static final String EMPTY_CARD = "Just the empty initial card";
    private static final String LOAD_FILES_COMMAND = "Load the selected files";
    private static final String CLEAR_FIELDS_COMMAND = "Clear the file selection fields";

    private RAnalysisWrapper wrapper;
    private FileLoadingController loadingController;
    private JPanel sampleDataCardPanel; // created by createSampleDataPanel().
    private JPanel analysisFields;
    private JComboBox sampleDataCombo;
    private MessageDisplay mdp;
    private HashMap<String, JTextField> fileChooserTextFields;
    private JCheckBox multiVariableMode; // created by

    // createSampleDataPanel().

    /**
     * Create a new RInterfacePanel with the specified wrapper as model. If
     * there is no wrapper until now, pass null. If there exists no
     * MessageDisplay you can pass null too.
     * 
     * @param wrapper
     */
    public RInterfacePanel(RAnalysisWrapper wrapper, MessageDisplay mdp) {
        this.wrapper = wrapper;
        this.mdp = mdp;
        loadingController = new FileLoadingController();

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        fileChooserTextFields = new HashMap<String, JTextField>();
        sampleDataCardPanel = null;
        this.add(createSampleDataPanel());

        analysisFields = new JPanel();
        this.add(analysisFields);
    }

    /**
     * Add a {@link Component} containing the input fields. As an R library may
     * provide more than one analysis function this Component itself might
     * provide one or more analysis interfaces.
     * {@see RInterfaceBuilder#createAnalysisInterface(String)}
     * 
     * @param analysisInterface
     */
    public void addAnalysisInterface(Component analysisInterface) {
        this.analysisFields.removeAll();
        this.analysisFields.add(analysisInterface);
        this.analysisFields.validate();
        this.validate();
    }

    private JPanel createSampleDataCardPanel() {
        sampleDataCardPanel = new JPanel(new CardLayout());

        if (wrapper != null) {
            JPanel card;

            card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.PAGE_AXIS));
            JLabel l = new JLabel("Select sample data:");
            card.add(l);

            sampleDataCombo = wrapper.hasSampleData() ? new JComboBox(wrapper
                    .listSampleData().toArray()) : new JComboBox();
            sampleDataCombo.setActionCommand(SAMPLE_DATA_SEL);
            sampleDataCombo.setEnabled(wrapper.hasSampleData());
            sampleDataCombo.setSelectedIndex(-1); // Don't select anything.
            sampleDataCombo.addActionListener(this);
            card.add(sampleDataCombo);
            sampleDataCardPanel.add(SAMPLE_DATA_CARD, card);

            card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.PAGE_AXIS));
            /*
             * Add buttons and text fields for showing the JFileChoosers. The
             * selection made by the user will be shown in the corresponding
             * JTextField. The text fields are stored in a HashMap. The key for
             * each text field is the buttons action command, which in turn is
             * the string representation of the corresponding FileTypeRecord.
             * Therefore the retrieval of the right text fields should be
             * fairly easy.
             */
            JTextField tf;
            JButton btn;
            JPanel pnl;
            for (FileTypeRecord ftr : wrapper.getAcceptedFileTypes()) {
                pnl = new JPanel(new GridLayout(1, 2));
                tf = new JTextField();
                tf.setEditable(true);
                pnl.add(tf);

                btn = new JButton(ftr.getFileType().buttonLabel());
                btn.setActionCommand(ftr.toString());
                btn.addActionListener(this);
                pnl.add(btn);
                pnl.setPreferredSize(new Dimension(btn.getWidth()
                        + tf.getWidth(), pnl.getPreferredSize().height));
                card.add(pnl);

                fileChooserTextFields.put(btn.getActionCommand(), tf);
            }
            pnl = new JPanel();
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
            btn = new JButton("Load files");
            btn.setActionCommand(LOAD_FILES_COMMAND);
            btn.addActionListener(this);
            pnl.add(btn);

            pnl.add(Box.createHorizontalGlue());
            multiVariableMode = new JCheckBox("Create multiple Variables in R");
            multiVariableMode.setSelected(true);
            pnl.add(multiVariableMode);
            pnl.add(Box.createHorizontalGlue());

            btn = new JButton("Clear fields");
            btn.setActionCommand(CLEAR_FIELDS_COMMAND);
            btn.addActionListener(this);
            pnl.add(btn);

            card.add(pnl);
            sampleDataCardPanel.add(OTHER_DATA_CARD, card);

            card = new JPanel();
            sampleDataCardPanel.add(EMPTY_CARD, card);
            CardLayout cl = (CardLayout) sampleDataCardPanel.getLayout();
            cl.show(sampleDataCardPanel, EMPTY_CARD);
        }

        return sampleDataCardPanel;
    }

    private JPanel createSampleDataPanel() {
        JPanel ret = new JPanel();
        ret.setLayout(new BoxLayout(ret, BoxLayout.LINE_AXIS));

        if (wrapper != null) {
            JPanel p;

            ret.add(createSampleDataCardPanel());
            ret.add(Box.createHorizontalGlue());

            JRadioButton sampleData = new JRadioButton("Use sample data");
            sampleData.setActionCommand(SAMPLE_DATA);
            sampleData.addActionListener(this);
            sampleData.setEnabled(wrapper.hasSampleData());

            JRadioButton otherData = new JRadioButton("Use other data");
            otherData.setActionCommand(OTHER_DATA);
            otherData.addActionListener(this);
            // otherData.doClick(); // Enable and assure ActionEvent is fired.

            ButtonGroup bGroup = new ButtonGroup();
            bGroup.add(sampleData);
            bGroup.add(otherData);

            p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
            p.add(sampleData);
            p.add(otherData);
            ret.add(p);

        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        // Don't do anything if there is no wrapper.
        if (wrapper == null) {
            return;
        }

        String cmd = e.getActionCommand();
        if (cmd.equals(SAMPLE_DATA)) {
            CardLayout cl = (CardLayout) sampleDataCardPanel.getLayout();
            cl.show(sampleDataCardPanel, SAMPLE_DATA_CARD);

            sampleDataCombo.setEnabled(wrapper.hasSampleData());
        }
        else if (cmd.equals(OTHER_DATA)) {
            CardLayout cl = (CardLayout) sampleDataCardPanel.getLayout();

            try {
                loadingController.setAcceptedFileTypes(wrapper
                        .getAcceptedFileTypes());
            } catch (UnsupportedFileTypeException ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(),
                        "Exception", JOptionPane.ERROR_MESSAGE);
                cl.show(sampleDataCardPanel, EMPTY_CARD);
            } catch (RException ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(),
                        "Exception", JOptionPane.ERROR_MESSAGE);
                cl.show(sampleDataCardPanel, EMPTY_CARD);
            }
            cl.show(sampleDataCardPanel, OTHER_DATA_CARD);

            sampleDataCombo.setEnabled(false);
        }
        else if (cmd.equals(SAMPLE_DATA_SEL)) {
            RDataSetWrapper ds = (RDataSetWrapper) ((JComboBox) e.getSource())
                    .getSelectedItem();
            try {
                wrapper.loadSampleData(ds);
                setMessageDisplayText("Sample data set \"" + ds.getName()
                        + "\" loaded!");
            } catch (RException e1) {
                setMessageDisplayText(e1.getMessage());
            }
        }
        else if (cmd.equals(LOAD_FILES_COMMAND)) {
            RFileParser parser = loadingController.getParser();
            /*
             * Tell the parser about the selected files.
             */
            String text;
            String[] fileNames;
            for (FileTypeRecord ftr : wrapper.getAcceptedFileTypes()) {
                text = fileChooserTextFields.get(ftr.toString()).getText();
                fileNames = text.split("; ");
                for (int i = 0; i < fileNames.length; i++) {
                    parser.addFileName(ftr, fileNames[i]);
                }
            }
            try {
                Rengine re = RController.getInstance().getEngine();
                RController.getInstance().getRMainLoopModel().rBusy(re, 1);
                parser.loadFiles(multiVariableMode.isSelected());
                RController.getInstance().getRMainLoopModel().rBusy(re, 0);
            } catch (RException e1) {
                JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(),
                        "Exception", JOptionPane.ERROR_MESSAGE);
            }

        }
        else if (cmd.equals(CLEAR_FIELDS_COMMAND)) {
            for (JTextField tf : fileChooserTextFields.values()) {
                tf.setText("");
            }
        }
        else if (fileChooserTextFields.containsKey(cmd)) {
            JFileChooser chooser = loadingController.getFileChoosers()
                    .get(cmd);
            int retVal = chooser.showOpenDialog(this);

            if (retVal == JFileChooser.APPROVE_OPTION) {
                StringBuffer buf = new StringBuffer();

                File[] files;
                if ((files = chooser.getSelectedFiles()).length != 0) {
                    for (int i = 0; i < files.length; i++) {
                        buf.append(files[i].getAbsolutePath());
                        buf.append("; ");
                    }
                    // Remove the last two characters. They are "; ".
                    buf.delete(buf.length() - 2, buf.length());
                }
                else {
                    buf.append(chooser.getSelectedFile().getAbsolutePath());
                }
                // System.out.println("Selected: " + buf.toString());
                fileChooserTextFields.get(cmd).setText(buf.toString());
            }
        }
    }

    private void setMessageDisplayText(String text) {
        if (mdp != null) {
            mdp.displayMessage(text);
        }
    }
}
