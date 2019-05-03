package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
@Immutable
class ExpansionCellModel extends SkeletalCellModel implements CellModel {

    private final MutableComposition mutableComposition;
    private final int column;
    private final int row;


    ExpansionCellModel(MutableComposition mutableComposition,
                       int row, int column) {
        this.mutableComposition = checkNotNull(mutableComposition);
        this.column = column;
        this.row = row;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public void insertCharacter(int index, String character) {
        mutableComposition.insertCharacters(TableType.MAIN_TABLE, row, column, index, character);
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

