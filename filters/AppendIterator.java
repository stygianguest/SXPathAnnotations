package filters;

import java.util.Iterator;

public class AppendIterator<U> implements Iterator<U> {

    AppendIterator(Iterator<U> prefix, Iterator<U> suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
    
    private boolean prefixHasNext() {
    	return prefix != null && prefix.hasNext();
    }
    
    private boolean suffixHasNext() {
    	return suffix != null && suffix.hasNext();
    }
    

    public static <T> Iterator<T> append(Iterator<T> prefix, Iterator<T> suffix) {
        if (prefix == null || !prefix.hasNext())
            return suffix;

        if (prefix == null || !suffix.hasNext())
            return prefix;

        return new AppendIterator<T>(prefix, suffix);
    }

    Iterator<U> prefix;
    Iterator<U> suffix;

	@Override
	public boolean hasNext() {
		return prefixHasNext() || suffixHasNext();
	}

	@Override
	public U next() {
        if (prefixHasNext())
            return prefix.next();

		return suffix.next();
	}

	@Override
	public void remove() {
        throw new UnsupportedOperationException();
	}
}