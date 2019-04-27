package org.ringingmaster.ui.desktop.edit;

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
public class CutEvent extends SkeletalEventDefinition implements EventDefinition {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public CutEvent() {
        super("/images/cut.png", "Cut");
        tooltipTextProperty().setValue("Cut");
    }

    @Override
    public void handle(ActionEvent event) {
        //TODO
    }

}
