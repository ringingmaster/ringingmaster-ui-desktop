package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.GridCellModel;
import org.ringingmaster.util.javafx.grid.model.GridCharacterModel;
import org.ringingmaster.util.javafx.grid.model.GridModelListener;
import org.ringingmaster.util.javafx.grid.model.SkeletalGridCellModel;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class ExpansionCell extends SkeletalGridCellModel implements GridCellModel {

    private final CompositionDocument compositionDocument;
    private final int column;
    private final int row;


    public ExpansionCell(List<GridModelListener> listeners,
                         CompositionDocument compositionDocument,
                         int row, int column) {
        super(listeners);
        this.compositionDocument = checkNotNull(compositionDocument);
        this.column = column;
        this.row = row;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public void insertCharacter(int index, String character) {

        compositionDocument.insertCharacter(row, column, index, character);
        compositionDocument.setUpdatePoint(() -> "Typing", true);

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

