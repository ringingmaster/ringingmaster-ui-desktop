package org.ringingmaster.ui.desktop.notationeditor;


import org.ringingmaster.util.javafx.dialog.EditMode;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class CallPointRow extends SkeletalNotationEditorTabController implements NotationEditorTabController {

	@Override
	public String getTabName() {
		return "Row Call Points";
	}

	@Override
	public void init(NotationEditorDialog parent, EditMode editMode) {
		super.init(parent, editMode);

	}

	@Override
	public void buildDialogDataFromNotation(NotationBody notation) {

	}

	@Override
	public void buildNotationFromDialogData(NotationBuilder notationBuilder) {

	}
}