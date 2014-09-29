package com.concurrentperformance.ringingmaster.fxui.desktop.document.definitiongrid;

import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;
import com.concurrentperformance.ringingmaster.fxui.desktop.document.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridCharacterGroup;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridCharacterModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridModelListener;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.SkeletalGridCellModel;
import com.concurrentperformance.ringingmaster.ui.common.TouchStyle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

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
		return definition.getName().length() + 3;
	}

	@Override
	public GridCharacterModel getGridCharacterModel(final int index) {
		return new GridCharacterModel() {
			@Override
			public char getCharacter() {
				final String name = definition.getName() + " = ";
				return name.charAt(index);
			}

			@Override
			public Font getFont() {
				return touchDocument.getTouchStyle().getFont(TouchStyle.TouchStyleFont.DEFINITION);
			}

			@Override
			public Color getColor() {
					return touchDocument.getTouchStyle().getColour(TouchStyle.TouchStyleColor.DEFINITON);
			}
		};
	}
}