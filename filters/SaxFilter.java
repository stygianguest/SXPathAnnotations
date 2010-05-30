package filters;

import org.xml.sax.Attributes;

//TODO: make this class entirely abstract and move the 'next' to the filters
//TODO: add a select() as the counterparty of deselect()
//TODO: abstract from the returned boolean to allow endpoint specific return values
public abstract class SaxFilter {
	
	SaxFilter next;
	
	public SaxFilter(SaxFilter next) {
		this.next = next;
	}

	public boolean characters(char[] ch, int start, int length) {
		return next.characters(ch, start, length);
	}

	public boolean endElement(String uri, String localName, String qName) {
		return next.endElement(uri, localName, qName);
	}
	
	public boolean deselect() {
		return false;
	}
	
	public boolean startElement(String uri, String localName, String qName) {
		return next.startElement(uri, localName, qName);
	}
	
	public boolean attributes(Attributes attributes) {
		return next.attributes(attributes);
	
	}
	
	public SelectionEndpoint[] getEndpoints() {
		return next.getEndpoints();
	}
	
	public abstract SaxFilter fork();
	
	public abstract SaxFilter merge(SaxFilter filter);

}
