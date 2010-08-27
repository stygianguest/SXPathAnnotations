package annotations;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import regexpath.AST;
import regexpath.Parser;
import filters.AttributeFilter;
import filters.BranchFilter;
import filters.ChildFilter;
import filters.DescendantFilter;
import filters.PredicateEndpoint;
import filters.SaxFilter;
import filters.SelectionEndpoint;

@SuppressWarnings("unchecked")
public class SXPathDriver extends DefaultHandler {
	

	SaxFilter filter;
	SaxFilter[] endpoints;
	

	public SXPathDriver(Object obj) {
		Class<? extends Object> cl = obj.getClass();
		for (Method m : cl.getDeclaredMethods()) {
			Trigger trigger = m.getAnnotation(Trigger.class);
			if (trigger == null) continue;
			
			SaxFilter filter = parseSaxFilter(trigger.path(), true);
			
			Annotation[][] parAnnots = m.getParameterAnnotations();
			for (Annotation[] annots : parAnnots) {
				String selection = null;
				for (Annotation annot : annots) {
					if (annot instanceof Selection) {
						selection = ((Selection) annot).path();
						break;
					}
				}
				if (selection == null)
					throw new RuntimeException("No selection for xpath triggered method parameter");

//				filter = filter.merge(parseSaxFilter(selection, false));
			}
		}
	}
	
	private static SaxFilter parseSaxFilter(String path, boolean isPredicate) { 
		Parser parser = new Parser();
		return ASTtoSaxFilter(parser.parseNode(path), true);
	}
	
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
		
		if (ast.getPredicates().length > 0) {
			for (AST pred : ast.getPredicates())
				childfilter = new BranchFilter(childfilter,
						ASTtoSaxFilter(pred, true));
		}
		
		if (ast.getChildren().length > 0) {
			for (AST child : ast.getChildren())
				childfilter = new BranchFilter(childfilter,
						ASTtoSaxFilter(child, isPredicate));			
		}
				
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
		printRows(filter.startElement(uri, localName, qName));		
		printRows(filter.attributes(attributes));
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		printRows(filter.characters(ch, start, length));
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		printRows(filter.endElement(uri, localName, qName));
	}
	
	void printRows(Iterator it) {
		while (it.hasNext())
			System.out.println(it.next().toString());
	}
}
	
//	public static void main(String[] args) {
//		SAXParserFactory factory = SAXParserFactory.newInstance();
//		try {			
//			if (args.length < 2) {
//				System.out.println("Usage: xml2csv XPATH FILES");
//			}
//			
//			XML2CSV driver = new XML2CSV(args[0]);
//			SAXParser saxParser = factory.newSAXParser();
//			
//			for (int i = 1; i < args.length; i++) {
//				saxParser.parse(new File(args[i]), driver);
//			}
//					
//		} catch (Throwable err) {
//			err.printStackTrace();
//		}
//	}
//}
