package filters;

import java.util.Stack;

import org.xml.sax.Attributes;

public class DescendantFilter implements SaxFilter {

	public DescendantFilter(String tagname, SaxFilter next) {
		this.next = next;
		this.tagname = tagname;
	}
	
	SaxFilter next;
	String tagname;
	Stack<SaxFilter> stack = new Stack<SaxFilter>();
	
	@Override
	public boolean startElement(String uri, String localName, String qName) {
		boolean match = false;
		
		for ( SaxFilter filter : stack )
			match = filter.startElement(uri, localName, qName) || match;
		
		if (tagname.equals(qName))
			stack.push(next.fork());
		
		return match;
	}
	
	@Override
	public boolean attributes(Attributes attributes) {
		boolean match = false;
		
		for (SaxFilter filter : stack)
			match = filter.attributes(attributes) || match;
		
		return match;
	}

	@Override
	public boolean characters(char[] ch, int start, int length) {
		boolean match = false;
		
		for ( SaxFilter filter : stack )
			match = filter.characters(ch, start, length) || match;
		
		return match;
	}

	@Override
	public boolean endElement(String uri, String localName, String qName) {
		boolean match = false;
		
		if (tagname.equals(qName)) {
			match = stack.pop().deselect();
		}

		for ( SaxFilter filter : stack )
			match = filter.endElement(uri, localName, qName) || match;

		return match;
	}

	@Override
	public SaxFilter fork() {
		return new DescendantFilter(tagname, next.fork());
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
