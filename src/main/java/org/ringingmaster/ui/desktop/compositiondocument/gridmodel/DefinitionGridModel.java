package org.ringingmaster.ui.desktop.compositiondocument.gridmodel;


import javafx.scene.paint.Color;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.GridModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalGridModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Steve Lake
 */
public class DefinitionGridModel extends SkeletalGridModel implements GridModel {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public static final int EXTRA_ROW_FOR_EXPANSION = 1;
    public static final int EQUALS_COLUMN = 1;


    private final CompositionDocument compositionDocument;
    private Parse parse;


    public DefinitionGridModel(CompositionDocument compositionDocument) {
        this.compositionDocument = checkNotNull(compositionDocument);

        compositionDocument.observableParse().subscribe(parse -> {
            DefinitionGridModel.this.parse = parse;
            fireCellContentsChanged();
        });
        compositionDocument.observableParse().subscribe(parse -> {
            log.info("Got [{}]", parse.getComposition().getActionName());
        });

    }

    @Override
    public int getRowSize() {
        return compositionDocument.getComposition().allDefinitionCells().getRowSize() +
                EXTRA_ROW_FOR_EXPANSION;
    }

    @Override
    public int getColumnSize() {
        return compositionDocument.getComposition().allDefinitionCells().getColumnSize() +
                EQUALS_COLUMN;
    }

    @Override
    public Color getGridColor() {
        return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.DEFINITION_GRID);
    }

    @Override
    public CellModel getCellModel(int row, int column) {
        checkElementIndex(row, getRowSize());
        checkElementIndex(column, getColumnSize());
        boolean outOfBoundRow = (row >= getRowSize() - EXTRA_ROW_FOR_EXPANSION);
        int definitionColumn = (column == 0) ? 0 : 1; //Takes account of equals cell in column 1.

        if (outOfBoundRow) {
            return new ExpansionCellModel(compositionDocument.getMutableComposition(), TableType.DEFINITION_TABLE, row, definitionColumn);
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