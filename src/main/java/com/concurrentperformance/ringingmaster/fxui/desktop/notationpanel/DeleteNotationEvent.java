package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.fxutils.events.EventDefinition;
import com.concurrentperformance.fxutils.events.SkeletalEventDefinition;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocumentTypeManager;
import javafx.event.ActionEvent;

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DeleteNotationEvent extends SkeletalEventDefinition implements EventDefinition {

	public static final String TOOLTIP_BAST_TEXT = "Remove method";

	private TouchDocumentTypeManager touchDocumentTypeManager;
	private PropertyNotationPanel propertyNotationPanel;


	public DeleteNotationEvent() {
		super("/images/remove.png", TOOLTIP_BAST_TEXT);
		tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
	}

	@Override
	public void handle(ActionEvent event) {
		int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
		NotationBody notation =  propertyNotationPanel.getNotation(index);
		Optional<TouchDocument> currentDocument = touchDocumentTypeManager.getCurrentDocument();
		if (currentDocument.isPresent()) {
			currentDocument.get().removeNotation(notation);
		}
	}

	public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
		this.propertyNotationPanel = propertyNotationPanel;

		propertyNotationPanel.addListener(selectedNotation -> {
			disableProperty().set(!selectedNotation.isPresent());

			if (selectedNotation.isPresent()) {
				tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
			} else {
				tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
			}

		});
	}

	public void setTouchDocumentTypeManager(TouchDocumentTypeManager touchDocumentTypeManager) {
		this.touchDocumentTypeManager = touchDocumentTypeManager;
	}
}
