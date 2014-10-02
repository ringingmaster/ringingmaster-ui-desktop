package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public interface GridCharacterGroup extends Iterable<GridCharacterModel>  {

	public int getLength();

	public GridCharacterModel getGridCharacterModel(int index);
}
