package org.ringingmaster.ui.desktop.documentpanel.grid.canvas;

import static com.google.common.base.Preconditions.*;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class GridDimension {

	public static final int ROW_HEADER_OFFSET = 1;
	public static final int ROW_HEADER_POSITION = 0;

	private final boolean zeroSized;

	private final double[] vertLinePositions;
	private final double[] horzLinePositions;

	private final double[] columnWidths;
	private final double[] rowHeights;
	private final double[] bottomGaps;

	private final GridCellDimension[][] cellDimensions;


	GridDimension(boolean zeroSized,
	              double[] vertLinePositions, double[] horzLinePositions,
	              double[] columnWidths, double[] rowHeights,
	              double[] bottomGaps,
	              GridCellDimension[][] cellDimensions) {
		this.zeroSized = zeroSized;
		this.vertLinePositions = vertLinePositions;
		this.horzLinePositions = horzLinePositions;
		this.columnWidths = columnWidths;
		this.rowHeights = rowHeights;
		this.bottomGaps = bottomGaps;
		this.cellDimensions = cellDimensions;
	}

	public double getRowHeaderLeft() {
		return vertLinePositions[0];
	}

	public double getTableLeft() {
		return vertLinePositions[ROW_HEADER_OFFSET];
	}

	public double getTableRight() {
		return vertLinePositions[vertLinePositions.length-1];
	}

	public double getTableTop() {
		return horzLinePositions[0];
	}

	public double getTableBottom() {
		return horzLinePositions[horzLinePositions.length-1];
	}

	public double getTableVerticalLinePosition(int vertLineIndex) {
		final int offsetVertLineIndex = ROW_HEADER_OFFSET + vertLineIndex;
		checkElementIndex(offsetVertLineIndex, vertLinePositions.length);
		return vertLinePositions[offsetVertLineIndex];
	}

	public double getTableHorizontalLinePosition(int horzLineIndex) {
		checkElementIndex(horzLineIndex, horzLinePositions.length);
		return horzLinePositions[horzLineIndex];
	}


	public GridCellDimension getCell(int colIndex, int rowIndex) {
		int offsetColIndex = ROW_HEADER_OFFSET + colIndex;
		checkElementIndex(offsetColIndex, cellDimensions.length);
		final GridCellDimension[] column = cellDimensions[offsetColIndex];
		checkElementIndex(rowIndex, column.length);
		return column[rowIndex];
	}

	public GridCellDimension getRowHeaderCellDimension(int rowIndex) {
		final GridCellDimension[] column = cellDimensions[ROW_HEADER_POSITION];
		checkElementIndex(rowIndex, column.length);
		return column[rowIndex];
	}

	public double getTextBottom(int rowIndex) {
		checkElementIndex(rowIndex + 1, horzLinePositions.length);
		checkElementIndex(rowIndex, bottomGaps.length);
		// The +1 is because the horizontal Line positions have a top line that we are not interested in.
		return getTableHorizontalLinePosition(rowIndex + 1) - bottomGaps[rowIndex];
	}

	public double getRowHeight(final int rowIndex) {
		checkElementIndex(rowIndex, rowHeights.length);
		return rowHeights[rowIndex];
	}

	public boolean isZeroSized() {
		return zeroSized;
	}

	public int getRowCount() {
		return rowHeights.length;
	}

	public int getColumnCount() {
		return columnWidths.length - ROW_HEADER_OFFSET;
	}
}
