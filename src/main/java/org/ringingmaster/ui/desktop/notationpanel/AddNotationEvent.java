package org.ringingmaster.ui.desktop.notationpanel;

import javafx.event.ActionEvent;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.ui.desktop.documentmodel.TouchDocumentTypeManager;
import org.ringingmaster.ui.desktop.notationeditor.NotationEditorDialogFactory;
import org.ringingmaster.util.javafx.events.EventDefinition;
import org.ringingmaster.util.javafx.events.SkeletalEventDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AddNotationEvent extends SkeletalEventDefinition implements EventDefinition {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private TouchDocumentTypeManager touchDocumentTypeManager;
	private NotationEditorDialogFactory notationEditorDialogFactory;

	public AddNotationEvent() {
		super("/images/add.png", "Add Method");
		tooltipTextProperty().setValue("Add a new method");
	}

	@Override
	public void handle(ActionEvent event) {
		if (!touchDocumentTypeManager.getCurrentDocument().isPresent()) {
			return;
		}
		NumberOfBells numberOfBells = touchDocumentTypeManager.getCurrentDocument().get().getNumberOfBells();

//TODO		notationEditorDialogFactory.newNotationShowDialog(numberOfBells, result -> {
//			log.info("AddMethodButton - adding", result.toString());
//			return touchDocumentTypeManager.getCurrentDocument().get().addNotation(result) == Touch.Mutated.MUTATED;
//		});
	}

	public void setTouchDocumentTypeManager(TouchDocumentTypeManager touchDocumentTypeManager) {
		this.touchDocumentTypeManager = touchDocumentTypeManager;
		touchDocumentTypeManager.addListener(document -> disableProperty().set(!document.isPresent()));
	}

	public void setNotationEditorDialogFactory(NotationEditorDialogFactory notationEditorDialogFactory) {
		this.notationEditorDialogFactory = notationEditorDialogFactory;
	}
}
