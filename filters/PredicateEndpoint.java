package filters;

import java.util.Iterator;

import org.xml.sax.Attributes;

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
}
