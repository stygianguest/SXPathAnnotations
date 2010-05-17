package driver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


import regexpath.AST;
import regexpath.Action;
import regexpath.Parser;

import junit.framework.TestCase;


public class SaxHandlerTest extends TestCase {
	
	public void testEmptyDoc() throws SAXException {
		// test handler with empty RegeXPath
		SaxHandler handler = new SaxHandler();
		
		handler.startDocument();
		handler.endDocument();
		
		// test handler with a single RegeXPath
		handler = new SaxHandler();
		handler.addRegeXPath((new Parser()).parseNode("/asdf"));
		
		handler.startDocument();
		handler.endDocument();
	}
	
	
	public void testSingleTag() throws SAXException {
		SaxHandler handler = new SaxHandler();
		AST path = (new Parser()).parseNode("/asdf");
		
		TestAction action = new TestAction();
		
		path.setAction(action);
		handler.addRegeXPath(path);
		
		// simulate the parsing of: <asdf></asdf>
		handler.startDocument();
		handler.startElement("", "", "asdf", new AttributesImpl());
		handler.endElement("", "", "asdf");
		
		action.assertDone();
	}
	
	public void testNestedTag() throws SAXException {
		SaxHandler handler = new SaxHandler();
		AST path = (new Parser()).parseNode("/p/asdf");
		
		TestAction action = new TestAction();
		
		path.setAction(action);
		handler.addRegeXPath(path);
		
		// simulate the parsing of: <p><asdf></asdf></p>
		handler.startDocument();
		handler.startElement("", "", "p", new AttributesImpl());
		handler.startElement("", "", "asdf", new AttributesImpl());
		handler.endElement("", "", "asdf");
		handler.endElement("", "", "p");
		
		action.assertDone();
	}
	
	public void testNestedTagAncestor() throws SAXException {
		SaxHandler handler = new SaxHandler();
		AST path = (new Parser()).parseNode("//asdf");
		
		TestAction action = new TestAction();
		
		path.setAction(action);
		handler.addRegeXPath(path);
		
		// simulate the parsing of: <p><asdf></asdf></p>
		handler.startDocument();
		handler.startElement("", "", "p", new AttributesImpl());
		handler.startElement("", "", "asdf", new AttributesImpl());
		handler.endElement("", "", "asdf");
		handler.endElement("", "", "p");
		
		action.assertDone();
	}
	
	
	public void testHanderReuse() throws SAXException {
		SaxHandler handler = new SaxHandler();
		AST path = (new Parser()).parseNode("/asdf");
		
		TestAction action = new TestAction();
		
		path.setAction(action);
		handler.addRegeXPath(path);
		
		// simulate the parsing of: <asdf></asdf>
		handler.startDocument();
		handler.startElement("", "", "asdf", new AttributesImpl());
		handler.endElement("", "", "asdf");
		
		action.assertDone();
		
		action = new TestAction();
		
		path.setAction(action);
		
		// simulate the parsing of: <asdf></asdf>
		handler.startDocument();
		handler.startElement("", "", "asdf", new AttributesImpl());
		handler.endElement("", "", "asdf");
		
		action.assertDone();
	}
	
	public void testHandlerDoubleMatch() {
		//TODO: simulate parsing of matching //jkl//asdf with xml 
		//      <jkl><jkl><asdf>error</asdf></jkl></jkl>
		// should match twice...
	}
	

	class TestAction extends Action {
		boolean startCalled = false;
		boolean endCalled = false;
		
		public void start() { 
			assertFalse(endCalled);
			assertFalse(startCalled);
			startCalled = true;
		}
		
		public void end() {
			assertFalse(endCalled);
			assertTrue(startCalled);
			endCalled = true;
		}
		
		public void assertDone() {
			assertTrue(startCalled);
			assertTrue(endCalled);
		}
	}
}
