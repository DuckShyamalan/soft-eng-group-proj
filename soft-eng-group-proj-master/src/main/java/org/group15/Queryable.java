package org.group15;

import java.util.*;
import java.util.function.*;

/**
 * Represents a sequence of elements which can be queried.
 * @param <T> The type of the elements in the sequence.
 */
public abstract class Queryable<T> implements Iterable<T>
{
    public abstract Enumerator<T> getEnumerator();

    // -------- QUERY LANGUAGE IMPLEMENTATION --------

    /**
     * Projects each element of the sequence through a function into a new sequence.
     * @param transformation The function to project each element of the sequence through
     * @param <TOut> The resulting sequence's element type.
     * @return A new sequence fo all the elements of the previous sequence projected through the function.
     */
    public <TOut> Queryable<TOut> select(Function<T, TOut> transformation) { return new Select<>(this, transformation); }

    /**
     * Filters a sequence with a predicate.
     * @param condition The predicate to filter the sequence with.
     * @return All elements of the sequence that satisfy the predicate.
     */
    public Queryable<T> where(Predicate<T> condition) { return new Where<>(this, condition); }

    /**
     * Performs an inner join on this sequence and another sequence on a specified key.
     * @param other The other sequence to inner join with.
     * @param sourceComp A function to produce a key from a given element of this sequence.
     * @param otherComp A function to produce a key from a given element of the other sequence.
     * @param combinator A function to combine an element from each sequence to produce an element in the output sequence.
     * @param <TOther> The type of the elements in the other sequence.
     * @param <TKey> The type of the keys to join the sequences on.
     * @param <TOut> The type of the elements in the output sequence.
     * @return A new sequence of elements representing the joined input sequences.
     */
    public <TOther, TKey, TOut> Queryable<TOut> innerJoin(Queryable<TOther> other, Function<T, TKey> sourceComp, Function<TOther, TKey> otherComp, Combinator<T, TOther, TOut> combinator)
    {
        return new InnerJoin<>(this, other, sourceComp, otherComp, combinator);
    }

    /**
     * Performs a left join on this sequence and another sequence on a specified key.
     * @param other The other sequence to inner join with.
     * @param sourceComp A function to produce a key from a given element of this sequence.
     * @param otherComp A function to produce a key from a given element of the other sequence.
     * @param combinator A function to combine an element from each sequence to produce an element in the output sequence.
     * @param <TOther> The type of the elements in the other sequence.
     * @param <TKey> The type of the keys to join the sequences on.
     * @param <TOut> The type of the elements in the output sequence.
     * @return A new sequence of elements representing the joined input sequences.
     */
    public <TOther, TKey, TOut> Queryable<TOut> leftJoin(Queryable<TOther> other, Function<T, TKey> sourceComp, Function<TOther, TKey> otherComp, Combinator<T, TOther, TOut> combinator)
    {
        return new LeftOuterJoin<>(this, other, sourceComp, otherComp, combinator);
    }

    //Useful for getting the output of queries.
    @Override
    public Iterator<T> iterator() { return new EnumeratorIterator<>(getEnumerator()); }

    /**
     * Counts the elements in the sequence.
     * @return The number of elements in the sequence.
     */
    public int count()
    {
        Enumerator<T> en = getEnumerator();
        int c = 0;
        while (en.moveNext()) c++;
        en.close();
        return c;
    }

    /**
     * Sums up a list of elements by a selected integer key.
     * @param selector Selects the integer to use as the value of the element.
     * @return The resulting sum of the selected elements.
     */
    public int sumI(Function<T, Integer> selector)
    {
        Enumerator<T> en = getEnumerator();
        int s = 0;
        while (en.moveNext()) s += selector.apply(en.getCurrent());
        en.close();
        return s;
    }

    /**
     * Sums up a list of elements by a selected double key.
     * @param selector Selects the double to use as the value of the element.
     * @return The resulting sum of the selected elements.
     */
    public double sumD(Function<T, Double> selector)
    {
        Enumerator<T> en = getEnumerator();
        double s = 0D;
        while (en.moveNext()) s += selector.apply(en.getCurrent());
        return s;
    }

