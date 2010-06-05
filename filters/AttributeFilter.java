package filters;

import org.xml.sax.Attributes;

public class AttributeFilter implements SaxFilter {

	public AttributeFilter(String attribute, SaxFilter next) {
		this.next = next;
		this.attribute = attribute;
	}
	
	SaxFilter next;
	String attribute;
	
	int depth = 0;
	
	@Override
	public boolean startElement(String uri, String localName, String qName) {
		depth++;

		return false;
	}
	
	@Override
	public boolean attributes(Attributes attributes) {
		if (depth == 0) {
			String value = attributes.getValue(attribute);
			
			if (value != null) {
				next.characters(value.toCharArray(), 0, value.length());
				return next.deselect();
			}
		}
		
		return false;
	}

	@Override
	public boolean characters(char[] ch, int start, int length) {
		return false;
	}

	@Override
	public boolean endElement(String uri, String localName, String qName) {
		depth--;
		return false;
	}

	@Override
	public SaxFilter fork() {
		return new AttributeFilter(attribute, next.fork());
	}

	@Override
	public boolean deselect() {
		return false;
	}

	@Override
	public SelectionEndpoint[] getEndpoints() {
		return next.getEndpoints();
	}

}
