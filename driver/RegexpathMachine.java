package driver;

import java.util.Iterator;
import java.util.Stack;

import regexpath.AST;

//TODO: implement proper predicate handling: failed predicates should cancel 
//      the entire regeXPath, i.e., remove all nodes in it from the stacks
public class RegexpathMachine {
	
	Stack<Stack<AST>> elementMatchers;
	Stack<Stack<AST>> attributeMatchers;
	Stack<Stack<AST>> textMatchers;
	Stack<Stack<AST>> endMatchers;
	AST attributeValueMatcher;
	
	public RegexpathMachine() {
		elementMatchers = new Stack<Stack<AST>>();
		elementMatchers.push(new Stack<AST>());
		
		attributeMatchers = new Stack<Stack<AST>>();
		
		textMatchers = new Stack<Stack<AST>>();
		
		endMatchers = new Stack<Stack<AST>>(); 
	}
	
	public void addRegeXPath(AST regeXPath) {
		elementMatchers.peek().push(regeXPath);
		//TODO: merge paths where possible?
	}
	
	/**
	 * Called at the start of a document. If this function returns false, the 
	 * driver (i.e. the caller) can only call endDocument(). A call to any other
	 * function is considered an error.
	 * @return True iff we are interested in this document.
	 */
	public boolean startDocument() {
		return true;
	}
	
	/**
	 * Called at the start of an element.
	 * @param name 
	 * @return True iff we are interested in the contents of this element.
	 */
	public boolean startElement(String name) {
		Stack<AST> nextStates = new Stack<AST>();
		
		// iterate over the active states
		Iterator<AST> nodeIt = elementMatchers.peek().iterator();
		while ( nodeIt.hasNext() )  {
			AST node = nodeIt.next();
			
			// ancestors can apply again at deeper levels
			if (node.getAxis() == AST.Axis.Descendant) nextStates.push(node);
			
			// continue with the next stack element if no match
			if (!node.getValue().equals(name)) continue;
			
			// FIXME: let start() return a boolean to decide wether or not it is
			//       interested in the end of the attribute?
			if (node.getAction() != null)
				node.getAction().start();
			
			// add a match for the end
			AST closingTag = new AST(AST.Axis.Closing, node.getValue(), 
					new AST[] {}, null);
			closingTag.setAction(node.getAction());
			nextStates.push(closingTag);
			
			// add the next element (if any)
			if (node.getChild() != null) nextStates.push(node.getChild());
			
			// add the predicates
			for (AST n : node.getPredicates()) nextStates.push(n);
		}
		
		pushNewStates(nextStates);
		
		// we're only interested in this element if new states have been added
		return !nextStates.isEmpty();
	}
	
	/**
	 * Called at the start of an attribute.
	 * @param name The name of the attribute.
	 * @return True iff we are interested in the contents of the attribute. 
	 */
	public boolean startAttribute(String name) {
		//FIXME: are we being too granular here? we could just handle attribute
		//       name and value at the same time...
		Stack<AST> nextStates = new Stack<AST>();
		
		// iterate over the active states
		Iterator<AST> nodeIt = attributeMatchers.peek().iterator();
		while ( nodeIt.hasNext() )  {
			AST node = nodeIt.next();
			
			// continue with the next stack element if no match
			if (!node.getValue().equals(name)) continue;
			
			if (node.getAction() != null)
				node.getAction().start();
			
			// an attribute can only match once
			nodeIt.remove();
			
			// add the next element (if any)
			if (node.getChild() != null) nextStates.push(node.getChild());
			
			// add the predicates
			for (AST n : node.getPredicates()) nextStates.push(n);
		}
		
		// FIXME: here the handling of attribute value and text in separate
		//        calls complicates things, because we need the second parameter
		addNewStates(nextStates, true /* parent is an attribute */);
		
		// we're only interested in the contents of this element if:
		// - new states have been added
		// - there are still other child elements that might match
		// - there are no other attributes that could still match
		// FIXME: isn't this double checking?! if no new states are added, there
		//        shouldn't be any attribute matchers nor element matchers...
		return !nextStates.isEmpty() || 
				!elementMatchers.isEmpty() ||
				!attributeMatchers.isEmpty();
	}
	
	
	/**
	 * Called for the value of an attribute.
	 * @param value The value of the atribute
	 * @return True iff we are still interested in the remaining text, 
	 * attributes, or elements of the current element. 
	 */
	public boolean attributeValue(String value) {
		// we can't match if there's not value matcher, but apparently we might
		// still match a next attribute
		if (attributeValueMatcher == null) return true; 
		
		Stack<AST> nextStates = new Stack<AST>();
		AST node = this.attributeValueMatcher;
		
		// do we have a match?
		if (node.getValue().equals(value)) {
			
			if (node.getAction() != null)
				node.getAction().start();
			
			// an attribute can only match once
			this.attributeValueMatcher = null;
			
			// add the next element (if any)
			if (node.getChild() != null) nextStates.push(node.getChild());
			
			// add the predicates
			for (AST n : node.getPredicates()) nextStates.push(n);
		}
		
		// we're only interested in the contents of this element if:
		// - new states have been added
		// - there are still other child elements that might match
		// - there are no other attributes that could still match
		return !nextStates.isEmpty() 
				|| !elementMatchers.isEmpty()
				|| !attributeMatchers.isEmpty();
	}
	
	
	/**
	 * Called at the end of an opening element and all its attributes.
	 * @return True iff we are still interested in the contents of the current
	 * element.
	 */
	public boolean endAttributes() {
		// any attributes still open cannot be matched anymore
		attributeMatchers.pop();
		attributeValueMatcher = null;
		return true;
	}

