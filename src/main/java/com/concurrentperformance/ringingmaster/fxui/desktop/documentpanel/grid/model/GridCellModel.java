package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public interface GridCellModel extends GridCharacterGroup {

	public void insertCharacter(int index, char character);
	public void removeCharacter(int index);

}
