package com.concurrentperformance.ringingmaster.fxui.desktop.edit;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class PasteButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final Image IMAGE = new Image(PasteButton.class.getResourceAsStream("/images/paste.png"));

	public PasteButton() {
		super("", new ImageView(IMAGE));
		setTooltip(new Tooltip("Paste"));
		setDisable(true);


		//TODO setOnAction(event -> documentManager.buildNewDocument());
	}
}