	/**
	 * Called for the text value (not child elements) of an element. Note that
	 * this function might be called multiple times, for consecutive blocks of
	 * the element text.
	 * 
	 * @param text
	 * @return True iff we are still interested in the remaining text,
	 *         attributes, or elements of the current element.
	 */
	public boolean elementText(String text) {
//		Stack<AST> nextStates = new Stack<AST>();
		
		// iterate over the active states
//		Iterator<AST> nodeIt = textMatchers.peek().iterator();
//		while ( nodeIt.hasNext() )  {
//			AST node = nodeIt.next();
//			
//			// continue with the next stack element if no match
//			if (node.getValue() != text) continue;
//			
//			// an attribute can only match once
//			nodeIt.remove();
//			
//			// add the next element (if any)
//			if (node.getChild() != null) nextStates.push(node.getChild());
//			
//			// add the predicates
//			for (AST n : node.getPredicates()) nextStates.push(n);
//		}
//		
		// FIXME: cannot just push new states here, cause we're not actually 
		//        entering a higher level. for example, we don't want a new 
		//        empty stack on top of the endMatcher stack
//		pushNewStates(nextStates);
		
		
		return true;
	}
	
	/**
	 * Called for the end of an element.
	 * @param name The name of the element.
	 * @return True iff we are still interested in the remaining text or
	 * elements occuring in the element that contains the now closed element.
	 */
	public boolean endElement(String name) {
		// iterate over the active states, note we're popping the matchers
		//FIXME: can the endmatcher stack ever have more than one element in its
		//       top element? i.e. is it really necessary to have a 2D stack?
		Iterator<AST> nodeIt = endMatchers.pop().iterator();
		while ( nodeIt.hasNext() )  {
			AST node = nodeIt.next();
			
			// continue with the next stack element if no match
			if (!node.getValue().equals(name)) continue;

			if (node.getAction() != null)
				node.getAction().end();
		}
		
		// descent a level on the other matchers as well
		elementMatchers.pop();
		textMatchers.pop();
		
		//TODO: return somethign more informed
		return true;
	}
	
	/**
	 * Called at the end of an XML document.
	 * @return True iff we are still interested in the next document
	 * (if there is one).
	 */
	public boolean endDocument() {
		return true;
	}
	
	private void pushNewStates(Stack<AST> newStates) {
		// TODO: define this in terms of addNewStates()
		Stack<AST> newElements = new Stack<AST>();
		Stack<AST> newAttributes = new Stack<AST>();
		Stack<AST> newTexts = new Stack<AST>();
		Stack<AST> newEnds = new Stack<AST>();
		
		for (AST node : newStates) {
			switch (node.getAxis()) {
			case Descendant:
			case Child:
				newElements.push(node);
				break;
			case Attribute:
				newAttributes.push(node);
				break;
			case Text:
			case Match:
				newTexts.push(node);
			case Closing:
				newEnds.push(node);
			default:
				break;
			}
		}
		
		elementMatchers.push(newElements);
		attributeMatchers.push(newAttributes);
		textMatchers.push(newTexts);
		endMatchers.push(newEnds);
	}
	
	private void addNewStates(Stack<AST> newStates, boolean parentIsAttribute) {
		
		for (AST node : newStates) {
			switch (node.getAxis()) {
			case Descendant:
			case Child:
				elementMatchers.peek().push(node);
				break;
			case Attribute:
				attributeMatchers.peek().push(node);
				break;
			case Text:
			case Match:
				if (parentIsAttribute)
					attributeValueMatcher = node;
				else
					textMatchers.peek().push(node);
					
			case Closing:
				endMatchers.peek().push(node);
			default:
				break;
			}
		}
	}
}
