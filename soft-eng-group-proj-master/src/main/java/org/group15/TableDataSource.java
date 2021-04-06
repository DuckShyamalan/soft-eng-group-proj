package org.group15;

public interface TableDataSource
{
     String[] getRowHeaders();
     Queryable<String[]> getRows();
}