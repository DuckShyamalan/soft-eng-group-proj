package org.group15;

public interface ErrorHandler
{
    void handleError(String message);

    void handleError(Exception e);
}
