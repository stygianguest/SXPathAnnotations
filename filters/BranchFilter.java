package filters;

import java.util.Arrays;
import java.util.Vector;

import org.xml.sax.Attributes;

public class BranchFilter implements SaxFilter {
	
	public BranchFilter(SaxFilter[] nexts) {
		this.nexts = nexts;
	}
	
	SaxFilter[] nexts;
	int counter = 0;

	@Override
	public boolean startElement(String uri, String localName, String qName)  {
		for (SaxFilter next : nexts) {
			if (next.startElement(uri, localName, qName))
				counter++;
		}
		
		return counter == nexts.length;
	}
	
	@Override
	public boolean attributes(Attributes attributes) {
		for (SaxFilter next : nexts) {
			if (next.attributes(attributes))
				counter++;
		}
		
		return counter == nexts.length;
	}

	@Override
	public boolean characters(char[] ch, int start, int length) {
		
		for (SaxFilter next : nexts) {
			if (next.characters(ch, start, length))
				counter++;
		}
		
		return counter == nexts.length;
	}

	@Override
	public boolean endElement(String uri, String localName, String qName) {
		for (SaxFilter next : nexts) {
			if (next.endElement(uri, localName, qName))
				counter++;
		}
		
		return counter == nexts.length;
	}
	
	@Override
	public boolean deselect() {
		for (SaxFilter next : nexts) {
			if (next.deselect())
				counter++;
		}
		boolean isMatch = counter == nexts.length;
		
		counter = 0;
		
		return isMatch;
	}


	@Override
	public SaxFilter fork() {
		SaxFilter[] forkedNexts = new SaxFilter[nexts.length];
		
		for (int i = 0; i < forkedNexts.length; i++)
			forkedNexts[i] = nexts[i].fork();
		
		return new BranchFilter(forkedNexts);
	}
	
	@Override
	public SelectionEndpoint[] getEndpoints() {
		Vector<SelectionEndpoint> endpoints = new Vector<SelectionEndpoint>();
		
		for (SaxFilter branch : nexts)
			endpoints.addAll(Arrays.asList(branch.getEndpoints()));
		
		return endpoints.toArray(new SelectionEndpoint[] {});
	}

}
