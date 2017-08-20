package org.ringingmaster.ui.desktop.edit;

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
public class CopyEvent extends SkeletalEventDefinition implements EventDefinition {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public CopyEvent() {
		super("/images/copy.png", "Copy");
		tooltipTextProperty().setValue("Copy");
	}

	@Override
	public void handle(ActionEvent event) {
		//TODO
	}

}
