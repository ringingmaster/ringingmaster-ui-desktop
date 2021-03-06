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
public class OpenDocumentEvent extends SkeletalEventDefinition implements EventDefinition {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private DocumentManager documentManager;

    public OpenDocumentEvent() {
        super("/images/open_file.png", "Open");
        tooltipTextProperty().setValue("Open Composition");
        disableProperty().set(false);
    }

    @Override
    public void handle(ActionEvent event) {
        documentManager.chooseAndOpenDocument();
    }

    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }
}