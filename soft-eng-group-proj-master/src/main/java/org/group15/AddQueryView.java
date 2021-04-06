package org.group15;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * <p>
 * This class handles the addition of custom "Query" tabs to the tabbedPane.
 * Every option the user can toggle or select a value for is tracked, and when the "Create" button
 * is pressed, the new tab is created.
 * <p>
 * To ensure that this tab is always the rightmost one, it is destroyed and rebuilt every time.
 * This has the useful side effect of giving focus to the new query created.
 */

public class AddQueryView extends JFrame
{
    private JPanel container = new JPanel(new BorderLayout());
    private JCheckBox ctr;  //line 93 onwards
    private JCheckBox cpm;
    private JCheckBox cpc;
    private JCheckBox cpa;
    private JCheckBox bounceRate;
    private JCheckBox totalCost;
    private JCheckBox nBounces;
    private JCheckBox nClicks;
    private JCheckBox nImp;
    private JCheckBox nConv;
    private JCheckBox nUniq;

    private JCheckBox allRange;  //line 198 onwards
    private JCheckBox ageRange;
    private JCheckBox genderRange;
    private JCheckBox incomeRange;
    private JCheckBox contextRange;

    private JTextField qNameField;
    private JRadioButton imp; //radio buttons - to erase
    private JRadioButton conv;
    private JRadioButton click;
    private JTextField dateF;
    private JTextField dateT;

    //These two are for getting data from the database and reconstructing this tab.
    private QueryEngine engine;
    private TabbedViewController tVC;
    private Persistence persistence;

