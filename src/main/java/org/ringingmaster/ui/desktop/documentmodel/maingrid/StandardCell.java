package org.ringingmaster.ui.desktop.documentmodel.maingrid;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.touch.container.TouchCell;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.ui.common.TouchStyle;
import org.ringingmaster.ui.desktop.documentmodel.TouchDocument;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.*;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class StandardCell extends SkeletalGridCellModel implements GridCellModel {

	private final TouchDocument touchDocument;
	private final TouchCell cell;

	public StandardCell(List<GridModelListener> listeners, TouchDocument touchDocument, TouchCell cell) {
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
//		touchDocument.setUpdatePoint(() -> "Typing", Touch.Mutated.MUTATED);
//		fireCellStructureChanged();
	}

	@Override
	public void removeCharacter(int index) {
////TODO		cell.remove(index);
//		touchDocument.collapseEmptyRowsAndColumns();
//		touchDocument.setUpdatePoint(() -> "Delete", Touch.Mutated.MUTATED);
//		fireCellStructureChanged();
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
				return touchDocument.getTouchStyle().getFont(TouchStyle.TouchStyleFont.MAIN);
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