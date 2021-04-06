package org.group15;

import java.util.Map;

public class Query
{

    public String tabName;
    public int[] dateFrom;
    public int[] dateTo;
    public Map boolMap;
    public Map filterBools;

    // For use by the Jackson (The JSON parser)
    public Query() { }

    public Query(String tabName, int[] dateFrom, int[] dateTo, Map boolMap, Map filterBools)
    {
        this.tabName = tabName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.boolMap = boolMap;
        this.filterBools = filterBools;
    }
}