    public AddQueryView(QueryEngine engine, TabbedViewController tVC, Persistence persistence)
    {

        this.persistence = persistence;
        this.tVC = tVC;
        this.engine = engine;

        Border thinBorder = LineBorder.createBlackLineBorder();

        // Contains everything.
        JPanel canvasPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // Query Name panel (as in, the part of the query tab that allows the user to pick a name
        //  for their new query tab).
        JPanel qNamePanel = new JPanel();
        Font qNFont = new Font("Arial", Font.BOLD, 20);
        JLabel qName = new JLabel("Query Name: ");
        qNameField = new JTextField();
        qNameField.setColumns(20);
        qName.setFont(qNFont);
        qNamePanel.add(qName);
        qNamePanel.add(qNameField);
        canvasPanel.add(qNamePanel);

        // Date and Radio Buttons panel.
        JPanel dateOptionPanel = new JPanel(); //contains date panel and option panel as 2x1 grid
        dateOptionPanel.setLayout(new GridLayout(2, 1, 10, 10));
        dateOptionPanel.setMinimumSize(new Dimension(450, 550));

        // Checkbox panel.
        JPanel typesDataPanel = new JPanel();
        typesDataPanel.setLayout(new GridLayout(12, 1));
        typesDataPanel.setBorder(thinBorder);

        // All possible checkbox options:
        JLabel dataTypes = new JLabel("Data Types: ");
        dataTypes.setFont(new Font("Arial", Font.BOLD, 14));
        nImp = new JCheckBox("Number of Impressions");
        nClicks = new JCheckBox("Number of Clicks");
        nBounces = new JCheckBox("Number of Bounces");
        nUniq = new JCheckBox("Number of Uniques");
        nConv = new JCheckBox("Number of Conversions");
        totalCost = new JCheckBox("Total Cost");
        ctr = new JCheckBox("CTR");
        cpa = new JCheckBox("CPA");
        cpc = new JCheckBox("CPC");
        cpm = new JCheckBox("CPM");
        bounceRate = new JCheckBox("Bounce Rate");

        typesDataPanel.add(dataTypes);
        typesDataPanel.add(nImp);
        typesDataPanel.add(nClicks);
        typesDataPanel.add(nBounces);
        typesDataPanel.add(nConv);
        typesDataPanel.add(nUniq);
        typesDataPanel.add(totalCost);
        typesDataPanel.add(ctr);
        typesDataPanel.add(cpa);
        typesDataPanel.add(cpc);
        typesDataPanel.add(cpm);
        typesDataPanel.add(bounceRate);

        typesDataPanel.setMinimumSize(new Dimension(450, 550));

        // Create button code:
        JPanel createButtonPanel = new JPanel();
        JButton createButton = new JButton("Create");
        createButton.addActionListener(new CreateListener());
        createButtonPanel.add(createButton);


        // Date Picker:
        JPanel qDatePanel = new JPanel(new GridLayout(5, 3));
        JLabel dateLabel = new JLabel("Query Date:");
        JLabel fromLabel = new JLabel("From:");
        JLabel toLabel = new JLabel("To:");
        dateLabel.setFont(new Font("Arial", 1, 12));
        qDatePanel.add(dateLabel);
        qDatePanel.add(new JLabel(""));
        qDatePanel.add(fromLabel);
        qDatePanel.add(new JLabel(""));

        // First date textbox + button:
        DatePicker datePick1 = new DatePicker();
        ImageIcon ii1 = datePick1.getImage();
        JButton dateButton1 = new JButton(ii1);
        dateF = new JTextField();
        qDatePanel.add(dateF);
        dateButton1.setPreferredSize(new Dimension(30, 24));
        dateButton1.setMargin(new Insets(0, 0, 0, 0));
        dateButton1.addActionListener(e ->
        {
            datePick1.setDate(dateF.getText());
            datePick1.popupShow(dateButton1);
        });
        datePick1.addPopupListener(e ->
        {
            dateF.setText(datePick1.getFormattedDate());
            datePick1.popupHide();
        });
        qDatePanel.add(dateButton1);
        qDatePanel.add(toLabel);
        qDatePanel.add(new JLabel(""));

        // Second date textbox + button:
        DatePicker datePick2 = new DatePicker();
        ImageIcon ii2 = datePick2.getImage();
        JButton dateButton2 = new JButton(ii2);
        dateT = new JTextField();
        qDatePanel.add(dateT);
        dateButton2.setPreferredSize(new Dimension(30, 24));
        dateButton2.setMargin(new Insets(0, 0, 0, 0));
        dateButton2.addActionListener(e ->
        {
            datePick2.setDate(dateT.getText());
            datePick2.popupShow(dateButton2);
        });
        datePick2.addPopupListener(e ->
        {
            dateT.setText(datePick2.getFormattedDate());
            datePick2.popupHide();
        });
        qDatePanel.add(dateButton2);


        qDatePanel.setBorder(thinBorder);
        JPanel qOptionPanel = new JPanel(new GridLayout(6, 1));
        JLabel chooseFilter = new JLabel("Select your filters:");
        chooseFilter.setFont(new Font("Arial", Font.BOLD, 14));
        //with the checkboxes, do what we did with typesOfData? (functionality to be implemented)
        allRange = new JCheckBox("Overall");
        ageRange = new JCheckBox("Age");
        genderRange = new JCheckBox("Gender");
        incomeRange = new JCheckBox("Income");
        contextRange = new JCheckBox("Context");
        qOptionPanel.add(chooseFilter);
        qOptionPanel.add(allRange);
        qOptionPanel.add(ageRange);
        qOptionPanel.add(genderRange);
        qOptionPanel.add(incomeRange);
        qOptionPanel.add(contextRange);
        qOptionPanel.setBorder(thinBorder);

        // Layout organisation.
        dateOptionPanel.add(qDatePanel);
        dateOptionPanel.add(qOptionPanel);

        JPanel centerOut = new JPanel(new GridLayout(1, 2, 50, 50));
        centerOut.add(dateOptionPanel);
        centerOut.add(typesDataPanel);

        container.setBorder(new EmptyBorder(0, 30, 0, 30));
        container.add(canvasPanel, BorderLayout.NORTH);
        container.add(createButtonPanel, BorderLayout.SOUTH);
        container.add(centerOut, BorderLayout.CENTER);


    }

    // Get contents of Query Tab (to add them to a new tab on tabbedPane)
    public JPanel getContents()
    {
        return container;
    }

    /**
     * This handles the creation of custom query tabs.
     */

