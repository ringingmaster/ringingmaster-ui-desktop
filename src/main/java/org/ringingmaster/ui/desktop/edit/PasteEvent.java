package org.ringingmaster.ui.desktop.edit;

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
public class PasteEvent extends SkeletalEventDefinition implements EventDefinition {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public PasteEvent() {
        super("/images/paste.png", "Paste");
        tooltipTextProperty().setValue("Paste");
    }

    @Override
    public void handle(ActionEvent event) {
        //TODO
    }

}
