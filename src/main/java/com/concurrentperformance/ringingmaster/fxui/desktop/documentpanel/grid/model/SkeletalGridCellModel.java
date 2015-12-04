package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public abstract class SkeletalGridCellModel implements GridCellModel {

	private List<GridModelListener> listeners;

	public SkeletalGridCellModel(final List<GridModelListener> listeners) {
		this.listeners = checkNotNull(listeners);
	}

	protected void fireCellStructureChanged() { //TODO rename
		for(GridModelListener listener : listeners) {
			listener.gridModelListener_contentsChanged();
		}
	}


	@Override
	public void insertCharacter(int index, char character) {
		//TODO
	}

	@Override
	public void removeCharacter(int index) {
		//TODO
	}

	@Override
	public Iterator<GridCharacterModel> iterator() {
		return new Iterator<GridCharacterModel>() {
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < getLength();
			}

			@Override
			public GridCharacterModel next() {
				return getGridCharacterModel(index++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
