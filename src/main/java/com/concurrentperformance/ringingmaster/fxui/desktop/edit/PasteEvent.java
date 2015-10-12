package com.concurrentperformance.ringingmaster.fxui.desktop.edit;

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
