package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.AdditionalStyleType;
import org.ringingmaster.util.javafx.grid.model.GridCellModel;
import org.ringingmaster.util.javafx.grid.model.GridCharacterModel;
import org.ringingmaster.util.javafx.grid.model.GridModelListener;
import org.ringingmaster.util.javafx.grid.model.SkeletalGridCellModel;

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
public class StandardCell extends SkeletalGridCellModel implements GridCellModel {

    private final CompositionDocument compositionDocument;
    private final int column;
    private final int row;
    private final Cell cell;

    public StandardCell(List<GridModelListener> listeners, CompositionDocument compositionDocument,
                        int row, int column, Cell cell) {
        super(listeners);
        this.compositionDocument = checkNotNull(compositionDocument);
        this.column = column;
        this.row = row;
        this.cell = checkNotNull(cell);
    }

    @Override
    public int getLength() {
        return cell.getElementSize();
    }

    @Override
    public void insertCharacter(int index, String character) {
        compositionDocument.insertCharacter(row,column, index, character);
		compositionDocument.setUpdatePoint(() -> "Typing", true);

		fireCellStructureChanged();
    }

    @Override
    public void removeCharacter(int index) {
        //TODO Reactive
        compositionDocument.removeCharacter(row,column,index);
		compositionDocument.setUpdatePoint(() -> "Delete", true);
		fireCellStructureChanged();
    }

    @Override
    public GridCharacterModel getGridCharacterModel(final int index) {
        return new GridCharacterModel() {
            @Override
            public char getCharacter() {
                return cell.getElement(index).getCharacter().charAt(0);
            }

            @Override
            public Font getFont() {
                return compositionDocument.getCompositionStyle().getFont(CompositionStyle.CompositionStyleFont.MAIN);
            }

            @Override
            public Color getColor() {
//                ParseType parseType = cell.getSectionAtElementIndex(index).get().getParseType();
                //return compositionDocument.getCompositionStyle().getColourFromParseType(parseType);
                return Color.AQUA;
            }

            @Override
            public Set<AdditionalStyleType> getAdditionalStyle() {
                //TODO Reactive
//                ParseType parseType = cell.getElement(index).getParseType();
//                if (parseType == ParseType.UNPARSED) {
//                    return EnumSet.of(AdditionalStyleType.WIGGLY_UNDERLINE);
//                }
                return Collections.emptySet();
            }

            @Override
            public Optional<String> getTooltipText() {
//                if (index >= cell.getLength()) {
//                    return Optional.empty();
//                }
//
//                String tooltipText = cell.getElement(index).getParseType().name();
//                return Optional.of(tooltipText);
                //TODO Reactive
                return Optional.empty();
            }
        };
    }
}