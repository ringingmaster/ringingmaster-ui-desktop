package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.AdditionalStyleType;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.GridModelListener;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class StandardCellModel extends SkeletalCellModel implements CellModel {

    private final Logger log = LoggerFactory.getLogger(StandardCellModel.class);

    private final CompositionDocument compositionDocument;
    private final int column;
    private final int row;
    private final Cell cell;

    public StandardCellModel(List<GridModelListener> listeners, CompositionDocument compositionDocument,
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
     //TODO    compositionDocument.insertCharacter(row,column, index, character);
		compositionDocument.setUpdatePoint(() -> "Typing", true);

		fireCellStructureChanged();
    }

    @Override
    public void removeCharacter(int index) {
        //TODO Reactive
      //  compositionDocument.removeCharacter(row,column,index);
		compositionDocument.setUpdatePoint(() -> "Delete", true);
		fireCellStructureChanged();
    }

    @Override
    public CharacterModel getCharacterModel(final int index) {
        return new CharacterModel() {
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