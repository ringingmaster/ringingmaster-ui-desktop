package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.GridModelListener;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class ExpansionCellModel extends SkeletalCellModel implements CellModel {

    private final CompositionDocument compositionDocument;
    private final int column;
    private final int row;


    public ExpansionCellModel(List<GridModelListener> listeners,
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

        //TODO compositionDocument.insertCharacter(row, column, index, character);
        compositionDocument.setUpdatePoint(() -> "Typing", true);

        fireCellStructureChanged();
    }

    @Override
    public void removeCharacter(int index) {
        throw new IllegalStateException("Attempt to call removeCharacter on empty ExpansionCellModel");
    }

    @Override
    public CharacterModel getCharacterModel(int index) {
        return null;
    }
}

