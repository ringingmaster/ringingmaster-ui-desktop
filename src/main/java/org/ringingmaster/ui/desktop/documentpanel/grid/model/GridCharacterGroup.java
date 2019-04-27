package org.ringingmaster.ui.desktop.documentpanel.grid.model;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public interface GridCharacterGroup extends Iterable<GridCharacterModel> {

    int getLength();

    GridCharacterModel getGridCharacterModel(int index);
}
