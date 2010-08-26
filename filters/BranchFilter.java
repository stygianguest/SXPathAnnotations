package filters;

import java.util.Iterator;
import java.util.Vector;

import org.xml.sax.Attributes;

public class BranchFilter<L,R> implements SaxFilter<Pair<L,R>> {
	
	public BranchFilter(SaxFilter<L> left, SaxFilter<R> right) {
        this.left = left;
        this.right = right;
	}
	
	SaxFilter<L> left;
	SaxFilter<R> right;

    Vector<L> leftBuffer;
    Vector<R> rightBuffer;

    private Iterator<Pair<L,R>> crossProduct(Iterator<L> leftNewIt, Iterator<R> rightNewIt) {
        Vector<Pair<L,R>> newPairs = new Vector<Pair<L,R>>();
        while (leftNewIt.hasNext()) {
            L lval = leftNewIt.next();
            leftBuffer.add(lval);
            for (R rval : rightBuffer)
                newPairs.add(new Pair<L,R>(lval, rval));
        }
        while (rightNewIt.hasNext()) {
            R rval = rightNewIt.next();
            rightBuffer.add(rval);
            for (L lval : leftBuffer)
                newPairs.add(new Pair<L,R>(lval, rval));
        }
		
		return newPairs.iterator();
    }

	@Override
	public Iterator<Pair<L,R>> startElement(String uri, String localName, String qName)  {
        return crossProduct(left.startElement(uri, localName, qName),
                right.startElement(uri, localName, qName));
	}
	
	@Override
	public Iterator<Pair<L,R>> attributes(Attributes attributes) {
        return crossProduct(left.attributes(attributes),
                right.attributes(attributes));
	}

	@Override
	public Iterator<Pair<L,R>> characters(char[] ch, int start, int length) {
        return crossProduct(left.characters(ch, start, length),
                right.characters(ch, start, length));
	}

	@Override
	public Iterator<Pair<L,R>> endElement(String uri, String localName, String qName) {
        return crossProduct(left.endElement(uri, localName, qName),
                right.endElement(uri, localName, qName));
	}
	
	@Override
	public Iterator<Pair<L,R>> deselect() {
		Iterator<Pair<L,R>> result = crossProduct(left.deselect(), right.deselect());
        leftBuffer = new Vector<L>();
        rightBuffer = new Vector<R>();
        return result;
	}


	@Override
	public SaxFilter<Pair<L,R>> fork() {
        BranchFilter<L,R> fork = new BranchFilter<L,R>(left.fork(), right.fork());
        fork.rightBuffer = new Vector<R>(this.rightBuffer);
        fork.leftBuffer = new Vector<L>(this.leftBuffer);
		
		return fork;
	}

}