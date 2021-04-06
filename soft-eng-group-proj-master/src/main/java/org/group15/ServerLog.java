package org.group15;


import java.text.ParseException;

/**
 *This class handles the serverlog
 */
public class ServerLog extends Queryable<ServerLogItem>{

    //Where it gets the data
    private Queryable<String[]> source;

    //The headers
    private String[] headers;
    private final String[] correctHeaders = {"Entry Date", "ID", "Exit Date", "Pages Viewed", "Conversion"};

    /**
     * Constructors, requires a source for data
     * @param tds The source of data
     * @throws ParseException Thrown if there are errors in parsing
     */
    public ServerLog(TableDataSource tds) throws ParseException
    {
        //checking headers
        headers = tds.getRowHeaders();
        if(!headersAreCorrect())
            throw new ParseException("The headers are not correct",-1);

        //getting data
        this.source = tds.getRows();

    }

    /**
     * Checks if headers are correct
     * @return true if they are, false otherwise
     */
    private boolean headersAreCorrect()
    {
        if(headers == null || headers.length != correctHeaders.length) return false;

        for (int i = 0; i < headers.length; i++)
            if(!correctHeaders[i].equals(headers[i])) return false;

        return true;
    }

    @Override
    public Enumerator<ServerLogItem> getEnumerator() {
        return new En();
    }

    /**
     * Subclass for the enumerator
     */
    private class En implements Enumerator<ServerLogItem>
    {
        private boolean closed;
        Enumerator<String[]> sourceEnumerator;
        ServerLogItem current = null;

        /**
         * Constructor, gets the enumerator from source
         */
        public En(){
            sourceEnumerator = source.getEnumerator();
        }

        @Override
        public ServerLogItem getCurrent()
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
            if (closed) return;
            sourceEnumerator.reset();
        }

        private ServerLogItem convert(String[] toConvert)
        {
            return new ServerLogItem(
                    DateTime.parse(toConvert[0]),
                    Long.parseLong(toConvert[1]),
                    DateTime.parse(toConvert[2]),
                    Integer.parseInt(toConvert[3]),
                    toConvert[4].equalsIgnoreCase("yes")
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
