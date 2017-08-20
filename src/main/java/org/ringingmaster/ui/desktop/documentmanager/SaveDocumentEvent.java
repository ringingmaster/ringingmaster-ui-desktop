package org.ringingmaster.ui.desktop.documentmanager;

import org.ringingmaster.fxutils.events.EventDefinition;
import org.ringingmaster.fxutils.events.SkeletalEventDefinition;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class SaveDocumentEvent extends SkeletalEventDefinition implements EventDefinition {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private DocumentManager documentManager;

	public SaveDocumentEvent() {
		super("/images/save.png", "Save");
		tooltipTextProperty().setValue("Save Touch");
	}

	@Override
	public void handle(ActionEvent event) {
		documentManager.saveCurrentDocument();
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
		documentManager.addListener(document -> disableProperty().set(document == null));
	}
}