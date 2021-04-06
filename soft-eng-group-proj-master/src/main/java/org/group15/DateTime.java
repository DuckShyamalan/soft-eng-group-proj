package org.group15;

import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTime implements Comparable<DateTime>
{
    private final int year, month, day, hour, minute, second;
    
    private static final Pattern parsePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})");
    private static final Pattern nullPattern = Pattern.compile("n/a", Pattern.CASE_INSENSITIVE);


    public DateTime(int year, int month, int day)
    {
        this(year, month, day, 0, 0, 0);
    }

    public DateTime(int year, int month, int day, int hour, int minute)
    {
        this(year, month, day, hour, minute, 0);
    }

    public DateTime(int year, int month, int day, int hour, int minute, int second)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getYear()
    {
        return year;
    }

    public int getMonth()
    {
        return month;
    }

    public int getDay()
    {
        return day;
    }

    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public int getSecond()
    {
        return second;
    }

    private static final int[] dIM = {31,28,31,30,31,30,31,31,30,31,30,31};

    private int daysInMonth (int year, int month)
    {
        if (month == 2 && year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))    //Leap years are weird. Look it up before you change this code!
            return 29;
        else return dIM[month - 1];
    }

    public DateTime addYears(int years, boolean ignoreExcessDays)
    {
        return new DateTime(getYear() + years, getMonth(), getDay(), getHour(), getMinute(), getSecond());
    }

    public DateTime addMonths(int months, boolean ignoreExcessDays)
    {
        int mon = getMonth() + months;
        DateTime ied = new DateTime(getYear() + (mon - 1) / 12, (mon-1) % 12 + 1, getDay(), getHour(), getMinute(), getSecond());
        if (ignoreExcessDays)
            return ied;
        else return ied.addDays(0, false);

    }

    public DateTime addDays(int days, boolean ignoreExcessDays)
    {
        int day = getDay() + days;
        if (ignoreExcessDays)
            return new DateTime(year, month, day, hour, minute, second);
        int month = getMonth();
        int year = getYear();
        int dim;
        while (day > (dim = daysInMonth(year, month)))
        {
            month++;
            day -= dim;
            year += (month - 1) / 12;
            month = (month - 1) % 12 + 1;
        }
        return new DateTime(year, month, day, hour, minute, second);
    }

    public DateTime addHours(int hours, boolean ignoreExcessDays)
    {
        int hour = (getHour() + hours) % 24;
        int day = getDay() + (getHour() + hours) / 24;
        DateTime ied = new DateTime(year, month, day, hour, minute, second);
        if (ignoreExcessDays)
            return ied;
        return ied.addDays(0, false);
    }

    public String toString()
    {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
    }

    public String toPrettyString()
    {
        return String.format("%02d:%02d:%02d  %02d/%02d/%04d", getHour(), getMinute(), getSecond(), getDay(), getMonth(), getYear());
    }

    public static DateTime parse(String str)
    {
        Matcher parseMatcher = parsePattern.matcher(str);
        if (parseMatcher.matches())
            return new DateTime(Integer.parseInt(parseMatcher.group(1)), Integer.parseInt(parseMatcher.group(2)),
                    Integer.parseInt(parseMatcher.group(3)), Integer.parseInt(parseMatcher.group(4)),
                    Integer.parseInt(parseMatcher.group(5)), Integer.parseInt(parseMatcher.group(6)));

        if (nullPattern.matcher(str).matches())
            return null;

        throw new DateTimeParseException("DateTime could not be parsed.", str, -1);
    }

    public static DateTime parseDate(String str, String pattern)
    {
        Matcher parseMatcher = Pattern.compile(pattern).matcher(str);
        if (parseMatcher.matches())
            return new DateTime(Integer.parseInt(parseMatcher.group("year")), Integer.parseInt(parseMatcher.group("month")),
                    Integer.parseInt(parseMatcher.group("day")));

        throw new DateTimeParseException("Date \"" + str + "\" could not be parsed.", str, -1);
    }

    @Override
    public int compareTo(DateTime o)
    {
        if (getYear() < o.getYear())
            return -1;
        if (getYear() > o.getYear())
            return 1;
        if (getMonth() < o.getMonth())
            return -1;
        if (getMonth() > o.getMonth())
            return 1;
        if (getDay() < o.getDay())
            return -1;
        if (getDay() > o.getDay())
            return 1;
        if (getHour() < o.getHour())
            return -1;
        if (getHour() > o.getHour())
            return 1;
        if (getMinute() < o.getMinute())
            return -1;
        if (getMinute() > o.getMinute())
            return 1;
        if (getSecond() < o.getSecond())
            return -1;
        if (getSecond() > o.getSecond())
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof DateTime && equals((DateTime)obj);
    }

    public boolean equals(DateTime oth)
    {
        return second == oth.second
            && minute == oth.minute
            && hour == oth.hour
            && day == oth.day
            && month == oth.month
            && year == oth.year;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(year, month, day, hour, minute, minute, second);
    }
}
