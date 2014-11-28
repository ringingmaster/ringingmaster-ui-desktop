package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DeleteMethodButton extends Button {

	private static Image IMAGE = new Image(DeleteMethodButton.class.getResourceAsStream("/images/delete.png"));

	public DeleteMethodButton() {
		super("", new ImageView(IMAGE));
	}

}
