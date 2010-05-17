package regexpath;
import junit.framework.TestCase;


public class ASTTest extends TestCase {
	
	public void testIsValidContentNode() {
		Parser parser = new Parser();
		
		assertTrue(parser.parseNode("=asdf").isValidContentNode());
		assertTrue(parser.parseNode("=~asdf").isValidContentNode());
		assertTrue(parser.parseNode("=").isValidContentNode());
		
		assertFalse(parser.parseNode("=~").isValidContentNode());
		assertFalse(parser.parseNode("/asdf").isValidContentNode());
		assertFalse(parser.parseNode("//asdf").isValidContentNode());
		assertFalse(parser.parseNode("@asdf").isValidContentNode());
	}
	
	public void testIsValidAttrNode() {
		Parser parser = new Parser();
		
		assertTrue(parser.parseNode("@asdf").isValidAttrNode());
		assertTrue(parser.parseNode("@asdf=jkl").isValidAttrNode());
		assertTrue(parser.parseNode("@asdf=~jkl").isValidAttrNode());
		assertTrue(parser.parseNode("@asdf[=jkl]").isValidAttrNode());
		assertTrue(parser.parseNode("@asdf[=~jkl]").isValidAttrNode());
		assertTrue(parser.parseNode("@asdf[=~jkl]=jkl").isValidAttrNode());
		
		assertFalse(parser.parseNode("@").isValidAttrNode());
		assertFalse(parser.parseNode("=asdf").isValidAttrNode());
		assertFalse(parser.parseNode("=~asdf").isValidAttrNode());
		assertFalse(parser.parseNode("/asdf").isValidAttrNode());
		assertFalse(parser.parseNode("//asdf").isValidAttrNode());
		assertFalse(parser.parseNode("@asdf/jkl").isValidAttrNode());
		assertFalse(parser.parseNode("@asdf//jkl").isValidAttrNode());
	}
	
	public void testIsValidNode() {
		Parser parser = new Parser();
		
		assertTrue(parser.parseNode("/asdf").isValidNode());
		assertTrue(parser.parseNode("//asdf").isValidNode());
		assertTrue(parser.parseNode("/*").isValidNode());
		
		assertFalse(parser.parseNode("/").isValidNode());
		assertFalse(parser.parseNode("@asdf").isValidNode());
		assertFalse(parser.parseNode("=asdf").isValidNode());
		assertFalse(parser.parseNode("=~asdf").isValidNode());
	}
}
