package org.group15;

import java.text.ParseException;

/**
 *  Class enumerating genders present in the files
 */
public enum Gender
{
    Male, Female;

    /**
     * Method for parsing a string to an element of this enumerator
     * @param toConvert The string to convert
     * @return The Gender the input represents.
     * @throws ParseException if the string is not recognised as a gender.
     */
    public static Gender parseGender(String toConvert) throws ParseException
    {
        if(toConvert.toLowerCase().equals("male")) return Male;
        else if (toConvert.toLowerCase().equals("female")) return Female;
        throw new ParseException("Parse error in gender field",0);
    }
}
