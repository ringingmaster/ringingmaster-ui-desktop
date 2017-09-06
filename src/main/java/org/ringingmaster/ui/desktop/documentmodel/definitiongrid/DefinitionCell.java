package org.ringingmaster.ui.desktop.documentmodel.definitiongrid;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.touch.container.TouchCell;
import org.ringingmaster.engine.touch.parser.ParseType;
import org.ringingmaster.ui.common.TouchStyle;
import org.ringingmaster.ui.desktop.documentmodel.TouchDocument;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DefinitionCell extends SkeletalGridCellModel implements GridCellModel {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final TouchDocument touchDocument;
	private final TouchCell cell;

	public DefinitionCell(List<GridModelListener> listeners, TouchDocument touchDocument, TouchCell cell) {
		super(listeners);
		this.touchDocument = checkNotNull(touchDocument);
		this.cell = checkNotNull(cell);
	}

	@Override
	public int getLength() {
		return cell.getLength();
	}

	@Override
	public void insertCharacter(int index, String character) {
//TODO		cell.insert(character, index);
//TODO		touchDocument.setUpdatePoint(() -> "Typing", Touch.Mutated.MUTATED);
		fireCellStructureChanged();
	}

	@Override
	public void removeCharacter(int index) {
		cell.remove(index);
		touchDocument.collapseEmptyRowsAndColumns();
//TODO		touchDocument.setUpdatePoint(() -> "Delete", Touch.Mutated.MUTATED);
		fireCellStructureChanged();
	}

	@Override
	public GridCharacterModel getGridCharacterModel(final int index) {
		return new GridCharacterModel() {
			@Override
			public char getCharacter() {
				return cell.getElement(index).getCharacter();
			}

			@Override
			public Font getFont() {
				return touchDocument.getTouchStyle().getFont(TouchStyle.TouchStyleFont.DEFINITION);
			}

			@Override
			public Color getColor() {
				ParseType parseType = cell.getElement(index).getParseType();
				return touchDocument.getTouchStyle().getColourFromParseType(parseType);
			}
			@Override
			public Set<AdditionalStyleType> getAdditionalStyle() {
				ParseType parseType = cell.getElement(index).getParseType();
				if (parseType == ParseType.UNPARSED) {
					return EnumSet.of(AdditionalStyleType.WIGGLY_UNDERLINE);
				}
				return Collections.emptySet();
			}

			@Override
			public Optional<String> getTooltipText() {
				if (index >= cell.getLength()) {
					return Optional.empty();
				}

				String tooltipText = cell.getElement(index).getParseType().name();
				return Optional.of(tooltipText);
			}
		};
	}
}