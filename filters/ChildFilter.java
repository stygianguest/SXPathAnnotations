package filters;

import java.util.Iterator;

import org.xml.sax.Attributes;

public class ChildFilter<T> implements SaxFilter<T> {

	public ChildFilter(String tagname, SaxFilter<T> next) {
		this.next = next;
		this.tagname = tagname;
	}
	
	SaxFilter<T> next;
	String tagname;
	int depth = 0;
	boolean isMatch = false;
	
	@Override
	public Iterator<T> startElement(String uri, String localName, String qName) {
		depth++;
		
		if (isMatch) // note isMatch => depth > 1
			return next.startElement(uri, localName, qName);
		
		if (depth == 1)
			isMatch = tagname.equals(qName);
			
		return new EmptyIterator<T>();
	}
	
	@Override
	public Iterator<T> attributes(Attributes attributes) {
		if (isMatch)
			return next.attributes(attributes);
		
		return new EmptyIterator<T>();
	}

	@Override
	public Iterator<T> characters(char[] ch, int start, int length) {
		if (isMatch)
			return next.characters(ch, start, length);
		
		return new EmptyIterator<T>();
	}

	@Override
	public Iterator<T> endElement(String uri, String localName, String qName) {
		depth--;
		
		if (depth == 0 && isMatch) {
			isMatch = false;
			return next.deselect();
		}
		
		if (isMatch)
			return next.endElement(uri, localName, qName);
		
		return new EmptyIterator<T>();
	}

	@Override
	public Iterator<T> deselect() {
        //FIXME: returning empty iterator here is justified because a deselect
        //should have been preceded by endElement, which would already have
        //generated a deselect, howerver, in case of erroneous xml files
        //(missing end-tags) it might be better to deselect anyway (and return
        //the results)
		return new EmptyIterator<T>();
	}

	@Override
	public SaxFilter<T> fork() {
		return new ChildFilter<T>(tagname, next.fork());
	}
	
	@Override
	public <U> SaxFilter<U> append(SaxFilter<U> tail) {
		return new ChildFilter<U>(tagname, next.append(tail));
	}
	
	@Override
	public String toString() {
		return "/" + tagname + next;
	}

}
