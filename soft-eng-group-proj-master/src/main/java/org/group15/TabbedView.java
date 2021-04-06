package org.group15;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;


/**
 * @version 1.0
 * The TabbedView class contains the TabbedViewer class which works on creating the
 * interface of the application. The class also defines a TabbedViewController object
 * for presenting tabs.
 */
public class TabbedView
{

    private TabbedViewController tVC = new TabbedViewController(new JTabbedPane());
    private FileSelect fileSelect = new FileSelect();
    private TitleView titleView;
    private MainViewController mainVC;
    private QueryEngine engine;
    private JLabel loading = new JLabel("Currently loading previously saved queries, app can be used normally");

    private String impressionLog = null;
    private String clickLog = null;
    private String serverLog = null;

    private String campaignFile = null;


    /**
     * The TabbedView class extends the JFrame and is the class actually responsible
     * for creating the interface along with the necessary components.
     * The previously defined TabbedViewController object is initialised here.
     */


    // Create the window, assign a new TabbedViewController to it and show it.
    public TabbedView(MainViewController mainVC)
    {
        loading.setVisible(false);

        this.mainVC = mainVC;
        // Set the 'Look and Feel' of the GUI and make it look similar across all operating systems.

        try
        {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }

        titleView = new TitleView(this);

    }

    public void setImpressionLog(String impressionLog)
    {
        this.impressionLog = impressionLog;
    }

    public void setServerLog(String serverLog)
    {
        this.serverLog = serverLog;
    }

    public void setClickLog(String clickLog)
    {
        this.clickLog = clickLog;
    }

    public void setCampaignFile(String campaignFile)
    {
        this.campaignFile = campaignFile;
    }

    /*
    public String getImpressionLog() {
        return impressionLog;
    }

    public String getServerLog() {
        return serverLog;
    }

    public String getClickLog() {
        return clickLog;
    }
    */

    public void analyzeC(JFrame window)
    {
        try
        {
            tVC.setPersistence(Persistence.fromFile(campaignFile));

            serverLog = tVC.getPersistence().getServerLogPath();
            impressionLog = tVC.getPersistence().getImpressionLogPath();
            clickLog = tVC.getPersistence().getClickLogPath();

            if ((serverLog == null) || (clickLog == null) || (impressionLog == null))
            {
                ErrorHandling.displayError("One or more log files cannot be located or loaded! Exiting.", true);
            }
            else
            {
                /*Container old = window.getContentPane();
                Container neew = new Container();
                JPanel loadPanel = new JPanel();
                JLabel loadLabel = new JLabel();
                URL iconLoadURL = getClass().getResource("loading.png");
                ImageIcon iconLoad = new ImageIcon(iconLoadURL);
                loadLabel.setIcon(iconLoad);
                loadPanel.setLayout(new BorderLayout());
                loadPanel.add(loadLabel, BorderLayout.CENTER);
                loadPanel.add(new JButton("Useless Button pls no press"));
                neew.add(loadPanel);

                window.setContentPane(neew);*/
                new Thread(() -> {
                    loading.setVisible(true);
                    analyze();
                    if (engine != null)
                    {
                        engine.setBounceMaxSecondsOnSite(tVC.getPersistence().getBounceMaxSecondsOnSite());
                        engine.setBounceMaxPagesViewed(tVC.getPersistence().getBounceMaxPagesViewed());
                        // Create saved tabs.
                        AddQueryView qV = tVC.getQueryView();
                        for (Query q : tVC.getPersistence().procureSavedTabs())
                        {
                            qV.createTab(q.tabName, q.dateFrom, q.dateTo, q.boolMap, q.filterBools);
                        }
                    }
                    else {
                        ErrorHandling.displayError("Engine failed to initialise. Cannot proceed.", true);
                    }
                    loading.setVisible(false);
                }).start();
            }

        }
        catch (IOException e)
        {
            ErrorHandling.displayError("Provided file cannot be read!", false);
        }
    }

    public void analyze()
    {
        if ((serverLog == null) || (clickLog == null) || (impressionLog == null))
        {
            ErrorHandling.displayError("One or more log files have not yet been provided!", false);
        }
        else
        {
            try
            {
                LoadingPopup loadingPopup = new LoadingPopup(titleView.getWindow().getX(), titleView.getWindow().getY(), titleView.getWindow().getSize());
                QueryEngine engine = new QueryEngine(impressionLog, clickLog, serverLog);
                tVC.getPersistence().setImpressionLogPath(impressionLog);
                tVC.getPersistence().setClickLogPath(clickLog);
                tVC.getPersistence().setServerLogPath(serverLog);
                mainVC.startAnalyzing(engine);
                titleView.getWindow().setVisible(false);
                titleView.getWindow().dispose();

                this.engine = engine;


                JFrame window = new JFrame("Ad Data Dashboard");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JPanel contentPane = new JPanel(new BorderLayout());
                JPanel canvasPanel = new JPanel(new BorderLayout());
                JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                //--- Creating configPanel (save button and bounce definitions)

                JButton saveButton = new JButton("Save");
                saveButton.addActionListener(actionEvent -> saveFunc());


                JButton bounceButton = new JButton("Bounce Configuration");

                bounceButton.addActionListener(actionEvent -> configureBounce(engine));
                configPanel.add(saveButton);

                configPanel.add(bounceButton);

                configPanel.add(loading);

                //--- End Creation of configPanel


                window.setContentPane(contentPane);
                contentPane.setLayout(new BorderLayout());
                canvasPanel.add(tVC.getTabbedPane(), BorderLayout.CENTER);

                window.add(canvasPanel, BorderLayout.CENTER);
                window.add(configPanel, BorderLayout.NORTH);

                window.setSize(920, 580);
                tVC.setWindow(window);
                loadingPopup.dispose();
                window.setVisible(true);
                window.setMinimumSize(new Dimension(920, 580));

            }
            catch (ParseException e)
            {
                // Empty because exception is handled elsewhere.
            }


        }
    }

