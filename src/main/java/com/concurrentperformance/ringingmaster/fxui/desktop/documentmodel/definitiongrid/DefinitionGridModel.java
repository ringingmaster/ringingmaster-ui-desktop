package com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.definitiongrid;

import com.concurrentperformance.ringingmaster.engine.touch.container.TouchDefinition;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCellModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterGroup;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.SkeletalGridModel;
import com.concurrentperformance.ringingmaster.ui.common.TouchStyle;
import javafx.scene.paint.Color;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DefinitionGridModel extends SkeletalGridModel implements GridModel {

	private final GridCharacterGroup gridCharacterGroup;
	private final GridCellModel gridCharacterModel;


	private final TouchDocument touchDocument;

	public DefinitionGridModel(TouchDocument touchDocument, TouchDefinition definition) {
		this.touchDocument = checkNotNull(touchDocument);
		checkNotNull(definition);
		gridCharacterGroup = new DefinitionName(getListeners(),touchDocument, definition);
		gridCharacterModel = new DefinitionCell(getListeners(), touchDocument, definition);
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Color getGridColor() {
		return touchDocument.getTouchStyle().getColour(TouchStyle.TouchStyleColor.GRID);
	}

	@Override
	public GridCellModel getCellModel(int column, int row) {
		checkState(column == 0);
		checkState(row == 0);
		return gridCharacterModel;
	}

	@Override
	public GridCharacterGroup getRowHeader(int row) {
		checkState(row == 0);
		return gridCharacterGroup;
	}

}