package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import javafx.scene.paint.Color;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.GridPosition;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.GridModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalGridModel;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class MainGridModel extends SkeletalGridModel implements GridModel {

    public static final int EXTRA_COLUMN_FOR_COURSEHEADS = 1;
    public static final int EXTRA_ROW_OR_COLUMN_FOR_EXPANSION = 1;

    private final CompositionDocument compositionDocument;

    public MainGridModel(CompositionDocument compositionDocument) {
        this.compositionDocument = checkNotNull(compositionDocument);
        // Column 1 because we have a row header.
        this.setCaretPosition(new GridPosition(0,1,0));
    }

    @Override
    public boolean hasRowHeader() {
        return true;
    }

    @Override
    public int getRowSize() {
        return compositionDocument.getRowSize() + EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
    }

    @Override
    public int getColumnSize() {
        return EXTRA_COLUMN_FOR_COURSEHEADS + compositionDocument.getColumnSize() + EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
    }

    @Override
    public Color getGridColor() {
        return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.GRID);
    }

    @Override
    public CellModel getCellModel(final int row, final int column) {
        checkElementIndex(row, getRowSize());
        checkElementIndex(column, getColumnSize());

        boolean outOfBoundCol = (column >= getColumnSize() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);
        boolean outOfBoundRow = (row >= getRowSize() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);

        if (column == 0) {
            return new CourseEndCellModel(getListeners(), compositionDocument, row);
        }
        else if (outOfBoundCol || outOfBoundRow) {
            return new ExpansionCellModel(getListeners(), compositionDocument, row, column);
        } else {
            int compositionColumn = column - EXTRA_COLUMN_FOR_COURSEHEADS ;
            Cell cell = compositionDocument.allCellsView().get(row, compositionColumn);
            return new StandardCellModel(getListeners(), compositionDocument, row, compositionColumn, cell);
        }
    }

}