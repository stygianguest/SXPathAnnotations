package filters;

import java.util.Iterator;
import java.util.Stack;

import org.xml.sax.Attributes;

public class DescendantFilter<T> implements SaxFilter<T> {

	public DescendantFilter(String tagname, SaxFilter<T> next) {
		this.next = next;
		this.tagname = tagname;
	}
	
	SaxFilter<T> next;
	String tagname;
	Stack<SaxFilter<T>> stack = new Stack<SaxFilter<T>>();
	
	@Override
	public Iterator<T> startElement(String uri, String localName, String qName) {
		Iterator<T> results = new EmptyIterator<T>();
		
		for ( SaxFilter<T> filter : stack )
			results = AppendIterator.append(results, filter.startElement(uri, localName, qName));
		
		if (tagname.equals(qName))
			stack.push(next.fork());
		
		return results;
	}
	
	@Override
	public Iterator<T> attributes(Attributes attributes) {
		Iterator<T> results = new EmptyIterator<T>();
		
		for (SaxFilter<T> filter : stack)
			results = AppendIterator.append(results, filter.attributes(attributes));
		
		return results;
	}

	@Override
	public Iterator<T> characters(char[] ch, int start, int length) {
		Iterator<T> results = new EmptyIterator<T>();
		
		for ( SaxFilter<T> filter : stack )
			results = AppendIterator.append(results, filter.characters(ch, start, length));
		
		return results;
	}

	@Override
	public Iterator<T> endElement(String uri, String localName, String qName) {
		Iterator<T> results = new EmptyIterator<T>();
		
		if (tagname.equals(qName))
			results = AppendIterator.append(results, stack.pop().deselect());

		for ( SaxFilter<T> filter : stack )
            results = AppendIterator.append(results, filter.endElement(uri, localName, qName));

		return results;
	}

	@Override
	public Iterator<T> deselect() {
        //FIXME: see ChildFilter.deselect()
		return new EmptyIterator<T>();
	}

	@Override
	public SaxFilter<T> fork() {
		return new DescendantFilter<T>(tagname, next.fork());
	}

	@Override
	public <U> SaxFilter<U> append(SaxFilter<U> tail) {
		return new DescendantFilter<U>(tagname, next.append(tail));
	}
}
