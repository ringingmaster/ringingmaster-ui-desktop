package org.ringingmaster.ui.desktop.compositiondocument.composition.gridmodel;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.util.javafx.grid.model.AdditionalStyleType;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
class MainCourseEndCellModel extends SkeletalCellModel implements CellModel {

    private final CompositionStyle compositionStyle;
    private final Composition composition;

    private final int row;

    MainCourseEndCellModel(Composition composition, CompositionStyle compositionStyle,
                           int row) {
        this.compositionStyle = compositionStyle;
        this.composition = checkNotNull(composition);
        this.row = row;
    }

    @Override
    public int getLength() {
        return (row >= composition.allCompositionCells().getRowSize()) ? 0 : 8;
    }

    @Override
    public CharacterModel getCharacterModel(int index) {
        return new CharacterModel() {
            @Override
            public char getCharacter() {
                return Integer.toString(index + 1).charAt(0);
            }

            @Override
            public Font getFont() {
                return compositionStyle.getFont(CompositionStyle.CompositionStyleFont.MAIN);
            }

            @Override
            public Color getColor() {
                return (index % 2 == 0) ? Color.LIGHTGRAY : Color.BLUE;
            }

            @Override
            public Set<AdditionalStyleType> getAdditionalStyle() {
                return Collections.emptySet();
            }

            @Override
            public Optional<String> getTooltipText() {
                return Optional.empty();
            }

        };
    }

    @Override
    public void insertCharacter(int index, String character) {
        throw new IllegalStateException("Attempt to call insertCharacter on read only MainCourseEndCellModel");
    }

    @Override
    public void removeCharacter(int index) {
        throw new IllegalStateException("Attempt to call removeCharacter on read only MainCourseEndCellModel");
    }

}
