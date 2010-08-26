package filters;

import org.xml.sax.Attributes;
import java.util.Iterator;

//TODO: add a select() as the counterparty of deselect()
//TODO: abstract from the returned boolean to allow endpoint specific return values
public interface SaxFilter<T> {

	public Iterator<T> characters(char[] ch, int start, int length);
	
	public Iterator<T> endElement(String uri, String localName, String qName);
	
	public Iterator<T> deselect();
	
	public Iterator<T> startElement(String uri, String localName, String qName);
	
	public Iterator<T> attributes(Attributes attributes);
	
	public SaxFilter<T> fork();
	
	//TODO: add and implement this function?
//	public abstract SaxFilter merge(SaxFilter filter);

}
