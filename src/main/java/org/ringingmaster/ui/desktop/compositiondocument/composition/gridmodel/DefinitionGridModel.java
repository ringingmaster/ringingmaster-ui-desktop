package org.ringingmaster.ui.desktop.compositiondocument.composition.gridmodel;


import javafx.scene.paint.Color;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.ui.desktop.style.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.GridModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalGridModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

//TODO When adding a new definition through the grid, and typing on the equals column, it does not leave the caret in the correct location
//TODO When deleting the last shorthand, the definition flips to the shorthand column.

/**
 * TODO comments ???
 *
 * @author Steve Lake
 */
public class DefinitionGridModel extends SkeletalGridModel implements GridModel {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public static final int EXTRA_ROW_FOR_EXPANSION = 1;
    public static final int EXTRA_ROW_FOR_EQUALS = 1;


    private final CompositionDocument compositionDocument;
    private Parse parse;


    public DefinitionGridModel(CompositionDocument compositionDocument) {
        this.compositionDocument = checkNotNull(compositionDocument);

        compositionDocument.observableParse().subscribe(parse -> {
            DefinitionGridModel.this.parse = parse; //TODO eventually drive the whole thing from the reactive stream
            fireCellContentsChanged();
        });

    }

    @Override
    public int getRowSize() {
        return compositionDocument.getComposition().allDefinitionCells().getRowSize() +
                EXTRA_ROW_FOR_EXPANSION;
    }

    @Override
    public int getColumnSize() {
        return 3;
    }

    @Override
    public Color getGridRowColor(int row) {
        if (row > 0 && row < getRowSize()) {
            return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.DEFINITION_GRID_SEPARATOR);
        }
        else {
            return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.DEFINITION_GRID_SEPARATOR);
        }
    }

    @Override
    public Color getGridColColor(int col) {
        return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.DEFINITION_GRID);
    }

    @Override
    public double getGridRowLineWidth(int row) {
        return 2.0;
    }

    @Override
    public double getGridColLineWidth(int col) {
        return 1.0;
    }

    @Override
    public CellModel getCellModel(int row, int column) {
        checkElementIndex(row, getRowSize());
        checkElementIndex(column, getColumnSize());
        boolean outOfBoundRow = (row >= getRowSize() - EXTRA_ROW_FOR_EXPANSION);
        boolean outOfBoundCol = column >= compositionDocument.getComposition().allDefinitionCells().getColumnSize() + EXTRA_ROW_FOR_EQUALS;
        int definitionColumn = (column == 0) ? 0 : 1; //Takes account of equals cell in column 1.

        if (getRowSize() == 1 && column == 1) { //When there are no definitions, show the equals cell
            return new DefinitionEqualsCellModel(compositionDocument.getMutableComposition(), compositionDocument.getCompositionStyle());
        }
        else if (outOfBoundRow || outOfBoundCol) {
            return new ExpansionCellModel(this, compositionDocument.getMutableComposition(), TableType.DEFINITION_TABLE, row, definitionColumn);
        }
        else if (column == 1) {
            return new DefinitionEqualsCellModel(compositionDocument.getMutableComposition(), compositionDocument.getCompositionStyle());
        }
        else {
            ParsedCell parsedCell = parse.allDefinitionCells().get(row, definitionColumn);

            return new StandardCellModel(
                    compositionDocument.getMutableComposition(),
                    compositionDocument.getCompositionStyle(),
                    TableType.DEFINITION_TABLE,
                    row, definitionColumn, parsedCell);
        }
    }

}