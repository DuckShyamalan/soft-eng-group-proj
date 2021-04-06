package org.group15;

/**
 * Class for representing an item in serverlog
 */
public class ServerLogItem {
    public final DateTime entryDate;
    public final long id;
    public final DateTime exitDate;
    public final int pagesViewed;
    public final boolean conversion;


    public ServerLogItem(DateTime entryDate, long id, DateTime exitDate, int pagesViewed, boolean conversion) {
        this.entryDate = entryDate;
        this.id = id;
        this.exitDate = exitDate;
        this.pagesViewed = pagesViewed;
        this.conversion = conversion;
    }

}
