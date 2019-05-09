package org.ringingmaster.ui.desktop.notationeditor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class CallModel {

    public CallModel() {
    }

    public CallModel(String callName, String callShorthand, String notation, Boolean defaultCall) {
        setCallName(callName);
        setCallShorthand(callShorthand);
        setNotation(notation);
        setDefaultCall(defaultCall);
    }

    private StringProperty callName = new SimpleStringProperty(this, "callName");

    public void setCallName(String value) {
        callName.set(value);
    }

    public String getCallName() {
        return callName.get();
    }

    public StringProperty callNameProperty() {
        return callName;
    }

    private StringProperty callShorthand = new SimpleStringProperty(this, "callShorthand");

    public void setCallShorthand(String value) {
        callShorthand.set(value);
    }

    public String getCallShorthand() {
        return callShorthand.get();
    }

    public StringProperty callShorthandProperty() {
        return callShorthand;
    }

    private StringProperty notation = new SimpleStringProperty(this, "notation");

    public void setNotation(String value) {
        notation.set(value);
    }

    public String getNotation() {
        return notation.get();
    }

    public StringProperty notationProperty() {
        return notation;
    }

    private BooleanProperty defaultCall = new SimpleBooleanProperty(this, "defaultCall");

    public void setDefaultCall(Boolean value) {
        defaultCall.set(value);
    }

    public Boolean getDefaultCall() {
        return defaultCall.get();
    }

    public BooleanProperty defaultCallProperty() {
        return defaultCall;
    }

    @Override
    public String toString() {
        return "CallModel{" +
                "callName=" + getCallName() +
                ", callShorthand=" + getCallShorthand() +
                ", notation=" + getNotation() +
                ", defaultCall=" + getDefaultCall() +
                '}';
    }
}
