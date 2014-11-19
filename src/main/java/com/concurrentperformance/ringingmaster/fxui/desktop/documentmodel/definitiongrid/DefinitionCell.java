package com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.definitiongrid;

import com.concurrentperformance.ringingmaster.engine.parser.ParseType;
import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCellModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModelListener;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.SkeletalGridCellModel;
import com.concurrentperformance.ringingmaster.ui.common.TouchStyle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DefinitionCell extends SkeletalGridCellModel implements GridCellModel {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final TouchDocument touchDocument;
	private final TouchDefinition definition;

	public DefinitionCell(List<GridModelListener> listeners, TouchDocument touchDocument, TouchDefinition definition) {
		super(listeners);
		this.touchDocument = checkNotNull(touchDocument);
		this.definition = checkNotNull(definition);
	}

	@Override
	public int getLength() {
		return definition.getLength();
	}

	@Override
	public void insertCharacter(int index, char character) {
		definition.insert(character, index);
		fireCellStructureChanged();
	}

	@Override
	public void removeCharacter(int index) {
		definition.remove(index);
		touchDocument.collapseEmptyRowsAndColumns();
		fireCellStructureChanged();
	}

	protected void fireCellStructureChanged() {
		touchDocument.parseAndProve();
		super.fireCellStructureChanged();
	}

	@Override
	public GridCharacterModel getGridCharacterModel(final int index) {
		return new GridCharacterModel() {
			@Override
			public char getCharacter() {
				return definition.getElement(index).getCharacter();
			}

			@Override
			public Font getFont() {
				return touchDocument.getTouchStyle().getFont(TouchStyle.TouchStyleFont.DEFINITION);
			}

			@Override
			public Color getColor() {
				ParseType parseType = definition.getElement(index).getParseType();
				return touchDocument.getTouchStyle().getColourFromParseType(parseType);
			}
		};
	}
}