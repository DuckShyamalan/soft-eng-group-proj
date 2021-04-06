package org.group15;

import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;


public class QueryEngineTests
{
    private static QueryEngine queryEngine;

    @BeforeClass
    public static void setup() throws ParseException
    {
        //queryEngine = new QueryEngine(() -> TestConstants.IMPRESSION_LOG_PATH, () -> TestConstants.CLICK_LOG_PATH, () ->  TestConstants.SERVER_LOG_PATH);
        queryEngine = new QueryEngine(TestConstants.IMPRESSION_LOG_PATH, TestConstants.CLICK_LOG_PATH, TestConstants.SERVER_LOG_PATH);
    }

    /**
     * Tests gathering of basic statistics in the query engine.
     */

    @Test
    public void basicImpressionCountTest()
    {
        assertEquals(486104, queryEngine.getImpressionCount());
    }

    @Test
    public void basicClickCountTest()
    {
        assertEquals(23923, queryEngine.getClickCount());
    }

    @Test
    public void basicUniquesTest()
    {
        assertEquals(23806, queryEngine.getUniquesCount());
    }

    @Test
    public void basicBouncesTest()
    {
        assertEquals(8665, queryEngine.getBouncesCount());
    }

    @Test
    public void basicConversionTest()
    {
        assertEquals(2026, queryEngine.getConversionCount());
    }

    @Test
    public void basicTotalCostTest()
    {
        assertTrue(queryEngine.getTotalCost() - 118097.921223001 < 0.00001);
    }


    @Test
    public void graphImpressionCountTest()
    {
        List<Pair<DateTime, Integer>> data = queryEngine.getImpressionCount(new DateTime(2015,1,1), new DateTime(2015,1,15), Interval.Day).toArrayList();
        HashMap<DateTime, Integer> expected = new HashMap<>();
        expected.put(new DateTime(2015,1,1), 22049);
        expected.put(new DateTime(2015,1,2), 32773);
        expected.put(new DateTime(2015,1,3), 34919);
        expected.put(new DateTime(2015,1,4), 33111);
        expected.put(new DateTime(2015,1,5), 35758);
        expected.put(new DateTime(2015,1,6), 37379);
        expected.put(new DateTime(2015,1,7), 37958);
        expected.put(new DateTime(2015,1,8), 37311);
        expected.put(new DateTime(2015,1,9), 39031);
        expected.put(new DateTime(2015,1,10), 36562);
        expected.put(new DateTime(2015,1,11), 42014);
        expected.put(new DateTime(2015,1,12), 40945);
        expected.put(new DateTime(2015,1,13), 42159);
        expected.put(new DateTime(2015,1,14), 14135);

        for (Pair<DateTime, Integer> p : data)
            assertEquals(expected.getOrDefault(p.getKey(), -1), p.getValue());
    }

    @Test
    public void graphClickCountTest()
    {
        List<Pair<DateTime, Integer>> data = queryEngine.getClickCount(new DateTime(2015,1,1), new DateTime(2015,1,15), Interval.Day).toArrayList();
        HashMap<DateTime, Integer> expected = new HashMap<>();
        expected.put(new DateTime(2015,1,1), 1079);
        expected.put(new DateTime(2015,1,2), 1638);
        expected.put(new DateTime(2015,1,3), 1754);
        expected.put(new DateTime(2015,1,4), 1631);
        expected.put(new DateTime(2015,1,5), 1747);
        expected.put(new DateTime(2015,1,6), 1833);
        expected.put(new DateTime(2015,1,7), 1857);
        expected.put(new DateTime(2015,1,8), 1797);
        expected.put(new DateTime(2015,1,9), 1957);
        expected.put(new DateTime(2015,1,10), 1785);
        expected.put(new DateTime(2015,1,11), 2042);
        expected.put(new DateTime(2015,1,12), 2024);
        expected.put(new DateTime(2015,1,13), 2053);
        expected.put(new DateTime(2015,1,14), 726);

        for (Pair<DateTime, Integer> p : data)
            assertEquals(expected.getOrDefault(p.getKey(), -1), p.getValue());
    }

