package org.group15;

import javax.swing.*;
import java.awt.*;
import static java.awt.BorderLayout.*;

/**
 * Title Screen, displayed when starting the program. It allows for analyzing a new ad campaign once the required
 * files are provided, or for loading an existing campaign from an adc file.
 * @version 1.2
 */


public class TitleView {

    private JFrame window = new JFrame("Ad Auction Dashboard");

    public TitleView(TabbedView tabbedView) {

        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //--- Outer container panel

        JPanel containerPanel0 = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Analyze Campaign Files or Load Campaign", SwingConstants.CENTER);
        Font titleFont = new Font("Arial", Font.BOLD, 26);
        titleLabel.setFont(titleFont);
        containerPanel0.add(titleLabel, NORTH);



        //--- End outer container panel


        //--- Center of inner container panel.

        // innerPanel0 contains containerPanel2, which is for creating a new campaign.
        // innerPanel1 contains containerPanel3, which is for loading an existing campaign.

        //--- Analyze Campaign Code

        JPanel innerPanel0 = new JPanel(new BorderLayout());
        JLabel innerTitle0 = new JLabel("Analyze New Campaign", SwingConstants.CENTER);
        Font innerFont = new Font("Arial", Font.BOLD, 20);
        innerTitle0.setFont(innerFont);
        innerPanel0.add(innerTitle0, NORTH);

        JButton analyzeButton = new JButton("Analyze");
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        analyzeButton.setFont(buttonFont);
        analyzeButton.addActionListener(actionEvent -> tabbedView.analyze());
        innerPanel0.add(analyzeButton, SOUTH);


        //--- Creating layout for containerPanel2.
        JPanel containerPanel2 = new JPanel();
        GroupLayout layout = new GroupLayout(containerPanel2);
        containerPanel2.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);


