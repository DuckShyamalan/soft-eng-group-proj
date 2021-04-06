package org.group15;


import java.awt.*;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

/**
 * @version 1.0
 *
 * This class holds the contents of each tab. Each tab contains a scrollpane, which
 * in turn contains labels with certain strings. This class defines and populates
 * the scrollpane.
 */


public class TabViewContainer extends JFrame
{
    private JScrollPane scrollPane = new JScrollPane();
    private JPanel contentPanel = new JPanel();
    private GroupLayout layout = new GroupLayout(contentPanel);
    private Font labelFont = new Font("Arial", 0, 30);

    //private Map<String,String> labels;
    private QueryEngine engine;
    private NumberFormat intFormat;
    private NumberFormat lowCostFormat;

    private JLabel numBounces;
    private JLabel bounceRate;

    private static TabViewContainer myTvc;


    /**
     *
     * @param labelMap          -- This is a Map representation of the data that will be displayed.
     *                          The keys describe a metric and their values hold the value of that metric.
     */

    public TabViewContainer(Map<String, String> labelMap, QueryEngine engine, NumberFormat intFormat, NumberFormat lowCostFormat)
    {
        myTvc = this;
        // Create panels, font and layout for the content panel.
        // The scrollpane contains the content panel, which in turn holds the values in the labelMap.
        JPanel contentPanel = new JPanel();
        GroupLayout layout = new GroupLayout(contentPanel);
        Font labelFont = new Font("Arial", 0, 30);
        contentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //labels = labelMap;
        this.engine = engine;
        this.intFormat = intFormat;
        this.lowCostFormat = lowCostFormat;

        // The horizontal group has two columns, key and value.
        ParallelGroup parallel = layout.createParallelGroup();
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(parallel));
        // The vertical group has just one.
        SequentialGroup sequential = layout.createSequentialGroup();
        layout.setVerticalGroup(sequential);

        // For each pair of values in the map, a new group is created that contains them both, which is then added to
        // the horizontal group. Then the same happens for the vertical group.
        // This has the effect of creating a list of key-value pairs displayed in order from top to bottom.
        for (String key : labelMap.keySet()) {
            JLabel keyLabel = new JLabel(key);
            keyLabel.setFont(labelFont);
            JLabel keyValue = new JLabel(labelMap.get(key));
            if (key.equals("Number of bounces")) {
                numBounces = keyValue;
            } else if (key.equals("Bounce Rate")) {
                bounceRate = keyValue;
            }
            keyValue.setFont(labelFont);
            keyLabel.setLabelFor(keyValue);
            parallel.addGroup(layout.createSequentialGroup().
                    addComponent(keyLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                    GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(keyValue));
            sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                    addComponent(keyLabel).addComponent(keyValue).addGap(60));
        }

        // This sets the contents of the scrollpane to be the panel which contains all the keys and values.
        scrollPane.setViewportView(contentPanel);
        add(scrollPane);


    }

    public static void replaceBounce(){
        //labels.put("Number of bounces", intFormat.format(engine.getBouncesCount()));
        //labels.put("Bounce Rate", lowCostFormat.format(engine.getBounceRate()));
        myTvc.numBounces.setText(myTvc.intFormat.format(myTvc.engine.getBouncesCount()));
        myTvc.bounceRate.setText(myTvc.lowCostFormat.format(myTvc.engine.getBounceRate()));
    }

    // Return the scrollpane. Used by classes to add tabs to it.
    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }

}


