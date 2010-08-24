package filters;

import java.util.Iterator;
import java.util.Vector;
import java.lang.UnsupportedOperationException;
import java.util.NoSuchElementException;

public class ReturnValue<T> implements Iterator<T> {

    protected ReturnValue(T value) {
        this.value = value;
    }

    private T value;

    public boolean hasNext() {
        return value != null;
    }

    public T next() {
        if (!this.hasNext()) {
            //TODO: proper error message
            throw new NoSuchElementException();
        }

        T v = value;
        value = null;
        return v;
    }

    public void remove() {
        //TODO: proper error message
        throw new UnsupportedOperationException();
    }


	protected ReturnValue<T> append(final ReturnValue<T> other) {
        if (!this.hasNext())
            return other;

        return new ReturnValue<T>(null) {
            @Override
            public boolean hasNext() {
                return ReturnValue.this.hasNext() || other.hasNext();
            }

            @Override
            public T next() {
                if (ReturnValue.this.hasNext())
                    return ReturnValue.this.next();
                else
                    return other.next();
            }
        };
    }


    protected <U> ReturnValue<Pair<T,U>> product(final ReturnValue<U> other) {
        if (!this.hasNext())
            return new ReturnValue<Pair<T,U>>(null);
        
        if (!other.hasNext())
            return new ReturnValue<Pair<T,U>>(null);

        final Vector<T> values = new Vector<T>();
        while (this.hasNext())
            values.add(this.next());

        return new ReturnValue<Pair<T,U>>(null) {
            U currentOther = other.next();
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < values.size() || other.hasNext();
            }

            @Override
            public Pair<T,U> next() {
            	if (!hasNext())
            		throw new NoSuchElementException();
            	
                Pair<T,U> retVal = new Pair<T,U>(values.get(i), currentOther);
                i += 1;
                if (i >= values.size()) {
                    i = 0;
                    currentOther = other.next();
                } 
                return retVal;
            }
        };
    }
}
