package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AddMethodButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static Image IMAGE = new Image(AddMethodButton.class.getResourceAsStream("/images/add_method.png"));

	public AddMethodButton() {
		super("", new ImageView(IMAGE));

		setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				log.info(event.toString());
				NotationEditor notationEditor = new NotationEditor();
				notationEditor.setText("Hello World!");
				getChildren().add(notationEditor);
			}


		});
	}

}
