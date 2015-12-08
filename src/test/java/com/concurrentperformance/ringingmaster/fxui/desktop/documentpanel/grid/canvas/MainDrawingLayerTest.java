package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Lake
 */
public class MainDrawingLayerTest {
	
	@Test
	public void canAlignToPixelBoundary() {
		assertEquals(-2, MainDrawingLayer.alignToPixelPitch(-2.1, 2, false));
		assertEquals(-2, MainDrawingLayer.alignToPixelPitch(-2.0, 2, false));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch(-1.9, 2, false));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch(-0.1, 2, false));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch( 0.0, 2, false));
		assertEquals( 2, MainDrawingLayer.alignToPixelPitch( 0.1, 2, false));
		assertEquals( 2, MainDrawingLayer.alignToPixelPitch( 1.9, 2, false));
		assertEquals( 2, MainDrawingLayer.alignToPixelPitch( 2.0, 2, false));
		assertEquals( 4, MainDrawingLayer.alignToPixelPitch( 2.1, 2, false));

		assertEquals(-3, MainDrawingLayer.alignToPixelPitch(-3.1, 3, false));
		assertEquals(-3, MainDrawingLayer.alignToPixelPitch(-3.0, 3, false));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch(-2.9, 3, false));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch(-0.1, 3, false));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch( 0.0, 3, false));
		assertEquals( 3, MainDrawingLayer.alignToPixelPitch( 0.1, 3, false));
		assertEquals( 3, MainDrawingLayer.alignToPixelPitch( 2.9, 3, false));
		assertEquals( 3, MainDrawingLayer.alignToPixelPitch( 3.0, 3, false));
		assertEquals( 6, MainDrawingLayer.alignToPixelPitch( 3.1, 3, false));

		assertEquals(-4, MainDrawingLayer.alignToPixelPitch(-2.1, 2, true));
		assertEquals(-2, MainDrawingLayer.alignToPixelPitch(-2.0, 2, true));
		assertEquals(-2, MainDrawingLayer.alignToPixelPitch(-1.9, 2, true));
		assertEquals(-2, MainDrawingLayer.alignToPixelPitch(-0.1, 2, true));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch( 0.0, 2, true));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch( 0.1, 2, true));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch( 1.9, 2, true));
		assertEquals( 2, MainDrawingLayer.alignToPixelPitch( 2.0, 2, true));
		assertEquals( 2, MainDrawingLayer.alignToPixelPitch( 2.1, 2, true));

		assertEquals(-6, MainDrawingLayer.alignToPixelPitch(-3.1, 3, true));
		assertEquals(-3, MainDrawingLayer.alignToPixelPitch(-3.0, 3, true));
		assertEquals(-3, MainDrawingLayer.alignToPixelPitch(-2.9, 3, true));
		assertEquals(-3, MainDrawingLayer.alignToPixelPitch(-0.1, 3, true));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch( 0.0, 3, true));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch( 0.1, 3, true));
		assertEquals( 0, MainDrawingLayer.alignToPixelPitch( 2.9, 3, true));
		assertEquals( 3, MainDrawingLayer.alignToPixelPitch( 3.0, 3, true));
		assertEquals( 3, MainDrawingLayer.alignToPixelPitch( 3.1, 3, true));
	}

	@Test
	public void upstrokeCalculationIsConsistent() {
		assertEquals(false, MainDrawingLayer.isUpStroke(-2, 2));
		assertEquals(true, MainDrawingLayer.isUpStroke(0, 2));
		assertEquals(false, MainDrawingLayer.isUpStroke(2, 2));
		assertEquals(true, MainDrawingLayer.isUpStroke(4, 2));

		assertEquals(false, MainDrawingLayer.isUpStroke(-3, 3));
		assertEquals(true, MainDrawingLayer.isUpStroke(0, 3));
		assertEquals(false, MainDrawingLayer.isUpStroke(3, 3));
		assertEquals(true, MainDrawingLayer.isUpStroke(6, 3));
	}

}