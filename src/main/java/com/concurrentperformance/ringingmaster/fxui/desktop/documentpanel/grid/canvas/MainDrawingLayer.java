package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterGroup;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments ???
 *
 * @author Lake
 */
class MainDrawingLayer extends Canvas {

	public static final int EXTRA_END_LINE_TO_CLOSE_ROW_OR_COL = 1;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final GridPane parent;

	public MainDrawingLayer(GridPane parent) {
		this.parent = parent;
	}

	public void draw() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.setFontSmoothingType(FontSmoothingType.LCD);
		clearBackground(gc);
		drawGrid(gc, parent.getModel(), parent.getDimensions());
		drawCellText(gc, parent.getModel(), parent.getDimensions());
		drawRowHeaderText(gc, parent.getModel(), parent.getDimensions());
	}

	private void clearBackground(GraphicsContext gc) {
		gc.clearRect(0,0,getWidth(), getHeight());
	}

	private void drawGrid(final GraphicsContext gc, GridModel model, GridDimension dimensions) {
		final int horzLineCount = model.getRowCount() + EXTRA_END_LINE_TO_CLOSE_ROW_OR_COL;
		final int vertLineCount = model.getColumnCount() + EXTRA_END_LINE_TO_CLOSE_ROW_OR_COL;

		gc.setFill(model.getGridColor());
		gc.setStroke(model.getGridColor());
		gc.setLineWidth(1.0);

		// Draw horizontal lines
		final double left = dimensions.getTableLeft();
		final double right = dimensions.getTableRight();
		for (int horizLineIndex = 0;horizLineIndex < horzLineCount ; horizLineIndex++) {
			final double horzLinePosition = dimensions.getTableHorizontalLinePosition(horizLineIndex);
			gc.strokeLine(left, horzLinePosition, right, horzLinePosition);
		}


		// Draw vertical lines
		final double top = dimensions.getTableTop();
		final double bottom = dimensions.getTableBottom();
		for (int vertLineIndex = 0;vertLineIndex < vertLineCount ; vertLineIndex++) {
			final double vertLinePosition = dimensions.getTableVerticalLinePosition(vertLineIndex);
			gc.strokeLine(vertLinePosition, top, vertLinePosition, bottom);
		}
	}

	private void drawCellText(final GraphicsContext gc, GridModel model, GridDimension dimensions) {
		final int rowCount = model.getRowCount();
		final int colCount = model.getColumnCount();

		for(int row = 0;row<rowCount;row++) {
			final double textBottom = dimensions.getTextBottom(row);
			for(int col = 0;col<colCount;col++) {
				final GridCharacterGroup gridCharacterGroup = model.getCellModel(col, row);
				final GridCellDimension tableCellDimension = dimensions.getTableCellDimension(col, row);

				drawCell(gc, gridCharacterGroup, tableCellDimension, textBottom);
			}
		}
	}

	private void drawCell(GraphicsContext gc, GridCharacterGroup gridCharacterGroup, GridCellDimension tableCellDimension, double textBottom) {
		for (int characterIndex=0;characterIndex<gridCharacterGroup.getLength();characterIndex++) {
			double textLeft = tableCellDimension.getVerticalCharacterStartPosition(characterIndex);

			GridCharacterModel gridEditorCharacterModel = gridCharacterGroup.getGridCharacterModel(characterIndex);
			final char cellText = gridEditorCharacterModel.getCharacter();
			final Font font = gridEditorCharacterModel.getFont();
			gc.setFill(gridEditorCharacterModel.getColor()); //TODO do we ned to set both for the font?
			gc.setStroke(gridEditorCharacterModel.getColor());
			gc.setFont(font);
			gc.fillText(String.valueOf(cellText), textLeft, textBottom);//What about the other version of this using and AttributedString
		}
	}

	private void drawRowHeaderText(GraphicsContext gc, GridModel model, GridDimension dimensions) {
		final int rowCount = model.getRowCount();

		for(int row = 0;row<rowCount;row++) {
			final double textBottom = dimensions.getTextBottom(row);
			final GridCharacterGroup cellModel = model.getRowHeader(row);
			final GridCellDimension tableCellDimension = dimensions.getRowHeaderCellDimension(row);

			drawCell(gc, cellModel, tableCellDimension, textBottom);
		}
	}
}
