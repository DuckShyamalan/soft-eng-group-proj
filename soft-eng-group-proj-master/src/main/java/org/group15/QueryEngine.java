package org.group15;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * Handles queries, producing the results as TableDataSources.
 */
public class QueryEngine implements AutoCloseable
{
    private ImpressionLog impressionLog;
    private ClickLog clickLog;
    private ServerLog serverLog;
    private List<FileHandler> fileHandlers = new ArrayList<>();
    private boolean closed;
    private int bounceMaxPagesViewed = 1;
    private int bounceMaxSecondsOnSite = 0;

    public QueryEngine(String impressionLogSelector, String clickLogSelector, String serverLogSelector) throws ParseException
    {
        while (impressionLog == null)
        {
            try
            {
                impressionLog = new ImpressionLog(getTableData(impressionLogSelector));
            }
            catch (ParseException e)
            {
                ErrorHandling.displayError("CSV file headers not recognised! Are you sure you selected the correct file for impression log?", false);
                throw e;
            }
        }

        while (clickLog == null)
        {
            try
            {
                clickLog = new ClickLog(getTableData(clickLogSelector));
            }
            catch (ParseException e)
            {
                ErrorHandling.displayError("CSV file headers not recognised! Are you sure you selected the correct file for click log?", false);
                throw e;
            }
        }

        while (serverLog == null)
        {
            try
            {
                serverLog = new ServerLog(getTableData(serverLogSelector));
            }
            catch (ParseException e)
            {
                ErrorHandling.displayError("CSV file headers not recognised! Are you sure you selected the correct file for server log?", false);
                throw e;
            }
        }

        // Cache key metric data:
        impressionCount =  impressionLog.count();
        clickCount = clickLog.count();
        uniquesCount = clickLog.groupBy(x -> x.id).count();
        conversionCount = serverLog.where(x -> x.conversion).count();
        totalCost = impressionLog.sumD(x -> x.cost) + clickLog.sumD(x -> x.cost);
    }

    public int getBounceMaxPagesViewed()
    {
        return bounceMaxPagesViewed;
    }

    public void setBounceMaxPagesViewed(int value)
    {
        bounceMaxPagesViewed = value;
        System.out.println("Max pages viewed set to " + value);
    }

    public int getBounceMaxSecondsOnSite()
    {
        return bounceMaxSecondsOnSite;
    }

    public void setBounceMaxSecondsOnSite(int value)
    {
        bounceMaxSecondsOnSite = value;
        System.out.println("Max seconds on site set to " + value);
    }

    private boolean isBounce(ServerLogItem x)
    {
        return !x.conversion && (bounceMaxPagesViewed == 0 || x.pagesViewed <= bounceMaxPagesViewed) && (bounceMaxSecondsOnSite == 0 || secondsBetween(x.entryDate, x.exitDate) <= bounceMaxSecondsOnSite);
    }

    private TableDataSource getTableData(String filePath)
    {
        FileHandler fileHandler = new FileHandler(filePath);
        fileHandlers.add(fileHandler);
        return fileHandler.getTableData();
    }


    private final int impressionCount;
    public int getImpressionCount() { return impressionCount; }

    private final int clickCount;
    public int getClickCount() { return clickCount; }


    private final int uniquesCount;
    public int getUniquesCount() { return uniquesCount; }

    public int getBouncesCount() { return serverLog.where(this::isBounce).count(); }

    private double secondsBetween (DateTime start, DateTime end)
    {
        LocalDateTime lStart = toLocalDateTime(start);
        LocalDateTime lEnd = toLocalDateTime(end);
        return Duration.between(lStart, lEnd).toMillis() / 1000d;
    }