    @Test
    public void graphUniquesTest()
    {
        List<Pair<DateTime, Integer>> data = queryEngine.getUniquesCount(new DateTime(2015,1,1), new DateTime(2015,1,15), Interval.Day).toArrayList();
        HashMap<DateTime, Integer> expected = new HashMap<>();
        expected.put(new DateTime(2015,1,1), 1079);
        expected.put(new DateTime(2015,1,2), 1635);
        expected.put(new DateTime(2015,1,3), 1745);
        expected.put(new DateTime(2015,1,4), 1623);
        expected.put(new DateTime(2015,1,5), 1733);
        expected.put(new DateTime(2015,1,6), 1825);
        expected.put(new DateTime(2015,1,7), 1850);
        expected.put(new DateTime(2015,1,8), 1788);
        expected.put(new DateTime(2015,1,9), 1949);
        expected.put(new DateTime(2015,1,10), 1773);
        expected.put(new DateTime(2015,1,11), 2028);
        expected.put(new DateTime(2015,1,12), 2017);
        expected.put(new DateTime(2015,1,13), 2041);
        expected.put(new DateTime(2015,1,14), 720);

        for (Pair<DateTime, Integer> p : data)
            assertEquals(expected.getOrDefault(p.getKey(), -1), p.getValue());
    }

    @Test
    public void graphBouncesCountTest()
    {
        List<Pair<DateTime, Integer>> data = queryEngine.getBouncesCount(new DateTime(2015,1,1), new DateTime(2015,1,15), Interval.Day).toArrayList();
        HashMap<DateTime, Integer> expected = new HashMap<>();
        expected.put(new DateTime(2015,1,1), 375);
        expected.put(new DateTime(2015,1,2), 584);
        expected.put(new DateTime(2015,1,3), 620);
        expected.put(new DateTime(2015,1,4), 591);
        expected.put(new DateTime(2015,1,5), 647);
        expected.put(new DateTime(2015,1,6), 678);
        expected.put(new DateTime(2015,1,7), 676);
        expected.put(new DateTime(2015,1,8), 650);
        expected.put(new DateTime(2015,1,9), 714);
        expected.put(new DateTime(2015,1,10), 626);
        expected.put(new DateTime(2015,1,11), 707);
        expected.put(new DateTime(2015,1,12), 747);
        expected.put(new DateTime(2015,1,13), 788);
        expected.put(new DateTime(2015,1,14), 262);

        for (Pair<DateTime, Integer> p : data)
            assertEquals(expected.getOrDefault(p.getKey(), -1), p.getValue());
    }

    @Test
    public void graphConversionCountTest()
    {
        List<Pair<DateTime, Integer>> data = queryEngine.getConversionCount(new DateTime(2015,1,1), new DateTime(2015,1,15), Interval.Day).toArrayList();
        HashMap<DateTime, Integer> expected = new HashMap<>();
        expected.put(new DateTime(2015,1,1), 95);
        expected.put(new DateTime(2015,1,2), 156);
        expected.put(new DateTime(2015,1,3), 143);
        expected.put(new DateTime(2015,1,4), 139);
        expected.put(new DateTime(2015,1,5), 138);
        expected.put(new DateTime(2015,1,6), 138);
        expected.put(new DateTime(2015,1,7), 147);
        expected.put(new DateTime(2015,1,8), 162);
        expected.put(new DateTime(2015,1,9), 167);
        expected.put(new DateTime(2015,1,10), 163);
        expected.put(new DateTime(2015,1,11), 168);
        expected.put(new DateTime(2015,1,12), 199);
        expected.put(new DateTime(2015,1,13), 151);
        expected.put(new DateTime(2015,1,14), 60);

        for (Pair<DateTime, Integer> p : data)
            assertEquals(expected.getOrDefault(p.getKey(), -1), p.getValue());
    }


//    @Test
//    public void output()
//    {
//        System.out.println("Test Output:");
//        for (Pair<DateTime, Integer> entry : queryEngine.getBouncesCount(new DateTime(2015,1,1), new DateTime(2015,1,15), Interval.Day))
//        {
//            System.out.println(entry.getKey().toPrettyString() + " : " + entry.getValue());
//        }
//    }
}
