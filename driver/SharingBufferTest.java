package driver;
import junit.framework.TestCase;

//TODO: write tests with unclosed tickets
//TODO: write tests with 'foreign' tickets
public class SharingBufferTest extends TestCase {
	
	SharingBuffer buffer = new SharingBuffer();
	SharingBuffer.StartNode ticket1, ticket2;
	
	public void testNoDataGetSimple() {
		// test a simple non-interleaved get
		ticket1 = buffer.startBuffering();
		buffer.stopBuffering(ticket1);
		assertEquals("", buffer.getString(ticket1));
	}
	
	public void testNoDataGetNested() {
		// test get with simple nesting
		buffer = new SharingBuffer();
		ticket1 = buffer.startBuffering();
		ticket2 = buffer.startBuffering();
		buffer.stopBuffering(ticket2);
		buffer.stopBuffering(ticket1);
		assertEquals("", buffer.getString(ticket1));
		assertEquals("", buffer.getString(ticket2));
	}
	
	public void testNoDataGetOverlap() {
		// test get with simple overlapping
		buffer = new SharingBuffer();
		ticket1 = buffer.startBuffering();
		ticket2 = buffer.startBuffering();
		buffer.stopBuffering(ticket1);
		buffer.stopBuffering(ticket2);
		assertEquals("", buffer.getString(ticket1));
		assertEquals("", buffer.getString(ticket2));
	}

	public void testNoDataGetSequence() {
		// test get with simple sequence
		buffer = new SharingBuffer();
		ticket1 = buffer.startBuffering();
		buffer.stopBuffering(ticket1);
		ticket2 = buffer.startBuffering();
		buffer.stopBuffering(ticket2);
		assertEquals("", buffer.getString(ticket1));
		assertEquals("", buffer.getString(ticket2));
	}

	public void testDataGetSimple() {
		// test a simple non-interleaved get
		ticket1 = buffer.startBuffering();
		buffer.pushString("asdf");
		buffer.stopBuffering(ticket1);
		assertEquals("asdf", buffer.getString(ticket1));
	}

	public void testDataGetNested() {
		// test get with simple nesting
		buffer = new SharingBuffer();
		ticket1 = buffer.startBuffering();
		buffer.pushString("a");
		ticket2 = buffer.startBuffering();
		buffer.pushString("sd");
		buffer.stopBuffering(ticket2);
		buffer.pushString("f");
		buffer.stopBuffering(ticket1);
		assertEquals("asdf", buffer.getString(ticket1));
		assertEquals("sd", buffer.getString(ticket2));
	}

	public void testDataGetOverlap() {
		// test get with simple overlapping
		buffer = new SharingBuffer();
		ticket1 = buffer.startBuffering();
		ticket2 = buffer.startBuffering();
		buffer.stopBuffering(ticket1);
		buffer.stopBuffering(ticket2);
		assertEquals("", buffer.getString(ticket1));
		assertEquals("", buffer.getString(ticket2));
	}

	public void testDataGetSequence() {
		// test get with simple sequence
		buffer = new SharingBuffer();
		ticket1 = buffer.startBuffering();
		buffer.stopBuffering(ticket1);
		ticket2 = buffer.startBuffering();
		buffer.stopBuffering(ticket2);
		assertEquals("", buffer.getString(ticket1));
		assertEquals("", buffer.getString(ticket2));
	}
}
