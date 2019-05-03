package org.ringingmaster.ui.desktop.compositiondocument.definitiongrid;


import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.AdditionalStyleType;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
class DefinitionShorthandCell extends SkeletalCellModel implements Iterable<CharacterModel> {

    private final CompositionDocument compositionDocument;
    private final Cell definition;

    public DefinitionShorthandCell(CompositionDocument compositionDocument, Cell definition) {
        this.compositionDocument = checkNotNull(compositionDocument);
        this.definition = checkNotNull(definition);
    }

    @Override
    public int getLength() {
        //TODO Reactive
        //	return definition.getShorthand().length() + 3;
        return 1;
    }

    @Override
    public CharacterModel getCharacterModel(final int index) {
        return new CharacterModel() {
            @Override
            public char getCharacter() {
                //TODO Reactive
//				final String shorthand = definition.getShorthand() + " = ";
//				return shorthand.charAt(index);
                return 'l';
            }

            @Override
            public Font getFont() {
                return compositionDocument.getCompositionStyle().getFont(CompositionStyle.CompositionStyleFont.MAIN);
            }

            @Override
            public Color getColor() {
                return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.DEFINITION);
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