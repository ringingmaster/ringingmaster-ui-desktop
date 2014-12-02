package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class NotationEditor extends VBox {
	//@FXML private TextField textField; //TODO remove example

	public NotationEditor() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/NotationEditor.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

//	public String getText() {
//		return textProperty().get();
//	}
//
//	public void setText(String value) {
//		textProperty().set(value);
//	}

	//public StringProperty textProperty() {
//		return textField.textProperty();
//	}

	@FXML
	protected void doSomething() {
		System.out.println("The button was clicked!");
	}
}