        GroupLayout.ParallelGroup parallel = layout.createParallelGroup();
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(parallel));
        GroupLayout.SequentialGroup sequential = layout.createSequentialGroup();
        layout.setVerticalGroup(sequential);

        //--- End layout creation for containerPanel2.

        //--- Creating labels and button for impression log section of containerPanel2.
        JLabel impressionLogLabel = new JLabel("Please select impression log csv file...");
        JButton impressionLogButton = new JButton("Select File...");
        JLabel iLLabel = new JLabel("Impression Log:");
        iLLabel.setFont(new Font("Arial", Font.BOLD, 15));
        impressionLogButton.addActionListener(actionEvent -> {
            String file;
            file = tabbedView.chooseCSVFile("Please Select the Impression Log CSV file:");

            if (file != null){
                tabbedView.setImpressionLog(file);
                if (file.length() > 35) {
                    file = "..." + file.substring(file.length()-32);
                    impressionLogLabel.setText(file);

                } else {
                    impressionLogLabel.setText(file);
                }
            }
        });

        //--- End creating labels and button for impression log section of containerPanel2.

        //--- Placing the labels and button for impression log section of containerPanel2.
        parallel.addGroup(layout.createSequentialGroup().
                addComponent(iLLabel));
        sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)).addComponent(iLLabel);

        parallel.addGroup(layout.createSequentialGroup().
                addComponent(impressionLogLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(impressionLogButton));
        sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(impressionLogLabel).addComponent(impressionLogButton).addGap(70));

        //--- End placing the labels and button for impression log section of containerPanel2.


        //--- Creating labels and button for server log section of containerPanel2.
        JLabel clickLogLabel = new JLabel("Please select click log csv file...");
        JButton clickLogButton = new JButton("Select File...");
        JLabel cLLabel = new JLabel("Click Log:");
        cLLabel.setFont(new Font("Arial", Font.BOLD, 15));
        clickLogButton.addActionListener(actionEvent -> {
            String file;
            file = tabbedView.chooseCSVFile("Please Select the Click Log CSV file:");

            if (file != null){
                tabbedView.setClickLog(file);
                if (file.length() > 35) {
                    file = "..." + file.substring(file.length()-32);
                    clickLogLabel.setText(file);

                } else {
                    clickLogLabel.setText(file);
                }
            }
        });

        //--- End creating labels and button for click log section of containerPanel2.

        //--- Placing the labels and button for click log section of containerPanel2.

        parallel.addGroup(layout.createSequentialGroup().
                addComponent(cLLabel));
        sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)).addComponent(cLLabel);

        parallel.addGroup(layout.createSequentialGroup().
                addComponent(clickLogLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(clickLogButton));
        sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(clickLogLabel).addComponent(clickLogButton).addGap(70));

        //--- End placing the labels and button for click log section of containerPanel2.


        //--- Creating labels and button for server log section of containerPanel2.
        JLabel serverLogLabel = new JLabel("Please select server log csv file...");
        JButton serverLogButton = new JButton("Select File...");
        JLabel sLLabel = new JLabel("Server Log:");
        sLLabel.setFont(new Font("Arial", Font.BOLD, 15));
        serverLogButton.addActionListener(actionEvent -> {
            String file;
            file = tabbedView.chooseCSVFile("Please Select the Server Log CSV file:");

            if (file != null){
                tabbedView.setServerLog(file);
                if (file.length() > 35) {
                    file = "..." + file.substring(file.length()-32);
                    serverLogLabel.setText(file);

                } else {
                    serverLogLabel.setText(file);
                }
            }
        });

        //--- End creating labels and button for server log section of containerPanel2.

        //--- Placing the labels and button for server log section of containerPanel2.

        parallel.addGroup(layout.createSequentialGroup().
            addComponent(sLLabel));
            sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)).addComponent(sLLabel);

        parallel.addGroup(layout.createSequentialGroup().
            addComponent(serverLogLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                        GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(serverLogButton));
            sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                        addComponent(serverLogLabel).addComponent(serverLogButton).addGap(70));

        //--- End placing the labels and button for server log section of containerPanel2.

        //--- Add containerPanel2 to innerPanel (one of the two inner containers of the window's content panel)
        innerPanel0.add(containerPanel2, CENTER);

        //--- End Analyze Campaign Code

        //--- Load Campaign Code

        //--- Creating Panels and button for containerPanel3.
        JPanel innerPanel1 = new JPanel(new BorderLayout());
        JLabel innerTitle1 = new JLabel("Load Campaign File", SwingConstants.CENTER);
        innerTitle1.setFont(innerFont);
        innerPanel1.add(innerTitle1, NORTH);

        JButton loadButton = new JButton("Load");
        loadButton.setFont(buttonFont);
        loadButton.addActionListener(actionEvent -> tabbedView.analyzeC(window));
        innerPanel1.add(loadButton, SOUTH);


        //--- Creating layout for containerPanel3.
        JPanel containerPanel3 = new JPanel();
        GroupLayout layout2 = new GroupLayout(containerPanel3);
        containerPanel3.setLayout(layout2);
        layout2.setAutoCreateGaps(true);
        layout2.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup parallel2 = layout2.createParallelGroup();
        layout2.setHorizontalGroup(layout.createSequentialGroup().addGroup(parallel2));
        GroupLayout.SequentialGroup sequential2 = layout2.createSequentialGroup();
        layout2.setVerticalGroup(sequential2);

        //--- End creating layout for containerPanel3.

        //--- Labels and button creation for loading a campaign file.

        JLabel LoadLabel = new JLabel("Please select a Campaign adc file...");
        JButton LoadLogButton = new JButton("Select File...");
        JLabel LLabel = new JLabel("Campaign File:");
        LLabel.setFont(new Font("Arial", Font.BOLD, 15));
        LoadLogButton.addActionListener(actionEvent -> {
            String file;
            file = tabbedView.chooseADCFile("Please Select the Campaign ADC file:");

            if (file != null){
                tabbedView.setCampaignFile(file);
                if (file.length() > 36) {
                    file = "..." + file.substring(file.length()-33);
                    LoadLabel.setText(file);

                } else {
                    LoadLabel.setText(file);
                }
           }
        });

        //--- End creating labels and button for loading a campaign file.

        //--- Placing the labels and button for loading a campaign file.

        parallel2.addGroup(layout2.createSequentialGroup().
                addComponent(LLabel));
        sequential2.addGroup(layout2.createParallelGroup(GroupLayout.Alignment.BASELINE)).addComponent(LLabel);

        parallel2.addGroup(layout2.createSequentialGroup().
                addComponent(LoadLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(LoadLogButton));
        sequential2.addGroup(layout2.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(LoadLabel).addComponent(LoadLogButton).addGap(70));

        innerPanel1.add(containerPanel3, CENTER);

        //--- End placing the labels and button for loading a campaign file.

        //--- End Load Campaign code.


        //--- End of inner container panel

        //--- Center of outer container panel

        // Creating the layout of the outer container panel
        JPanel containerPanel1 = new JPanel();
        GroupLayout outerlayout = new GroupLayout(containerPanel1);
        containerPanel1.setLayout(outerlayout);
        outerlayout.setAutoCreateGaps(true);
        outerlayout.setAutoCreateContainerGaps(true);

        // Placing the two inner panels on the outer panel.
        outerlayout.setHorizontalGroup(outerlayout.createSequentialGroup().addComponent(innerPanel0).addComponent(innerPanel1));
        outerlayout.setVerticalGroup(outerlayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(innerPanel0).addComponent(innerPanel1));


        //--- End of center of outer container panel

        // Adding everything to the content pane of the window JFrame.
        containerPanel0.add(containerPanel1, CENTER);

        // Configuring and showing the window JFrame.
        window.setContentPane(containerPanel0);
        window.setSize(920,580);
        window.setVisible(true);
        window.setMinimumSize(new Dimension(920,580));

    }

    // Return the window JFrame.
    // This is used to close and dispose it once the user has selected to load a campaign or analyze a new one.
    public JFrame getWindow() {
        return window;
    }


}
