package filters;

import java.util.Iterator;

public class AppendIterator<U> implements Iterator<U> {

    AppendIterator(Iterator<U> prefix, Iterator<U> suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
    

    public static <T> Iterator<T> append(Iterator<T> prefix, Iterator<T> suffix) {
        if (!prefix.hasNext())
            return suffix;

        if (!suffix.hasNext())
            return prefix;

        return new AppendIterator<T>(prefix, suffix);
    }

    Iterator<U> prefix;
    Iterator<U> suffix;

	@Override
	public boolean hasNext() {
		return prefix.hasNext() || suffix.hasNext();
	}

	@Override
	public U next() {
        if (prefix.hasNext())
            return prefix.next();

		return suffix.next();
	}

	@Override
	public void remove() {
        throw new UnsupportedOperationException();
	}
}