package annotations;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import regexpath.AST;
import regexpath.Parser;
import xml2csv.XML2CSV;
import filters.AttributeFilter;
import filters.BranchFilter;
import filters.ChildFilter;
import filters.DescendantFilter;
import filters.PredicateEndpoint;
import filters.SaxFilter;
import filters.SelectionEndpoint;

@SuppressWarnings("unchecked")
public class SXPathDriver extends DefaultHandler {
	
	Vector<SaxFilter> filters = new Vector<SaxFilter>();
	Vector<Method> callbacks = new Vector<Method>();
	
	public SXPathDriver(String xpath) {

		for (Method m : this.getClass().getMethods()) {
			SaxFilter triggerFilter = null;
			SaxFilter selectFilter = null;
			
			Trigger trigger = m.getAnnotation(Trigger.class);
			triggerFilter = 
				trigger == null ? null : parseFilter(trigger.path(), true);
			
			Annotation[][] parAnnotations = m.getParameterAnnotations();
			int annotationsCnt = 0;
			for (Annotation[] as : m.getParameterAnnotations()) {
				for (Annotation a : as) {
					if (a instanceof Selection) {
						selectFilter = crossJoin(selectFilter, parseFilter(
								((Selection) a).path(), false));
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
			if (triggerFilter != null) {
				filters.add(triggerFilter.addEndpoint(selectFilter));
				
			} else {
				filters.add(selectFilter);
			}
		}

	}

	private SaxFilter crossJoin(SaxFilter selectFilter, SaxFilter parseFilter) {
		// TODO Auto-generated method stub
		return null;
	}

	Parser parser = new Parser();
	
	private SaxFilter parseFilter(String string, boolean isPred) {
		return ASTtoSaxFilter(parser.parseNode(string), isPred); 
	}
	
	//TODO: this is the same code as in XML2CSV, it should be shared
	public static SaxFilter ASTtoSaxFilter(AST ast, boolean isPredicate) {
		SaxFilter childfilter;
		
		if (ast.getChild() == null) {
			if (isPredicate || ast.getChildren().length > 0)
				childfilter = new PredicateEndpoint();
			else
				childfilter = new SelectionEndpoint();
		} else {
			childfilter = ASTtoSaxFilter(ast.getChild(), isPredicate);  
		}
		
		for (AST pred : ast.getPredicates())
			childfilter = new BranchFilter(childfilter,
					ASTtoSaxFilter(pred, true));
		
		for (AST child : ast.getChildren())
			childfilter = new BranchFilter(childfilter,
					ASTtoSaxFilter(child, isPredicate));			
				
		switch (ast.getAxis()) {
		case Child :
			return new ChildFilter(ast.getValue(), childfilter);
		case Descendant :
			return new DescendantFilter(ast.getValue(), childfilter);
		case Attribute :
			return new AttributeFilter(ast.getValue(), childfilter);
		case Text : //TODO: create filter
			return null;
		case Match : //TODO: create filter
			return null;
		default : 
			return null;
		}
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		//FIXME: can we match twice?
		for (SaxFilter filter : filters) {
			printRows(filter.startElement(uri, localName, qName));		
			printRows(filter.attributes(attributes));
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		for (SaxFilter filter : filters) {
			printRows(filter.characters(ch, start, length));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		for (SaxFilter filter : filters) {
			printRows(filter.endElement(uri, localName, qName));
		}
	}
	
	
	void printRows(Iterator it) {
		while (it.hasNext())
			System.out.println(it.next().toString());
	}
	
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			
//			if (args.length < 2) {
//				System.out.println("Usage: xml2csv XPATH FILES");
//				return;
//			}
//			
//			XML2CSV driver = new XML2CSV(args[0]);
//			SAXParser saxParser = factory.newSAXParser();
//			
//			for (int i = 1; i < args.length; i++) {
//				saxParser.parse(new File(args[i]), driver);
//			}
					
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}
}
