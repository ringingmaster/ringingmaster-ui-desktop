package org.ringingmaster.ui.desktop.documentmodel.maingrid;

import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.ui.desktop.documentmodel.CompositionDocument;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCellModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCharacterModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridModelListener;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.SkeletalGridCellModel;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class StandardCell extends SkeletalGridCellModel implements GridCellModel {

    private final CompositionDocument compositionDocument;
    private final Cell cell;

    public StandardCell(List<GridModelListener> listeners, CompositionDocument compositionDocument, Cell cell) {
        super(listeners);
        this.compositionDocument = checkNotNull(compositionDocument);
        this.cell = checkNotNull(cell);
    }

    @Override
    public int getLength() {
        return cell.getElementSize();
    }

    @Override
    public void insertCharacter(int index, char character) {
        //TODO Reactive
//		cell.insert(character, index);
//		compositionDocument.setUpdatePoint(() -> "Typing", Composition.Mutated.MUTATED);
//		fireCellStructureChanged();
    }

    @Override
    public void removeCharacter(int index) {
        //TODO Reactive
//		cell.remove(index);
//		compositionDocument.collapseEmptyRowsAndColumns();
//		compositionDocument.setUpdatePoint(() -> "Delete", Composition.Mutated.MUTATED);
//		fireCellStructureChanged();
    }

    @Override
    public GridCharacterModel getGridCharacterModel(final int index) {
        //TODO Reactive
        return null;
//		return new GridCharacterModel() {
//			@Override
//			public char getCharacter() {
//				return cell.getElement(index).getCharacter();
//			}
//
//			@Override
//			public Font getFont() {
//				return compositionDocument.getCompositionStyle().getFont(CompositionStyle.CompositionStyleFont.MAIN);
//			}
//
//			@Override
//			public Color getColor() {
//				ParseType parseType = cell.getElement(index).getParseType();
//				return compositionDocument.getCompositionStyle().getColourFromParseType(parseType);
//			}
//			@Override
//			public Set<AdditionalStyleType> getAdditionalStyle() {
//				ParseType parseType = cell.getElement(index).getParseType();
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
//				String tooltipText = cell.getElement(index).getParseType().name();
//				return Optional.of(tooltipText);
//			}
//		};
    }
}