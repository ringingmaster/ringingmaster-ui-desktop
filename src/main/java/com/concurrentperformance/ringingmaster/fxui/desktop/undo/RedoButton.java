package com.concurrentperformance.ringingmaster.fxui.desktop.undo;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
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
public class RedoButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Image IMAGE = new Image(RedoButton.class.getResourceAsStream("/images/redo.png"));

	public RedoButton() {
		super("", new ImageView(IMAGE));
		setTooltip(new Tooltip("Redo"));// TODO more informative Redo M=message

		setOnAction(event -> DocumentManager.buildNewDocument());


	}

}
