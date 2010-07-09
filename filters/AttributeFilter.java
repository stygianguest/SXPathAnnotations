package filters;

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
	public ReturnValue<T> startElement(String uri, String localName, String qName) {
		depth++;

		return new ReturnValue.Nothing<T>();
	}
	
	@Override
	public ReturnValue<T> attributes(Attributes attributes) {
		if (depth == 0) {
			String value = attributes.getValue(attribute);
			
			if (value != null) {
				next.characters(value.toCharArray(), 0, value.length());
				return next.deselect();
			}
		}
		
		return new ReturnValue.Nothing<T>();
	}

	@Override
	public ReturnValue<T> characters(char[] ch, int start, int length) {
		return new ReturnValue.Nothing<T>();
	}

	@Override
	public ReturnValue<T> endElement(String uri, String localName, String qName) {
		depth--;
		return new ReturnValue.Nothing<T>();
	}

	@Override
	public ReturnValue<T> deselect() {
		return new ReturnValue.Nothing<T>();
	}

	@Override
	public SaxFilter<T> fork() {
		return new AttributeFilter<T>(attribute, next.fork());
	}
	
	@Override
	public SelectionEndpoint[] getEndpoints() {
		return next.getEndpoints();
	}

}
