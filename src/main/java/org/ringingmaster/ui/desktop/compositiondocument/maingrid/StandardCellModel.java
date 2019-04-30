package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.util.javafx.grid.model.AdditionalStyleType;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
class StandardCellModel extends SkeletalCellModel implements CellModel {

    private final Logger log = LoggerFactory.getLogger(StandardCellModel.class);

    private final CompositionStyle compositionStyle;
    private final MutableComposition mutableComposition;
    private final int column;
    private final int row;
    private final ParsedCell parsedCell;

    StandardCellModel(MutableComposition mutableComposition,
                      CompositionStyle compositionStyle,
                      int row, int column, ParsedCell parsedCell) {
        this.mutableComposition = checkNotNull(mutableComposition);
        this.compositionStyle = checkNotNull(compositionStyle);
        this.column = column;
        this.row = row;
        this.parsedCell = checkNotNull(parsedCell);
    }

    @Override
    public int getLength() {
        return parsedCell.size();
    }

    @Override
    public void insertCharacter(int index, String character) {
        mutableComposition.insertCharacters(TableType.MAIN_TABLE, row, column, index, character);
    }

    @Override
    public void removeCharacter(int index) {
        mutableComposition.removeCharacters(TableType.MAIN_TABLE, row,column,index, 1);
    }

    @Override
    public CharacterModel getCharacterModel(final int index) {
        return new CharacterModel() {
            @Override
            public char getCharacter() {
                return parsedCell.get(index);
            }

            @Override
            public Font getFont() {
                return compositionStyle.getFont(CompositionStyle.CompositionStyleFont.MAIN);
            }

            @Override
            public Color getColor() {
                Optional<Section> sectionAtElementIndex = parsedCell.getSectionAtElementIndex(index);
                if (sectionAtElementIndex.isEmpty()) {
                    //This happens when we have an unparsed character
                    return compositionStyle.getColour(CompositionStyle.CompositionStyleColor.FALLBACK);
                }
                ParseType parseType = sectionAtElementIndex.get().getParseType();
                return compositionStyle.getColourFromParseType(parseType);
            }

            @Override
            public Set<AdditionalStyleType> getAdditionalStyle() {
                Optional<Group> groupAtElementIndex = parsedCell.getGroupAtElementIndex(index);
                if (groupAtElementIndex.isPresent()) {
                    if (!groupAtElementIndex.get().isValid()) {
                        return EnumSet.of(AdditionalStyleType.WIGGLY_UNDERLINE);
                    }
                }
                else {
                    if (parsedCell.get(index) != ' ') {
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
//                String tooltipText = cell.get(index).getParseType().name();
//                return Optional.of(tooltipText);
                //TODO Reactive
                return Optional.empty();
            }
        };
    }
}