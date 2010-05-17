package driver;
import org.w3c.dom.*;
import org.xml.sax.Attributes;


public class SharingBuffer {
	
	private StringBuilder tailBuffer = new StringBuilder();
	private int noListeners = 0;
		
	private Node rootNode = new Node();
	private Node lastNode = rootNode;
	
	public StartNode startBuffering() {
		noListeners++;
		
		StartNode newNode = new StartNode();
		addNode(newNode);
		
		return newNode;
	}
	
	public void stopBuffering(StartNode startNode) {
		assert startNode != null;
		
		startNode.endNode = new EndNode();
		addNode(startNode.endNode);
		
		noListeners--;
	}
	
	private void addNode(Node node) {
		assert node != null;
		assert lastNode.child == null;
		assert node.parent == null;
		assert node.child == null;
		
		// first add a characternode if there's charactes to save
		if ( tailBuffer.length() > 0 ) {
			CharacterNode charNode = new CharacterNode(tailBuffer.toString());
			tailBuffer = new StringBuilder();
			addNode(charNode);
		}
		
		// now append the node
		node.parent = lastNode;
		lastNode.child = node;
		lastNode = node;
	}
	
	public String getString(StartNode node) {
		// TODO: rename to popString, which is more accurate, iff, it does a free
		assert node != null;
		assert node.endNode != null;
		assert node.child != null;
		
		StringBuilder builder = new StringBuilder();
		
		Node currentNode = node;
		do {
			currentNode = currentNode.child;
			builder.append(currentNode);
		} while ( currentNode != node.endNode );
		
		free(node); //TODO: remove this?!
		
		return builder.toString();
	}
	
	public Element getElement(StartNode node) {
		return null;
	}
	
	public Attr getAttribute(StartNode node) {
		return null;
	}
	
	public Text getText(StartNode node) {
		return null;
	}
	
	public void free(StartNode node) {
		assert node != null;
		
		// decrement noListeners if there is no corresponding stopnode
		if ( node.endNode == null ) {
			noListeners--;
			
			// if needed empty the buffer
			if ( noListeners == 0 )
				tailBuffer = new StringBuilder();
		}
		
		// remove the node, this should nullify all its contents
		// to prevent references to nodes in usercode to prevent
		// the garbage collector from removing the buffered string
		node.remove();
		
		// Clean up the loose endnodes if it's not still needed.
		// To determine whether it's still needed we do a sweep over all Nodes
		// skipping from each startNode to its endNode. Any endNodes between
		// a begin and endNode should not be removed, since they can still be needed.
		// So we only remove the endnodes we encounter between start-end jumps
		// Note:   It is quite possible we remove nothing, in a sweep because 
		//         the buffered string in the endnode is still needed.
		//         It is also possible that a sweep will remove multiple endnodes
		//         whose corresponding startnodes had already been removed.
		Node currentNode = rootNode.child;
		// FIXME: the next assertions gives some trouble, but it should be 
		//        re-enabled or corrected
		assert currentNode == null || currentNode instanceof StartNode;
		while ( currentNode != null ) {
			if ( currentNode instanceof StartNode ) {
				StartNode currentStartNode = (StartNode) currentNode;
				// if we encounter an unclosed startnode, nothing after it can be removed
				if ( currentStartNode.endNode == null )
					break;
				
				// move on with the child of our current node's endNode
				currentNode = currentStartNode.endNode.child;
			} else { // has to be a loose endNode or some datanode so remove it
				Node previousNode = currentNode;
				currentNode = currentNode.child;
				previousNode.remove();
			}
		}
	}
	
	protected void pushString(String string) {
		if ( noListeners > 0 )
			tailBuffer.append(string);
	}
	
	protected void pushCharacters(char[] chars, int start, int length) {
		if ( noListeners > 0 )
			tailBuffer.append(chars, start, length);
	}
	
	protected void pushStartElement(String tagname, Attributes attributes) {
		if ( noListeners <= 0 )
			return;
		
		addNode(new StartElementNode(tagname, attributes));
	}
	
	protected void pushEndElement(String tagname) {
		if ( noListeners <= 0 )
			return;
		
		addNode(new EndElementNode(tagname));
	}
	
	private class Node {
		Node child = null;
		Node parent = null;
		
		void remove() {
			if ( this == lastNode ) {
				lastNode = this.parent;
			}
			
			parent.child = child;
			
			if ( child != null )
				child.parent = parent;
			
			child = null;
			parent = null;
		}
		
		public String toString() {
			return "";
		}
	}
	
	public final class StartNode extends Node {
		EndNode endNode;
		
		void remove() {
			endNode = null;
			super.remove();
		}
	}
	
	private class EndNode extends Node {}
	
	private class CharacterNode extends Node {
		String data;
		
		CharacterNode (String data) {
			this.data = data;
		}
		
		public String toString() {
			return data;
		}
		
		void remove() {
			data = null;
			super.remove();
		}
	}
	
	private class StartElementNode extends Node {
		String tagname;
		Attributes attributes;
		
		StartElementNode(String tagname, Attributes attributes) {
			this.tagname = tagname;
			this.attributes = attributes;
		}
		
		public String toString() {
			String str = "<" + tagname;
			//TODO: print attributes
			//str += attributes.toString();
			str += ">";
			return str;
		}
		
		void remove() {
			super.remove();
			tagname = null;
			attributes = null;
		}
	}
	
	private class EndElementNode extends Node {
		String tagname;
		
		EndElementNode(String tagname) {
			this.tagname = tagname;
		}
		
		public String toString() {
			return "</" + tagname + ">";
		}
		
		void remove() {
			super.remove();
			tagname = null;
		}
	}
}
