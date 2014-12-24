package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

/**
 * TODO Comments
 *
 * @author Lake
 */
public enum NotationEditorEditMode {

	ADD_NOTATION("Add"),
	EDIT_NOTATION("Editing")
	;

	private final String editText;

	NotationEditorEditMode(String editText) {
		this.editText = editText;
	}

	public String getEditText() {
		return editText;
	}
}
