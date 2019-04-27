package org.ringingmaster.ui.desktop.documentpanel.grid.model;

import com.google.common.collect.ImmutableSet;

import java.util.Iterator;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class EmptyGridCharacterGroup implements GridCharacterGroup {
    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public GridCharacterModel getGridCharacterModel(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<GridCharacterModel> iterator() {
        return ImmutableSet.<GridCharacterModel>of().iterator();
    }
}
