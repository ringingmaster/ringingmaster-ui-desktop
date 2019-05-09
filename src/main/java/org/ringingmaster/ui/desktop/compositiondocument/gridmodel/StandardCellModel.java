package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Steve Lake
 */
@Immutable
class StandardCellModel extends SkeletalCellModel implements CellModel {

    private final Logger log = LoggerFactory.getLogger(StandardCellModel.class);

    private final CompositionStyle compositionStyle;
    private final MutableComposition mutableComposition;
    private final int column;
    private final int row;
    private final ParsedCell parsedCell;
    private final TableType tableType;

    StandardCellModel(MutableComposition mutableComposition,
                      CompositionStyle compositionStyle,
                      TableType tableType,
                      int row, int column, ParsedCell parsedCell) {
        this.mutableComposition = checkNotNull(mutableComposition);
        this.compositionStyle = checkNotNull(compositionStyle);
        this.tableType = checkNotNull(tableType);
        this.column = column;
        this.row = row;
        this.parsedCell = checkNotNull(parsedCell);
    }

    @Override
    public int getLength() {
        return parsedCell.size();
    }

    @Override
    public void insertCharacter(int index, String character) {
        mutableComposition.insertCharacters(tableType, row, column, index, character);
    }

    @Override
    public void removeCharacter(int index) {
        mutableComposition.removeCharacters(tableType, row,column,index, 1);
    }

    @Override
    public CharacterModel getCharacterModel(final int index) {
        return new StandardRenderedCharacterModel(index, parsedCell, compositionStyle);
    }
}