package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.fxutils.events.EventDefinition;
import com.concurrentperformance.fxutils.events.SkeletalEventDefinition;
import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor.NotationEditorDialogBuilder;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AddNotationEvent extends SkeletalEventDefinition implements EventDefinition {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private DocumentManager documentManager;
	private NotationEditorDialogBuilder notationEditorDialogBuilder;

	public AddNotationEvent() {
		super("/images/add.png", "Add Method");
		tooltipTextProperty().setValue("Add a new method");
	}

	@Override
	public void handle(ActionEvent event) {
		if (!documentManager.getCurrentDocument().isPresent()) {
			return;
		}
		NumberOfBells numberOfBells = documentManager.getCurrentDocument().get().getNumberOfBells();

		notationEditorDialogBuilder.newNotationShowDialog(numberOfBells, result -> {
			log.info("AddMethodButton - adding", result.toString());
			return documentManager.getCurrentDocument().get().addNotation(result);
		});
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
		documentManager.addListener(touchDocument -> disableProperty().set(!touchDocument.isPresent()));
	}

	public void setNotationEditorDialogBuilder(NotationEditorDialogBuilder notationEditorDialogBuilder) {
		this.notationEditorDialogBuilder = notationEditorDialogBuilder;
	}
}