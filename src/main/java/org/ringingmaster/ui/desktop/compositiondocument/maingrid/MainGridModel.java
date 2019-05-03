package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import javafx.scene.paint.Color;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.parse.Parse;
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
    private Parse parse;

    public MainGridModel(CompositionDocument compositionDocument) {
        this.compositionDocument = checkNotNull(compositionDocument);
        compositionDocument.observableParse().subscribe(parse -> {
            MainGridModel.this.parse = parse;
            fireCellContentsChanged();
        });
        this.setCaretPosition(new GridPosition(0,1,0));// Column 1 because we have a row header.
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

        int compositionColumn = column - EXTRA_COLUMN_FOR_COURSEHEADS ;

        if (column == 0) {
            return new CourseEndCellModel( compositionDocument.getComposition(), compositionDocument.getCompositionStyle(), row);
        }
        else if (outOfBoundCol || outOfBoundRow) {
            return new ExpansionCellModel(compositionDocument.getMutableComposition(), row, compositionColumn);
        } else {
            ParsedCell parsedCell = parse.allCompositionCells().get(row, compositionColumn);
            return new StandardCellModel(
                    compositionDocument.getMutableComposition(),
                    compositionDocument.getCompositionStyle(),
                    row, compositionColumn, parsedCell);
        }
    }

}