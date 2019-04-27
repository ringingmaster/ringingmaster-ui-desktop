package org.ringingmaster.ui.desktop.documentmodel.maingrid;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.documentmodel.CompositionDocument;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.AdditionalStyleType;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCellModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCharacterGroup;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCharacterModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.SkeletalGridModel;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class MainGridModel extends SkeletalGridModel implements GridModel {

    public static final int EXTRA_ROW_OR_COLUMN_FOR_EXPANSION = 1;

    private final CompositionDocument compositionDocument;

    public MainGridModel(CompositionDocument compositionDocument) {
        this.compositionDocument = checkNotNull(compositionDocument);
    }

    @Override
    public int getColumnCount() {
        return compositionDocument.getColumnCount() + EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
    }

    @Override
    public int getRowCount() {
        return compositionDocument.getRowCount() + EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
    }

    @Override
    public Color getGridColor() {
        return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.GRID);
    }

    @Override
    public GridCellModel getCellModel(final int column, final int row) {
        checkElementIndex(column, getColumnCount());
        checkElementIndex(row, getRowCount());

        boolean outOfBoundCol = (column >= getColumnCount() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);
        boolean outOfBoundRow = (row >= getRowCount() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);

        if (outOfBoundCol || outOfBoundRow) {
            return new ExpansionCell(getListeners(), compositionDocument, outOfBoundCol, outOfBoundRow, column, row);
        } else {
            Cell cell = compositionDocument.allCellsView().get(row, column);
            return new StandardCell(getListeners(), compositionDocument, cell);
        }
    }

    @Override
    public GridCharacterGroup getRowHeader(int row) {//TODO move from here, and drive from composition.
        return new GridCharacterGroup() {
            @Override
            public int getLength() {
                return (row >= compositionDocument.getRowCount()) ? 0 : 8;
            }

            @Override
            public GridCharacterModel getGridCharacterModel(int index) {
                return new GridCharacterModel() {
                    @Override
                    public char getCharacter() {
                        return Integer.toString(index + 1).charAt(0);
                    }

                    @Override
                    public Font getFont() {
                        return compositionDocument.getCompositionStyle().getFont(CompositionStyle.CompositionStyleFont.MAIN);
                    }

                    @Override
                    public Color getColor() {
                        return (index % 2 == 0) ? Color.LIGHTGRAY : Color.BLUE;
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