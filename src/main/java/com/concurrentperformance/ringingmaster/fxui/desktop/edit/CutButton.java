package com.concurrentperformance.ringingmaster.fxui.desktop.edit;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
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
public class CutButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Image IMAGE = new Image(CutButton.class.getResourceAsStream("/images/cut.png"));

	public CutButton() {
		super("", new ImageView(IMAGE));

		setOnAction(event -> DocumentManager.buildNewDocument());
	}

}
