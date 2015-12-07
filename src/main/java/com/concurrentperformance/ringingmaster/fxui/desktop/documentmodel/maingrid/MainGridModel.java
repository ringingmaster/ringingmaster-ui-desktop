package com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.maingrid;

import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCellModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterGroup;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.SkeletalGridModel;
import com.concurrentperformance.ringingmaster.ui.common.TouchStyle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class MainGridModel extends SkeletalGridModel implements GridModel {

	public static final int EXTRA_ROW_OR_COLUMN_FOR_EXPANSION = 1;

	private final TouchDocument touchDocument;

	public MainGridModel(TouchDocument touchDocument) {
		this.touchDocument = checkNotNull(touchDocument);
	}

	@Override
	public int getColumnCount() {
		return touchDocument.getColumnCount() + EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
	}

	@Override
	public int getRowCount() {
		return touchDocument.getRowCount() + EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
	}
	@Override
	public Color getGridColor() {
		return touchDocument.getTouchStyle().getColour(TouchStyle.TouchStyleColor.GRID);
	}

	@Override
	public GridCellModel getCellModel(final int column, final int row) {
		checkElementIndex(column, getColumnCount());
		checkElementIndex(row, getRowCount());

		boolean outOfBoundCol = (column >= getColumnCount() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);
		boolean outOfBoundRow = (row >= getRowCount() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);

		if (outOfBoundCol || outOfBoundRow) {
			return new ExpansionCell(getListeners(), touchDocument, outOfBoundCol, outOfBoundRow, column, row);
		}
		else {
			TouchCell cell = touchDocument.allCellsView().getCell(column, row);
			return new StandardCell(getListeners(), touchDocument, cell);
		}
	}

	@Override
	public GridCharacterGroup getRowHeader(int row) {//TODO move from here, and drive from touch.
		return new GridCharacterGroup() {
			@Override
			public int getLength() {
				return (row >= touchDocument.getRowCount())?0:8;
			}

			@Override
			public GridCharacterModel getGridCharacterModel(int index) {
				return new GridCharacterModel() {
					@Override
					public char getCharacter() {
						return Integer.toString(index+1).charAt(0);
					}

					@Override
					public Font getFont() {
						return touchDocument.getTouchStyle().getFont(TouchStyle.TouchStyleFont.MAIN);
					}

					@Override
					public Color getColor() {
						return (index%2==0)?Color.LIGHTGRAY:Color.BLUE;
					}
				};
			}

			@Override
			public Iterator<GridCharacterModel> iterator() {
				return new Iterator<GridCharacterModel>() {
					int index = 0;

					@Override
					public boolean hasNext() {
						return index < getLength();
					}

					@Override
					public GridCharacterModel next() {
						return getGridCharacterModel(index++);
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}