package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;

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
	public void init(NotationBody notation, NotationEditorDialog parent, NotationEditorDialog.EditMode editMode) {
		super.init(notation, parent, editMode);

	}

	@Override
	public void build(NotationBuilder notationBuilder) {

	}
}