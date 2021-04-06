package org.group15;

import org.junit.Assert;
import org.junit.Test;

public class FileHandlerTests
{
    @Test
    public void loadImpressionLogTest()
    {
        FileHandler handler = new FileHandler(TestConstants.IMPRESSION_LOG_PATH);
        TableDataSource td = handler.getTableData();
        Assert.assertArrayEquals(new String[] {"Date", "ID", "Gender", "Age", "Income", "Context", "Impression Cost"}, td.getRowHeaders());
        Assert.assertArrayEquals(new String[] {"2015-01-01 12:00:02", "4620864431353617408", "Male", "25-34", "High", "Blog", "0.001713"}, td.getRows().first());
        //TODO: Add more data integrity tests here.
    }

    @Test
    public void loadClickLogTest()
    {
        FileHandler handler = new FileHandler(TestConstants.CLICK_LOG_PATH);
        TableDataSource td = handler.getTableData();
        Assert.assertArrayEquals(new String[] {"Date", "ID", "Click Cost"}, td.getRowHeaders());
        Assert.assertArrayEquals(new String[] {"2015-01-01 12:01:21", "8895519749317550080", "11.794442"}, td.getRows().first());
        //TODO: Add more data integrity tests here.
    }

    @Test
    public void loadServerLogTest()
    {
        FileHandler handler = new FileHandler(TestConstants.SERVER_LOG_PATH);
        TableDataSource td = handler.getTableData();
        Assert.assertArrayEquals(new String[] {"Entry Date", "ID", "Exit Date", "Pages Viewed", "Conversion"}, td.getRowHeaders());
        Assert.assertArrayEquals(new String[] {"2015-01-01 12:01:21", "8895519749317550080", "2015-01-01 12:05:13", "7", "No"}, td.getRows().first());
    }
}
