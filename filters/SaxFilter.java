package filters;

import org.xml.sax.Attributes;

//TODO: add a select() as the counterparty of deselect()
//TODO: abstract from the returned boolean to allow endpoint specific return values
public interface SaxFilter<T> {

	public ReturnValue<T> characters(char[] ch, int start, int length);
	
	public ReturnValue<T> endElement(String uri, String localName, String qName);
	
	public ReturnValue<T> deselect();
	
	public ReturnValue<T> startElement(String uri, String localName, String qName);
	
	public ReturnValue<T> attributes(Attributes attributes);
	
	
	public SelectionEndpoint[] getEndpoints();
	
	public SaxFilter<T> fork();
	
	//TODO: add and implement this function
//	public abstract SaxFilter merge(SaxFilter filter);

}
