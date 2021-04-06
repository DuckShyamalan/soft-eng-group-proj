package org.group15;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;

/**
 * Provides an interface for reading CSV files.
 */
public class FileHandler implements AutoCloseable
{
    private boolean closed;
    private TableData data;

    /**
     * Constructors, needs a path
     * @param filePath filepath
     */
    public FileHandler(String filePath)
    {
        data = new TableData(new File(filePath));
    }

    /**
     * Produces TableDataSource representing the content of the file.
     */
    public TableDataSource getTableData()
    {
        return data;
    }

    // Table data source for a file.
    private class TableData implements TableDataSource{

        //filepath for that file
        File file;

        //Headers
        String[] headers;


        List<String[]> lines = new ArrayList<>();

        /**
         * Constructor, needs a file
         * @param file the file to read
         */
        public TableData(File file)
        {
            this.file = file;
            try
            {
                boolean first = false;
                for (String line : Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8))
                {
                    if (line.matches("\\s*"))
                        continue;

                    //Read in the headers from the first non-blank line.
                    if (!first)
                    {
                        headers = line.split(",");
                        first = true;
                    }
                    else
                    {
                        lines.add(line.split(","));
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public String[] getRowHeaders()
        {
            if (closed) return null;
            return headers;
        }

        @Override
        public Queryable<String[]> getRows()
        {
            if (closed) return null;
            return new Qr();
        }

        /**
         * Subclass for queryable implementation
         */
        private class Qr extends Queryable<String[]>
        {

            @Override
            public Enumerator<String[]> getEnumerator() {
                return new En();
            }

            /**
             * Subclass for the needed enumerator
             */
            private class En implements Enumerator<String[]>
            {
                private boolean closed;
                private String[] current = null;
                private int index = -1;

                @Override
                public String[] getCurrent()
                {
                    if (closed) return null;
                    return current;
                }

                @Override
                public boolean moveNext()
                {
                    if (closed) return false;
                    if (++index >= lines.size())
                    {
                        current = null;
                        return false;
                    }
                    current = lines.get(index);
                    return true;
                }

                @Override
                public void reset()
                {
                    index = -1;
                    current = null;
                }

                @Override
                public void close()
                {
                    closed = true;
                }
            }
        }
    }


    @Override
    public void close()
    {
        data = null;
        closed = true;
    }
}
