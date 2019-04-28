package org.ringingmaster.ui.desktop.documentpanel.grid.canvas;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.apache.commons.lang.CharUtils;
import org.ringingmaster.ui.desktop.documentpanel.grid.GridPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class InteractionLayer extends Pane implements BlinkTimerListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final GridPane parent;

    private static final double CARET_WIDTH = 2.0;
    private volatile boolean caretVisible = false;
    private volatile boolean caretBlinkOn = false;
    private final Rectangle caret = new Rectangle(0, 0, 0, 0);

    private final CaretPositionMover caretPositionMover;
    boolean mouseDown = false;

//TODO Reactive 	private Tooltip tooltip = new Tooltip("");

    public InteractionLayer(GridPane parent) {
        this.parent = parent;
        caretPositionMover = new CaretPositionMover(parent);

        BlinkTimerManager.getInstance().addListener(this);

        getChildren().add(caret);

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            //log.info("[{}] focussed [{}]", parent.getName(), newValue);
            caretVisible = newValue;
            forceCaretBlinkOnIfVisible();
        });

        setOnKeyPressed(this::handleKeyPressed);

        setOnKeyTyped(this::handleKeyTyped);

        setOnMousePressed(this::handleMousePressed);

        setOnMouseReleased(this::handleMouseReleased);

        setOnMouseDragged(this::handleMouseDragged);

        setOnMouseMoved(this::handleMouseMoved);

        setFocusTraversable(true);

        //TODO Reactive Tooltip.install(this, tooltip);
    }

    @Override
    public void blinkTimerManager_triggerBlink(boolean blinkOn) {
        caretBlinkOn = blinkOn;
        Platform.runLater(() -> caret.setVisible(caretVisible && caretBlinkOn));
    }

    void forceCaretBlinkOnIfVisible() {
        caretBlinkOn = false;
        Platform.runLater(() -> caret.setVisible(caretVisible));
    }

    private void handleKeyTyped(KeyEvent e) {
        String character = e.getCharacter();
        if (Strings.isNullOrEmpty(character)) {
            return;
        }

        if (CharUtils.isAsciiPrintable(character.charAt(0))) {
            Preconditions.checkState(character.length() == 1);
            GridPosition caretPosition = parent.getModel().getCaretPosition();
            parent.getModel().getCellModel(caretPosition.getRow(), caretPosition.getColumn()).insertCharacter(caretPosition.getCharacterIndex(), character);
            caretPositionMover.moveRight();
            //log.info("keyTyped:" + e);
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case RIGHT:
                caretPositionMover.moveRight();
                break;
            case LEFT:
                caretPositionMover.moveLeft();
                break;
            case UP:
                caretPositionMover.moveUp();
                break;
            case DOWN:
                caretPositionMover.moveDown();
                break;
            case END:
                if (event.isAltDown() && !event.isShiftDown() && !event.isControlDown()) {
                    caretPositionMover.moveToStartOfLastCellIfItHasContentsElseLastButOne();
                }
                break;
            case HOME:
                if (event.isAltDown() && !event.isShiftDown() && !event.isControlDown()) {
                    caretPositionMover.moveToStartOfRow();
                }
                break;
            case BACK_SPACE:
                caretPositionMover.deleteBack();
                break;
            case DELETE:
                caretPositionMover.deleteForward();
                break;
        }

        //log.info(event.toString());
        event.consume();

    }

    void draw() {
        GridPosition caretPosition = parent.getModel().getCaretPosition();

        final double left = parent.getDimensions().getCell(caretPosition.getRow(), caretPosition.getColumn()).getVerticalCharacterStartPosition(caretPosition.getCharacterIndex());
        final double cellTop = parent.getDimensions().getTableHorizontalLinePosition(caretPosition.getRow());
        final double cellHeight = parent.getDimensions().getRowHeight(caretPosition.getRow());

        caret.setX(left);
        caret.setY(cellTop);
        caret.setHeight(cellHeight);
        caret.setWidth(CARET_WIDTH);
    }

    private void handleMousePressed(MouseEvent e) {
        //log.info("[{}] mouse pressed", parent.getName());
        Optional<GridPosition> gridPosition = mouseCoordinatesToGridPosition(e.getX(), e.getY(), Align.BOUNDARY_MID_CHARACTER);
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
        Optional<GridPosition> gridPosition = mouseCoordinatesToGridPosition(e.getX(), e.getY(), Align.BOUNDARY_MID_CHARACTER);
        if (gridPosition.isPresent()) {
            parent.getModel().setSelectionEndPosition(gridPosition.get());
        }
        e.consume();
    }

    private void handleMouseDragged(MouseEvent e) {
        //log.info("mouseDragged " + e);
        if (mouseDown) {
            Optional<GridPosition> gridPosition = mouseCoordinatesToGridPosition(e.getX(), e.getY(), Align.BOUNDARY_MID_CHARACTER);
            if (gridPosition.isPresent()) {
                parent.getModel().setSelectionEndPosition(gridPosition.get());
            }
        }
        e.consume();
    }

    private void handleMouseMoved(MouseEvent e) {
        //TODO Reactive
//		if (tooltip.isShowing()) {
//			return;
//		}
//		Optional<GridPosition> gridPosition = mouseCoordinatesToGridPosition(e.getX(), e.getY(), Align.BOUNDARY_BETWEEN_CHARACTER);
//		if (!gridPosition.isPresent()) {
//			return;
//		}
//
//		GridCharacterModel characterModel = parent.getModel().getCharacterModel(gridPosition.get());
//		if (characterModel!= null) {
//			Optional<String> tooltipText = characterModel.getTooltipText();
//			if (tooltipText.isPresent()) {
//				tooltip.setText(tooltipText.get());
//			}
//			else {
//				tooltip.setText(null);
//			}
//		}
    }

    private enum Align {
        BOUNDARY_BETWEEN_CHARACTER,
        BOUNDARY_MID_CHARACTER
    }

    public Optional<GridPosition> mouseCoordinatesToGridPosition(final double x, final double y, Align align) {
        GridDimension dimensions = parent.getDimensions();
        if (dimensions.isZeroSized()) {
            return Optional.empty();
        }

        // Calculate the row index
        int rowIndex;
        if (y <= dimensions.getTableHorizontalLinePosition(0)) {
            // We are above the top of the grid, so set to the top row
            rowIndex = 0;
        } else if (y >= dimensions.getTableBottom()) {
            // We are below the bottom of the grid, so set to bottom row
            rowIndex = dimensions.getRowCount() - 1;
        } else {
            // We are inside the grid. Calculate what row.
            for (rowIndex = 0; rowIndex < dimensions.getRowCount(); rowIndex++) {
                if (y > dimensions.getTableHorizontalLinePosition(rowIndex) &&
                        y <= dimensions.getTableHorizontalLinePosition(rowIndex + 1)) {
                    break;
                }
            }
        }

        // Calculate the column index.
        int columnIndex;
        int characterIndex;
        if (x <= dimensions.getTableVerticalLinePosition(0)) {
            // We are to the left of the grid, so set to start of left column.
            columnIndex = 0;
            characterIndex = 0;
        } else if (x >= dimensions.getTableRight()) {
            // We are to the right of the grid, so set to end of right column.
            columnIndex = dimensions.getColumnCount() - 1;
            characterIndex = dimensions.getCell(rowIndex, columnIndex).getCharacterCount();
        } else {
            // We are inside the grid. Calculate what column.
            for (columnIndex = 0; columnIndex < dimensions.getColumnCount(); columnIndex++) {
                if (x < dimensions.getTableVerticalLinePosition(columnIndex + 1)) {
                    break;
                }
            }

            // Now calculate the character index.
            final GridCellDimension cell = dimensions.getCell(rowIndex, columnIndex);
            characterIndex = 0;

            if (align == Align.BOUNDARY_MID_CHARACTER) {
                while (characterIndex < cell.getCharacterCount() &&
                        x >= cell.getVerticalCharacterMidPosition(characterIndex)) {
                    characterIndex++;
                }
            } else if (align == Align.BOUNDARY_BETWEEN_CHARACTER) {
                while (characterIndex < cell.getCharacterCount() &&
                        x >= cell.getVerticalCharacterEndPosition(characterIndex)) {
                    characterIndex++;
                }
            }
        }

        return Optional.of(new GridPosition( rowIndex, columnIndex, characterIndex));
    }
}
