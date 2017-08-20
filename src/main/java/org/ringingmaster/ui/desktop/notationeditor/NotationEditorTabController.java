package org.ringingmaster.ui.desktop.notationeditor;

import org.ringingmaster.fxutils.dialog.EditMode;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;

/**
 * TODO Comments
 *
 * @author Lake
 */
public interface NotationEditorTabController {

	String getTabName();
	void init(NotationEditorDialog parent, EditMode editMode);
	void buildDialogDataFromNotation(NotationBody notation);
	void buildNotationFromDialogData(NotationBuilder notationBuilder);
}
