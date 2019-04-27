package org.ringingmaster.ui.desktop.notationpanel;

import org.ringingmaster.util.javafx.events.EventDefinition;
import org.ringingmaster.util.javafx.events.SkeletalEventDefinition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.ui.desktop.documentmodel.CompositionDocument;
import org.ringingmaster.ui.desktop.documentmodel.CompositionDocumentTypeManager;
import javafx.event.ActionEvent;

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class SetActiveNotationEvent  extends SkeletalEventDefinition implements EventDefinition {

	public static final String TOOLTIP_BAST_TEXT = "Set active method";

	private CompositionDocumentTypeManager compositionDocumentTypeManager;
	private PropertyNotationPanel propertyNotationPanel;

	public SetActiveNotationEvent() {
		super("/images/flag.png", TOOLTIP_BAST_TEXT);
		tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
	}

	@Override
	public void handle(ActionEvent event) {
		int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
		Notation selectedNotation = propertyNotationPanel.getNotation(index);

		if (selectedNotation != null) {
			if (compositionDocumentTypeManager.getCurrentDocument().get().isSpliced()) {
				compositionDocumentTypeManager.getCurrentDocument().get().setSpliced(false);
				compositionDocumentTypeManager.getCurrentDocument().get().setSingleMethodActiveNotation(selectedNotation);
			}
			else {
				// Not Spliced
				if (compositionDocumentTypeManager.getCurrentDocument().get().getSingleMethodActiveNotation() == selectedNotation) {
					compositionDocumentTypeManager.getCurrentDocument().get().setSpliced(true);
				}
				else {
					compositionDocumentTypeManager.getCurrentDocument().get().setSingleMethodActiveNotation(selectedNotation);
				}
			}
		}
	}

	public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
		this.propertyNotationPanel = propertyNotationPanel;

		propertyNotationPanel.addListener(selectedNotation -> {
				Optional<CompositionDocument> currentDocument = compositionDocumentTypeManager.getCurrentDocument();
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

	public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
		this.compositionDocumentTypeManager = compositionDocumentTypeManager;
	}
}
