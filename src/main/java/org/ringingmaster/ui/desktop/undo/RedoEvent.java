package org.ringingmaster.ui.desktop.undo;

import javafx.event.ActionEvent;
import org.ringingmaster.util.javafx.events.EventDefinition;
import org.ringingmaster.util.javafx.events.SkeletalEventDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class RedoEvent extends SkeletalEventDefinition implements EventDefinition {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public RedoEvent() {
        super("/images/redo.png", "Redo");
        tooltipTextProperty().setValue("Redo");
    }

    @Override
    public void handle(ActionEvent event) {
        //TODO
    }

}