    public void createTab(String tabName, int[] dateFrom, int[] dateTo, Map boolMap, Map filterBools)
    {

        JTabbedPane tabbedPane = tVC.getTabbedPane();

        AddChartView chartView = new AddChartView(tabName, dateFrom, dateTo, boolMap, filterBools, engine, tVC, persistence);
        tabbedPane.add(chartView.getContent());
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, chartView.getTitle());
        tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1, tabName);

        // Create new Query object to store this new Query.

        // Remove and re-add this tab to ensure it is always on the right side.
        tabbedPane.remove(tabbedPane.getComponentAt(tabbedPane.getTabCount() - 2));
        tVC.addQ(engine);

    }

    public class CreateListener implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            String regexpr = "(?<day>\\d{1,2})\\/(?<month>\\d{1,2})\\/(?<year>\\d{4})";

            try
            {
                if ((dateF.getText().equals("")) || (dateT.getText().equals("")) || (DateTime.parseDate(dateT.getText(), regexpr).compareTo(DateTime.parseDate(dateF.getText(), regexpr)) < 0))
                {
                    ErrorHandling.displayError("The date period you have entered is invalid", false);
                    dateF.setText("");
                    dateT.setText("");
                }
                else if ((!allRange.isSelected()) && (!ageRange.isSelected()) && (!genderRange.isSelected()) && (!incomeRange.isSelected()) && (!contextRange.isSelected()))
                {
                    ErrorHandling.displayError("At least one of the filters must be selected!", false);
                }
                else
                {

                    // Adding a blank space in the end of the tab name prevents the mess that results
                    // from an empty name and it makes the tab look better.
                    JTabbedPane tabbedPane = tVC.getTabbedPane();
                    String tabName = qNameField.getText() + " ";
                    int sameTabName = 0;
                    for (int tabCount = 0; tabCount < tabbedPane.getTabCount(); tabCount++)
                    {
                        System.out.println(tabbedPane.getTitleAt(tabCount));
                        if (tabbedPane.getTitleAt(tabCount).equals(tabName))
                        {
                            sameTabName++;
                        }
                    }

                    // If the same name exists, append an underscore and the number of duplicates of this name.
                    if (sameTabName > 0)
                    {
                        tabName = tabName + "_" + sameTabName;
                    }

                    if (tabName.equals(" "))
                    {
                        ErrorHandling.displayError("Query Name cannot be empty!", false);
                    }
                    else
                    {
                        int[] dateFrom = Arrays.stream(dateF.getText().split("/")).mapToInt(Integer::parseInt).toArray();
                        int[] dateTo = Arrays.stream(dateT.getText().split("/")).mapToInt(Integer::parseInt).toArray();

                        // Putting all the options in a HashMap. This makes the class more easily extensible,
                        //  especially in cases where these might need to be segmented.
                        Map<String, Boolean> boolMap = new HashMap<>();
                        boolMap.put("Number of Impressions", nImp.isSelected());
                        boolMap.put("Number of Clicks", nClicks.isSelected());
                        boolMap.put("Number of Conversions", nConv.isSelected());
                        boolMap.put("Number of Bounces", nBounces.isSelected());
                        boolMap.put("Total Cost", totalCost.isSelected());
                        boolMap.put("CTR", ctr.isSelected());
                        boolMap.put("CPA", cpa.isSelected());
                        boolMap.put("CPC", cpc.isSelected());
                        boolMap.put("CPM", cpm.isSelected());
                        boolMap.put("Bounce Rate", bounceRate.isSelected());
                        boolMap.put("Number of Uniques", nUniq.isSelected());

                        Map<String, Boolean> filterBools = new HashMap<>();
                        filterBools.put("All", allRange.isSelected());
                        filterBools.put("Age Range", ageRange.isSelected());
                        filterBools.put("Gender Range", genderRange.isSelected());
                        filterBools.put("Income Range", incomeRange.isSelected());
                        filterBools.put("Context Range", contextRange.isSelected());


                        boolean somethingSelected = false;
                        for (Map.Entry<String, Boolean> item : boolMap.entrySet())
                        {
                            if (item.getValue())
                            {
                                somethingSelected = true;
                                break;
                            }
                        }

                        if (somethingSelected)
                        {
                            LoadingPopup loadingPopup = new LoadingPopup(tVC.getWindow().getX(), tVC.getWindow().getY(), tVC.getSize());
                            createTab(tabName, dateFrom, dateTo, boolMap, filterBools);
                            persistence.addQuery(new Query(tabName, dateFrom, dateTo, boolMap, filterBools));
                            loadingPopup.dispose();
                        }
                        else
                        {
                            ErrorHandling.displayError("Please select a metric for the graph.", false);
                        }
                    }
                }

            }
            catch (DateTimeParseException exc)
            {
                ErrorHandling.displayError("Please enter a valid date range.", false);
            }
        }
    }
}
