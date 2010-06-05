package filters;

import org.xml.sax.Attributes;

//TODO: add a select() as the counterparty of deselect()
//TODO: abstract from the returned boolean to allow endpoint specific return values
public interface SaxFilter {

	public boolean characters(char[] ch, int start, int length);
	
	public boolean endElement(String uri, String localName, String qName);
	
	public boolean deselect();
	
	public boolean startElement(String uri, String localName, String qName);
	
	public boolean attributes(Attributes attributes);
	
	public SelectionEndpoint[] getEndpoints();
	
	public SaxFilter fork();
	
	//TODO: add and implement this function
//	public abstract SaxFilter merge(SaxFilter filter);

}
