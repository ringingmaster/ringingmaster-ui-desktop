package org.ringingmaster.ui.desktop.documentmodel.maingrid;

import org.ringingmaster.ui.desktop.documentmodel.TouchDocument;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCellModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCharacterModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridModelListener;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.SkeletalGridCellModel;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class ExpansionCell extends SkeletalGridCellModel implements GridCellModel {

	private final TouchDocument touchDocument;
	private final boolean expandColumn;
	private final boolean expandRow;
	private final int column;
	private final int row;

	public ExpansionCell(List<GridModelListener> listeners,
	                     TouchDocument touchDocument,
	                     boolean expandColumn, boolean expandRow,
	                     int column, int row) {
		super(listeners);
		this.touchDocument = checkNotNull(touchDocument);
		this.expandColumn = expandColumn;
		this.expandRow = expandRow;
		this.column = column;
		this.row = row;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public void insertCharacter(int index, String character) {
		if (expandColumn) {
			touchDocument.incrementColumnCount();
		}
		if (expandRow) {
			touchDocument.incrementRowCount();
		}

		// We do not use the cell version here as we do not have a cell until the increment above.
		touchDocument.insertCharacter(column, row, index, character);
	//TODO	touchDocument.setUpdatePoint(() -> "Typing", Touch.Mutated.MUTATED);

		fireCellStructureChanged();
	}

	@Override
	public void removeCharacter(int index) {
		throw new IllegalStateException("Attempt to call getGridEditorCharacterModel on empty ExpansionCell");
	}

	@Override
	public GridCharacterModel getGridCharacterModel(int index) {
		return null;
	}
}

