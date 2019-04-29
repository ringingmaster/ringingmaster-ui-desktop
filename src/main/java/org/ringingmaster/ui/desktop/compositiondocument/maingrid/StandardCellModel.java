package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;
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
public class StandardCellModel extends SkeletalCellModel implements CellModel {

    private final Logger log = LoggerFactory.getLogger(StandardCellModel.class);

    private final CompositionDocument compositionDocument;
    private final int column;
    private final int row;
    private final ParsedCell cell;

    public StandardCellModel(List<GridModelListener> listeners, CompositionDocument compositionDocument,
                             int row, int column, ParsedCell cell) {
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
        compositionDocument.getObservableComposition().insertCharacters(TableType.MAIN_TABLE, row, column, index, character);
        fireCellStructureChanged();
    }

    @Override
    public void removeCharacter(int index) {
        compositionDocument.getObservableComposition().removeCharacters(TableType.MAIN_TABLE, row,column,index, 1);
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
                Optional<Section> sectionAtElementIndex = cell.getSectionAtElementIndex(index);
                if (sectionAtElementIndex.isEmpty()) {
                    //This happens when we have an unparsed character
                    return Color.BLACK;
                }
                ParseType parseType = sectionAtElementIndex.get().getParseType();
                return compositionDocument.getCompositionStyle().getColourFromParseType(parseType);
            }

            @Override
            public Set<AdditionalStyleType> getAdditionalStyle() {
                Optional<Group> groupAtElementIndex = cell.getGroupAtElementIndex(index);
                if (groupAtElementIndex.isPresent()) {
                    if (!groupAtElementIndex.get().isValid()) {
                        return EnumSet.of(AdditionalStyleType.WIGGLY_UNDERLINE);
                    }
                }
                else {
                    if (cell.getElement(index).getCharacter().charAt(0) != ' ') {
                        return EnumSet.of(AdditionalStyleType.WIGGLY_UNDERLINE);
                    }
                }
                return Collections.emptySet();
            }

            @Override
            public Optional<String> getTooltipText() {
                //TODO Reactive
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