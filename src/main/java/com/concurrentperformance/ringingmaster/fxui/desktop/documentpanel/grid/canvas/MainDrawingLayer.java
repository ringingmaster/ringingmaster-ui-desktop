package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.AdditionalStyleType;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterGroup;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModel;
import com.google.common.annotations.VisibleForTesting;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
		drawCellsText(gc, parent.getModel(), parent.getDimensions());
		drawRowHeadersText(gc, parent.getModel(), parent.getDimensions());
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

	private void drawCellsText(final GraphicsContext gc, GridModel model, GridDimension dimensions) {
		final int rowCount = model.getRowCount();
		final int colCount = model.getColumnCount();

		for(int row = 0;row<rowCount;row++) {
			final double textBottom = dimensions.getTextBottom(row);
			for(int col = 0;col<colCount;col++) {
				final GridCharacterGroup gridCharacterGroup = model.getCellModel(col, row);
				final GridCellDimension tableCellDimension = dimensions.getTableCellDimension(col, row);

				drawCellText(gc, gridCharacterGroup, tableCellDimension, textBottom);
				drawCellUnderline(gc, gridCharacterGroup, tableCellDimension, textBottom);
			}
		}
	}

	private void drawCellText(GraphicsContext gc, GridCharacterGroup gridCharacterGroup, GridCellDimension tableCellDimension, double textBottom) {
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

	private void drawCellUnderline(GraphicsContext gc, GridCharacterGroup gridCharacterGroup, GridCellDimension tableCellDimension, double textBottom) {
		gc.setFill(Color.RED);
		gc.setStroke(Color.RED);
		gc.setLineWidth(1.0);

		final int bottomOffset = 2;
		final int pixelPitch = 2;
		final double bottomUpper = textBottom + bottomOffset;
		final double bottomLower = bottomUpper + pixelPitch;

		//     spxzpdsf


		for (int characterIndex=0;characterIndex<gridCharacterGroup.getLength();characterIndex++) {
			GridCharacterModel gridCharacterModel = gridCharacterGroup.getGridCharacterModel(characterIndex);
			if (gridCharacterModel.getAdditionalStyle().contains(AdditionalStyleType.WIGGLY_UNDERLINE)) {
				int textLeft = alignToPixelPitch(tableCellDimension.getVerticalCharacterStartPosition(characterIndex),pixelPitch, false);
				int textRight = alignToPixelPitch(tableCellDimension.getVerticalCharacterEndPosition(characterIndex),pixelPitch, true);

				for (int horzPos = textLeft;horzPos < textRight; horzPos+=pixelPitch ) {
					boolean upStroke = isUpStroke(horzPos,pixelPitch);
					gc.strokeLine(horzPos,
							upStroke?bottomLower:bottomUpper,
							horzPos+pixelPitch,
							upStroke?bottomUpper:bottomLower
							);
				}
			}
		}
	}

	@VisibleForTesting
	static int alignToPixelPitch(double value, int pixelPitch, boolean roundDown) {
		return ((int)(roundDown?Math.floor(value/pixelPitch):Math.ceil(value/pixelPitch))) * pixelPitch;
	}

	@VisibleForTesting
	static boolean isUpStroke(int value, int pixelPitch) {
		return  ((value/pixelPitch) % 2) == 0;
	}

	private void drawRowHeadersText(GraphicsContext gc, GridModel model, GridDimension dimensions) {
		final int rowCount = model.getRowCount();

		for(int row = 0;row<rowCount;row++) {
			final double textBottom = dimensions.getTextBottom(row);
			final GridCharacterGroup cellModel = model.getRowHeader(row);
			final GridCellDimension tableCellDimension = dimensions.getRowHeaderCellDimension(row);

			drawCellText(gc, cellModel, tableCellDimension, textBottom);
		}
	}
}
