package org.group15;

import java.text.ParseException;

/**
 * Acts as a queryable interface into the data in the impression log file.
 */
public class ImpressionLog extends Queryable<ImpressionLogItem>{


    private Queryable<String[]> source;
    private String[] headers;
    private final String[] correctHeaders = {"Date","ID","Gender","Age","Income","Context","Impression Cost"};

    
    public ImpressionLog(TableDataSource tds) throws ParseException
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
        if( headers == null || headers.length != correctHeaders.length) return false;

        for (int i = 0; i < headers.length; i++)
            if(!correctHeaders[i].equals(headers[i])) return false;

        return true;
    }

    @Override
    public Enumerator<ImpressionLogItem> getEnumerator() {
        return new En();
    }

    private class En implements Enumerator<ImpressionLogItem>
    {
        private boolean closed;
        Enumerator<String[]> sourceEnumerator;
        ImpressionLogItem current = null;

        public En(){
            sourceEnumerator = source.getEnumerator();
        }

        @Override
        public ImpressionLogItem getCurrent()
        {
            if (closed) return null;
            return current;
        }

        @Override
        public boolean moveNext()
        {
            if (closed) return false;

            if(sourceEnumerator.moveNext())
            {
                try
                {
                    current = convert(sourceEnumerator.getCurrent());
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
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
            if (closed) return;
            sourceEnumerator.reset();
        }

        /**
         * FOr converting a list of string to an item
         * @param current the list of string
         * @return the impression log item
         * @throws ParseException if there are errors in parsing
         */
        private ImpressionLogItem convert(String[] current) throws ParseException
        {
            return new ImpressionLogItem(
                DateTime.parse(current[0]),
                Long.parseLong(current[1]),
                Gender.parseGender(current[2]),
                Age.parseAge(current[3]),
                Income.parseIncome(current[4]),
                Context.parseContext(current[5]),
                Double.parseDouble(current[6])
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
