package org.ringingmaster.ui.desktop.compositiondocument.definitiongrid;

import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.grid.model.CellModel;
import org.ringingmaster.util.javafx.grid.model.CharacterModel;
import org.ringingmaster.util.javafx.grid.model.SkeletalCellModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DefinitionDefinitionCell extends SkeletalCellModel implements CellModel {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CompositionDocument compositionDocument;
    private final Cell cell;

    public DefinitionDefinitionCell(CompositionDocument compositionDocument, Cell cell) {
        this.compositionDocument = checkNotNull(compositionDocument);
        this.cell = checkNotNull(cell);
    }

    @Override
    public int getLength() {
        //TODO Reactive return cell.getLength();
        return 1;
    }

    @Override
    public void insertCharacter(int index, String character) {
        //TODO Reactive cell.insert(character, index);
        compositionDocument.setUpdatePoint(() -> "Typing", true);
    }

    @Override
    public void removeCharacter(int index) {
//TODO Reactive 		cell.remove(index);
        compositionDocument.setUpdatePoint(() -> "Delete", true);
    }

    @Override
    public CharacterModel getCharacterModel(final int index) {
//		return new CharacterModel() {
//			@Override
//			public char getCharacter() {
//				//TODO Reactive return cell.get(index).getCharacter();
//				return 'p';
//			}
//
//			@Override
//			public Font getFont() {
//				return compositionDocument.getCompositionStyle().getFont(CompositionStyle.CompositionStyleFont.DEFINITION);
//			}
//
//			@Override
//			public Color getColor() {
//				ParseType parseType = cell.get(index)..getParseType();
//				return compositionDocument.getCompositionStyle().getColourFromParseType(parseType);
//			}
//			@Override
//			public Set<AdditionalStyleType> getAdditionalStyle() {
//				ParseType parseType = cell.get(index).getParseType();
//				if (parseType == ParseType.UNPARSED) {
//					return EnumSet.of(AdditionalStyleType.WIGGLY_UNDERLINE);
//				}
//				return Collections.emptySet();
//			}
//
//			@Override
//			public Optional<String> getTooltipText() {
//				if (index >= cell.getLength()) {
//					return Optional.empty();
//				}
//
//				String tooltipText = cell.get(index).getParseType().name();
//				return Optional.of(tooltipText);
//			}
//		};
        //TODO Reactive
        return null;
    }
}