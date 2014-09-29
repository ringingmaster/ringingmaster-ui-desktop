package com.concurrentperformance.ringingmaster.fxui.desktop.grid.canvas;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class GridCellDimension {

	private final double[] characterStart;
	private final double[] characterMids;

	public GridCellDimension(double[] characterStart, double[] characterMids) {
		this.characterStart = checkNotNull(characterStart);
		this.characterMids = checkNotNull(characterMids);
		checkState(characterStart.length == characterMids.length);
	}

	public double getVerticalCharacterStartPosition(int characterIndex) {
		checkElementIndex(characterIndex, characterStart.length);
		return characterStart[characterIndex];
	}

	public double getVerticalCharacterMidPosition(int characterIndex) {
		checkElementIndex(characterIndex, characterMids.length);
		return characterMids[characterIndex];
	}

	public int getCharacterCount() {
		// Each character has a start and there is an additional value for the end.
		// Therefore there is always at least one value here, the -1 makeing the character count a min of 0
		return characterStart.length-1;
	}
}
