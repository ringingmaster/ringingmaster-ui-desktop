package com.concurrentperformance.ringingmaster.ui.desktop.documentpanel.grid.model;

import com.concurrentperformance.ringingmaster.ui.desktop.documentpanel.grid.GridPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public abstract class SkeletalGridModel implements GridModel {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final List<GridModelListener> listeners = new ArrayList<>();

	private GridPosition caretPosition = new GridPosition(0,0,0);
	private GridPosition selectionStartPosition = caretPosition;

	@Override
	public void registerListener(final GridModelListener listener) {
		listeners.add(listener);
	}

	@Override
	public void deRegisterListener(GridModelListener listener) {
		boolean removed = listeners.remove(listener);
		checkState(removed == true);
	}

	protected List<GridModelListener> getListeners() {
		return listeners;
	}

	@Override
	public GridPosition getCaretPosition() {
		return caretPosition;
	}

	@Override
	public GridPosition getSelectionStartPosition() {
		return selectionStartPosition;
	}

	@Override
	public void setCaretPosition(GridPosition newPosition) {
		boolean wasInSelection = isSelection();
		doSetCaretPosition(newPosition);
		this.selectionStartPosition = newPosition;
		if (wasInSelection) {
			fireSelectionChanged();
		}
		//log.info("Caret [" + caretPosition+ "], Selection Start [" + selectionStartPosition + "]");

		doSetCaretPosition(caretPosition);
		fireCaretPositionMoved();
	}

	@Override
	public boolean isSelection() {
		return !caretPosition.equals(selectionStartPosition);
	}


	private void fireCaretPositionMoved() {
		for (GridModelListener listener : listeners) {
			listener.gridModelListener_caretMoved();
		}
	}

	@Override
	public void setSelectionEndPosition(GridPosition selectionEndPosition) {
		checkNotNull(selectionEndPosition);
		doSetCaretPosition(selectionEndPosition);
		fireSelectionChanged();
		//log.info("Caret [" + caretPosition + "], Selection Start [" + selectionStartPosition + "]");
	}

	private void fireSelectionChanged() {
		for (GridModelListener listener : listeners) {
			listener.gridModelListener_selectionChanged();
		}
	}

	private void doSetCaretPosition(GridPosition newPosition) {
		checkValidGridPosition(newPosition);
		this.caretPosition = newPosition;
		fireCaretPositionMoved();
	}

	private void checkValidGridPosition(GridPosition position) {
		checkPositionIndex(position.getColumn(), getColumnCount(), "column");
		checkPositionIndex(position.getRow(), getRowCount(), "row");
		GridCellModel cellModel = getCellModel(position.getColumn(), position.getRow());
		checkPositionIndex(position.getCharacterIndex(), cellModel.getLength()+1, "cell");
	}

	@Override
	public boolean isZeroSized() {
		return (getColumnCount() == 0 || getRowCount() == 0);
	}

	@Override
	public GridCharacterModel getCharacterModel(GridPosition gridPosition) {
		checkNotNull(gridPosition);
		GridCellModel cellModel = getCellModel(gridPosition.getColumn(), gridPosition.getRow());
		if (cellModel != null) {
			return cellModel.getGridCharacterModel(gridPosition.getCharacterIndex());
		}
		return null;
	}

}
