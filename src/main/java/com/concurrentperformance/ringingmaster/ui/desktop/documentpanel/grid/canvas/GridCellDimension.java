package com.concurrentperformance.ringingmaster.ui.desktop.documentpanel.grid.canvas;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class GridCellDimension {

	private int characterCount;
	private final double[] characterStarts;
	private final double[] characterMids;
	private final double[] characterEnds;

	public GridCellDimension(int characterCount, double[] characterStarts, double[] characterMids, double[] characterEnds) {
		this.characterCount = characterCount;
		this.characterStarts = checkNotNull(characterStarts);
		this.characterMids = checkNotNull(characterMids);
		this.characterEnds = checkNotNull(characterEnds);
		checkState(characterStarts.length == characterCount + 1);
		checkState(characterMids.length == characterCount);
		checkState(characterEnds.length == characterCount);
	}

	public double getVerticalCharacterStartPosition(int characterIndex) {
		checkElementIndex(characterIndex, characterStarts.length);
		return characterStarts[characterIndex];
	}

	public double getVerticalCharacterMidPosition(int characterIndex) {
		checkElementIndex(characterIndex, characterMids.length);
		return characterMids[characterIndex];
	}

	public double getVerticalCharacterEndPosition(int characterIndex) {
		checkElementIndex(characterIndex, characterEnds.length);
		return characterEnds[characterIndex];
	}

	public int getCharacterCount() {
		return characterCount;
	}
}
