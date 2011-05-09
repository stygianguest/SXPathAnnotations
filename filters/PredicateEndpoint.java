package filters;

import java.util.Iterator;

import org.xml.sax.Attributes;

//FIXME: this thing actually returns an empty list or a list with one boolean
// the value of which will always be true, I need to add some kind of special
// predicate branch that ignores the value of the predicate endpoint
public class PredicateEndpoint implements SaxFilter<Boolean> {

	public PredicateEndpoint() {
	}

	@Override
	public Iterator<Boolean> attributes(Attributes attributes) {
        return new UnitIterator();
	}

	@Override
	public Iterator<Boolean> characters(char[] ch, int start, int length) {
        return new UnitIterator();
	}

	@Override
	public Iterator<Boolean> endElement(String uri, String localName, String qName) {
        return new UnitIterator();
	}

	@Override
	public Iterator<Boolean> deselect() {
        return new UnitIterator();
	}

	@Override
	public Iterator<Boolean> startElement(String uri, String localName, String qName) {
        return new UnitIterator();
	}

	@Override
	public SaxFilter<Boolean> fork() {
        // since this class is stateless, there's no need to make a copy
		return this;
	}
	
	@Override
	public <U> SaxFilter<U> append(SaxFilter<U> tail) {
		return tail;
	}
	
	@Override
	public String toString() {
		return "?";
	}
}
