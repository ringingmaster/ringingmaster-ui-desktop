package com.concurrentperformance.ringingmaster.ui.desktop.notationeditor;

import com.concurrentperformance.fxutils.dialog.EditMode;
import javafx.stage.Window;

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
	public void init(NotationEditorDialog parent, EditMode editMode) {
		checkState(this.parent == null, "Don't init more than once");
		this.parent = checkNotNull(parent);
	}

	Window getOwner() {
		return parent.getStage();
	}
}
