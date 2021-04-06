package org.group15;

import org.jfree.chart.ChartPanel;
import org.jfree.data.time.Second;

import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * The AddChartView class creates an interface which displays the types of data selected
 * along with buttons to allow for showing or hiding the specified type of data.
 * It also creates a new Chart object and displays it.
 * The class also allows for adjusting the granularity of the created chart.
 */
public class AddChartView{
    private JPanel contentPane;
    private JPanel granPanel;
    private String name;
    private JTabbedPane tabbedPane;
    private TabbedViewController tVC;
    private ArrayList<String> trueList;
    private boolean[] filterList;
    private Charts chart1;
    private Second s1;
    private Second s2;
    private QueryEngine qEngine;
    private JPanel subDataType;
    private Map<Interval,Charts> cache = new HashMap<>();  //creating a "cache" allowing to reload previously loaded charts quickly
    private Persistence persistence;
    private JFrame popup;
    private Charts histo;
    private JPanel dataTypePanel = new JPanel();


    public AddChartView(String name, int[] dateFrom, int[] dateTo, Map dataTypes, Map filterBools, QueryEngine engine, TabbedViewController tVC, Persistence persistence) {
        this.name = name;
        this.persistence = persistence;
        this.tabbedPane = tVC.getTabbedPane();
        this.tVC = tVC;
        s1 = new Second(0, 0, 0, dateFrom[0], dateFrom[1], dateFrom[2]);
        s2 = new Second(59, 59, 23, dateTo[0], dateTo[1], dateTo[2]);
        qEngine = engine;

        JPanel contentPane = new JPanel(new BorderLayout());

        JScrollPane dataTypeContainer = new JScrollPane();
        dataTypeContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        dataTypePanel.setLayout(new BorderLayout());
        dataTypePanel.add(new JLabel("Key"), BorderLayout.NORTH);

        JScrollBar scrollBar = dataTypeContainer.getVerticalScrollBar();
        dataTypeContainer.remove(scrollBar);
        dataTypePanel.add(scrollBar, BorderLayout.EAST);

        subDataType = new JPanel();  //a subpanel within dataTypePanel which contains the selected data types and the buttons to show or hide the data on the graph
        GroupLayout layout = new GroupLayout(subDataType);
        subDataType.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //chartPanel.setLayout(new BorderLayout());  //layout of chartPanel
        filterList = new boolean[5];
        for(Object key : filterBools.keySet()) {
        	if(filterBools.get(key).equals(true)) {
        		switch((String) key) {
        			case "All":
        				filterList[0] = true;
        				break;
        			case "Age Range":
        				filterList[1] = true;
        				break;
        			case "Gender Range":
        				filterList[2] = true;
        				break;
        			case "Income Range":
        				filterList[3] = true;
        				break;
        			case "Context Range":
        				filterList[4] = true;
        				break;
        		}
        	}
        }

        GroupLayout.ParallelGroup parallel = layout.createParallelGroup();
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(parallel));
        GroupLayout.SequentialGroup sequential = layout.createSequentialGroup();
        layout.setVerticalGroup(sequential);

       for (Object key : dataTypes.keySet()) {  //iterate over the different data types
            if (dataTypes.get(key).equals(true)) {  //if selected, initialise with "Show" button
            	
            	if(filterList[0]) {
            		createLegendButtons(key.toString(), layout, parallel, sequential);
            	}
            	
            	if(filterList[1]) {
            		createLegendButtons(key.toString() + ": < 25 y/o", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": 25 - 34 y/o", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": 35 - 44 y/o", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": 45 - 54 y/o", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": > 54 y/o", layout, parallel, sequential);
            	}
            	
            	if(filterList[2]) {
            		createLegendButtons(key.toString() + ": Female", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": Male", layout, parallel, sequential);
            	}
            	
            	if(filterList[3]) {
            		createLegendButtons(key.toString() + ": Low $$", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": Medium $$", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": High $$", layout, parallel, sequential);
            	}
            	
            	if(filterList[4]) {
            		createLegendButtons(key.toString() + ": Blog", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": Hobbies", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": Media", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": News", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": Shopping", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": Social", layout, parallel, sequential);
            		createLegendButtons(key.toString() + ": Travel", layout, parallel, sequential);
            	}
            }
        }

        for (int i = 0; i < 12-(dataTypes.size()); i++) {  //for loop to fill blank space in the grid
            subDataType.add(new JLabel(""));
            subDataType.add(new JLabel(""));
        }
        dataTypeContainer.add(subDataType);
        dataTypeContainer.setViewportView(subDataType);
        dataTypePanel.add(dataTypeContainer);

        trueList = new ArrayList<>();  //list of all data types which are to be shown(have value as true)
        for (Object key : dataTypes.keySet()) {
            if (dataTypes.get(key).equals(true)) {
                trueList.add((String) key);
            }
        }
        
