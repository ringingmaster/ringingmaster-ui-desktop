package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
class ExpansionCellModel extends SkeletalCellModel implements CellModel {

    private final ObservableComposition observableComposition;
    private final int column;
    private final int row;


    ExpansionCellModel(ObservableComposition observableComposition,
                              int row, int column) {
        this.observableComposition = checkNotNull(observableComposition);
        this.column = column;
        this.row = row;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public void insertCharacter(int index, String character) {
        observableComposition.insertCharacters(TableType.MAIN_TABLE, row, column, index, character);
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

