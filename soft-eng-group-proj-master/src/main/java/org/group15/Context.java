package org.group15;

import java.text.ParseException;

/**
 * Enumeration for ad context.
 */
public enum Context
{
    News,Shopping,Social,Media,Blog,Hobbies,Travel;

    /**
     * Parse a context
     * @param toConvert the string to parse from
     * @return the context element
     * @throws ParseException in case it doesn't match anything
     */
    public static Context parseContext(String toConvert) throws ParseException
    {
        switch (toConvert.toLowerCase())
        {
            case "news":
                return News;
            case "shopping":
                return Shopping;
            case "social media":
                return Social;
            case "media":
                return Media;
            case "blog":
                return Blog;
            case "hobbies":
                return Hobbies;
            case "travel":
                return Travel;
            default: throw new ParseException("Parse error in context field for " + toConvert, -1);
        }

    }
}