        chart1 = new Charts(name, trueList, filterList, s1, s2, engine, Interval.Day);
        cache.put(Interval.Day,chart1);

        //new panel to contain buttons to adjust granularity
        granPanel = new JPanel();
        granPanel.setLayout(new FlowLayout());
        //3 buttons displaying types of granularity
        JButton hB1 = new JButton("Hour");
        JButton dB1 = new JButton("Day");
        JButton mB1 = new JButton("Month");
        granPanel.add(hB1); granPanel.add(dB1); granPanel.add(mB1);

        JButton histogramButton = new JButton("Visualize Histogram");
        JButton fullScreen = new JButton("Full Screen");
        granPanel.add(histogramButton, BorderLayout.SOUTH);
        granPanel.add(fullScreen, BorderLayout.SOUTH);
        contentPane.add(granPanel, BorderLayout.SOUTH);

        hB1.addActionListener(new GranularityListener(granPanel, Interval.Hour));
        dB1.addActionListener(new GranularityListener(granPanel, Interval.Day));
        mB1.addActionListener(new GranularityListener(granPanel, Interval.Month));

        histogramButton.addActionListener(new HistoListener());

        fullScreen.addActionListener(new FSListener());
        contentPane.add(dataTypePanel, BorderLayout.WEST);
        contentPane.add(chart1, BorderLayout.CENTER);
        contentPane.addComponentListener(new ComponentListener() {
    		
		    public void componentResized(ComponentEvent e) {
		        ChartPanel cp = (ChartPanel)chart1.getComponents()[0];
		        cp.setPreferredSize(new java.awt.Dimension(chart1.getWidth(), chart1.getHeight() - 10));
		        cp.setSize(new java.awt.Dimension(chart1.getWidth(), chart1.getHeight()));
		        contentPane.validate();
		    }
			public void componentMoved(ComponentEvent e) {    				
			}
			public void componentShown(ComponentEvent e) {
			}
			public void componentHidden(ComponentEvent e) {
			}
		});

