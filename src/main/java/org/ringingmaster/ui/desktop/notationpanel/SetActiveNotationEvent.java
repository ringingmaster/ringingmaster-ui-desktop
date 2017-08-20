package org.ringingmaster.ui.desktop.notationpanel;

import org.ringingmaster.fxutils.events.EventDefinition;
import org.ringingmaster.fxutils.events.SkeletalEventDefinition;
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
public class SetActiveNotationEvent  extends SkeletalEventDefinition implements EventDefinition {

	public static final String TOOLTIP_BAST_TEXT = "Set active method";

	private TouchDocumentTypeManager touchDocumentTypeManager;
	private PropertyNotationPanel propertyNotationPanel;

	public SetActiveNotationEvent() {
		super("/images/flag.png", TOOLTIP_BAST_TEXT);
		tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
	}

	@Override
	public void handle(ActionEvent event) {
		int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
		NotationBody selectedNotation = propertyNotationPanel.getNotation(index);

		if (selectedNotation != null) {
			if (touchDocumentTypeManager.getCurrentDocument().get().isSpliced()) {
				touchDocumentTypeManager.getCurrentDocument().get().setSpliced(false);
				touchDocumentTypeManager.getCurrentDocument().get().setSingleMethodActiveNotation(selectedNotation);
			}
			else {
				// Not Spliced
				if (touchDocumentTypeManager.getCurrentDocument().get().getSingleMethodActiveNotation() == selectedNotation) {
					touchDocumentTypeManager.getCurrentDocument().get().setSpliced(true);
				}
				else {
					touchDocumentTypeManager.getCurrentDocument().get().setSingleMethodActiveNotation(selectedNotation);
				}
			}
		}
	}

	public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
		this.propertyNotationPanel = propertyNotationPanel;

		propertyNotationPanel.addListener(selectedNotation -> {
				Optional<TouchDocument> currentDocument = touchDocumentTypeManager.getCurrentDocument();
				disableProperty().set(!selectedNotation.isPresent() ||
						!currentDocument.isPresent() ||
						!currentDocument.get().getSortedValidNotations().contains(selectedNotation.get()));

				pressedProperty().set(selectedNotation.isPresent() &&
						currentDocument.isPresent() &&
						currentDocument.get().getSingleMethodActiveNotation() == selectedNotation.get());

				if (selectedNotation.isPresent() &&
					currentDocument.isPresent()){
					if (!currentDocument.get().isSpliced() &&
							currentDocument.get().getSingleMethodActiveNotation() == selectedNotation.get()) {
						tooltipTextProperty().setValue("Set Spliced");
					} else {
						tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
					}
				} else {
					tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
				}
			}
		);

	}

	public void setTouchDocumentTypeManager(TouchDocumentTypeManager touchDocumentTypeManager) {
		this.touchDocumentTypeManager = touchDocumentTypeManager;
	}
}
