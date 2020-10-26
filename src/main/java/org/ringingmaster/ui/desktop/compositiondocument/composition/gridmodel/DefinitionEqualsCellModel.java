package org.ringingmaster.ui.desktop.compositiondocument.composition.gridmodel;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.ui.desktop.style.CompositionStyle;
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
 * TODO comments ???
 *
 * @author Steve Lake
 */
@Immutable
class DefinitionEqualsCellModel extends SkeletalCellModel implements CellModel {

    private final MutableComposition mutableComposition;
    private final CompositionStyle compositionStyle;



    DefinitionEqualsCellModel(MutableComposition mutableComposition, CompositionStyle compositionStyle) {
        this.mutableComposition = checkNotNull(mutableComposition);
        this.compositionStyle = compositionStyle;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public void insertCharacter(int index, String character) {
        // Do nothing
    }

    @Override
    public void removeCharacter(int index) {
        //Do nothing
    }

    @Override
    public CharacterModel getCharacterModel(int index) {
        return new CharacterModel() {
            @Override
            public char getCharacter() {
                return '=';
            }

            @Override
            public Font getFont() {
                return compositionStyle.getFont(CompositionStyle.CompositionStyleFont.MAIN);
            }

            @Override
            public Color getColor() {
                return compositionStyle.getColour(CompositionStyle.CompositionStyleColor.DEFINITION);
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
}

