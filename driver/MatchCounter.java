package driver;
import regexpath.Action;


public class MatchCounter extends Action {
	
	int count = 0;

	@Override
	public void end() {
		count++;
		super.end();
	}

	@Override
	public void start() {
		
		super.start();
	}
	
	class SubMatcher extends Action {
		
		boolean doesMatch;
		
		SubMatcher[] subMatchers;
		
		@Override
		public void end() {
			
		}
		
		public boolean doesMatch() {
			for (SubMatcher matcher : subMatchers) {
				if (!matcher.doesMatch())
					return false;
			}
			
			return doesMatch;
		}
	}

}
