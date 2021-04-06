package org.group15;

import java.util.Iterator;

public class IterableEnumerator<T> implements Enumerator<T>
{
    private boolean closed;
    private Iterable<T> iterable;
    private Iterator<T> iterator;
    private T current = null;

    public IterableEnumerator(Iterable<T> iterable)
    {
        this.iterable = iterable;
        iterator = iterable.iterator();
    }

    @Override
    public T getCurrent()
    {
        return current;
    }

    @Override
    public boolean moveNext()
    {
        if (closed) return false;

        if (!iterator.hasNext())
        {
            current = null;
            return false;
        }

        current = iterator.next();
        return true;
    }

    @Override
    public void reset()
    {
        if (closed) return;
        iterator = iterable.iterator();
    }

    @Override
    public void close()
    {
        if (closed) return;
        iterable = null;
        iterator = null;
        current = null;
        closed = true;
    }
}
