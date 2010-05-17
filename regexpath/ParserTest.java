package regexpath;
import java.text.StringCharacterIterator;

import junit.framework.TestCase;

//TODO: test error handling
public class ParserTest extends TestCase {
	
	AST.Axis[] allAxes = new AST.Axis[] 
		{ AST.Axis.Child
		, AST.Axis.Descendant
		, AST.Axis.Attribute
		, AST.Axis.Text
		, AST.Axis.Match
		};
	
	public void testParseEmptyNode() {
		Parser parser = new Parser();
		
		// parse empty string
		assertNull(parser.parseNode(new StringCharacterIterator("")));
	}

	public void testParseSimpleNode() {
		Parser parser = new Parser();

		// parse simple nodes
		for ( AST.Axis axis : new AST.Axis[] {AST.Axis.Child,AST.Axis.Attribute, AST.Axis.Text} ) {
			assertEquals
			( new AST(axis, "asdf", null)
		    , parser.parseNode(axis + "asdf")
		    );
		}
		
		// parse simple two-token nodes
		assertEquals
		( new AST(AST.Axis.Descendant, "asdf", null)
	    , parser.parseNode("//asdf")
	    );
		assertEquals
		( new AST(AST.Axis.Match, "asdf", null)
	    , parser.parseNode("=~asdf")
	    );
	}
	

	public void testParseNestedNode() {
		Parser parser = new Parser();

		// test all possible combinations of axes
		for ( AST.Axis axis1 : allAxes ) {
			for ( AST.Axis axis2 : allAxes ) {
				assertEquals
				( new AST(axis1, "asdf", new AST(axis2, "jkl", null))
			    , parser.parseNode(axis1 + "asdf" + axis2 + "jkl")
			    );
			}
		}
	}
	
	public void testParsePredicateNode() {
		Parser parser = new Parser();

		// test single predicate
		assertEquals
		( new AST( AST.Axis.Child
				 , "asdf"
				 , new AST[] {new AST(AST.Axis.Child, "jkl", null)}
				 , new AST[] {}
				 )
	    , parser.parseNode("/asdf[/jkl]")
	    );
		
		// test two predicates
		assertEquals
		( new AST( AST.Axis.Child
				 , "asdf"
				 , new AST[] { new AST(AST.Axis.Child, "jkl", null)
				             , new AST(AST.Axis.Child, "qwer", null)
				             }
		 		 , new AST[] {}
				 )
	    , parser.parseNode("/asdf[/jkl][/qwer]")
	    );
		
		// test subsequent predicates
		assertEquals
		( new AST( AST.Axis.Child
				 , "asdf"
				 , new AST[] {new AST(AST.Axis.Child, "jkl", new AST[] {}, new AST[] {})}
		         , new AST[] {
					new AST( AST.Axis.Child
						   , "qwer"
						   , new AST[] {new AST(AST.Axis.Child, "uio", new AST[] {}, new AST[] {})}
				 		   , new AST[] {}
						   )}
				 )
	    , parser.parseNode("/asdf[/jkl]/qwer[/uio]")
	    );
	}
	
	public void testParsePredicate() {
		//TODO
	}
	
	public void testParseRegexEmpty() {
		Parser parser = new Parser();
		
		// parse empty string
		assertEquals(parser.parseRegex(new StringCharacterIterator("")), "");
		
		// parse non-regex string (empty result)
		for ( char c : new char[] {'/','@','=','[',']'} ) {
			StringCharacterIterator it = new StringCharacterIterator(Character.toString(c));
			assertEquals("", parser.parseRegex(it), "");
			assertEquals(c, it.current());
		}
	}
	
	public void testParseRegexEscaped() {
		Parser parser = new Parser();
		
		// parsing escaped end of string (illigal)
		//TODO: check for raised error
		parser.parseRegex(new StringCharacterIterator(Character.toString(Parser.ESCAPECHAR)));
		
		// parsing escaped but otherwise meaningful characters
		for ( char c : new char[] {'/','@','=','[',']','\\', Parser.ESCAPECHAR} ) {
			StringCharacterIterator it = 
				new StringCharacterIterator(Character.toString(Parser.ESCAPECHAR) + c);
			assertEquals(Character.toString(c), parser.parseRegex(it));
			assertEquals(StringCharacterIterator.DONE, it.current());
		}
		
		// parsing escaped but meaningless characters
		for ( char c : new char[] {'a','*','\'','\"','4','(','{'} ) {
			StringCharacterIterator it = 
				new StringCharacterIterator(Parser.ESCAPECHAR + Character.toString(c));
			assertEquals(Character.toString(c), parser.parseRegex(it));
			assertEquals(StringCharacterIterator.DONE, it.current());
		}
	}
	
	public void testParseRegex() {
		Parser parser = new Parser();
		
		assertEquals("asdfjkl", parser.parseRegex(new StringCharacterIterator("asdfjkl")));
		assertEquals("QWERTY", parser.parseRegex(new StringCharacterIterator("QWERTY")));
		assertEquals("!#$^&*()_-+", parser.parseRegex(new StringCharacterIterator("!#$^%&*()_-+")));
		assertEquals("{};'|,.<>?\\", parser.parseRegex(new StringCharacterIterator("{};'|,.<>?\\")));
		assertEquals("0123456789", parser.parseRegex(new StringCharacterIterator("0123456789")));
		assertEquals("\t \n", parser.parseRegex(new StringCharacterIterator("\t \n")));
		
	}
}
