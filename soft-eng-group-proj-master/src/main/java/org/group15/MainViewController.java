package org.group15;

import javax.swing.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @version 1.0
 * This is the main GUI controller
 */
public class MainViewController
{

    //Query engine is used for getting data
    private QueryEngine engine;

    //The tabbed interface to control
    private TabbedView tView = new TabbedView(this);

    //Formats
    private NumberFormat intFormat;
    private NumberFormat smallNumberFormat;
    private NumberFormat highCostFormat;
    private NumberFormat lowCostFormat;

    /**
     * Constructor for this class, may throw parse exception if there are problems
     * @throws ParseException will be thrown if there are errors in parsing
     */
    public MainViewController() throws ParseException {
        ErrorHandling.addHandler(new ErrorHandler() {
            @Override
            public void handleError(String message) {
                JOptionPane.showMessageDialog(null, message);
            }

            @Override
            public void handleError(Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });

        // Set up various number formatters for the GUI. This is really view logic, and will be moved there later.

        intFormat = NumberFormat.getInstance();
        intFormat.setMaximumFractionDigits(0);

        highCostFormat = NumberFormat.getInstance();
        highCostFormat.setMinimumFractionDigits(2);
        highCostFormat.setMaximumFractionDigits(2);

        lowCostFormat = NumberFormat.getInstance();
        lowCostFormat.setMinimumFractionDigits(4);
        lowCostFormat.setMaximumFractionDigits(4);

        smallNumberFormat = NumberFormat.getInstance();
        smallNumberFormat.setMinimumFractionDigits(4);
        smallNumberFormat.setMaximumFractionDigits(4);

    }

    public void startAnalyzing(QueryEngine engine) throws ParseException {


        //Setting up the engine for queries and passing it filepaths for data.
        //engine = new QueryEngine(impressionLogFile, clickLogFile, serverLogFile);
        this.engine = engine;

        /*
        The data is stored in maps. The key of each item in the map describes the data,
        while the value of each item holds the value of the data. Both are stored as strings,
        as they are put into labels to present to the user.
        */

        Map<String,String> dashboardTabData = new HashMap<>();
        dashboardTabData.put("Number of Clicks", intFormat.format(engine.getClickCount()));
        dashboardTabData.put("Unique Clicks", intFormat.format(engine.getUniquesCount()));
        dashboardTabData.put("Click Through Rate", smallNumberFormat.format(engine.getClickThroughRate()));
        dashboardTabData.put("Cost per click (pence)", lowCostFormat.format(engine.getCostPerClick()));

        dashboardTabData.put("Number of impressions", intFormat.format(engine.getImpressionCount()));
        dashboardTabData.put("Cost per thousand impressions (pence)", highCostFormat.format(engine.getCostPerThousandImpressions()));

        dashboardTabData.put("Number of bounces", intFormat.format(engine.getBouncesCount()));
        dashboardTabData.put("Bounce Rate", lowCostFormat.format(engine.getBounceRate()));
        dashboardTabData.put("Number of Conversions", intFormat.format(engine.getConversionCount()));
        dashboardTabData.put("Total Cost (£)", highCostFormat.format(engine.getTotalCost() / 100));
        dashboardTabData.put("Cost per acquisition (pence)", highCostFormat.format(engine.getCostPerAcquisition()));

        // Creating the Dashboard tab. Each tab gets passed a name and a map with the data to display.
        tView.getTabbedViewController().createTab("Dashboard", dashboardTabData, engine, intFormat, lowCostFormat);

        tView.getTabbedViewController().addQ(engine);

    }

}
