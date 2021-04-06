package org.group15;

import java.util.Objects;

public class Pair<K,V>
{
    private final K key;
    private final V value;

    public Pair(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    public K getKey()
    {
        return key;
    }

    public V getValue()
    {
        return value;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Pair && this.equals((Pair) obj);
    }

    public boolean equals (Pair p)
    {
        return key.equals(p.key) && value.equals(p.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(key, value);
    }
}
