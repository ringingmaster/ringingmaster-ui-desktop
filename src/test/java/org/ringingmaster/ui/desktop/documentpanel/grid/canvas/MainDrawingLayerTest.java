package org.ringingmaster.ui.desktop.documentpanel.grid.canvas;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Lake
 */
public class MainDrawingLayerTest {

	@Test
	public void verticalOffsetFollowsWiggle() {
		// NOTE: a pitch 2 wiggle will have the coordinates  0,0 2,2 4,0 6,2

		// Simple alignment on the pixel pitch
		assertEquals(0.0, MainDrawingLayer.getVerticalWiggleOffset(-4.0, 2), 0.000001);
		assertEquals(2.0, MainDrawingLayer.getVerticalWiggleOffset(-2.0, 2), 0.000001);
		assertEquals(0.0, MainDrawingLayer.getVerticalWiggleOffset( 0.0, 2), 0.000001);
		assertEquals(2.0, MainDrawingLayer.getVerticalWiggleOffset( 2.0, 2), 0.000001);
		assertEquals(0.0, MainDrawingLayer.getVerticalWiggleOffset( 4.0, 2), 0.000001);
		assertEquals(2.0, MainDrawingLayer.getVerticalWiggleOffset( 6.0, 2), 0.000001);

		assertEquals(0.0, MainDrawingLayer.getVerticalWiggleOffset(-6.0, 3), 0.000001);
		assertEquals(3.0, MainDrawingLayer.getVerticalWiggleOffset(-3.0, 3), 0.000001);
		assertEquals(0.0, MainDrawingLayer.getVerticalWiggleOffset( 0.0, 3), 0.000001);
		assertEquals(3.0, MainDrawingLayer.getVerticalWiggleOffset( 3.0, 3), 0.000001);
		assertEquals(0.0, MainDrawingLayer.getVerticalWiggleOffset( 6.0, 3), 0.000001);
		assertEquals(3.0, MainDrawingLayer.getVerticalWiggleOffset( 9.0, 3), 0.000001);

		assertEquals(0.0, MainDrawingLayer.getVerticalWiggleOffset(0.0, 2), 0.000001);
		assertEquals(0.5, MainDrawingLayer.getVerticalWiggleOffset(0.5, 2), 0.000001);
		assertEquals(1.0, MainDrawingLayer.getVerticalWiggleOffset(1.0, 2), 0.000001);
		assertEquals(1.5, MainDrawingLayer.getVerticalWiggleOffset(1.5, 2), 0.000001);
		assertEquals(2.0, MainDrawingLayer.getVerticalWiggleOffset(2.0, 2), 0.000001);
		assertEquals(1.5, MainDrawingLayer.getVerticalWiggleOffset(2.5, 2), 0.000001);
		assertEquals(1.0, MainDrawingLayer.getVerticalWiggleOffset(3.0, 2), 0.000001);
		assertEquals(0.5, MainDrawingLayer.getVerticalWiggleOffset(3.5, 2), 0.000001);
		assertEquals(0.0, MainDrawingLayer.getVerticalWiggleOffset(4.0, 2), 0.000001);

	}
	
	@Test
	public void canAlignToNextPixelBoundary() {
		assertEquals(-2, MainDrawingLayer.alignToNextPixelPitch(-2.1, 2));
		assertEquals( 0, MainDrawingLayer.alignToNextPixelPitch(-2.0, 2));
		assertEquals( 0, MainDrawingLayer.alignToNextPixelPitch(-1.9, 2));
		assertEquals( 0, MainDrawingLayer.alignToNextPixelPitch(-0.1, 2));
		assertEquals( 2, MainDrawingLayer.alignToNextPixelPitch(0.0, 2));
		assertEquals( 2, MainDrawingLayer.alignToNextPixelPitch(0.1, 2));
		assertEquals( 2, MainDrawingLayer.alignToNextPixelPitch(1.9, 2));
		assertEquals( 4, MainDrawingLayer.alignToNextPixelPitch(2.0, 2));
		assertEquals( 4, MainDrawingLayer.alignToNextPixelPitch(2.1, 2));

		assertEquals(-3, MainDrawingLayer.alignToNextPixelPitch(-3.1, 3));
		assertEquals( 0, MainDrawingLayer.alignToNextPixelPitch(-3.0, 3));
		assertEquals( 0, MainDrawingLayer.alignToNextPixelPitch(-2.9, 3));
		assertEquals( 0, MainDrawingLayer.alignToNextPixelPitch(-0.1, 3));
		assertEquals( 3, MainDrawingLayer.alignToNextPixelPitch(0.0, 3));
		assertEquals( 3, MainDrawingLayer.alignToNextPixelPitch(0.1, 3));
		assertEquals( 3, MainDrawingLayer.alignToNextPixelPitch(2.9, 3));
		assertEquals( 6, MainDrawingLayer.alignToNextPixelPitch(3.0, 3));
		assertEquals( 6, MainDrawingLayer.alignToNextPixelPitch(3.1, 3));
	}
}