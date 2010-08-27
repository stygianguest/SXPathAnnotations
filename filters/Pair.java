package filters;

public class Pair<L,R> {

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L left;
    public R right;
    
	@Override
	public String toString() {
		return left.toString() + "," + right.toString();
	}
}
