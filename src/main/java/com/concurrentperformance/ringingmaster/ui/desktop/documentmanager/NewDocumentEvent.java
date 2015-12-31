package com.concurrentperformance.ringingmaster.ui.desktop.documentmanager;

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
public class NewDocumentEvent extends SkeletalEventDefinition implements EventDefinition {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private DocumentManager documentManager;

	public NewDocumentEvent() {
		super("/images/new_file.png", "New");
		disableProperty().set(false);
		tooltipTextProperty().setValue("New Touch");
	}

	@Override
	public void handle(ActionEvent event) {
		documentManager.newDocument();
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}
}