        this.contentPane = contentPane;
    }
    
    private void createLegendButtons(String name,GroupLayout layout, ParallelGroup parallel, SequentialGroup seq) {
    	JLabel keyLabel = new JLabel(name);
        JButton b1 = new JButton("Hide");
        //b1.setPreferredSize(new Dimension(50,50));
        b1.addActionListener(new ShowHideListener(b1, name));

        parallel.addGroup(layout.createSequentialGroup().
                addComponent(keyLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(b1));
        seq.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(keyLabel).addComponent(b1).addGap(45));
    }

    public JPanel getContent() {
        return contentPane;
    }

    public JPanel getTitle() {
        JLabel titleLabel = new JLabel(this.name);
        Font titleFont = new Font("Arial", Font.BOLD, 18);
        titleLabel.setFont(titleFont);
        JButton titleClose = new JButton("x");
        //titleClose.setFont(titleFont);

        GridBagConstraints gCConstraints = new GridBagConstraints();
        gCConstraints.gridx = 0;
        gCConstraints.gridy = 0;
        gCConstraints.weightx = 1;

        ChartViewListener cListener = new ChartViewListener(this.name, this.tabbedPane);
        titleClose.addActionListener(cListener);
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, gCConstraints);
        gCConstraints.gridx = 1;
        gCConstraints.weightx = 0;
        titlePanel.add(titleClose, gCConstraints);

        return titlePanel;

    }

    //listener to manage what happens on clicking the "Show"/"Hide" button
    private class ShowHideListener implements ActionListener {
        JButton button;
        String key;

        public ShowHideListener(JButton button, String key) {
            this.button = button;
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(button.getText().equals("Show")) {
                chart1.toggleLine(key, true);
                button.setText("Hide");
            } else {
                chart1.toggleLine(key, false);
                button.setText("Show");
            }
        }
    }

    //listener to handle the closing of chart tabs
    private class ChartViewListener implements ActionListener {
        private JTabbedPane tabbedPane;
        private String tabName;

        public ChartViewListener(String tabName, JTabbedPane tabbedPane) {
            this.tabName = tabName;
            this.tabbedPane = tabbedPane;
        }


        public void actionPerformed(ActionEvent evt) {
            tabbedPane.remove(this.getIndexOfTab());
            persistence.removeQuery(name);
        }

        private int getIndexOfTab() {
            for (int count = 0; count < tabbedPane.getTabCount(); count++){
                //System.out.println('"' + tabbedPane.getTitleAt(count) + '"');
                if (tabbedPane.getTitleAt(count).equals(tabName)) {
                    return count;
                }
            }
            //Should never happen.
            return tabbedPane.getTabCount() -1;
        }


    }

    //listener to handle what happens on selecting a type of granularity
    private class GranularityListener implements ActionListener {
        JPanel cPanel;
        Interval interval;

        public GranularityListener(JPanel panel, Interval intvl) {
            cPanel = panel;
            interval = intvl;

        }

        @Override
        public void actionPerformed(ActionEvent e) {

            contentPane.removeAll();
            //if the granularity has been selected before, reload the chart with that granularity.
            //used to save time on generating charts
            if(cache.containsKey(interval)) chart1 = cache.get(interval);
            else {
                LoadingPopup loadingPopup = new LoadingPopup(tVC.getWindow().getX(), tVC.getWindow().getY(), tVC.getWindow().getSize());
                chart1 = new Charts(name, trueList, filterList, s1, s2, qEngine, interval);
                cache.put(interval, chart1);
                loadingPopup.dispose();
            }
            for (Object key : trueList) {  //iterate over the different data types
               	if(filterList[0]) {
                   	chart1.toggleLine(key.toString(), true);
                }
                    	
                if(filterList[1]) {
               		chart1.toggleLine(key.toString() + ": < 25 y/o", true);
               		chart1.toggleLine(key.toString() + ": 25 - 34 y/o", true);	
               		chart1.toggleLine(key.toString() + ": 35 - 44 y/o", true);
               		chart1.toggleLine(key.toString() + ": 45 - 54 y/o", true);
               		chart1.toggleLine(key.toString() + ": > 54 y/o", true);
               	}
                    	
                if(filterList[2]) {
               		chart1.toggleLine(key.toString() + ": Female", true);
                   	chart1.toggleLine(key.toString() + ": Male", true);
                }
                    	
                if(filterList[3]) {
                	chart1.toggleLine(key.toString() + ": Low $$", true);
                	chart1.toggleLine(key.toString() + ": Medium $$", true);
                	chart1.toggleLine(key.toString() + ": High $$", true);
                }
                    	
                   	if(filterList[4]) {
                   		chart1.toggleLine(key.toString() + ": Blog", true);
                   		chart1.toggleLine(key.toString() + ": Hobbies", true);
                   		chart1.toggleLine(key.toString() + ": Media", true);
                   		chart1.toggleLine(key.toString() + ": News", true);
                   		chart1.toggleLine(key.toString() + ": Shopping", true);
                   		chart1.toggleLine(key.toString() + ": Social", true);
                   		chart1.toggleLine(key.toString() + ": Travel", true);
                   	}
  
            }
            contentPane.add(chart1);

            contentPane.updateUI();
            for (int bCount = 0; bCount < subDataType.getComponentCount(); bCount++) {
               if (subDataType.getComponent(bCount) instanceof JButton){
                   ((JButton) subDataType.getComponent(bCount)).setText("Hide");
               }
            }
            contentPane.add(granPanel, BorderLayout.SOUTH);
            contentPane.add(dataTypePanel, BorderLayout.WEST);
        }
    }

    private class HistoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            popup = new JFrame();
            popup.setLayout(new BorderLayout());
            
            histo = new Charts(name, s1, s2, filterList, qEngine, "age");  //creating a new histogram


            popup.add(histo, BorderLayout.CENTER);
            
           // contentPane.setLayout(new BorderLayout());
            
            popup.addComponentListener(new ComponentListener() {
        		
    		    public void componentResized(ComponentEvent e) {
    		        ChartPanel cp = (ChartPanel)histo.getComponents()[0];
    		        cp.setPreferredSize(new java.awt.Dimension(histo.getWidth(), histo.getHeight() - 10));
    		        cp.setSize(new java.awt.Dimension(histo.getWidth(), histo.getHeight()));
    		        popup.validate();
    		    }
    			public void componentMoved(ComponentEvent e) {    				
    			}
    			public void componentShown(ComponentEvent e) {
    			}
    			public void componentHidden(ComponentEvent e) {
    			}
    		});

            popup.setSize(920,580);
            popup.setVisible(true);
            popup.setMinimumSize(new Dimension(920,580));
        }
    }

    
    private class FSListener implements ActionListener {

    	FSListener(){}
    	
    	  public void actionPerformed(ActionEvent e) {
    		  JFrame popup = new JFrame();
    		  popup.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
              ChartPanel hmm = new ChartPanel(chart1.getChart());

              popup.add(hmm);
            //  contentPane.setLayout(new BorderLayout());
              popup.addComponentListener(new ComponentListener() {
          		
      		    public void componentResized(ComponentEvent e) {
      		        ChartPanel cp = (ChartPanel)chart1.getComponents()[0];
      		        cp.setPreferredSize(new java.awt.Dimension(chart1.getWidth(), chart1.getHeight() - 10));
      		        cp.setSize(new java.awt.Dimension(chart1.getWidth(), chart1.getHeight()));
      		        popup.validate();
      		    }
      			public void componentMoved(ComponentEvent e) {
      			}
      			public void componentShown(ComponentEvent e) {
      			}
      			public void componentHidden(ComponentEvent e) {
      			}
      		});

              popup.setSize(920,580);
              popup.setVisible(true);
              popup.setMinimumSize(new Dimension(920,580));
          }
    }

}
