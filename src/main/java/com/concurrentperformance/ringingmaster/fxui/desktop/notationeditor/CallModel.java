package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CallModel {

	private String callName = "";
	private String callShorthand = "";
	private String notation = "";
	private String selected = ""; //TODO boolean?

	public CallModel() {
	}

	public CallModel(String callName,String callShorthand, String notation, String selected) {
		setCallName(callName);
		setCallShorthand(callShorthand);
		setNotation(notation);
		setSelected(selected);
	}

	public String getCallName() {
		return callName;
	}

	public void setCallName(String callName) {
		this.callName = callName;
	}

	public String getCallShorthand() {
		return callShorthand;
	}

	public void setCallShorthand(String callShorthand) {
		this.callShorthand = callShorthand;
	}

	public String getNotation() {
		return notation;
	}

	public void setNotation(String notation) {
		this.notation = notation;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
}
