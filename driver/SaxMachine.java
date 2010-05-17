package driver;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxMachine extends DefaultHandler {

	RegexpathMachine machine = new RegexpathMachine();

	SharingBuffer buffer = new SharingBuffer();

	int currentDepth;

	int skipLevel;

	@Override
	public void startDocument() throws SAXException {
		currentDepth = 0;
		skipLevel = Integer.MAX_VALUE;

		// FIXME: (re)initialize the sharing buffer here?

		if (!machine.startDocument())
			// TODO: stop parsing process
			skipLevel = 0;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentDepth++;

		// skip if we're not interested
		if (skipLevel <= currentDepth)
			return;

		// call the machine for the element and its attributes
		if (!machine.startElement(qName))
			skip();

		for (int i = 0; i < attributes.getLength(); i++) {
			if (!machine.startAttribute(attributes.getLocalName(i)))
				continue;
			if (!machine.attributeValue(attributes.getValue(i)))
				skip();
		}

		if (!machine.endAttributes())
			skip();

		// FIXME: spread this out, as to not record all the attributes?
		buffer.pushStartElement(qName, attributes);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// skip if we're not interested
		if (skipLevel <= currentDepth)
			return;

		// pass on the text
		if (!machine.elementText(new String(ch, start, length)))
			skip();

		buffer.pushCharacters(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		currentDepth--;
		
		// first pass on the data to the buffer
		buffer.pushEndElement(qName);

		// skip if we're not interested, or else reset skip depth
		if (skipLevel <= currentDepth)
			return;
		else
			skipLevel = Integer.MAX_VALUE;

		// should we skip the rest of this level?
		if (!machine.endElement(qName))
			skip();
	}

	@Override
	public void endDocument() throws SAXException {
		machine.endDocument();
	}

	private void skip() {
		skipLevel = currentDepth;
	}

}
