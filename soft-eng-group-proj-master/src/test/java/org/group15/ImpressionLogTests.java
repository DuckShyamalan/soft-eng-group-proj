package org.group15;

import org.junit.Test;

import java.text.ParseException;

public class ImpressionLogTests
{

    @Test(expected = ParseException.class)
    public void testParseExceptionThrown() throws ParseException
    {
        new ServerLog(new TableDataSource()
        {
            @Override
            public String[] getRowHeaders()
            {
                return new String[]{"no", "plsNo", "plsNoWork"};
            }

            @Override
            public Queryable<String[]> getRows()
            {
                return null;
            }
        });
    }

    @Test
    public void testParseGoesFine() throws ParseException
    {
        new ImpressionLog(new TableDataSource()
        {
            @Override
            public String[] getRowHeaders()
            {
                return new String[]{"Date","ID","Gender","Age","Income","Context","Impression Cost"};
            }

            @Override
            public Queryable<String[]> getRows() {
                return null;
            }
        });
    }

    @Test
    public void readActualImpressionLog() throws ParseException
    {
        FileHandler fileHandler = new FileHandler(TestConstants.IMPRESSION_LOG_PATH);
        ImpressionLog impressionLog = new ImpressionLog(fileHandler.getTableData());
        impressionLog.toArrayList();
        fileHandler.close();
    }
}
