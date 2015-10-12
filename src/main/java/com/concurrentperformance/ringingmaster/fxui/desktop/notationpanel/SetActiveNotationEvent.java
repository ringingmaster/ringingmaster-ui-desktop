package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.fxutils.events.EventDefinition;
import com.concurrentperformance.fxutils.events.SkeletalEventDefinition;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import javafx.event.ActionEvent;

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class SetActiveNotationEvent  extends SkeletalEventDefinition implements EventDefinition {

	public static final String TOOLTIP_BAST_TEXT = "Set active method";

	private DocumentManager documentManager;
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
			if (documentManager.getCurrentDocument().get().isSpliced()) {
				documentManager.getCurrentDocument().get().setSpliced(false);
				documentManager.getCurrentDocument().get().setSingleMethodActiveNotation(selectedNotation);
			}
			else {
				// Not Spliced
				if (documentManager.getCurrentDocument().get().getSingleMethodActiveNotation() == selectedNotation) {
					documentManager.getCurrentDocument().get().setSpliced(true);
				}
				else {
					documentManager.getCurrentDocument().get().setSingleMethodActiveNotation(selectedNotation);
				}
			}
		}
	}

	public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
		this.propertyNotationPanel = propertyNotationPanel;

		propertyNotationPanel.addListener(selectedNotation -> {
				Optional<TouchDocument> currentDocument = documentManager.getCurrentDocument();
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

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}
}
