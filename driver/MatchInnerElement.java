package driver;

import java.util.Stack;



import regexpath.AST;
import regexpath.Action;
import regexpath.Callback;

//TODO: we could have an option to match only text, not sub-elements..
public class MatchInnerElement extends Action {
	
	SharingBuffer buffer;
	Stack<SharingBuffer.StartNode> nodes = new Stack<SharingBuffer.StartNode>();
	
	Callback callback;
	
	public MatchInnerElement(Callback callback, SharingBuffer buffer) {
		this.buffer = buffer;
		this.callback = callback;
	}
	
	@Override
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
