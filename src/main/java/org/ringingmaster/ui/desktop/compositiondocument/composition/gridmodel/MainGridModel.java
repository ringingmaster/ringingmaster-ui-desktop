package org.ringingmaster.ui.desktop.compositiondocument.composition.gridmodel;

import javafx.scene.paint.Color;
import org.ringingmaster.engine.composition.TableType;
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
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;

/**
 * TODO comments ???
 *
 * @author Steve Lake
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
        return compositionDocument.getComposition().allCompositionCells().getRowSize()
                 + EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
    }

    @Override
    public int getColumnSize() {
        return EXTRA_COLUMN_FOR_COURSEHEADS +
                compositionDocument.getComposition().allCompositionCells().getColumnSize() +
                EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
    }

    @Override
    public Color getGridRowColor(int row) {
        if (row == 1 &&
                compositionDocument.getComposition().getCompositionType() == COURSE_BASED) {
            return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.COMPOSITION_GRID_SEPARATOR);
        }
        else {
            return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.COMPOSITION_GRID);
        }
    }

    @Override
    public Color getGridColColor(int col) {
        if ((col > 0) && (col == (getColumnSize() - 2 - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION)) &&
                compositionDocument.getComposition().isSpliced()) {
            return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.COMPOSITION_GRID_SEPARATOR);
        }
        else {
            return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.COMPOSITION_GRID);
        }
    }

    @Override
    public double getGridRowLineWidth(int row) {
        if (row == 1 &&
                compositionDocument.getComposition().getCompositionType() == COURSE_BASED) {
            return 2.0;
        }
        else {
            return 1.0;
        }
    }

    @Override
    public double getGridColLineWidth(int col) {
        if ((col > 0) && (col == (getColumnSize() - 2 - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION)) &&
                compositionDocument.getComposition().isSpliced()) {
            return 2.0;
        }
        else {
            return 1.0;
        }
    }

    @Override
    public CellModel getCellModel(final int row, final int column) {
        checkElementIndex(row, getRowSize());
        checkElementIndex(column, getColumnSize());

        boolean outOfBoundCol = (column >= getColumnSize() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);
        boolean outOfBoundRow = (row >= getRowSize() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);

        int compositionColumn = column - EXTRA_COLUMN_FOR_COURSEHEADS ;

        if (column == 0) {
            return new MainCourseEndCellModel( compositionDocument.getComposition(), compositionDocument.getCompositionStyle(), row);
        }
        else if (outOfBoundCol || outOfBoundRow) {
            return new ExpansionCellModel(this, compositionDocument.getMutableComposition(), TableType.COMPOSITION_TABLE, row, compositionColumn);
        } else {
            ParsedCell parsedCell = parse.allCompositionCells().get(row, compositionColumn);
            return new StandardCellModel(
                    compositionDocument.getMutableComposition(),
                    compositionDocument.getCompositionStyle(),
                    TableType.COMPOSITION_TABLE,
                    row, compositionColumn, parsedCell);
        }
    }

}