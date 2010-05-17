package driver;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.parsers.*;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import regexpath.AST;
import regexpath.Callback;
import regexpath.Parser;

public class SaxHandler extends DefaultHandler {

	Stack<AST> regeXPaths = new Stack<AST>();
	Stack<Stack<AST>> activeNodeStack;
	SharingBuffer buffer = new SharingBuffer();
	
	public void addRegeXPath(AST regeXPath) {
		regeXPaths.push(regeXPath);
		//TODO: merge paths where possible?
	}
	
	public void startDocument() throws SAXException {
		//TODO: the sharingbuffer should probably be initialized here
		//      but that makes it difficult to put it into CallbackActions
		//buffer = new SharingBuffer();
		
		activeNodeStack = new Stack<Stack<AST>>();
		activeNodeStack.push(regeXPaths);
	}
	
	public void endDocument() throws SAXException {
		
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		Stack<AST> nextStates = new Stack<AST>();
		
		// iterate over the active states
		Iterator<AST> nodeIt = activeNodeStack.peek().iterator();
		while ( nodeIt.hasNext() )  {
			AST node = nodeIt.next();
			
			switch (node.getAxis()) {			
			case Match:
			case Text:
				// skip matches on text, since it can't match here
				continue;
				
			case Attribute:
				// attribute nodes can only apply once
				nodeIt.remove();
				
				// if we don't have a match, continue
				if (attributes.getIndex(node.getValue()) == -1)
					continue;
				
				//TODO: if we have a child or predicates, match the attribute contents				
				break;

			case Descendant:
				// ancestors can apply again at deeper levels
				nextStates.push(node);
			case Child:
				// continue the loop if we don't have a match on the tagname
				if (!node.getValue().equals(qName))
					continue;
				
				// add a closing tag
				AST closingTag = new AST(AST.Axis.Closing, node.getValue(), 
						new AST[] {}, null);
				closingTag.setAction(node.getAction());
				nextStates.push(closingTag);
				
				break;
				
			case Closing:
				// doesn't apply here
				continue;
			}
			// the fact that we're still here, means that we have a match
			
			// if applicable, add the child
			if ( node.getChild() != null )
				nextStates.push(node.getChild());
			
			// add all predicates
			for ( AST pred : node.getPredicates() )
				nextStates.push(pred);

			// apply the getAction()
			if ( node.getAction() != null )
				node.getAction().start();
		}
	
		// finally push the new states onto the stack
		activeNodeStack.push(nextStates);
		
		// and pass on the element's data to the buffer
		buffer.pushStartElement(qName, attributes);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		// first pass on the data to the buffer
		buffer.pushEndElement(qName);
		
		// iterate over the active states, note that we also pop the stack
		Iterator<AST> nodeIt = activeNodeStack.pop().iterator();
		while ( nodeIt.hasNext() )  {
			AST node = nodeIt.next();
			
			switch (node.getAxis()) {			
			case Match:
			case Text:
				// text matches do nothing here
				continue;
			case Attribute:
				// we shouldn't be here, attribute nodes should be popped in startElement()
				assert false; 
				continue;

			case Descendant:
			case Child:
				// these do not apply here
				continue;
			
			case Closing:
				// continue the loop if we don't have a match on the tagname
				if (!node.getValue().equals(qName))
					continue;
				
				break;
			}
			// the fact that we're still here, means that we have a match
			
			// apply the getAction()
			if ( node.getAction() != null )
				node.getAction().end();
		}
	}
		
	@Override
	public void characters(char[] chars, int start, int length) throws SAXException {
		 buffer.pushCharacters(chars, start, length);
	}

	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// build handler
			SaxHandler handler = new SaxHandler();
			AST path = (new Parser()).parseNode("//jkl//asdf");
			Callback printer = new Callback() {
				public void call(String str) {
					System.out.println(str);
				}
			};
			MatchInnerElement action = new MatchInnerElement(printer, handler.buffer);
			action.attach(path); //path.getChild().setAction(getAction());
			handler.addRegeXPath(path);
			
			// now parse
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File("/tmp/test.xml"), handler);
					
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}

}
