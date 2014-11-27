package com.concurrentperformance.ringingmaster.fxui.desktop.property;


import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class NotationEditor extends VBox {
	@FXML private TextField textField;

	public NotationEditor() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/notationeditor.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public String getText() {
		return textProperty().get();
	}

	public void setText(String value) {
		textProperty().set(value);
	}

	public StringProperty textProperty() {
		return textField.textProperty();
	}

	@FXML
	protected void doSomething() {
		System.out.println("The button was clicked!");
	}
}