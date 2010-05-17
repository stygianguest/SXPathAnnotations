package filters;

import org.xml.sax.Attributes;

//TODO: for now this only records text
public class SelectionEndpoint extends SaxFilter {

	public SelectionEndpoint() {
		super(null);
	}
	
	StringBuilder builder = new StringBuilder();
	String value;


	@Override
	public boolean startElement(String uri, String localName, String qName) {
		return false;
	}
	
	@Override
	public boolean attributes(Attributes attributes) {
		return false;
	}


	@Override
	public boolean characters(char[] ch, int start, int length) {
		builder.append(ch, start, length);
		return false;
	}


	@Override
	public boolean endElement(String uri, String localName, String qName) {
		return false;
	}


	@Override
	public boolean deselect() {
		value = builder.toString();
		builder = new StringBuilder();
		return true;
	}

	@Override
	public SaxFilter fork() {
		return this;
	}

	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public SelectionEndpoint[] getEndpoints() {
		return new SelectionEndpoint[] {this};
	}
}
