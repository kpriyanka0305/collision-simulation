import static org.junit.jupiter.api.Assertions.*;
import math.geom2d.line.LineSegment2D;

import org.junit.jupiter.api.Test;

class UtilTest {

	@Test
	void distanceBetweenLineSegmentAndPoint() {
		LineSegment2D line = new LineSegment2D(0,0, 10,0);
		assertEquals(5.0, line.distance(0,5), 0.001);
		assertEquals(5.0, line.distance(5,5), 0.001);
		assertEquals(5.0, line.distance(10,5), 0.001);

		assertEquals(Math.sqrt(50), line.distance(15,5), 0.001);
		assertEquals(6.0, line.distance(16,0), 0.001);
	}
}
