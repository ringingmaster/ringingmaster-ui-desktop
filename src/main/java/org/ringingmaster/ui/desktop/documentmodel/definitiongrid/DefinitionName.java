package org.ringingmaster.ui.desktop.documentmodel.definitiongrid;

import org.ringingmaster.engine.touch.container.TouchDefinition;
import org.ringingmaster.ui.desktop.documentmodel.TouchDocument;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.AdditionalStyleType;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCharacterGroup;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCharacterModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridModelListener;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.SkeletalGridCellModel;
import org.ringingmaster.ui.common.TouchStyle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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

	private final TouchDocument touchDocument;
	private final TouchDefinition definition;

	public DefinitionName(List<GridModelListener> listeners, TouchDocument touchDocument, TouchDefinition definition) {
		super(listeners);
		this.touchDocument = checkNotNull(touchDocument);
		this.definition = checkNotNull(definition);
	}

	@Override
	public int getLength() {
		return definition.getShorthand().length() + 3;
	}

	@Override
	public GridCharacterModel getGridCharacterModel(final int index) {
		return new GridCharacterModel() {
			@Override
			public char getCharacter() {
				final String shorthand = definition.getShorthand() + " = ";
				return shorthand.charAt(index);
			}

			@Override
			public Font getFont() {
				return touchDocument.getTouchStyle().getFont(TouchStyle.TouchStyleFont.DEFINITION);
			}

			@Override
			public Color getColor() {
					return touchDocument.getTouchStyle().getColour(TouchStyle.TouchStyleColor.DEFINITON);
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