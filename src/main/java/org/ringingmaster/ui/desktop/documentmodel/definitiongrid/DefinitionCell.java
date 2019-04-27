package org.ringingmaster.ui.desktop.documentmodel.definitiongrid;

import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.ui.desktop.documentmodel.CompositionDocument;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCellModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridCharacterModel;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridModelListener;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.SkeletalGridCellModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DefinitionCell extends SkeletalGridCellModel implements GridCellModel {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CompositionDocument compositionDocument;
    private final Cell cell;

    public DefinitionCell(List<GridModelListener> listeners, CompositionDocument compositionDocument, Cell cell) {
        super(listeners);
        this.compositionDocument = checkNotNull(compositionDocument);
        this.cell = checkNotNull(cell);
    }

    @Override
    public int getLength() {
        //TODO Reactive return cell.getLength();
        return 1;
    }

    @Override
    public void insertCharacter(int index, char character) {
        //TODO Reactive cell.insert(character, index);
        compositionDocument.setUpdatePoint(() -> "Typing", true);
        fireCellStructureChanged();
    }

    @Override
    public void removeCharacter(int index) {
//TODO Reactive 		cell.remove(index);
        compositionDocument.collapseEmptyRowsAndColumns();
        compositionDocument.setUpdatePoint(() -> "Delete", true);
        fireCellStructureChanged();
    }

    @Override
    public GridCharacterModel getGridCharacterModel(final int index) {
//		return new GridCharacterModel() {
//			@Override
//			public char getCharacter() {
//				//TODO Reactive return cell.getElement(index).getCharacter();
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
//				ParseType parseType = cell.getElement(index)..getParseType();
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
        //TODO Reactive
        return null;
    }
}