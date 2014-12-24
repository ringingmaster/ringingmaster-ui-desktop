package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class CourseHeadPoint extends SkeletalNotationEditorTabController implements NotationEditorTabController {

	@Override
	public String getTabName() {
		return "Course Head Point";
	}

	@Override
	public void init(NotationEditorDialog parent, NotationEditorEditMode editMode) {
		super.init(parent, editMode);

	}

	@Override
	public void buildDialogDataFromNotation(NotationBody notation) {

	}

	@Override
	public void buildNotationFromDialogData(NotationBuilder notationBuilder) {

	}
}