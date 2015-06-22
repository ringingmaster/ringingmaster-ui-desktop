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
public class SaveDocumentButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Image IMAGE = new Image(SaveDocumentButton.class.getResourceAsStream("/images/save.png"));

	public SaveDocumentButton() {
		super("", new ImageView(IMAGE));

		//TODO setOnAction(event -> DocumentManager.buildNewDocument());
	}

}
