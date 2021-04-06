package org.group15;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * @version 1.0
 *
 *
 * This is the class of each tab.
 */


public class TabView{

    private JTabbedPane tabbedPane;
    private JPanel panel;

    /**
     *
     * @param tabbedPaneImport  -- This is the tabbed pane that each tab goes into.
     * @param index             -- This is the position of the tab relative to the others. It counts up as tabs are created.
     * @param tabName           -- This is the name of the tab.
     *
     *
     * This class creates and maintains a tab and its listener(s). At the moment it is mostly used for
     * referring to tabs by name. Its functionality will be expanded in the second sprint.
     */
    public TabView(JTabbedPane tabbedPaneImport, int index, String tabName) {

        // Create a panel with the name of the tab. Code that added a quit button has been added as
        // placeholder and commented out.
        this.tabbedPane = tabbedPaneImport;
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(tabName);
        Font titleFont = new Font("Arial", Font.BOLD, 18);
        lblTitle.setFont(titleFont);

        // Spacing arguments for the tab panel. This will be important when the close button etc. are added.
        GridBagConstraints gCConstraints = new GridBagConstraints();
        gCConstraints.gridx = 0;
        gCConstraints.gridy = 0;
        gCConstraints.weightx = 1;

        pnlTab.add(lblTitle, gCConstraints);


        // Make sure the user cannot close the first tab or the tab that adds tabs.
        // Saving the user from himself, as detailed in the specification.
        if (index > 0 ) {
            JButton btnClose = new JButton("x");
            gCConstraints.gridx++;
            gCConstraints.weightx = 0;
            pnlTab.add(btnClose, gCConstraints);
            TabViewListener tabListener = new TabViewListener(tabName, tabbedPane);
            btnClose.addActionListener(tabListener);

        }

        this.panel = pnlTab;
    }

    public JPanel getTab() {
        return panel;
    }


    // The code that makes the close button work.
    public class TabViewListener implements ActionListener {
        private JTabbedPane tabbedPane;
        private String tabName;

        public TabViewListener(String tabName, JTabbedPane tabbedPane) {
            this.tabName = tabName;
            this.tabbedPane = tabbedPane;
        }

        public void actionPerformed(ActionEvent evt) {
            tabbedPane.remove(getIndexOfTab());


        }

        // Does what it says on the tin.
        private int getIndexOfTab() {
            for (int count = 0; count < tabbedPane.getTabCount(); count++){
                if (tabbedPane.getTitleAt(count).equals(tabName)) {
                    return count;
                }
            }
            //Should never happen.
            return tabbedPane.getTabCount() -1;
        }


    }

}


