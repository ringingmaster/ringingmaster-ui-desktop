package com.concurrentperformance.ringingmaster.ui.desktop.notationeditor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CallModel {

	public CallModel() {
	}

	public CallModel(String callName,String callShorthand, String notation, String selected) {
		setCallName(callName);
		setCallShorthand(callShorthand);
		setNotation(notation);
		setSelected(selected);
	}

	private StringProperty callName = new SimpleStringProperty(this, "callName");
	public void setCallName(String value) { callName.set(value); }
	public String getCallName() { return callName.get(); }
	public StringProperty callNameProperty() {return callName;}

	private StringProperty callShorthand = new SimpleStringProperty(this, "callShorthand");
	public void setCallShorthand(String value) { callShorthand.set(value); }
	public String getCallShorthand() { return callShorthand.get(); }
	public StringProperty callShorthandProperty() {return callShorthand;}

	private StringProperty notation = new SimpleStringProperty(this, "notation");
	public void setNotation(String value) { notation.set(value); }
	public String getNotation() { return notation.get(); }
	public StringProperty notationProperty() {return notation;}

	private StringProperty selected = new SimpleStringProperty(this, "selected");
	public void setSelected(String value) { selected.set(value); }
	public String getSelected() { return selected.get(); }
	public StringProperty selectedProperty() {
		return selected;
	}

	@Override
	public String toString() {
		return "CallModel{" +
				"callName=" + getCallName() +
				", callShorthand=" + getCallShorthand() +
				", notation=" + getNotation() +
				", selected=" + getSelected() +
				'}';
	}
}
