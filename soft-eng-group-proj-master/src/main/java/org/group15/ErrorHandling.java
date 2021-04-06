package org.group15;

import java.util.HashSet;

public class ErrorHandling
{
    private static HashSet<ErrorHandler> handlers = new HashSet<>();

    /**
     * Display error message, if fatal close the program when the dialog is closed
     * @param msg the message
     * @param fatal whether the error requires program closure
     */
    public static void displayError(String msg, boolean fatal)
    {
        for (ErrorHandler h : handlers)
            h.handleError(msg);
        if(fatal) closeProgram();
    }

    /**
     * Display error message taken from exception e, if fatal close the program when the dialog is closed
     * @param e The exception from which to take the message
     * @param fatal whether the error requires program closure
     */
    public static void displayError(Exception e, boolean fatal)
    {
        for (ErrorHandler h : handlers)
            h.handleError(e);
        if(fatal) closeProgram();
    }




    //In case we need to do stuff before closing in the future
    public static void closeProgram(){
        System.exit(0);
    }

    public static void addHandler(ErrorHandler handler)
    {
        handlers.add(handler);
    }

    public static void removeHandler(ErrorHandler handler)
    {
        handlers.remove(handler);
    }
}
