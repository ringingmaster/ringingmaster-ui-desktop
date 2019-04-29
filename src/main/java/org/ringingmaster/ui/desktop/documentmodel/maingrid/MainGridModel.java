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
    public int getRowSize() {
        return compositionDocument.getRowSize() + EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
    }

    @Override
    public int getColumnSize() {
        return compositionDocument.getColumnSize() + EXTRA_ROW_OR_COLUMN_FOR_EXPANSION;
    }

    @Override
    public Color getGridColor() {
        return compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.GRID);
    }

    @Override
    public GridCellModel getCellModel(final int row, final int column) {
        checkElementIndex(row, getRowSize());
        checkElementIndex(column, getColumnSize());

        boolean outOfBoundCol = (column >= getColumnSize() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);
        boolean outOfBoundRow = (row >= getRowSize() - EXTRA_ROW_OR_COLUMN_FOR_EXPANSION);

        if (outOfBoundCol || outOfBoundRow) {
            return new ExpansionCell(getListeners(), compositionDocument, row, column);
        } else {
            Cell cell = compositionDocument.allCellsView().get(row, column);
            return new StandardCell(getListeners(), compositionDocument, row, column, cell);
        }
    }

    @Override
    public GridCharacterGroup getRowHeader(int row) {//TODO move from here, and drive from composition.
        return new GridCharacterGroup() {
            @Override
            public int getLength() {
                return (row >= compositionDocument.getRowSize()) ? 0 : 8;
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
                return new Iterator<>() {
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