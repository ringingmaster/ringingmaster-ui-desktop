package com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.maingrid;

import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.parser.ParseType;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.AdditionalStyleType;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCellModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModelListener;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.SkeletalGridCellModel;
import com.concurrentperformance.ringingmaster.ui.common.TouchStyle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
	public void insertCharacter(int index, char character) {
		cell.insert(character, index);
		touchDocument.setUpdatePoint(() -> "Typing", Touch.Mutated.MUTATED);
		fireCellStructureChanged();
	}

	@Override
	public void removeCharacter(int index) {
		cell.remove(index);
		touchDocument.collapseEmptyRowsAndColumns();
		touchDocument.setUpdatePoint(() -> "Delete", Touch.Mutated.MUTATED);
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
				String tooltipText = cell.getElement(index).getParseType().name();
				return Optional.of(tooltipText);
			}
		};
	}
}