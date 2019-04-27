package org.ringingmaster.ui.desktop.notationeditor;

import javafx.stage.Window;
import org.ringingmaster.util.javafx.dialog.EditMode;

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
