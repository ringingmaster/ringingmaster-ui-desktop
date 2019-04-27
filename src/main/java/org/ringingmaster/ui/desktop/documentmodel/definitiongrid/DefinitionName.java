package org.ringingmaster.ui.desktop.documentmodel.definitiongrid;


import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.documentmodel.CompositionDocument;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.AdditionalStyleType;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCharacterGroup;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCharacterModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridModelListener;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.SkeletalGridCellModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
class DefinitionName extends SkeletalGridCellModel implements GridCharacterGroup {

    private final CompositionDocument compositionDocument;
    private final DefinitionCell definition;

    public DefinitionName(List<GridModelListener> listeners, CompositionDocument compositionDocument, DefinitionCell definition) {
        super(listeners);
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
    public GridCharacterModel getGridCharacterModel(final int index) {
        return new GridCharacterModel() {
            @Override
            public char getCharacter() {
                //TODO Reactive
//				final String shorthand = definition.getShorthand() + " = ";
//				return shorthand.charAt(index);
                return 'l';
            }

            @Override
            public Font getFont() {
                return compositionDocument.getCompositionStyle().getFont(CompositionStyle.CompositionStyleFont.DEFINITION);
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