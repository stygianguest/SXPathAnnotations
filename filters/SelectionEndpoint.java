package filters;

import java.util.Iterator;

import org.xml.sax.Attributes;

//TODO: build more of these to handle the different types, and support regular expressions
//TODO: for now this only records text it should record the original xml content
public class SelectionEndpoint implements SaxFilter<String> {

	public SelectionEndpoint() {
	}
	
	StringBuilder builder = new StringBuilder();

	@Override
	public Iterator<String> startElement(String uri, String localName, String qName) {
		return new EmptyIterator<String>();
	}
	
	@Override
	public Iterator<String> attributes(Attributes attributes) {
		return new EmptyIterator<String>();
	}


	@Override
	public Iterator<String> characters(char[] ch, int start, int length) {
		builder.append(ch, start, length);
		return new EmptyIterator<String>();
	}


	@Override
	public Iterator<String> endElement(String uri, String localName, String qName) {
		return new EmptyIterator<String>();
	}


	@Override
	public Iterator<String> deselect() {
		String value = builder.toString();
		builder = new StringBuilder();
		return new SingletonIterator<String>(value);
	}

	@Override
	public SaxFilter<String> fork() {
		//FIXME: we really don't need to copy the value?!
		return this;
	}
//
//	@Override
//	public String toString() {
//		return builder.toString();
//	}

    class SingletonIterator<V> implements Iterator<V> {
    	
    	public SingletonIterator(V value) {
			this.value = value;
		}
    	
    	private V value = null;
    	
		@Override
		public boolean hasNext() {
			return value != null;
		}

		@Override
		public V next() {
			V value = this.value;
			this.value = null;
			return value;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
    }

	@Override
	public <U> SaxFilter<U> append(SaxFilter<U> tail) {
		return tail;
	}	
	
	@Override
	public String toString() {
		return "!";
	}
}
