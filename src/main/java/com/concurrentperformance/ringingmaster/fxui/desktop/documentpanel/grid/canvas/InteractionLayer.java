package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.GridPosition;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas.tooltip.Tooltip;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCellModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterModel;
import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class InteractionLayer extends Pane {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Timer timer = new Timer("Caret", true);
	private final TimerTask blinkTask;

	private static final int BLINK_RATE_MS = 500;
	private static final double CARET_WIDTH = 2.0;

	private volatile boolean showCaret = false;
	private volatile boolean caretBlinkOn = false;

	private final Rectangle caret = new Rectangle(0,0,0,0);
	// This allows the character position to be held when navigating up and down.
	private static final int NOT_SET = -1;
	private int stickyCharacterPosition = NOT_SET;
	boolean mouseDown = false;

	private final GridPane parent;

	private final Runnable setVisibleTask;

	private Tooltip tooltip = new Tooltip("");


	public InteractionLayer(GridPane parent) {
		this.parent = parent;

		setVisibleTask = new Runnable() {
			public void run() {
				caret.setVisible(showCaret && caretBlinkOn);
			}
		};
		Platform.runLater(setVisibleTask);

		blinkTask = new TimerTask() {
			@Override
			public void run() {
				caretBlinkOn = !caretBlinkOn;
				Platform.runLater(setVisibleTask);
			}
		};

		getChildren().add(caret);

		focusedProperty().addListener((observable, oldValue, newValue) -> {
			//log.info("[{}] focussed [{}]", parent.getName(), newValue);
			showCaret = newValue;
			caretBlinkOn = true;
			Platform.runLater(setVisibleTask);
		});

		setOnKeyPressed(this::handleKeyPressed);

		setOnKeyTyped(this::handleKeyTyped);

		setOnMousePressed(this::handleMousePressed);

		setOnMouseReleased(this::handleMouseReleased);

		setOnMouseDragged(this::handleMouseDragged);

		setOnMouseMoved(this::handleMouseMoved);

		timer.schedule(blinkTask, BLINK_RATE_MS, BLINK_RATE_MS);

		setFocusTraversable(true);

		Tooltip.install(this, tooltip);
	}

	private void handleKeyTyped(KeyEvent e) {
		String characterAsString = e.getCharacter();
		if (Strings.isNullOrEmpty(characterAsString)) {
			return;
		}
		char character = characterAsString.charAt(0);
		if (character >= 32 && character < 127 ) {
			GridPosition caretPosition = parent.getModel().getCaretPosition();
			parent.getModel().getCellModel(caretPosition.getColumn(), caretPosition.getRow()).insertCharacter(caretPosition.getCharacterIndex(), character);
			moveRight();
			//log.info("keyTyped:" + e);
		}
	}


	private void handleKeyPressed(KeyEvent event) {
		switch (event.getCode()) {
			case RIGHT:
				moveRight();
				break;
			case LEFT:
				moveLeft();
				break;
			case UP:
				moveUp();
				break;
			case DOWN:
				moveDown();
				break;
			case END:
				if (event.isAltDown() && !event.isShiftDown() && !event.isControlDown()){
					moveToStartOfLastCellIfItHasContentsElseLastButOne();
				}
				break;
			case HOME:
				if (event.isAltDown() && !event.isShiftDown() && !event.isControlDown()){
					moveToStartOfRow();
				}
				break;
			case BACK_SPACE:
				deleteBack();
				break;
			case DELETE:
				deleteForward();
				break;
		}

		//log.info(event.toString());
		event.consume();

	}

	void draw() {
		GridPosition caretPosition = parent.getModel().getCaretPosition();

		final double left = parent.getDimensions().getTableCellDimension(caretPosition.getColumn(), caretPosition.getRow()).getVerticalCharacterStartPosition(caretPosition.getCharacterIndex());
		final double cellTop = parent.getDimensions().getTableHorizontalLinePosition(caretPosition.getRow());
		final double cellHeight = parent.getDimensions().getRowHeight(caretPosition.getRow());

		caret.setX(left);
		caret.setY(cellTop);
		caret.setHeight(cellHeight);
		caret.setWidth(CARET_WIDTH);
	}

	void showCaret() {
		caretBlinkOn = true;
		Platform.runLater(setVisibleTask);
	}

	protected void moveRight() {
		if (parent.getModel().isZeroSized()) {
			return;
		}

		GridPosition caretPosition = parent.getModel().getCaretPosition();

		// If in selection, the just move to the right end of the selection
		if (parent.getModel().isSelection()) {
			GridPosition selectionStartPosition = parent.getModel().getSelectionStartPosition();
			parent.getModel().setCaretPosition((selectionStartPosition.compareTo(caretPosition) > 0 )?selectionStartPosition:caretPosition);
			return;
		}

		int col = caretPosition.getColumn();
		int row = caretPosition.getRow();
		int character = caretPosition.getCharacterIndex()+1;
		GridCellModel cellModel = parent.getModel().getCellModel(col, row);
		if (character > cellModel.getLength()) {
			// roll to start of next cell
			col++;
			character = 0;
			if (col >= parent.getModel().getColumnCount()) {
				// roll to next row
				row++;
				col=0;
			}
		}
		// if we have blown the row count, then there is nowhere else to go
		// (we are in as far right and down as we can go), ignore the move.
		if (row < parent.getModel().getRowCount()) {
			parent.getModel().setCaretPosition(new GridPosition(col, row, character));
			stickyCharacterPosition = character;
		}

	}

	protected void moveLeft() {
		if (parent.getModel().isZeroSized()) {
			return;
		}

		GridPosition caretPosition = parent.getModel().getCaretPosition();

		// If in selection, the just move to the left end of the selection
		if (parent.getModel().isSelection()) {
			GridPosition selectionStartPosition = parent.getModel().getSelectionStartPosition();
			parent.getModel().setCaretPosition((selectionStartPosition.compareTo(caretPosition) < 0 )?selectionStartPosition:caretPosition);
			return;
		}

		int col = caretPosition.getColumn();
		int row = caretPosition.getRow();
		int character = caretPosition.getCharacterIndex()-1;
		if (character < 0) {
			// roll to end of previous cell
			col--;
			if (col < 0) {
				// roll to previous row
				row--;
				col = parent.getModel().getColumnCount()-1;
			}
			if (row >= 0) {
				character = parent.getModel().getCellModel(col, row).getLength();
			}
		}

		// if we have blown the row count, then there is nowhere else to go
		// (we are in as far right and down as we can go), ignore the move.
		if (row >= 0) {
			parent.getModel().setCaretPosition(new GridPosition(col, row, character));
			stickyCharacterPosition = character;
		}

	}

	protected void moveUp() {
		if (parent.getModel().isZeroSized()) {
			return;
		}

		GridPosition pos = parent.getModel().getCaretPosition();
		int col = pos.getColumn();
		int row = pos.getRow()-1;
		int character = pos.getCharacterIndex();
		if (row >= 0) {
			int cellLength = parent.getModel().getCellModel(col, row).getLength();
			if (stickyCharacterPosition != NOT_SET) {
				character = stickyCharacterPosition;
			}
			character = Math.min(cellLength, character);
			parent.getModel().setCaretPosition(new GridPosition(col, row, character));
		}
	}

	protected void moveDown() {
		if (parent.getModel().isZeroSized()) {
			return;
		}

		GridPosition pos = parent.getModel().getCaretPosition();
		int col = pos.getColumn();
		int row = pos.getRow()+1;
		int character = pos.getCharacterIndex();
		if (row < parent.getModel().getRowCount()) {
			int cellLength = parent.getModel().getCellModel(col, row).getLength();
			if (stickyCharacterPosition != NOT_SET) {
				character = stickyCharacterPosition;
			}
			character = Math.min(cellLength, character);
			parent.getModel().setCaretPosition(new GridPosition(col, row, character));
		}
	}

	protected void moveToStartOfLastCellIfItHasContentsElseLastButOne() {
		if (parent.getModel().isZeroSized()) {
			return;
		}

		GridPosition pos = parent.getModel().getCaretPosition();
		int col = parent.getModel().getColumnCount()-1;
		int row = pos.getRow();
		int character = 0;
		if (parent.getModel().getCellModel(col, row).getLength() == 0 &&
				col-1 >= 0 ) {
			col--;
		}
		parent.getModel().setCaretPosition(new GridPosition(col, row, character));
		stickyCharacterPosition = 0;
	}

	protected void moveToStartOfRow() {
		if (parent.getModel().isZeroSized()) {
			return;
		}

		GridPosition pos = parent.getModel().getCaretPosition();
		int col = 0;
		int row = pos.getRow();
		int character = 0;
		parent.getModel().setCaretPosition(new GridPosition(col, row, character));
		stickyCharacterPosition = 0;

	}

	protected void deleteBack() {
		if(parent.getModel().getCaretPosition().getCharacterIndex() > 0) {
			GridPosition caretPosition = parent.getModel().getCaretPosition();
			moveLeft();
			parent.getModel().getCellModel(caretPosition.getColumn(), caretPosition.getRow()).removeCharacter(caretPosition.getCharacterIndex()-1);
		}
	}

	protected void deleteForward() {
		GridPosition caretPosition = parent.getModel().getCaretPosition();
		GridCellModel cellModel = parent.getModel().getCellModel(caretPosition.getColumn(), caretPosition.getRow());
		if (caretPosition.getCharacterIndex() < cellModel.getLength()) {
			cellModel.removeCharacter(caretPosition.getCharacterIndex());
		}
	}

	private void handleMousePressed(MouseEvent e) {
		//log.info("[{}] mouse pressed", parent.getName());
		Optional<GridPosition> gridPosition = mouseCoordinatesToGridPosition(e.getX(), e.getY());
		if (gridPosition.isPresent()) {
			parent.getModel().setCaretPosition(gridPosition.get());
		}
		mouseDown = true;
		requestFocus();
		e.consume();
	}

	private void handleMouseReleased(MouseEvent e) {
		//log.info("Mouse Released" + e);
		mouseDown = false;
		Optional<GridPosition> gridPosition = mouseCoordinatesToGridPosition(e.getX(), e.getY());
		if (gridPosition.isPresent()) {
			parent.getModel().setSelectionEndPosition(gridPosition.get());
		}
		e.consume();
	}

	private void handleMouseDragged(MouseEvent e) {
		//log.info("mouseDragged " + e);
		if (mouseDown) {
			Optional<GridPosition> gridPosition = mouseCoordinatesToGridPosition(e.getX(), e.getY());
			if (gridPosition.isPresent()) {
				parent.getModel().setSelectionEndPosition(gridPosition.get());
			}
		}
		e.consume();
	}

	private void handleMouseMoved(MouseEvent e) {
		if (tooltip.isShowing()) {
			return;
		}
		Optional<GridPosition> gridPosition = mouseCoordinatesToGridPosition(e.getX(), e.getY());
		if (!gridPosition.isPresent()) {
			return;
		}

		GridCharacterModel characterModel = parent.getModel().getCharacterModel(gridPosition.get());
		if (characterModel!= null) {
			Optional<String> tooltipText = characterModel.getTooltipText();
			if (tooltipText.isPresent()) {
				tooltip.setText(tooltipText.get());
			}
			else {
				tooltip.setText("");
			}
		}
	}

	public Optional<GridPosition> mouseCoordinatesToGridPosition(final double x, final double y) {
		GridDimension dimensions = parent.getDimensions();
		if (dimensions.isZeroSized()) {
			return Optional.empty();
		}

		// Calculate the row index
		int rowIndex;
		if (y <= dimensions.getTableHorizontalLinePosition(0))   {
			// We are above the top of the grid, so set to the top row
			rowIndex = 0;
		}
		else if (y >= dimensions.getTableBottom()) {
			// We are below the bottom of the grid, so set to bottom row
			rowIndex = dimensions.getRowCount() - 1;
		}
		else {
			// We are inside the grid. Calculate what row.
			for (rowIndex=0;rowIndex<dimensions.getRowCount();rowIndex++) {
				if (y > dimensions.getTableHorizontalLinePosition(rowIndex) &&
						y <= dimensions.getTableHorizontalLinePosition(rowIndex + 1)) {
					break;
				}
			}
		}

		// Calculate the column index.
		int columnIndex;
		int characterIndex;
		if (x <= dimensions.getTableVerticalLinePosition(0))   {
			// We are to the left of the grid, so set to start of left column.
			columnIndex = 0;
			characterIndex = 0;
		}
		else if (x >= dimensions.getTableRight()) {
			// We are to the right of the grid, so set to end of right column.
			columnIndex = dimensions.getColumnCount() - 1;
			characterIndex = dimensions.getCharacterCountInCell(columnIndex, rowIndex);
		}
		else {
			// We are inside the grid. Calculate what column.
			for (columnIndex=0;columnIndex<dimensions.getColumnCount();columnIndex++) {
				if (x < dimensions.getTableVerticalLinePosition(columnIndex + 1)) {
					break;
				}
			}

			// Now calculate the character index.
			for (characterIndex=0;characterIndex<dimensions.getCharacterCountInCell(columnIndex, rowIndex);characterIndex++) {
				if (x <= dimensions.getTableCellDimension(columnIndex, rowIndex).getVerticalCharacterMidPosition(characterIndex)) {
					break;
				}
			}
		}

		return Optional.of(new GridPosition(columnIndex,rowIndex,characterIndex));
	}

}
