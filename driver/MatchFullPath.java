package driver;

import java.util.Stack;

import regexpath.AST;
import regexpath.Action;
import regexpath.Callback;

//TODO: implement this: now it is only a copy of MatchInnerElement
//      it should yield the full subtree of all matched elements and attributes
//      also those matched in predicates
public class MatchFullPath extends Action {
	SharingBuffer buffer;
	Stack<SharingBuffer.StartNode> nodes = new Stack<SharingBuffer.StartNode>();
	
	Callback callback;
	
	public MatchFullPath(Callback callback, SharingBuffer buffer) {
		this.buffer = buffer;
		this.callback = callback;
	}
	
	public void attach(AST ast) {
		while (ast.getChild() != null)
			ast = ast.getChild();
		
		ast.setAction(this);
	}
	
	public void start() {
		nodes.push(buffer.startBuffering());
	}
	
	public void end() {
		SharingBuffer.StartNode node = nodes.pop();
		buffer.stopBuffering(node);
		callback.call(buffer.getString(node));
	}
}
