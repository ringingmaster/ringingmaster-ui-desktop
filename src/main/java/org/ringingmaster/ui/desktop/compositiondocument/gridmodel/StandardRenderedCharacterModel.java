package org.ringingmaster.ui.desktop.compositiondocument.gridmodel;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.util.javafx.grid.model.AdditionalStyleType;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static org.ringingmaster.util.javafx.grid.model.AdditionalStyleType.SUPERSCRIPT;
import static org.ringingmaster.util.javafx.grid.model.AdditionalStyleType.WIGGLY_UNDERLINE;

public class StandardRenderedCharacterModel implements CharacterModel {
    private int index;
    private final ParsedCell parsedCell;
    private final CompositionStyle compositionStyle;


    public StandardRenderedCharacterModel(int index, ParsedCell parsedCell, CompositionStyle compositionStyle) {
        this.index = index;
        this.parsedCell = parsedCell;
        this.compositionStyle = compositionStyle;
    }

    @Override
    public char getCharacter() {
        return parsedCell.get(index);
    }

    @Override
    public Font getFont() {
        Optional<Section> sectionAtElementIndex = parsedCell.getSectionAtElementIndex(index);
        if (sectionAtElementIndex.isPresent() && sectionAtElementIndex.get().getParseType() == ParseType.VARIANCE_DETAIL) {
            return compositionStyle.getFont(CompositionStyle.CompositionStyleFont.MAIN_VARIANCE);
        } else {
            return compositionStyle.getFont(CompositionStyle.CompositionStyleFont.MAIN);
        }
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
        EnumSet<AdditionalStyleType> style = EnumSet.noneOf(AdditionalStyleType.class);
        Optional<Group> groupAtElementIndex = parsedCell.getGroupAtElementIndex(index);
        if (groupAtElementIndex.isPresent()) {
            if (!groupAtElementIndex.get().isValid()) {
                style.add(WIGGLY_UNDERLINE);
            }
        } else {
            if (parsedCell.get(index) != ' ') {
                style.add(WIGGLY_UNDERLINE);
            }
        }

        Optional<Section> sectionAtElementIndex = parsedCell.getSectionAtElementIndex(index);
        if (sectionAtElementIndex.isPresent() && sectionAtElementIndex.get().getParseType() == ParseType.VARIANCE_DETAIL) {
            style.add(SUPERSCRIPT);
        }
        return style;
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
}