package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO Comments
 *
 * @author Lake
 */
public abstract class SkeletalNotationEditorTabController implements NotationEditorTabController {

	protected NotationEditorDialog parent;

	@Override
	public void init(NotationEditorDialog parent, NotationEditorEditMode editMode) {
		checkState(this.parent == null, "Don't init more than once");
		this.parent = checkNotNull(parent);
	}
}
