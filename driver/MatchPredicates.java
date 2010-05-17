package driver;

import java.util.Stack;

import regexpath.AST;
import regexpath.Action;

public class MatchPredicates extends Action {
	
	Stack<Integer> noMatchesStack = new Stack<Integer>();
	int currentNoMatches = 0;
	int noEndpoints = 0;
	
	public MatchPredicates() {
		//remainingMatches.push(0);
	}
		

	@Override
	public void attach(AST node) {
		// attach this action to all endpoints and count the
		// total number of endpoints
		
		noEndpoints++;
		
		while (node.getChild() != null) {
			for (AST predicate : node.getPredicates())
				attach(predicate);
		}	
	}

	@Override
	public void start() {
		// we could also do this in end() but this will match more in xml with 
		// parse errors
		currentNoMatches++;
		
		
	}
	
//	@Override
//	public void stop() {
//		
//	}

}
