package org.group15;

import org.junit.Test;

import java.text.ParseException;

public class ServerLogTests
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
        new ServerLog(new TableDataSource()
        {
            @Override
            public String[] getRowHeaders()
            {
                return new String[]{"Entry Date", "ID", "Exit Date", "Pages Viewed", "Conversion"};
            }

            @Override
            public Queryable<String[]> getRows()
            {
                return null;
            }
        });
    }

    @Test
    public void readActualServerLog() throws ParseException
    {
        FileHandler fileHandler = new FileHandler(TestConstants.SERVER_LOG_PATH);
        ServerLog serverLog = new ServerLog(fileHandler.getTableData());
        serverLog.toArrayList();
        fileHandler.close();
    }
}
