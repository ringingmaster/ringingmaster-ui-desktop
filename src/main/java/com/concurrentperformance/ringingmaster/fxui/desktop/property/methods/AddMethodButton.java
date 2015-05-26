package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor.NotationEditorDialogBuilder;
import javafx.application.Platform;
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

				NumberOfBells numberOfBells = DocumentManager.getInstance().getCurrentDocument().getNumberOfBells();
				NotationBody result = NotationEditorDialogBuilder.showDialog(numberOfBells);
//				NotationEditorDialogBuilder.showDialog(TouchDocument.buildPlainBobMajor());

				Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().addNotation(result));

				log.info(result.toString());
			}

		});
	}

}
