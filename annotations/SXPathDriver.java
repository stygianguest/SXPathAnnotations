package annotations;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import filters.BranchFilter;
import filters.FilterParser;
import filters.Pair;
import filters.SaxFilter;

@SuppressWarnings("unchecked")
public class SXPathDriver extends DefaultHandler {
	
	Vector<SaxFilter> filters = new Vector<SaxFilter>();
	Vector<Method> callbacks = new Vector<Method>();
	
	public SXPathDriver() {
		FilterParser parser = new FilterParser();
		
		for (Method m : this.getClass().getMethods()) {
			Trigger trigger = m.getAnnotation(Trigger.class);
			if (trigger == null) continue; // skip trigger-less methods
			SaxFilter triggerFilter = parser.parseFilter(trigger.value());
			
			Annotation[][] parAnnotations = m.getParameterAnnotations();
			int annotationsCnt = 0;
			SaxFilter selectFilter = null;
			for (Annotation[] as : m.getParameterAnnotations()) {
				for (Annotation a : as) {
					if (a instanceof Select) {
						if (selectFilter == null)
							selectFilter = parser.parseFilter(((Select) a).value());
						else
							selectFilter = new BranchFilter(selectFilter, 
									parser.parseFilter(((Select) a).value()));
						annotationsCnt++;
						break;
					}
				}
			}
			
			if (annotationsCnt < parAnnotations.length) {
				throw new RuntimeException("Missing parameter Selection " +
						"annotations for method " + m);
			}
			
			// we have a filter, add it and the corresponding method
			callbacks.add(m);
			filters.add(triggerFilter.append(selectFilter));
		}

	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		//FIXME: can we match twice?
		for (int i = 0; i < filters.size(); i++) {
			invokeCallback(i, filters.get(i).startElement(uri, localName, qName));
			invokeCallback(i, filters.get(i).attributes(attributes));
//			printRows(filter.startElement(uri, localName, qName));		
//			printRows(filter.attributes(attributes));
		}
	}
	
	private void invokeCallback(int i, Iterator it) {
		while (it.hasNext()) {
			try {
				callbacks.get(i).invoke(this, flatten(it.next()));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static Object[] flatten(Object obj) {
		Vector<Object> list = new Vector<Object>();
		
		//TODO: for now a recursive implementation, but this could easily become a bottleneck
		if (obj instanceof Pair<?, ?>) {
			for (Object o : flatten(((Pair) obj).left)) list.add(o);
			for (Object o : flatten(((Pair) obj).right)) list.add(o);
		} else {
			list.add(obj);
		}
		
		return list.toArray();
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		for (int i = 0; i < filters.size(); i++) {
			invokeCallback(i, filters.get(i).characters(ch, start, length));
//			printRows(filter.characters(ch, start, length));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		for (int i = 0; i < filters.size(); i++) {
			invokeCallback(i, filters.get(i).endElement(uri, localName, qName));
//			printRows(filter.endElement(uri, localName, qName));
		}
	}
	
	public void parseFile(String filename) throws SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse(new File(filename), this);
		} catch (ParserConfigurationException e) {
			// this shouldn't happen (tm) and i don't want to clutter
			// the function signature with the exception
			e.printStackTrace();
		}
	}
}
