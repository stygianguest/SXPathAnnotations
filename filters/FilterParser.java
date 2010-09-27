package filters;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * A simple recursive descent parser to create stings.
 * The syntax is based on XPath, but has many tweaks for the branches.
 * 
 * <pre>
 * <f> ::= '/' <e>
 *       | '//' <e>
 *       | '@' <e>
 *       | '=' <e>
 *       | '=~' <e>
 *       | '[' <f> (',' <f>)* ']' <f>
 *       | '(' <f> (',' <f>)* ')'
 *       
 * <e> ::= ...
 * </pre>
 * 
 * @author Gideon Smeding
 *
 */
@SuppressWarnings("unchecked")
public class FilterParser {
	
	public static final char ESCAPECHAR ='%'; 

	public SaxFilter parseFilter(String str) {
		return parseFilter(new StringCharacterIterator(str), false);
	}

	SaxFilter parseFilter(CharacterIterator it, boolean isPred) { 
		SaxFilter filter;
		
		switch (it.current()) {
//        case '(' :
//            it.next();
//            filter = parseFilterList(it, isPred);
//            if (it.current() == ')') {
//            	it.next();
//                return filter;
//            }
//            else
//                throw new RuntimeException("Error while parsing, expecting ')'");
        case '[' :
            it.next();
            filter = parseFilterList(it, true);
            if (it.current() == ']') {
            	it.next();
                return new PredicateFilter(filter, parseFilter(it, isPred));
            }
            else
                throw new RuntimeException("Error while parsing, expecting ']'");
		case '/':
			if (it.next() == '/' ) {
				it.next();
                return new DescendantFilter(parseRegex(it), 
                        parseFilter(it, isPred));
			} else {
                return new ChildFilter(parseRegex(it), 
                        parseFilter(it, isPred));
			}
		case '@':
			it.next();
                return new AttributeFilter(parseRegex(it),
                        parseFilter(it, isPred));
		case '=':
            //TODO: this should become something different, i.e. an actual check
            // on the contents
			if (it.next() == '~' ) {
				it.next();
			} else {
			}
			//break; //for now, we just handle this as any other endpoint
        case ',': // end of sub path
        case ')': // idem
        case ']': // idem
		case CharacterIterator.DONE: // EOF
            if (isPred)
                return new PredicateEndpoint();
            else
                return new SelectionEndpoint();
		default:
			//TODO: throw a proper error message
			throw new RuntimeException("Parsing xpath expression failed");
		}
	}

    SaxFilter parseFilterList(CharacterIterator it, boolean isPred) {
        SaxFilter head = parseFilter(it, isPred);
        
        while (it.current() == ',') {
        	it.next();
            head = new BranchFilter(head, parseFilter(it, isPred));
        }

        return head;
    }

    String parseRegex(CharacterIterator it) {
        //FIXME: do we allow it to return an empty string?
		StringBuilder builder = new StringBuilder();

		while (true) {
			switch (it.current()) {
			case ESCAPECHAR:
				// escaped character, append the next without question
				// provided that it's not the end of the parsed string
				it.next();
				assert it.current() != CharacterIterator.DONE;
				builder.append(it.current());
				break;
			
			// for anything else we immediately stop the loop
			// and return the string we've parsed so far
			case '/':
			case '@':
			case '=':
			case '[':
			case ']':
			case '(':
			case ')':
			case ',':
			case CharacterIterator.DONE: // EOF
				return builder.toString();
				
			default:
				builder.append(it.current());
			}
			
			it.next();
		}
    }
}
