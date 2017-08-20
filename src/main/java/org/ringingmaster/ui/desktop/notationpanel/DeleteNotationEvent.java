package org.ringingmaster.ui.desktop.notationpanel;

import org.ringingmaster.util.javafx.events.EventDefinition;
import org.ringingmaster.util.javafx.events.SkeletalEventDefinition;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.ui.desktop.documentmodel.TouchDocument;
import org.ringingmaster.ui.desktop.documentmodel.TouchDocumentTypeManager;
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
