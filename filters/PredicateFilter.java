package filters;

import java.util.Iterator;

import org.xml.sax.Attributes;

public class PredicateFilter<T> implements SaxFilter<T> {

	public PredicateFilter(SaxFilter<Boolean> pred, SaxFilter<T> next) {
		this.pred = pred;
		this.next = next;
	}
	
	SaxFilter<Boolean> pred;
	SaxFilter<T> next;
	
	boolean hasMatched = false;
	Iterator<T> buffer = null;
	
	private Iterator<T> conditionalReturn(final Iterator<T> retval) {
		Iterator<T> joinedRetVal = new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return buffer != null && buffer.hasNext() 
					|| retval != null && retval.hasNext();
			}

			@Override
			public T next() {
				if (buffer != null && buffer.hasNext())
					return buffer.next();
				else
					return retval.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		
		if (hasMatched) {
			buffer = null;
			return joinedRetVal;
		} else {
			buffer = joinedRetVal;
			return null;
		}
	}
	
	private void testPredicate(Iterator<Boolean> predResult) {
		//FIXME in principle it should be enough to just test hasNext()
		hasMatched = hasMatched || predResult.hasNext();
	}
	
	
	@Override
	public Iterator<T> attributes(Attributes attributes) {
		if (!hasMatched) testPredicate(pred.attributes(attributes));
		return conditionalReturn(next.attributes(attributes));
	}
	
	@Override
	public Iterator<T> characters(char[] ch, int start, int length) {
		if (!hasMatched) testPredicate(pred.characters(ch, start, length));
		return conditionalReturn(next.characters(ch, start, length));
	}
	
	@Override
	public Iterator<T> deselect() {
		if (!hasMatched) testPredicate(pred.deselect());
		Iterator<T> retval = conditionalReturn(next.deselect());
		hasMatched = false;
		buffer = null;
		return retval;
	}
	
	@Override
	public Iterator<T> endElement(String uri, String localName, String qName) {
		if (!hasMatched) testPredicate(pred.startElement(uri, localName, qName));
		return conditionalReturn(next.endElement(uri, localName, qName));
	}
	
	
	@Override
	public Iterator<T> startElement(String uri, String localName, String qName) {
		if (!hasMatched) testPredicate(pred.startElement(uri, localName, qName));
		return conditionalReturn(next.startElement(uri, localName, qName));
	}
	
	@Override
	public SaxFilter<T> fork() {
		//FIXME: not duplicating the buffer, nor the state of hasMatched because
		// it seems fork() is only called on instances where the buffer is empty
		// and hasMatched is false
		assert !hasMatched;
		assert !buffer.hasNext();
		return new PredicateFilter<T>(pred, next);
	}

	@Override
	public <U> SaxFilter<U> append(SaxFilter<U> tail) {
		return new PredicateFilter<U>(pred, next.append(tail));
	}

	
	@Override
	public String toString() {
		return "[" + pred + "]" + next;
	}
}
