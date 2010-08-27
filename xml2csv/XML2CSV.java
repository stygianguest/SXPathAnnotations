package xml2csv;

import java.io.File;
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
public class XML2CSV extends DefaultHandler {
	
	SaxFilter filter;
	
	public XML2CSV() {
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
	
	public XML2CSV(String xpath) {
		Parser parser = new Parser();
		
		filter = ASTtoSaxFilter(parser.parseNode(xpath), false);
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
	
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
//			 //exchange-document (@country, @doc-number, @kind) \
//			      //citation/patcit/document-id (/country, /doc-number, /kind)
//			XML2CSVCommandline driver = new XML2CSVCommandline();
//			
//			SaxFilter country = new SelectionEndpoint();
//			SaxFilter docNumber = new SelectionEndpoint();
//			SaxFilter kind = new SelectionEndpoint();
//			SaxFilter patcitCountry = new SelectionEndpoint();
//			SaxFilter patcitDocNumber = new SelectionEndpoint();
//			SaxFilter patcitKind = new SelectionEndpoint();
//			
//			SaxFilter filter = new DescendantFilter(
//					"exch:exchange-document",
//					new BranchFilter(new SaxFilter[] {
//						new AttributeFilter("country", country),
//						new AttributeFilter("doc-number", docNumber),
//						new AttributeFilter("kind", kind) },
//						new DescendantFilter("exch:citation",
//								new ChildFilter("patcit", 
//									new ChildFilter("document-id",
//										new BranchFilter(new SaxFilter[] {
//												new ChildFilter(
//														"country",
//														patcitCountry),
//												new ChildFilter(
//														"doc-number",
//														patcitDocNumber),
//												new ChildFilter("kind",
//														patcitKind) },
//												new PredicateEndpoint()))))));
//			
//			driver.filter = filter;
//			driver.endpoints = new SaxFilter[] {
//					country, docNumber, kind, 
//					patcitCountry, patcitDocNumber, patcitKind };
			
			// now parse
//			SAXParser saxParser = factory.newSAXParser();			
//			long start = System.currentTimeMillis();
//			saxParser.parse(new File("/tmp/DOCDB-200906-001-AP-0001.xml"), driver);
//			System.out.println("done in " + (System.currentTimeMillis() - start) + "ms");
			
			if (args.length < 2) {
				System.out.println("Usage: xml2csv XPATH FILES");
			}
			
			XML2CSV driver = new XML2CSV(args[0]);
			SAXParser saxParser = factory.newSAXParser();
			
			for (int i = 1; i < args.length; i++) {
				saxParser.parse(new File(args[i]), driver);
			}
					
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}
}
