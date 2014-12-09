package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class PlainCourse {

	@FXML
	private TextField name;
	@FXML
	private TextField shorthand;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@FXML
	protected void doSomething() {
		log.info("The button was clicked!");
	}

	public void setNotation(NotationBody notation) {
		name.setText(notation.getName());
		shorthand.setText(notation.getSpliceIdentifier());
	}

}