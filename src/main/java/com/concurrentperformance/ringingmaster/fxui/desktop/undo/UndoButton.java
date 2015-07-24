package com.concurrentperformance.ringingmaster.fxui.desktop.undo;

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
public class UndoButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final Image IMAGE = new Image(UndoButton.class.getResourceAsStream("/images/undo.png"));

	public UndoButton() {
		super("", new ImageView(IMAGE));
		setTooltip(new Tooltip("Undo"));// TODO more informative Redo M=message

		//TODO setOnAction(event -> DocumentManager.buildNewDocument());
	}

}
