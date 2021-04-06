package org.group15;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EnumeratorIterator<T> implements Iterator<T>
{
    private Enumerator<T> en;
    private T next = null;

    public EnumeratorIterator(Enumerator<T> enumerator)
    {
        en = enumerator;
    }

    @Override
    public boolean hasNext()
    {
        if (next != null)
            return true;
        if (en.moveNext())
        {
            next = en.getCurrent();
            return true;
        }

        return false;
    }

    @Override
    public T next()
    {
        if (next != null)
        {
            T r = next;
            next = null;
            return r;
        }
        if (en.moveNext())
            return en.getCurrent();

        throw new NoSuchElementException();
    }
}