    /**
     * Folds all elements of a sequence together in an accumulator.
     * @param combinator A function to combine the current value in the accumulator with the next value in the sequence, producing a new accumulator value.
     * @param initialValue The initial value to start with in the accumulator.
     * @param <TOut> The type of the value in the accumulator.
     * @return The final value of the folded sequence.
     */
    public <TOut> TOut aggregate(Combinator<TOut,T,TOut> combinator, TOut initialValue)
    {
        Enumerator<T> en = getEnumerator();
        TOut agg = initialValue;
        while (en.moveNext())
            agg = combinator.apply(agg, en.getCurrent());
        en.close();
        return agg;
    }

    /**
     * Groups elements together based on a given key.
     * @param keySelector A function to get the key of a given element.
     * @param <TKey> The type of the key to group elements by.
     * @return The grouped elements in a multi-layered sequence.
     */
    public <TKey> Queryable<Queryable<T>> groupBy(Function<T, TKey> keySelector) { return new GroupBy<>(this, keySelector); }


    public <TOther, TKey, TOut> Queryable<TOut> groupJoin(Queryable<TOther> other, Function<T, TKey> keySelector, Function<TOther, TKey> otherKeySelector, Combinator<T, Queryable<TOther>, TOut> selector)
    {
        return leftJoin(other.groupBy(otherKeySelector), keySelector, x -> otherKeySelector.apply(x.first()), (x, xs) -> selector.apply(x, xs == null ? empty() : xs));
    }

    public Queryable<T> reverse() { return new Reverse<>(this); }

    /**
     * Produces an ArrayList of all the elements of the sequence.
     * @return An arraylist containing the elements in the sequence.
     */
    public ArrayList<T> toArrayList()
    {
        ArrayList<T> arrL = new ArrayList<>();
        Enumerator<T> en = getEnumerator();
        while (en.moveNext())
            arrL.add(en.getCurrent());
        en.close();
        return arrL;
    }

    /**
     * Gets the first element of the sequence.
     * @return The first element of the sequence, or null if the sequence is empty.
     */
    public T first()
    {
        Enumerator<T> en = getEnumerator();
        if (en.moveNext())
        {
            T r = en.getCurrent();
            en.close();
            return r;
        }
        else return null;
    }

    /**
     * Gets the first element of the sequence.
     * @param defaultValue The value to return if the sequence is empty.
     * @return The first element of the sequence, or the specified default value if the sequence is empty.
     */
    public T first(T defaultValue)
    {
        T first = first();
        return first == null ? defaultValue : first;
    }

    /**
     * Produces an empty sequence.
     * @param <T> The type of elements expected to be in the empty sequence.
     * @return An empty sequence.
     */
    public static <T> Queryable<T> empty()
    {
        return new Queryable<T>()
        {
            @Override
            public Enumerator<T> getEnumerator()
            {
                return new Enumerator<T>()
                {
                    @Override
                    public T getCurrent()
                    {
                        return null;
                    }

                    @Override
                    public boolean moveNext()
                    {
                        return false;
                    }

                    @Override
                    public void reset()
                    {
                        return;
                    }

                    @Override
                    public void close()
                    {
                        return;
                    }
                };
            }
        };
    }
}

// -------- QUERY TYPE SUBCLASSES --------

class Select<TIn, TOut> extends Queryable<TOut>
{
    private final Queryable<TIn> source;
    private final Function<TIn, TOut> transformation;

    Select(Queryable<TIn> source, Function<TIn, TOut> transformation)
    {
        this.source = source;
        this.transformation = transformation;
    }

    @Override
    public Enumerator<TOut> getEnumerator() { return new En(); }

    public class En implements Enumerator<TOut>
    {
        private boolean closed;
        private TOut current = null;
        private final Enumerator<TIn> sourceEn = source.getEnumerator();

        @Override
        public TOut getCurrent()
        {
            if (closed) return null;
            return current;
        }

        @Override
        public boolean moveNext()
        {
            if (closed) return false;
            if (sourceEn.moveNext())
            {
                current = transformation.apply(sourceEn.getCurrent());
                return true;
            }

            return false;
        }

        @Override
        public void reset()
        {
            if (closed) return;
            sourceEn.reset();
            current = null;
        }

        @Override
        public void close()
        {
            if (closed) return;
            sourceEn.close();
            closed = true;
        }
    }
}

class Where<T> extends Queryable<T>
{
    private final Queryable<T> source;
    private final Predicate<T> condition;

    Where(Queryable<T> source, Predicate<T> condition)
    {
        this.source = source;
        this.condition = condition;
    }

    @Override
    public Enumerator<T> getEnumerator() { return new En(); }

    private class En implements Enumerator<T>
    {
        private T current = null;
        private boolean closed;

