package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor.NotationEditorDialogBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AddMethodButton extends Button {

	private static Image IMAGE = new Image(AddMethodButton.class.getResourceAsStream("/images/add_method.png"));

	public AddMethodButton() {
		super("", new ImageView(IMAGE));

		setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				NotationEditorDialogBuilder.createInstance(TouchDocument.buildPlainBobMajor());
			}

		});
	}

}
