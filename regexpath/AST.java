package regexpath;


import java.util.Arrays;

import filters.*;

//TODO: add negative predicates!
public class AST {
	
	Axis axis;
	String value;
	AST[] predicates;
	AST[] children;
	
	AST child;
	
	public AST[] getBranches() {
		return children;
	}

	public void setBranches(AST[] branches) {
		this.children = branches;
	}

	public Axis getAxis() {
		return axis;
	}

	public AST getChild() {
		return child;
	}

	public AST[] getPredicates() {
		return predicates;
	}

	public String getValue() {
		return value;
	}
	
	public SaxFilter toSaxFilter(boolean isPredicate) {
		SaxFilter childfilter;
		
		if (child == null) {
			if (isPredicate || children.length > 0)
				childfilter = new PredicateEndpoint();
			else
				childfilter = new SelectionEndpoint();
		} else {
			childfilter = child.toSaxFilter(isPredicate);  
		}
		
		if (predicates.length > 0) {
			SaxFilter[] predFilters = new SaxFilter[predicates.length];
			
			for (int i = 0; i < predicates.length; i++)
				predFilters[i] = predicates[i].toSaxFilter(true);
			
			childfilter = new BranchFilter(predFilters, childfilter);
		}
		
		if (children.length > 0) {
			SaxFilter[] branchFilters = new SaxFilter[children.length];
			
			for (int i = 0; i < children.length; i++)
				branchFilters[i] = children[i].toSaxFilter(isPredicate);
			
			childfilter = new BranchFilter(branchFilters, childfilter);
		}
				
		switch (axis) {
		case Child :
			return new ChildFilter(value, childfilter);
		case Descendant :
			return new DescendantFilter(value, childfilter);
		case Attribute :
			return new AttributeFilter(value, childfilter);
		case Text : //TODO: create filter
			return null;
		case Match : //TODO: create filter
			return null;
		default : 
			return null;
		}
	}

	public AST(Axis axis, String value, AST child) {
		setAST(axis, value, new AST[] {}, new AST[] {}, child);
	}
	
	public AST(Axis axis, String value, AST[] predicates, AST[] branches, AST child) {
		setAST(axis, value, predicates, branches, child);
	}
	
	public AST(Axis axis, String value, AST[] predicates, AST child) {
		setAST(axis, value, predicates, new AST[] {}, child);
	} 

	public void setAST(Axis axis, String value, AST[] predicates, AST[] branches, AST child) {
		assert value != null;
		assert axis != null;
		assert predicates != null;
		assert branches != null;
		
		this.axis = axis;
		this.value = value;
		this.predicates = predicates;
		this.children = branches;
		this.child = child;
	}
	
	public boolean isValidNode() {
		//TODO: nodes, or attribute nodes could have contradictionary 
		//      text predicates, eg /asdf[=asdf][=jkl] can never match
		//      we could conside these 'invalid' as well
		switch (axis) {
		case Child:
		case Descendant:
			if ( value.isEmpty() )
				return false;
			
			for (AST node : predicates) {
				if (  !node.isValidNode() 
				   || !node.isValidAttrNode() 
				   || !isValidContentNode()
				   ) return false;
			}
			
			if ( child != null )
				return child.isValidNode();
			
			return true;
		}
		
		return false;
	}
	
	public boolean isValidAttrNode() {
		switch (axis) {
		case Attribute:
			if ( value.isEmpty() )
				return false;
			
			for (AST node : predicates) {
				if (!node.isValidContentNode())
					return false;
			}
			
			if ( child != null )
				return child.isValidContentNode();
			
			return true;
		}
		
		return false;		
	}
	
	public boolean isValidContentNode() {
		//TODO: check validity of regex?
		switch (axis) {
		case Match:
			if ( value.isEmpty() )
				return false;
		case Text:
			return child == null && predicates.length == 0;
		}
		
		return false;
	}
	
	public enum Axis 
	{ 
		Child, Descendant, Attribute, Text, Match;
	
		@Override
		public String toString() {
			switch (this) {
			case Child:	return "/";
			case Descendant: return "//";
			case Attribute: return "@";
			case Text: return "=";
			case Match:	return "=~";
			}
			
			return super.toString();
		}
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		AST currentNode = this;
			
		while ( currentNode != null ) {
			builder.append(currentNode.axis);
			// TODO: insert escapes for 'active' characters in node values
			builder.append(currentNode.value);
			
			//TODO: the following kindof defeats the purpose of using the stringbuilder
			//      because we're using recursion
			for ( AST node : currentNode.predicates ) {
				builder.append('[');
				builder.append(node.toString());
				builder.append(']');
			}
			
			currentNode = currentNode.child;
		}
		
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((axis == null) ? 0 : axis.hashCode());
		result = PRIME * result + ((child == null) ? 0 : child.hashCode());
		result = PRIME * result + Arrays.hashCode(predicates);
		result = PRIME * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AST other = (AST) obj;
		if (axis == null) {
			if (other.axis != null)
				return false;
		} else if (!axis.equals(other.axis))
			return false;
		if (child == null) {
			if (other.child != null)
				return false;
		} else if (!child.equals(other.child))
			return false;
		if (!Arrays.equals(predicates, other.predicates))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
