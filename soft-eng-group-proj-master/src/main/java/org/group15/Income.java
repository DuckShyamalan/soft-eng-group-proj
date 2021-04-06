package org.group15;

import java.text.ParseException;

/**
 * Class enumerating income types
 */
public enum Income
{
    Low, Medium, High;

    /**
     * Method for converting a string to an element of this enumerator
     * @param toConvert The string to convert
     * @return The element of this enumerator
     * @throws ParseException in case it doesn't match anything
     */
    public static Income parseIncome(String toConvert) throws ParseException
    {
        if(toConvert.toLowerCase().equals("low"))           return Low;
        else if (toConvert.toLowerCase().equals("medium"))  return Medium;
        else if (toConvert.toLowerCase().equals("high"))    return High;

        throw new ParseException("Parse error in income field", -1);
    }
}
