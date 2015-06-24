package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

/**
 * TODO Comments
 *
 * @author Lake
 */
public enum NotationEditorEditMode {

	ADD_NOTATION("Add"),
	EDIT_NOTATION("Edit")
	;

	private final String editText;

	NotationEditorEditMode(String editText) {
		this.editText = editText;
	}

	public String getEditText() {
		return editText;
	}
}
