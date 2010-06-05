package filters;

import org.xml.sax.Attributes;

public class ChildFilter implements SaxFilter {

	public ChildFilter(String tagname, SaxFilter next) {
		this.next = next;
		this.tagname = tagname;
	}
	
	SaxFilter next;
	String tagname;
	int depth = 0;
	boolean isMatch = false;
	
	@Override
	public boolean startElement(String uri, String localName, String qName) {
		depth++;
		
		if (isMatch) // note isMatch => depth > 1
			return next.startElement(uri, localName, qName);
		
		if (depth == 1)
			isMatch = tagname.equals(qName);
			
		return false; // FIXME: this is strange.. shouldn't we return isMatch?
	}
	
	@Override
	public boolean attributes(Attributes attributes) {
		if (isMatch)
			return next.attributes(attributes);
		
		return false;
	}

	@Override
	public boolean characters(char[] ch, int start, int length) {
		if (isMatch)
			return next.characters(ch, start, length);
		
		return false;
	}

	@Override
	public boolean endElement(String uri, String localName, String qName) {
		depth--;
		
		if (depth == 0 && isMatch) {
			isMatch = false;
			return next.deselect();
		}
		
		if (isMatch)
			return next.endElement(uri, localName, qName);
		
		return false;
	}

	@Override
	public SaxFilter fork() {
		return new ChildFilter(tagname, next.fork());
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
