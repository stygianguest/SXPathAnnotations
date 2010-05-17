package driver;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import regexpath.AST;
import regexpath.Callback;
import regexpath.Parser;

public class SaxMachineTest {
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// build handler
			SaxMachine handler = new SaxMachine();
			AST path = (new Parser()).parseNode("/asdf//b@attr=none");
			//AST path = (new Parser()).parseNode("/asdf//b");
			Callback printer = new Callback() {
				public void call(String str) {
					System.out.println("match: " + str);
				}
			};
			MatchInnerElement action = new MatchInnerElement(printer, handler.buffer);
			action.attach(path);
			handler.machine.addRegeXPath(path);
			
			// now parse
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File("/tmp/test.xml"), handler);
					
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}
}
