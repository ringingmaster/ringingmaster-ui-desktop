package org.ringingmaster.ui.desktop.documentmanager;

import javafx.event.ActionEvent;
import org.ringingmaster.util.javafx.events.EventDefinition;
import org.ringingmaster.util.javafx.events.SkeletalEventDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class NewDocumentEvent extends SkeletalEventDefinition implements EventDefinition {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private DocumentManager documentManager;

    public NewDocumentEvent() {
        super("/images/new_file.png", "New");
        disableProperty().set(false);
        tooltipTextProperty().setValue("New Composition");
    }

    @Override
    public void handle(ActionEvent event) {
        documentManager.createNewDocument();
    }

    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }
}