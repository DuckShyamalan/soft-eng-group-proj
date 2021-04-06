package org.group15;

/**
 * Class for representing an entry in the click log.
 */
public class ClickLogItem
{
    public final DateTime date;
    public final long id;
    public final double cost;


    public ClickLogItem(DateTime date, long id, double cost) {
        this.date = date;
        this.id = id;
        this.cost = cost;
    }
}