    private LocalDateTime toLocalDateTime(DateTime dt)
    {
        return LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDay(), dt.getHour(), dt.getMinute(), dt.getSecond());
    }

    private final int conversionCount;
    public int getConversionCount() { return conversionCount; }

    // In pence
    private final double totalCost;
    public double getTotalCost() { return totalCost; }

    public double getClickThroughRate() { return getClickCount() / (double)getImpressionCount(); }

    public double getCostPerAcquisition() { return getTotalCost() / getConversionCount(); }

    public double getCostPerClick() { return getTotalCost() / getClickCount(); }

    public double getCostPerThousandImpressions() { return getTotalCost() / (getImpressionCount() / 1000D); }

    public double getBounceRate() { return getBouncesCount() / (double)getClickCount(); }


    public Queryable<Pair<DateTime, Integer>> getImpressionCount(DateTime start, DateTime end, Interval interval)
    {

        switch(interval)
        {
            case Hour:
                return getImpressionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getImpressionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getImpressionCount(start, end, map::get);
            case Month:
                return getImpressionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Integer>> getImpressionCount(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {

        switch(interval)
        {
            case Hour:
                return getImpressionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getImpressionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getImpressionCount(start, end, map::get, gender, age, income, context);
            case Month:
                return getImpressionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Integer>> getClickCount(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getClickCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getClickCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getClickCount(start, end, map::get);
            case Month:
                return getClickCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Integer>> getClickCount(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getClickCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getClickCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getClickCount(start, end, map::get, gender, age, income, context);
            case Month:
                return getClickCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Integer>> getUniquesCount(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getUniquesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getUniquesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getUniquesCount(start, end, map::get);
            case Month:
                return getUniquesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Integer>> getUniquesCount(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getUniquesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getUniquesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getUniquesCount(start, end, map::get, gender, age, income, context);
            case Month:
                return getUniquesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Integer>> getBouncesCount(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getBouncesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getBouncesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getBouncesCount(start, end, map::get);
            case Month:
                return getBouncesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Integer>> getBouncesCount(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getBouncesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getBouncesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getBouncesCount(start, end, map::get, gender, age, income, context);
            case Month:
                return getBouncesCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Integer>> getConversionCount(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getConversionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getConversionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getConversionCount(start, end, map::get);
            case Month:
                return getConversionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Integer>> getConversionCount(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getConversionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getConversionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getConversionCount(start, end, map::get, gender, age, income, context);
            case Month:
                return getConversionCount(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getTotalCost(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getTotalCost(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getTotalCost(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getTotalCost(start, end, map::get);
            case Month:
                return getTotalCost(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getTotalCost(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getTotalCost(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getTotalCost(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getTotalCost(start, end, map::get, gender, age, income, context);
            case Month:
                return getTotalCost(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<Gender, Double>> getTotalCostGender(DateTime start, DateTime end)
    {
        return getTotalCostBy(start, end, x -> x.gender);
    }

    public Queryable<Pair<Age, Double>> getTotalCostAge(DateTime start, DateTime end)
    {
        return getTotalCostBy(start, end, x -> x.age);
    }

    public Queryable<Pair<Income, Double>> getTotalCostIncome(DateTime start, DateTime end)
    {
        return getTotalCostBy(start, end, x -> x.income);
    }

    public Queryable<Pair<Context, Double>> getTotalCostContext(DateTime start, DateTime end)
    {
        return getTotalCostBy(start, end, x -> x.context);
    }

    private <T> Queryable<Pair<T, Double>> getTotalCostBy(DateTime start, DateTime end, Function<ImpressionLogItem, T> keySelector)
    {
        Queryable<Pair<T, Double>> impressionGroups = impressionLog.where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0).groupBy(keySelector).select(x -> new Pair<>(keySelector.apply(x.first()), x.sumD(z -> z.cost)));
        Queryable<Pair<T, Double>> clickGroups = linkImpressionsToClicks(impressionLog, clickLog).where(x -> x.getValue().date.compareTo(start) >= 0 && x.getValue().date.compareTo(end) <= 0).groupBy(x -> keySelector.apply(x.getKey())).select(x -> new Pair<>(keySelector.apply(x.first().getKey()), x.sumD(z -> z.getValue().cost)));

        HashMap<T, Double> map = new HashMap<>();
        for (Pair<T, Double> g : impressionGroups)
            map.put(g.getKey(), g.getValue());

        for (Pair<T, Double> g : clickGroups)
            if (!map.containsKey(g.getKey()))
                map.put(g.getKey(), g.getValue());
            else map.put(g.getKey(), g.getValue() + map.get(g.getKey()));

        List<Pair<T, Double>> rl = new ArrayList<>();
        for (T key : map.keySet())
            rl.add(new Pair<>(key, map.get(key)));

        return new Queryable<Pair<T, Double>>()
        {
            @Override
            public Enumerator<Pair<T, Double>> getEnumerator()
            {
                return new IterableEnumerator<>(rl);
            }
        };
    }

    public Queryable<Pair<DateTime, Double>> getClickThroughRate(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getClickThroughRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getClickThroughRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getClickThroughRate(start, end, map::get);
            case Month:
                return getClickThroughRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getClickThroughRate(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getClickThroughRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getClickThroughRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getClickThroughRate(start, end, map::get, gender, age, income, context);
            case Month:
                return getClickThroughRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getCostPerAcquisition(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getCostPerAcquisition(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getCostPerAcquisition(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getCostPerAcquisition(start, end, map::get);
            case Month:
                return getCostPerAcquisition(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getCostPerAcquisition(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getCostPerAcquisition(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getCostPerAcquisition(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getCostPerAcquisition(start, end, map::get, gender, age, income, context);
            case Month:
                return getCostPerAcquisition(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getCostPerClick(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getCostPerClick(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getCostPerClick(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getCostPerClick(start, end, map::get);
            case Month:
                return getCostPerClick(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getCostPerClick(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getCostPerClick(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getCostPerClick(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getCostPerClick(start, end, map::get, gender, age, income, context);
            case Month:
                return getCostPerClick(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getCostPerThousandImpressions(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getCostPerThousandImpressions(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getCostPerThousandImpressions(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getCostPerThousandImpressions(start, end, map::get);
            case Month:
                return getCostPerThousandImpressions(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getCostPerThousandImpressions(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getCostPerThousandImpressions(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getCostPerThousandImpressions(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getCostPerThousandImpressions(start, end, map::get, gender, age, income, context);
            case Month:
                return getCostPerThousandImpressions(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getBounceRate(DateTime start, DateTime end, Interval interval)
    {
        switch(interval)
        {
            case Hour:
                return getBounceRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0));
            case Day:
                return getBounceRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0));
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getBounceRate(start, end, map::get);
            case Month:
                return getBounceRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0));
            default:
                throw new IllegalArgumentException();
        }
    }

    public Queryable<Pair<DateTime, Double>> getBounceRate(DateTime start, DateTime end, Interval interval, Gender gender, Age age, Income income, Context context)
    {
        switch(interval)
        {
            case Hour:
                return getBounceRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), d.getHour(), 0, 0), gender, age, income, context);
            case Day:
                return getBounceRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0), gender, age, income, context);
            case Week:
                HashMap<DateTime, DateTime> map = new HashMap<>();
                for (DateTime d = start, wd = d; d.compareTo(end) <= 0; wd = d)
                    for (int i = 0; i < 7; i++, d = d.addDays(1, false))
                        map.put(d, wd);
                return getBounceRate(start, end, map::get, gender, age, income, context);
            case Month:
                return getBounceRate(start, end, d -> new DateTime(d.getYear(), d.getMonth(), 0, 0, 0, 0), gender, age, income, context);
            default:
                throw new IllegalArgumentException();
        }
    }


    private Queryable<Pair<DateTime, Integer>> getImpressionCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return impressionLog.where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0)
                .groupBy(x -> normalizeDateTime.apply(x.date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().date), x.count()));
    }

    private Queryable<Pair<DateTime, Integer>> getImpressionCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return filterImpressions(impressionLog.where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0), x -> x, gender, age, income, context)
                .groupBy(x -> normalizeDateTime.apply(x.date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().date), x.count()));
    }

    private Queryable<Pair<DateTime, Integer>> getClickCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return clickLog.where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0)
                .groupBy(x -> normalizeDateTime.apply(x.date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().date), x.count()));
    }

    private Queryable<Pair<DateTime, Integer>> getClickCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return filterImpressions(linkImpressionsToClicks(impressionLog, clickLog).where(x -> x.getKey() != null && x.getValue() != null && x.getValue().date.compareTo(start) >= 0 && x.getValue().date.compareTo(end) <= 0), Pair::getKey, gender, age, income, context)
                .groupBy(x -> normalizeDateTime.apply(x.getValue().date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().getValue().date), x.count()));
    }

    private Queryable<Pair<DateTime, Integer>> getUniquesCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return clickLog.groupBy(x -> x.id).select(Queryable::first).where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0)
                .groupBy(x -> normalizeDateTime.apply(x.date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().date), x.count()));
    }

    private Queryable<Pair<DateTime, Integer>> getUniquesCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return filterImpressions(linkImpressionsToClicks(impressionLog, clickLog), Pair::getKey, gender, age, income, context).select(Pair::getValue).groupBy(x -> x.id).select(Queryable::first).where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0)
                .groupBy(x -> normalizeDateTime.apply(x.date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().date), x.count()));
    }

    private Queryable<Pair<DateTime, Integer>> getBouncesCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return serverLog.where(x -> x.entryDate.compareTo(start) >= 0 && x.entryDate.compareTo(end) <= 0 && isBounce(x))
                .groupBy(x -> normalizeDateTime.apply(x.entryDate)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().entryDate), x.count()));
    }

    private Queryable<Pair<DateTime, Integer>> getBouncesCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return filterImpressions(serverLog.where(x -> x.entryDate.compareTo(start) >= 0 && x.entryDate.compareTo(end) <= 0 && isBounce(x))
                .innerJoin(linkImpressionsToClicks(impressionLog, clickLog), x -> x.entryDate, x -> x.getValue().date, Pair::new), x -> x.getValue().getKey(), gender, age, income, context)
                .select(Pair::getKey)
                .groupBy(x -> normalizeDateTime.apply(x.entryDate)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().entryDate), x.count()));
    }

    private Queryable<Pair<DateTime, Integer>> getConversionCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return serverLog.where(x -> x.entryDate.compareTo(start) >= 0 && x.entryDate.compareTo(end) <= 0 && x.conversion)
                .groupBy(x -> normalizeDateTime.apply(x.entryDate)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().entryDate), x.count()));
    }

    private Queryable<Pair<DateTime, Integer>> getConversionCount(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return filterImpressions(serverLog.where(x -> x.entryDate.compareTo(start) >= 0 && x.entryDate.compareTo(end) <= 0 && x.conversion)
                .innerJoin(linkImpressionsToClicks(impressionLog, clickLog), x -> x.entryDate, x -> x.getValue().date, Pair::new), x -> x.getValue().getKey(), gender, age, income, context)
                .select(Pair::getKey)
                .groupBy(x -> normalizeDateTime.apply(x.entryDate)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().entryDate), x.count()));
    }

    private Queryable<Pair<DateTime, Double>> getTotalCost(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        Queryable<Pair<DateTime, Double>> impressionGroups = impressionLog.where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0).groupBy(x -> normalizeDateTime.apply(x.date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().date), x.sumD(z -> z.cost)));
        Queryable<Pair<DateTime, Double>> clickGroups = clickLog.where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0).groupBy(x -> normalizeDateTime.apply(x.date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().date), x.sumD(z -> z.cost)));

        HashMap<DateTime, Double> map = new HashMap<>();
        for (Pair<DateTime, Double> g : impressionGroups)
            map.put(g.getKey(), g.getValue());

        for (Pair<DateTime, Double> g : clickGroups)
            if (!map.containsKey(g.getKey()))
                map.put(g.getKey(), g.getValue());
            else map.put(g.getKey(), g.getValue() + map.get(g.getKey()));

            List<Pair<DateTime, Double>> rl = new ArrayList<>();
            for (DateTime key : map.keySet())
                rl.add(new Pair<>(key, map.get(key)));

        return new Queryable<Pair<DateTime, Double>>()
        {
            @Override
            public Enumerator<Pair<DateTime, Double>> getEnumerator()
            {
                return new IterableEnumerator<>(rl);
            }
        };
    }

    private Queryable<Pair<DateTime, Double>> getTotalCost(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        Queryable<Pair<DateTime, Double>> impressionGroups = filterImpressions(impressionLog.where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0), x -> x, gender, age, income, context).groupBy(x -> normalizeDateTime.apply(x.date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().date), x.sumD(z -> z.cost)));
        Queryable<Pair<DateTime, Double>> clickGroups = filterImpressions(linkImpressionsToClicks(impressionLog, clickLog), Pair::getKey, gender, age, income, context).select(Pair::getValue).where(x -> x.date.compareTo(start) >= 0 && x.date.compareTo(end) <= 0).groupBy(x -> normalizeDateTime.apply(x.date)).select(x -> new Pair<>(normalizeDateTime.apply(x.first().date), x.sumD(z -> z.cost)));

        HashMap<DateTime, Double> map = new HashMap<>();
        for (Pair<DateTime, Double> g : impressionGroups)
            map.put(g.getKey(), g.getValue());

        for (Pair<DateTime, Double> g : clickGroups)
            if (!map.containsKey(g.getKey()))
                map.put(g.getKey(), g.getValue());
            else map.put(g.getKey(), g.getValue() + map.get(g.getKey()));

        List<Pair<DateTime, Double>> rl = new ArrayList<>();
        for (DateTime key : map.keySet())
            rl.add(new Pair<>(key, map.get(key)));

        return new Queryable<Pair<DateTime, Double>>()
        {
            @Override
            public Enumerator<Pair<DateTime, Double>> getEnumerator()
            {
                return new IterableEnumerator<>(rl);
            }
        };
    }

    private Queryable<Pair<DateTime, Double>> getClickThroughRate(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return getImpressionCount(start, end, normalizeDateTime)
                .innerJoin(getClickCount(start, end, normalizeDateTime), Pair::getKey, Pair::getKey, (i,c) -> new Pair<>(i.getKey(), c.getValue() / (double) i.getValue()));
    }

    private Queryable<Pair<DateTime, Double>> getClickThroughRate(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return getImpressionCount(start, end, normalizeDateTime, gender, age, income, context)
                .innerJoin(getClickCount(start, end, normalizeDateTime, gender, age, income, context), Pair::getKey, Pair::getKey, (i,c) -> new Pair<>(i.getKey(), c.getValue() / (double) i.getValue()));
    }

    private Queryable<Pair<DateTime, Double>> getCostPerAcquisition(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return getTotalCost(start, end, normalizeDateTime)
                .innerJoin(getConversionCount(start, end, normalizeDateTime), Pair::getKey, Pair::getKey, (t,c) -> new Pair<>(t.getKey(),  t.getValue() / c.getValue()));
    }

    private Queryable<Pair<DateTime, Double>> getCostPerAcquisition(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return getTotalCost(start, end, normalizeDateTime, gender, age, income, context)
                .innerJoin(getConversionCount(start, end, normalizeDateTime, gender, age, income, context), Pair::getKey, Pair::getKey, (t,c) -> new Pair<>(t.getKey(),  t.getValue() / c.getValue()));
    }

    private Queryable<Pair<DateTime, Double>> getCostPerClick(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return getTotalCost(start, end, normalizeDateTime)
                .innerJoin(getClickCount(start, end, normalizeDateTime), Pair::getKey, Pair::getKey, (t,c) -> new Pair<>(t.getKey(),  t.getValue() / c.getValue()));
    }

    private Queryable<Pair<DateTime, Double>> getCostPerClick(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return getTotalCost(start, end, normalizeDateTime, gender, age, income, context)
                .innerJoin(getClickCount(start, end, normalizeDateTime, gender, age, income, context), Pair::getKey, Pair::getKey, (t,c) -> new Pair<>(t.getKey(),  t.getValue() / c.getValue()));
    }

    private Queryable<Pair<DateTime, Double>> getCostPerThousandImpressions(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return getTotalCost(start, end, normalizeDateTime)
                .innerJoin(getImpressionCount(start, end, normalizeDateTime), Pair::getKey, Pair::getKey, (t,i) -> new Pair<>(t.getKey(),  t.getValue() / (i.getValue() / 1000)));
    }

    private Queryable<Pair<DateTime, Double>> getCostPerThousandImpressions(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return getTotalCost(start, end, normalizeDateTime, gender, age, income, context)
                .innerJoin(getImpressionCount(start, end, normalizeDateTime, gender, age, income, context), Pair::getKey, Pair::getKey, (t,i) -> new Pair<>(t.getKey(),  t.getValue() / (i.getValue() / 1000)));
    }

    private Queryable<Pair<DateTime, Double>> getBounceRate(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime)
    {
        return getBouncesCount(start, end, normalizeDateTime)
                .innerJoin(getClickCount(start, end, normalizeDateTime), Pair::getKey, Pair::getKey, (b,c) -> new Pair<>(b.getKey(),  b.getValue() / (double) c.getValue()));
    }

    private Queryable<Pair<DateTime, Double>> getBounceRate(DateTime start, DateTime end, Function<DateTime, DateTime> normalizeDateTime, Gender gender, Age age, Income income, Context context)
    {
        return getBouncesCount(start, end, normalizeDateTime, gender, age, income, context)
                .innerJoin(getClickCount(start, end, normalizeDateTime, gender, age, income, context), Pair::getKey, Pair::getKey, (b,c) -> new Pair<>(b.getKey(),  b.getValue() / (double) c.getValue()));
    }


    private DateTime addInterval(DateTime dt, Interval inter)
    {
        switch (inter)
        {
            case Hour:
                return dt.addHours(1, false);
            case Day:
                return dt.addDays(1, false);
            case Week:
                dt.addDays(7, false);
            case Month:
                return dt.addMonths(1, true);
            default:
                throw new IllegalArgumentException();
        }
    }

    private Queryable<Pair<ImpressionLogItem, ClickLogItem>> linkImpressionsToClicks(Queryable<ImpressionLogItem> impressions, Queryable<ClickLogItem> clicks)
    {
        HashMap<Long, Queryable<ImpressionLogItem>> map = new HashMap<>();
        for (Queryable<ImpressionLogItem> logItems : impressions.reverse().groupBy(x -> x.id))
            map.put(logItems.first().id, logItems);

        return clicks.select(x -> map.get(x.id) == null ? new Pair<>((ImpressionLogItem) null, x) : new Pair<>(map.get(x.id).where(z -> z.date.compareTo(x.date) <= 0).first(null), x)).where(x -> x.getKey() != null);
    }

    private <T> Queryable<T> filterImpressions(Queryable<T> items, Function<T, ImpressionLogItem> getILog, Gender gender, Age age, Income income, Context context)
    {
        return items.where(z ->
        {
            ImpressionLogItem x = getILog.apply(z);
            return (gender == null || x.gender == gender) && (age == null || x.age == age) && (income == null || x.income == income) && (context == null || x.context == context);
        });
    }

    public Queryable<Double> getHistogramData(DateTime start, DateTime end)
    {
        return clickLog.where(x -> start.compareTo(x.date) <= 0 && end.compareTo(x.date) >= 0).select(x -> x.cost);
    }

    @Override
    public void close()
    {
        if (closed) return;
        // Close files (must be done here as some queries are lazily evaluated).
        for (FileHandler fileHandler : fileHandlers)
            fileHandler.close();
        closed = true;
    }
}
