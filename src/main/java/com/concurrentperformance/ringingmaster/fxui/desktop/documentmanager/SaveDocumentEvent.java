package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.fxutils.events.EventDefinition;
import com.concurrentperformance.fxutils.events.SkeletalEventDefinition;
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
	}

	@Override
	public void handle(ActionEvent event) {
		documentManager.saveCurrentDocument();
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
		documentManager.addListener(touchDocument -> disableProperty().set(!touchDocument.isPresent()));
	}
}