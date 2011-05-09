package filters;

import java.util.Iterator;

import org.xml.sax.Attributes;

public class AttributeFilter<T> implements SaxFilter<T> {

	public AttributeFilter(String attribute, SaxFilter<T> next) {
		this.next = next;
		this.attribute = attribute;
	}
	
	SaxFilter<T> next;
	String attribute;
	
	int depth = 0;
	
	@Override
	public Iterator<T> startElement(String uri, String localName, String qName) {
		depth++;

		return new EmptyIterator<T>();
	}
	
	@Override
	public String toString() {
		return "@" + attribute + next;
	}

	@Override
	public Iterator<T> attributes(Attributes attributes) {
		if (depth == 0) {
			String value = attributes.getValue(attribute);
			
			if (value != null) {
				return AppendIterator.append(
                    next.characters(value.toCharArray(), 0, value.length()),
                    next.deselect());
			}
		}
		
		return new EmptyIterator<T>();
	}

	@Override
	public Iterator<T> characters(char[] ch, int start, int length) {
		return new EmptyIterator<T>();
	}

	@Override
	public Iterator<T> endElement(String uri, String localName, String qName) {
		depth--;
		return new EmptyIterator<T>();
	}

	@Override
	public Iterator<T> deselect() {
		return new EmptyIterator<T>();
	}

	@Override
	public SaxFilter<T> fork() {
		return new AttributeFilter<T>(attribute, next.fork());
	}

	@Override
	public <U> SaxFilter<U> append(SaxFilter<U> tail) {
		return new AttributeFilter<U>(attribute, next.append(tail));
	}
}
