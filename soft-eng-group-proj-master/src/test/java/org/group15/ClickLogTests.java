package org.group15;

import org.junit.Test;

import java.text.ParseException;

public class ClickLogTests
{

    @Test(expected = ParseException.class)
    public void testParseExceptionThrown()throws ParseException
    {
        new ServerLog(new TableDataSource()
        {
            @Override
            public String[] getRowHeaders() {
                return new String[]{"no", "plsNo", "plsNoWork"};
            }

            @Override
            public Queryable<String[]> getRows() {
                return null;
            }
        });
    }

    @Test
    public void testParseGoesFine() throws ParseException
    {
        new ClickLog(new TableDataSource()
        {
            @Override
            public String[] getRowHeaders() {
                return new String[]{"Date","ID","Click Cost"};
            }

            @Override
            public Queryable<String[]> getRows() {
                return null;
            }
        });
    }

    @Test
    public void readActualClickLog() throws ParseException
    {
        FileHandler fileHandler = new FileHandler(TestConstants.CLICK_LOG_PATH);
        ClickLog clickLog = new ClickLog(fileHandler.getTableData());
        clickLog.toArrayList();
        fileHandler.close();
    }
}
