package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor.NotationEditorDialogBuilder;
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
public class AddNotationButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Image IMAGE = new Image(AddNotationButton.class.getResourceAsStream("/images/add.png"));

	public AddNotationButton() {
		super("", new ImageView(IMAGE));

		setOnAction(event -> {
			NumberOfBells numberOfBells = DocumentManager.getCurrentDocument().getNumberOfBells();

			NotationEditorDialogBuilder.newNotationShowDialog(numberOfBells, result -> {
				log.info("AddMethodButton - adding", result.toString());
				return DocumentManager.getCurrentDocument().addNotation(result);
			});
		});
	}

}
