package org.group15;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Map;
import javax.swing.*;

/**
 * @version 2.0
 * The TabbedViewController class extends the JPanel class and contains the
 * mechanism to create the necessary tabs for clicks, impressions, and conversions.
 */
public class TabbedViewController extends JPanel {

    private JTabbedPane tabbedPane;
    private Persistence persistence;
    private AddQueryView queryView;
    private JFrame window;

    public TabbedViewController(JTabbedPane tabPane){
        this.tabbedPane = tabPane;
        this.persistence = new Persistence();

    }

    /**
     * Accepts a tab name and its labels to create a TabViewContainer object to adjust
     * the layout and add the scrollbar.
     * This function creates the first three tabs that contain static information.
     */
    public void createTab(String tabName, Map<String, String> tabLabels, QueryEngine engine, NumberFormat intFormat, NumberFormat lowCostFormat) {

        TabView newTab = new TabView(tabbedPane, tabbedPane.getTabCount(), tabName);
        TabViewContainer newTabC = new TabViewContainer(tabLabels, engine, intFormat, lowCostFormat);
        tabbedPane.add(newTabC.getScrollPane());

        tabbedPane.setTabComponentAt(tabbedPane.getTabCount()-1, newTab.getTab());

    }

    // This returns the tabbedPane, the component which holds all the tabs created.
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    // This adds the Query tab, which allows the user to create custom Queries.
    public void addQ(QueryEngine engine) {
        //TabView newTab = new TabView(tabbedPane, tabbedPane.getTabCount(), "+");
        AddQueryView newQuery = new AddQueryView(engine, this, persistence);
        tabbedPane.add(newQuery.getContents());
        JPanel qNameLabel = new JPanel();
        JLabel qName = new JLabel("+");
        Font titleFont = new Font("Arial", Font.BOLD, 18);
        qName.setFont(titleFont);

        qNameLabel.add(qName);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount()-1, qName);
        this.queryView = newQuery;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public AddQueryView getQueryView() {
        return this.queryView;
    }

    public void setWindow(JFrame window) {
        this.window = window;
    }

    public JFrame getWindow() {
        return this.window;
    }


}




