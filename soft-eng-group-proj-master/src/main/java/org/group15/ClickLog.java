package org.group15;

import java.text.ParseException;

/**
 * Represents the click log file, and acts as an interface for queries on it.
 */
public class ClickLog extends Queryable<ClickLogItem>
{
    private Queryable<String[]> source;
    private String[] headers;
    private final String[] correctHeaders = {"Date","ID","Click Cost"};

    /**
     * Constructor for getting data source
     * @param tds the source
     * @throws ParseException in case there is an error in parsing
     */
    public ClickLog(TableDataSource tds) throws ParseException
    {

        //checking headers
        headers = tds.getRowHeaders();
        if(!headersAreCorrect())
            throw new ParseException("The headers are not correct",-1);

        //getting data
        this.source = tds.getRows();
    }

    private boolean headersAreCorrect()
    {
        if(headers == null || headers.length != correctHeaders.length) return false;

        for (int i = 0; i < headers.length; i++)
            if(!correctHeaders[i].equals(headers[i])) return false;

        return true;
    }

    @Override
    public Enumerator<ClickLogItem> getEnumerator() {
        return new En();
    }

    private class En implements Enumerator<ClickLogItem>
    {
        private boolean closed;
        Enumerator<String[]> sourceEnumerator;
        ClickLogItem current = null;

        public En(){
            sourceEnumerator = source.getEnumerator();
        }

        @Override
        public ClickLogItem getCurrent()
        {
            if (closed) return null;
            return current;
        }

        @Override
        public boolean moveNext()
        {
            if (closed) return false;
            if (sourceEnumerator.moveNext())
            {
                current = convert(sourceEnumerator.getCurrent());
                return true;
            }
            else
            {
                current = null;
                return false;
            }
        }

        @Override
        public void reset()
        {
            sourceEnumerator.reset();
        }

        /**
         * This converts a list of string to a click log element
         * @param toConvert the list of strings
         * @return the click log element
         */
        private ClickLogItem convert(String[] toConvert)
        {
            return new ClickLogItem(
                    DateTime.parse(toConvert[0]),
                    Long.parseLong(toConvert[1]),
                    Double.parseDouble(toConvert[2])
            );
        }

        @Override
        public void close()
        {
            if (closed) return;
            sourceEnumerator.close();
            closed = true;
        }
    }
}