    private void saveFunc()
    {
        String saveFile = fileSelect.setADCFile("Select or type a filename to save as:");
        if (saveFile.substring(saveFile.length() - 4).equals(".adc"))
        {
            tVC.getPersistence().save(saveFile);
        }
        else
        {
            saveFile = saveFile + ".adc";
            tVC.getPersistence().save(saveFile);
        }
    }

    private void configureBounce(QueryEngine engine)
    {


        JFrame window = new JFrame("Ad Data Dashboard");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        window.setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        JPanel bouncePanel = new JPanel();

        GroupLayout layout = new GroupLayout(bouncePanel);
        bouncePanel.setLayout(layout);
        GroupLayout.ParallelGroup parallel = layout.createParallelGroup();
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(parallel));
        GroupLayout.SequentialGroup sequential = layout.createSequentialGroup();
        layout.setVerticalGroup(sequential);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel maxPagesL = new JLabel("Max Pages Viewed:");
        JLabel maxSecondsL = new JLabel("Max Seconds on Site:");
        JTextField maxPagesT = new JTextField(String.valueOf(engine.getBounceMaxPagesViewed()), 5);
        maxPagesT.setMaximumSize(new Dimension(100, maxPagesT.getHeight()));
        maxPagesT.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (maxPagesT.getText().length() >= 6)
                {
                    e.consume();
                }
            }
        });

        JTextField maxSecondsT = new JTextField(String.valueOf(engine.getBounceMaxSecondsOnSite()), 5);
        maxSecondsT.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (maxSecondsT.getText().length() >= 6)
                {
                    e.consume();
                }
            }
        });

        maxSecondsT.setMaximumSize(new Dimension(100, maxSecondsT.getHeight()));

        parallel.addGroup(layout.createSequentialGroup().
                addComponent(maxPagesL).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(maxPagesT));
        sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(maxPagesL).addComponent(maxPagesT).addGap(45));

        parallel.addGroup(layout.createSequentialGroup().
                addComponent(maxSecondsL).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(maxSecondsT));
        sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(maxSecondsL).addComponent(maxSecondsT).addGap(45));

        JButton bounceButton = new JButton("Configure bounces");

        bounceButton.addActionListener(new ActionListener()
        {
            int maxPages = engine.getBounceMaxPagesViewed();
            int maxSeconds = engine.getBounceMaxSecondsOnSite();

            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                try
                {
                    if (maxPagesT.getText().length() > 6)
                    {
                        maxPages = Integer.parseInt(maxPagesT.getText().substring(0, 6));
                    }
                    else
                    {
                        maxPages = Integer.parseInt(maxPagesT.getText());
                    }
                }
                catch (NumberFormatException exception)
                {
                    ErrorHandling.displayError("Max Pages Viewed input is not a number.", false);
                }
                engine.setBounceMaxPagesViewed(maxPages);
                maxPagesT.setText(String.valueOf(maxPages));
                tVC.getPersistence().setBounceMaxPagesViewed(maxPages);
                try
                {
                    if (maxPagesT.getText().length() > 6)
                    {
                        maxSeconds = Integer.parseInt(maxSecondsT.getText().substring(0, 6));
                    }
                    else
                    {
                        maxSeconds = Integer.parseInt(maxSecondsT.getText());
                    }
                }
                catch (NumberFormatException exception)
                {
                    ErrorHandling.displayError("Max Seconds on Page input is not a number.", false);
                }
                engine.setBounceMaxSecondsOnSite(maxSeconds);
                maxSecondsT.setText(String.valueOf(maxSeconds));
                tVC.getPersistence().setBounceMaxSecondsOnSite(maxSeconds);

                TabViewContainer.replaceBounce();

                ErrorHandling.displayError("Bounce Definition Reconfigured.", false);
                window.setVisible(false);
                window.dispose();
            }
        });

        bouncePanel.add(maxPagesL);
        bouncePanel.add(maxPagesT);
        bouncePanel.add(maxSecondsL);
        bouncePanel.add(maxSecondsT);

        window.add(bouncePanel, BorderLayout.CENTER);
        window.add(bounceButton, BorderLayout.SOUTH);

        window.setSize(320, 175);
        window.setVisible(true);
        window.setMinimumSize(new Dimension(320, 175));

    }

    public String chooseCSVFile(String prompt)
    {
        return fileSelect.selectCSVFile(prompt);
    }

    public String chooseADCFile(String prompt)
    {
        return fileSelect.selectADCFile(prompt);
    }

    public TabbedViewController getTabbedViewController()
    {
        return tVC;
    }
}


