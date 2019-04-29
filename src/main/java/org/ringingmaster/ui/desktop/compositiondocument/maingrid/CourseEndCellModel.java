package org.ringingmaster.ui.desktop.compositiondocument.maingrid;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.AdditionalStyleType;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.GridModelListener;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class CourseEndCellModel extends SkeletalCellModel implements CellModel {

    private final CompositionDocument compositionDocument;

    private final int row;

    public CourseEndCellModel(List<GridModelListener> listeners,
                              CompositionDocument compositionDocument,
                              int row) {
        super(listeners);
        this.compositionDocument = compositionDocument;
        this.row = row;
    }

    @Override
    public int getLength() {
        return (row >= compositionDocument.getRowSize()) ? 0 : 8;
    }

    @Override
    public CharacterModel getCharacterModel(int index) {
        return new CharacterModel() {
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
    public void insertCharacter(int index, String character) {
        throw new IllegalStateException("Attempt to call insertCharacter on read only CourseEndCellModel");
    }

    @Override
    public void removeCharacter(int index) {
        throw new IllegalStateException("Attempt to call removeCharacter on read only CourseEndCellModel");
    }

}
