/*
 * File:	RInterfacePanel.java
 * Created: 11.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.Raster;

import ideogram.r.RDataSetWrapper;
import ideogram.r.RException;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

/**
 * Stub class for R interface panels.
 *
 * @author Ferdinand Hofherr
 *
 */
public class RInterfacePanel extends JPanel implements ActionListener{

    // Action commands.
    private static final String SAMPLE_DATA = "sample data";
    private static final String SAMPLE_DATA_SEL = "sample data sel";
    private static final String OTHER_DATA = "other data";

    private RLibraryWrapper wrapper;
    private JPanel analysisFields;
    private JComboBox sampleDataCombo;
    private MessageDisplay mdp;

    /**
     * Create a new RInterfacePanel with the specified wrapper as model. If
     * there is no wrapper until now, pass null. If there exists no 
     * MessageDisplay you can pass null too. 
     *
     * @param wrapper
     */
    public RInterfacePanel(RLibraryWrapper wrapper, MessageDisplay mdp) {
        this.wrapper = wrapper;
        this.mdp = mdp;

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
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

    private JPanel createSampleDataPanel() {
        JPanel ret =  new JPanel();
        if (wrapper != null) {
            ret.setLayout(new BoxLayout(ret, BoxLayout.LINE_AXIS));

            JPanel p = new JPanel();

            p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
            JLabel l = new JLabel("Select sample data:");
            p.add(l);

            sampleDataCombo = wrapper.hasSampleData() ? 
                    new JComboBox(wrapper.listSampleData().toArray()) : new JComboBox();
                    sampleDataCombo.setActionCommand(SAMPLE_DATA_SEL);
                    sampleDataCombo.setEnabled(false);
                    sampleDataCombo.setSelectedIndex(-1);
                    sampleDataCombo.addActionListener(this);
                    p.add(sampleDataCombo);
                    ret.add(p);

                    ret.add(Box.createHorizontalGlue());

                    JRadioButton sampleData = new JRadioButton("Use sample data");
                    sampleData.setActionCommand(SAMPLE_DATA);
                    sampleData.addActionListener(this);
                    sampleData.setEnabled(wrapper.hasSampleData());

                    JRadioButton otherData = new JRadioButton("Use other data");
                    otherData.setActionCommand(OTHER_DATA);
                    otherData.addActionListener(this);
                    otherData.setSelected(true);

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

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals(SAMPLE_DATA)) {
            sampleDataCombo.setEnabled(true);
        } 
        else if (cmd.equals(OTHER_DATA)){
            sampleDataCombo.setEnabled(false);
        }
        else if (cmd.equals(SAMPLE_DATA_SEL)) {
            RDataSetWrapper ds = (RDataSetWrapper)((JComboBox)e.getSource()).getSelectedItem();
            try {
                wrapper.loadSampleData(ds);
                setMessageDisplayText("Sample data set \"" + ds.getName() + "\" loaded!");
            } catch (RException e1) {
                setMessageDisplayText(e1.getMessage());
            }
        }
    }

    private void setMessageDisplayText(String text) {
        if (mdp != null) {
            mdp.setMessage(text);
        }
    }
}
