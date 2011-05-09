package filters;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class UnitIterator implements Iterator<Boolean> {
	
	boolean wasCounted = false;

	@Override
	public boolean hasNext() {
		return !wasCounted;
	}

	@Override
	public Boolean next() {
		if (wasCounted)
			throw new NoSuchElementException();
		
		wasCounted = true;
		
		return new Boolean(true);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