        private final Enumerator<T> sourceEn;

        En()
        {
            sourceEn = source.getEnumerator();
        }

        @Override
        public T getCurrent()
        {
            if (closed) return null;
            return current;
        }

        @Override
        public boolean moveNext()
        {
            if (closed) return false;
            while (sourceEn.moveNext())
            {
                T cur = sourceEn.getCurrent();
                if (condition.test(cur))
                {
                    current = cur;
                    return true;
                }
            }
            current = null;
            return false;
        }

        @Override
        public void reset()
        {
            if (closed) return;
            sourceEn.reset();
            current = null;
        }

        @Override
        public void close()
        {
            if (closed) return;
            sourceEn.close();
            closed = true;
        }
    }
}

class InnerJoin<TSource, TOther, TKey, TOut> extends Queryable<TOut>
{
    private final Queryable<TSource> source;
    private final Queryable<TOther> other;
    private final Function<TSource, TKey> sourceComp;
    private final Function<TOther, TKey> otherComp;
    private final Combinator<TSource, TOther, TOut> function;

    InnerJoin(Queryable<TSource> source, Queryable<TOther> other, Function<TSource, TKey> sourceComp,
                     Function<TOther, TKey> otherComp, Combinator<TSource, TOther, TOut> function)
    {
        this.source = source;
        this.other = other;
        this.sourceComp = sourceComp;
        this.otherComp = otherComp;
        this.function = function;
    }

    @Override
    public Enumerator<TOut> getEnumerator() { return new En(); }

    public class En implements Enumerator<TOut>
    {
        private boolean closed;
        private TOut current = null;
        private final Enumerator<TSource> sourceEn;
        private final Enumerator <TOther> otherEn;
        private HashMap<TKey, ArrayList<TOther>> otherMap;
        private Iterator<TOther> currentIt = null;

        En()
        {
            sourceEn = source.getEnumerator();
            otherEn = other.getEnumerator();
        }

        @Override
        public TOut getCurrent()
        {
            if (closed) return null;
            return current;
        }

        @Override
        public boolean moveNext()
        {
            if (closed) return false;
            if (otherMap == null)
            {
                otherMap = new HashMap<>();

                while (otherEn.moveNext())
                {
                    TOther cOther = otherEn.getCurrent();
                    TKey otherKey = otherComp.apply(cOther);
                    otherMap.putIfAbsent(otherKey, new ArrayList<>());
                    otherMap.get(otherKey).add(cOther);
                }
            }

            if (currentIt == null)
            {
                if (!sourceEn.moveNext())
                    return false;
                TKey sKey = sourceComp.apply(sourceEn.getCurrent());
                if (otherMap.containsKey(sKey))
                    currentIt = otherMap.get(sKey).iterator();
                return moveNext();
            }
            if (currentIt.hasNext())
            {
                current = function.apply(sourceEn.getCurrent(), currentIt.next());
                return true;
            }
            currentIt = null;
            return moveNext();
        }

        @Override
        public void reset()
        {
            if (closed) return;
            otherMap = null;
            sourceEn.reset();
            otherEn.reset();
            current = null;
        }

        @Override
        public void close()
        {
            if (closed) return;
            sourceEn.close();
            otherEn.close();
            closed = true;
        }
    }
}

class LeftOuterJoin<TSource, TOther, TKey, TOut> extends Queryable<TOut>
{
    private final Queryable<TSource> source;
    private final Queryable<TOther> other;
    private final Function<TSource, TKey> sourceComp;
    private final Function<TOther, TKey> otherComp;
    private final Combinator<TSource, TOther, TOut> function;

    LeftOuterJoin(Queryable<TSource> source, Queryable<TOther> other, Function<TSource, TKey> sourceComp,
              Function<TOther, TKey> otherComp, Combinator<TSource, TOther, TOut> function)
    {
        this.source = source;
        this.other = other;
        this.sourceComp = sourceComp;
        this.otherComp = otherComp;
        this.function = function;
    }

    @Override
    public Enumerator<TOut> getEnumerator() { return new En(); }

    public class En implements Enumerator<TOut>
    {
        private boolean closed;
        private TOut current = null;
        private final Enumerator<TSource> sourceEn;
        private final Enumerator <TOther> otherEn;
        private HashMap<TKey, ArrayList<TOther>> otherMap;
        private Iterator<TOther> currentIt = null;

        En()
        {
            sourceEn = source.getEnumerator();
            otherEn = other.getEnumerator();
        }

