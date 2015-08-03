package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor.NotationEditorDialogBuilder;
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
public class AddNotationButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final Image IMAGE = new Image(AddNotationButton.class.getResourceAsStream("/images/add.png"));

	private NotationEditorDialogBuilder notationEditorDialogBuilder;

	public AddNotationButton() {
		super("", new ImageView(IMAGE));
		setTooltip(new Tooltip("Add a new method"));
		setDisable(true);
	}

	public void setDocumentManager(DocumentManager documentManager) {
		setOnAction(event -> {
			if (!documentManager.getCurrentDocument().isPresent()) {
				return;
			}
			NumberOfBells numberOfBells = documentManager.getCurrentDocument().get().getNumberOfBells();

			notationEditorDialogBuilder.newNotationShowDialog(numberOfBells, result -> {
				log.info("AddMethodButton - adding", result.toString());
				return documentManager.getCurrentDocument().get().addNotation(result);
			});
		});
	}

	public void setNotationEditorDialogBuilder(NotationEditorDialogBuilder notationEditorDialogBuilder) {
		this.notationEditorDialogBuilder = notationEditorDialogBuilder;
	}
}
