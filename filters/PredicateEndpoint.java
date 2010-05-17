package filters;

import org.xml.sax.Attributes;

public class PredicateEndpoint extends SaxFilter {

	public PredicateEndpoint() {
		super(null);
	}

	@Override
	public boolean attributes(Attributes attributes) {
		return false;
	}

	@Override
	public boolean characters(char[] ch, int start, int length) {
		return false;
	}

	@Override
	public boolean endElement(String uri, String localName, String qName) {
		return false;
	}

	@Override
	public boolean deselect() {
		return true;
	}

	@Override
	public boolean startElement(String uri, String localName, String qName) {
		return false;
	}

	@Override
	public SaxFilter fork() {
		//FIXME: can we really just return this? 
		return this;
	}
	
	@Override
	public SelectionEndpoint[] getEndpoints() {
		return new SelectionEndpoint[] {};
	}
}
