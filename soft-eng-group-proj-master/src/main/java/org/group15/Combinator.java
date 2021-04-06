package org.group15;

public interface Combinator<TSource, TOther, TOut>
{
    TOut apply(TSource first, TOther second);
}