        @Override
        public TOut getCurrent()
        {
            if (closed) return null;
            return current;
        }

        @Override
        public boolean moveNext()
        {
            if (closed) return false;
            if (otherMap == null)
            {
                otherMap = new HashMap<>();

                while (otherEn.moveNext())
                {
                    TOther cOther = otherEn.getCurrent();
                    TKey otherKey = otherComp.apply(cOther);
                    otherMap.putIfAbsent(otherKey, new ArrayList<>());
                    otherMap.get(otherKey).add(cOther);
                }
            }

            if (currentIt == null)
            {
                if (!sourceEn.moveNext())
                    return false;
                TKey sKey = sourceComp.apply(sourceEn.getCurrent());
                if (otherMap.containsKey(sKey) && otherMap.get(sKey).size() > 0)
                {
                    currentIt = otherMap.get(sKey).iterator();
                    return moveNext();
                }
                else
                {
                    current = function.apply(sourceEn.getCurrent(), null);
                    return true;
                }
            }
            if (currentIt.hasNext())
            {
                current = function.apply(sourceEn.getCurrent(), currentIt.next());
                return true;
            }
            currentIt = null;
            return moveNext();
        }

        @Override
        public void reset()
        {
            if (closed) return;
            otherMap = null;
            sourceEn.reset();
            otherEn.reset();
            current = null;
        }

        @Override
        public void close()
        {
            if (closed) return;
            sourceEn.close();
            otherEn.close();
            closed = true;
        }
    }
}

// Basic implementation at the moment. Could be improved if required for performance.
class GroupBy<T, TKey> extends Queryable<Queryable<T>>
{
    private Function<T, TKey> keySelector;
    private Queryable<T> source;

    public GroupBy(Queryable<T> source, Function<T, TKey> keySelector)
    {
        this.source = source;
        this.keySelector = keySelector;
    }

    @Override
    public Enumerator<Queryable<T>> getEnumerator()
    {
        return new En();
    }

    private class En implements Enumerator<Queryable<T>>
    {
        private boolean closed;
        private HashMap<TKey, ArrayList<T>> map;
        private Iterator<ArrayList<T>> it;
        private Queryable<T> current;

        En()
        {
            Enumerator<T> sourceEn = source.getEnumerator();
            map = new HashMap<>();

            while (sourceEn.moveNext())
            {
                T c = sourceEn.getCurrent();
                TKey k = keySelector.apply(c);
                if (!map.containsKey(k))
                    map.put(k, new ArrayList<>());
                map.get(k).add(c);
            }
            it = map.values().iterator();
        }

        @Override
        public Queryable<T> getCurrent()
        {
            return current;
        }

        @Override
        public boolean moveNext()
        {
            if (closed) return false;
            if (!it.hasNext())
            {
                current = null;
                return false;
            }
            current = new Qu(it.next());
            return true;
        }

        @Override
        public void reset()
        {
            if (closed) return;
            it = map.values().iterator();
            current = null;
        }

        @Override
        public void close()
        {
            if (closed) return;
            current = null;
            closed = true;
        }

        private class Qu extends Queryable<T>
        {
            private ArrayList<T> arr;

            Qu(ArrayList<T> arr)
            {
                this.arr = arr;
            }

            @Override
            public Enumerator<T> getEnumerator()
            {
                return new IterableEnumerator<>(arr);
            }
        }
    }
}

class Reverse<T> extends Queryable<T>
{
    private Queryable<T> source;

    public Reverse(Queryable<T> source)
    {
        this.source = source;
    }

    @Override
    public Enumerator<T> getEnumerator()
    {
        return new En();
    }

    private class En implements Enumerator<T>
    {
        private int nextPos = -1;
        private T current;
        private boolean closed;
        private List<T> list;

        @Override
        public T getCurrent()
        {
            return current;
        }

        @Override
        public boolean moveNext()
        {
            if (closed) return false;
            if (list == null)
                initialise();

            if (nextPos < 0)
            {
                current = null;
                return false;
            }
            current = list.get(nextPos--);
            return true;
        }

        private void initialise()
        {
            list = source.toArrayList();
            nextPos = list.size() - 1;
        }

        @Override
        public void reset()
        {
            if (closed) return;
            list = null;
            nextPos = -1;
        }

        @Override
        public void close()
        {
            if (closed) return;
            closed = true;
            list = null;
            current = null;
        }
    }
}
