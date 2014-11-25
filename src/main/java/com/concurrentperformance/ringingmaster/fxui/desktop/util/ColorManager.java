package com.concurrentperformance.ringingmaster.fxui.desktop.util;

import javafx.scene.paint.Color;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class ColorManager {

	public static Color getErrorHighlight() {
		return Color.rgb(255, 120, 120);
	}

	public static Color getWarnHighlight() {
		return Color.rgb(255, 120, 255);
	}

	public static Color getPassHighlight() {
		return Color.rgb(120, 255, 120);
	}

	public static Color getClearHighlight() {
		return Color.WHITE;
	}
}

