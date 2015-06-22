package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

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
public class NewDocumentButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Image IMAGE = new Image(NewDocumentButton.class.getResourceAsStream("/images/new_file.png"));

	public NewDocumentButton() {
		super("", new ImageView(IMAGE));

		setOnAction(event -> DocumentManager.buildNewDocument());
	}

}
