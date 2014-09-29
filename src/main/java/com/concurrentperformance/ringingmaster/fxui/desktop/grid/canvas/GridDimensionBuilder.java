package com.concurrentperformance.ringingmaster.fxui.desktop.grid.canvas;

import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridCellModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridCharacterGroup;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridModel;

/**
 * Contains all the calculations to turn a model, first into a list of width and height measurements
 * and from that into final absolute dimensions.
 *
 * @author Lake
 */
public class GridDimensionBuilder {

	public static final int MIN_ROW_HEIGHT = 15;
	public static final int MIN_COL_WIDTH = 15;
	private GridModel model;

	private static GridDimension ZERO_SIZED_GRID_DIMENSION = new GridDimension(true, null,null,null,null,null,null);

	GridDimensionBuilder setModel(GridModel model) {
		this.model = model;
		return this;
	}

	GridDimension build() {
		if (model.isZeroSized()) {
			return ZERO_SIZED_GRID_DIMENSION;
		}
		else {
			return calculateFromModel();
		}
	}

	private GridDimension calculateFromModel() {
		CharacterGroupMeasurer measurer = new CharacterGroupMeasurer();

		final int rowCount = model.getRowCount();
		final int colCount = model.getColumnCount();
		final int colCountInclRowHeader = colCount + GridDimension.ROW_HEADER_OFFSET;

		final CharacterGroupMeasurement[][] cellMeasurements = new CharacterGroupMeasurement[colCountInclRowHeader][rowCount];

		// Measure all row headers
		for (int row=0;row<rowCount;row++) {
			GridCharacterGroup rowHeaderModel = model.getRowHeader(row);
			CharacterGroupMeasurement cellDimension = measurer.measureCell(rowHeaderModel);
			cellMeasurements[GridDimension.ROW_HEADER_POSITION][row] = cellDimension;
		}

		// measure all the cells.
		for (int row=0;row< rowCount;row++) {
			for (int col=0;col< colCount;col++) {
				final GridCellModel cellModel = model.getCellModel(col, row);
				CharacterGroupMeasurement cellDimension = measurer.measureCell(cellModel);
				cellMeasurements[col + GridDimension.ROW_HEADER_OFFSET][row] = cellDimension;
			}
		}


		double[] tableColumnWidths = calculateColumnWidths(cellMeasurements, colCountInclRowHeader, rowCount);
		double[] tableRowHeights = calculateRowHeights(cellMeasurements, colCountInclRowHeader, rowCount);

		double[] vertLinePositions = calculateLinePositionsFromGaps(tableColumnWidths);
		double[] horzLinePositions = calculateLinePositionsFromGaps(tableRowHeights);

		double[] tableBottomGaps = calculateBottomGaps(cellMeasurements, colCountInclRowHeader, rowCount);

		GridCellDimension[][] cells = calculateCells(cellMeasurements, vertLinePositions, colCountInclRowHeader, rowCount);

		return new GridDimension(false, vertLinePositions, horzLinePositions,
				tableColumnWidths, tableRowHeights, tableBottomGaps, cells);
	}

	private double[] calculateColumnWidths(final CharacterGroupMeasurement[][] cellMeasurements, int colCount, int rowCount) {
		final double[] columnWidths = new double[colCount];

		for (int col=0;col<colCount;col++) {
			columnWidths[col] = measureColumnWidth(cellMeasurements, rowCount, col);
		}
		return columnWidths;
	}

	private double measureColumnWidth(final CharacterGroupMeasurement[][] cellMeasurements, int rowCount, final int col) {
		double maxColWidth = 0;
		for (int row=0;row<rowCount;row++) {
			final double cellWidth = cellMeasurements[col][row].getTotalWidth();
			if (cellWidth > maxColWidth) {
				maxColWidth = cellWidth;
			}
		}
		return Math.max(maxColWidth, MIN_COL_WIDTH);
	}

	private double[] calculateRowHeights(final CharacterGroupMeasurement[][] cellMeasurements, int colCount, int rowCount) {
		final double[] rowHeights = new double[rowCount];

		for (int row=0;row<rowCount;row++) {
			rowHeights[row] = measureRowHeight(cellMeasurements, colCount, row);
		}
		return rowHeights;
	}

	private double measureRowHeight(final CharacterGroupMeasurement[][] cellMeasurements, int colCount, final int row) {
		double maxRowHeight = 0;
		for (int col=0;col<colCount;col++) {
			final double cellHeight = cellMeasurements[col][row].getMaxHeight();
			if (cellHeight > maxRowHeight) {
				maxRowHeight = cellHeight;
			}
		}
		return Math.max(maxRowHeight, MIN_ROW_HEIGHT);
	}

	private double[] calculateLinePositionsFromGaps(final double[] gaps) {
		final double[] positions = new double[gaps.length + 1];
		positions[0] = 0.0;

		for (int i=0;i<gaps.length;i++) {
			positions[i+1] = positions[i] + gaps[i];
		}
		return positions;
	}

	private double[] calculateBottomGaps(final CharacterGroupMeasurement[][] cellMeasurements, int colCount, int rowCount) {
		final double[] bottomGaps = new double[rowCount];

		for (int row=0;row<rowCount;row++) {
			for (int col=0;col<colCount;col++) {
				double cellBottomGap = cellMeasurements[col][row].getBottomGap();
				bottomGaps[row] = Math.max(bottomGaps[row], cellBottomGap);
			}
		}

		return bottomGaps;
	}

	private GridCellDimension[][] calculateCells(final CharacterGroupMeasurement[][] cellMeasurements, double[] vertLinePositions,
	                                             int colCount, int rowCount) {

		GridCellDimension[][] cells = new GridCellDimension[colCount][rowCount];
		for (int rowIndex=0;rowIndex<rowCount;rowIndex++) {
			for (int colIndex=0;colIndex<colCount;colIndex++) {
				double vertLinePosition = vertLinePositions[colIndex];
				final double horizontalPadding = cellMeasurements[colIndex][rowIndex].getHorizontalPadding();
				double[] characterWidths = cellMeasurements[colIndex][rowIndex].getCharacterWidths();
				cells[colIndex][rowIndex] = calculateCell(vertLinePosition, horizontalPadding, characterWidths);
			}
		}


		return cells;
	}

	private GridCellDimension calculateCell(double vertLinePosition, double horizontalPadding, double[] characterWidths) {
		double[] characterStarts = new double[characterWidths.length+1];
		double[] characterMids = new double[characterWidths.length+1];

		characterStarts[0] = vertLinePosition + horizontalPadding;
		for (int characterIndex=1;characterIndex<characterWidths.length+1;characterIndex++) {
			characterStarts[characterIndex] = characterStarts[characterIndex-1] + characterWidths[characterIndex-1];
			characterMids[characterIndex] = characterStarts[characterIndex] + (characterWidths[characterIndex-1]/2);
		}
		return new GridCellDimension(characterStarts, characterMids);
	}
}
