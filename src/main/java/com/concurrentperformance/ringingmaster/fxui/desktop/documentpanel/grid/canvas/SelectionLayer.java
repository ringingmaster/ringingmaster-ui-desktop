package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.GridPosition;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class SelectionLayer extends Canvas {


	private final GridPane parent;

	public SelectionLayer(GridPane parent) {
		this.parent  = parent;
	}

	public void draw() {

		GraphicsContext gc = getGraphicsContext2D();

		clearBackground(gc);
		drawSelection(gc, parent.getModel(), parent.getDimensions());

	}

	private void clearBackground(GraphicsContext gc) {
		gc.clearRect(0,0,getWidth(), getHeight());
	}

	private void drawSelection(final GraphicsContext gc, final GridModel model, final GridDimension dimensions) {
		GridPosition caretPosition1 = model.getCaretPosition();
		GridPosition caretPosition2 = model.getSelectionStartPosition();

		if (caretPosition1.equals(caretPosition2)) {
			return;
		}

		if (caretPosition1.compareTo(caretPosition2) > 0) {
			GridPosition temp = caretPosition1;
			caretPosition1 = caretPosition2;
			caretPosition2 = temp;
		}

		gc.setFill(Color.RED);
		gc.setStroke(Color.RED);

		while(true) {
			double startX = dimensions.getTableCellDimension(caretPosition1.getColumn(), caretPosition1.getRow()).getVerticalCharacterStartPosition(caretPosition1.getCharacterIndex());
			double endX = 0;
			if (caretPosition1.getRow() == caretPosition2.getRow()) {
				endX = dimensions.getTableCellDimension(caretPosition2.getColumn(), caretPosition2.getRow()).getVerticalCharacterStartPosition(caretPosition2.getCharacterIndex());
			}
			else {
				endX = dimensions.getTableVerticalLinePosition(dimensions.getColumnCount());
			}

			double startY = dimensions.getTableHorizontalLinePosition(caretPosition1.getRow());
			double endY = dimensions.getTableHorizontalLinePosition(caretPosition1.getRow() + 1);

			gc.fillRect(startX, startY, endX - startX, endY - startY);

			if (caretPosition1.getRow() == caretPosition2.getRow()) {
				break;
			}
			caretPosition1 = new GridPosition(0, caretPosition1.getRow()+1, 0);
		}
	}

}