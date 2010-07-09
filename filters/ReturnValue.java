package filters;

public abstract class ReturnValue<T> {
	
	public abstract boolean isNothing();
	public abstract T getValue();
	
	public static final class Nothing<U> extends ReturnValue<U> {
		public boolean isNothing() { return true; }
		public U getValue() { throw new RuntimeException("No value"); }
	}
	
	public static final class Just<U> extends ReturnValue<U> {
		public U value;
		public boolean isNothing() { return false; }
		public U getValue() { return value; } 
	}
	
}
