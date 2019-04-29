package org.ringingmaster.ui.desktop.compositiondocument.definitiongrid;


import javafx.scene.paint.Color;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.GridModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalGridModel;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DefinitionGridModel extends SkeletalGridModel implements GridModel {

    private final CellModel cellModel;
    private final CellModel gridCharacterModel;


    private final CompositionDocument compositionDocument;

    public DefinitionGridModel(CompositionDocument compositionDocument, Cell definition) {
        this.compositionDocument = checkNotNull(compositionDocument);
        checkNotNull(definition);
        cellModel = new DefinitionShorthandCell(getListeners(), compositionDocument, definition);
        gridCharacterModel = new DefinitionDefinitionCell(getListeners(), compositionDocument, definition);
    }

    @Override
    public int getRowSize() {
        return 1;
    }

    @Override
    public int getColumnSize() {
        return 1;
    }

    @Override
    public Color getGridColor() {
        return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.GRID);
    }

    @Override
    public CellModel getCellModel(int row, int column) {
        checkState(column == 0);
        checkState(row == 0);
        return gridCharacterModel;
    }

}