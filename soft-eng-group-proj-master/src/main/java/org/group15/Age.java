package org.group15;

import java.text.ParseException;

/**
 * Enumeration for age groups.
 */
public enum Age
{
    Less25, Less34, Less44, Less54, More54;

    /**
     * Parses a string to an age range.
     * @param toConvert The string to convert
     * @return An Age object representing the age range expressed in the string.
     * @throws ParseException Thrown if the string isn't recognised as an expected age range.
     */
    public static Age parseAge(String toConvert) throws ParseException {
        if(toConvert.toLowerCase().equals("<25")) return Less25;
        else if (toConvert.toLowerCase().equals("25-34")) return Less34;
        else if (toConvert.toLowerCase().equals("35-44")) return Less44;
        else if (toConvert.toLowerCase().equals("45-54")) return Less54;
        else if (toConvert.toLowerCase().equals(">54")) return More54;
        throw new ParseException("Parse error in age field",-1);
    }

}
