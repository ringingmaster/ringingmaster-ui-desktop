package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;

/**
 * TODO Comments
 *
 * @author Lake
 */
public interface NotationEditorTabController {

	String getTabName();
	void init(NotationEditorDialog parent, NotationEditorEditMode editMode);
	void buildDialogDataFromNotation(NotationBody notation);
	void buildNotationFromDialogData(NotationBuilder notationBuilder);
}
