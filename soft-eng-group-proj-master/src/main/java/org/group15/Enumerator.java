package org.group15;

/**
 * A simple interface to represent readonly enumerations.
 * @param <T> The type of the objects in the enumeration.
 */
public interface Enumerator<T> extends AutoCloseable
{
    /**
     * Returns the current element.
     */
    T getCurrent();

    /**
     * Moves to the next element in the enumeration.
     * @return true if the operation was successful, false if the end of the enumeration has been reached.
     */
    boolean moveNext();

    /**
     * Resets the current element to before the first element in the enumeration.
     */
    void reset();

    @Override
    void close();

}
