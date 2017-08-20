package org.ringingmaster.ui.desktop.undo;

import org.ringingmaster.util.javafx.events.EventDefinition;
import org.ringingmaster.util.javafx.events.SkeletalEventDefinition;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class UndoEvent extends SkeletalEventDefinition implements EventDefinition {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public UndoEvent() {
		super("/images/undo.png", "Undo");
		tooltipTextProperty().setValue("Undo");
	}

	@Override
	public void handle(ActionEvent event) {
		//TODO
	}

}
