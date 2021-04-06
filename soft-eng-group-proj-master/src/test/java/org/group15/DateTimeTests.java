package org.group15;

import org.junit.Test;
import static org.junit.Assert.*;

public class DateTimeTests
{
    @Test
    public void identityTest()
    {
        DateTime test = new DateTime(2015,2,15);
        assertEquals(test, new DateTime(2015, 2, 15));
        assertEquals(test, new DateTime(2015,2,15,0,0));
        assertEquals(test, new DateTime(2015,2,15,0,0,0));
        assertNotEquals(test, new DateTime(2016,2,5,0,0,0));
        assertNotEquals(test, new DateTime(2015,3,5,0,0,0));
        assertNotEquals(test, new DateTime(2015,2,4,0,0,0));
        assertNotEquals(test, new DateTime(2015,2,5,1,0,0));
        assertNotEquals(test, new DateTime(2015,2,5,0,26,0));
        assertNotEquals(test, new DateTime(2015,2,5,0,0,2));
        assertEquals(new DateTime(2017,5,31,5,8,23), new DateTime(2017,5,31,5,8,23));
        assertNotEquals(new DateTime(2017,5,31,5,8,23), new DateTime(2017,5,31,5,8,24));
    }

    @Test
    public void dayRollOverTest()
    {
        assertEquals(new DateTime(2015, 5, 2, 0, 34), new DateTime(2015, 5, 1, 21, 34).addHours(3, false));
        assertEquals(new DateTime(2015, 5, 2, 0, 34), new DateTime(2015, 5, 1, 21, 34).addHours(3, true));
        assertEquals(new DateTime(2015, 5, 32, 0, 34), new DateTime(2015, 5, 31, 21, 34).addHours(3, true));
        assertEquals(new DateTime(2015, 6, 1, 0, 34), new DateTime(2015, 5, 31, 21, 34).addHours(3, false));
    }

    @Test
    public void monthRollOverTest()
    {
        assertEquals(new DateTime(2017,5,17), new DateTime(2017,5,15).addDays(2, false));
        assertEquals(new DateTime(2017,5,17), new DateTime(2017,5,15).addDays(2, true));
        assertEquals(new DateTime(2017,5,32), new DateTime(2017,5,31).addDays(1, true));
        assertEquals(new DateTime(2017,6,1), new DateTime(2017,5,31).addDays(1, false));
        assertEquals(new DateTime(2017,10,1), new DateTime(2017,9,30).addDays(1, false));
        assertEquals(new DateTime(2017,10,2), new DateTime(2017,9,31).addDays(1, false));
        assertEquals(new DateTime(2017,9,32), new DateTime(2017,9,31).addDays(1, true));
        assertEquals(new DateTime(2017,11,24), new DateTime(2017,9,25).addDays(60, false));
    }

    @Test
    public void yearRolloverTest()
    {
        assertEquals(new DateTime(2018, 1, 1), new DateTime(2017, 12, 31).addDays(1, false));
        assertEquals(new DateTime(2018, 1, 1), new DateTime(2016, 12, 31).addDays(366, false));
    }

    @Test
    public void leapYearTest()
    {
        assertEquals(new DateTime(2015,3,1), new DateTime(2015,2,28).addDays(1, false));
        assertEquals(new DateTime(2012,2,29), new DateTime(2012,2,28).addDays(1, false));
        assertEquals(new DateTime(2012,3,1), new DateTime(2012,2,28).addDays(2, false));
        assertEquals(new DateTime(2012,3,1), new DateTime(2012,2,29).addDays(1, false));
        assertEquals(new DateTime(2100,3,1), new DateTime(2100,2,28).addDays(1, false));
        assertEquals(new DateTime(2000,2,29), new DateTime(2000,2,28).addDays(1, false));
        assertEquals(new DateTime(2000,3,1), new DateTime(2000,2,29).addDays(1, false));
    }
}
