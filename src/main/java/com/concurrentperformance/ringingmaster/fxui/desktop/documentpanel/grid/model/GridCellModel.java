package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public interface GridCellModel extends GridCharacterGroup {

	void insertCharacter(int index, char character);
	void removeCharacter(int index);

}
