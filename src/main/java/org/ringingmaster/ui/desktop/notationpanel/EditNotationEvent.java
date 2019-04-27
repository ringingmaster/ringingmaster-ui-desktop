package org.ringingmaster.ui.desktop.notationpanel;

import javafx.event.ActionEvent;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.ui.desktop.documentmodel.CompositionDocumentTypeManager;
import org.ringingmaster.ui.desktop.notationeditor.NotationEditorDialogFactory;
import org.ringingmaster.util.javafx.events.EventDefinition;
import org.ringingmaster.util.javafx.events.SkeletalEventDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class EditNotationEvent extends SkeletalEventDefinition implements EventDefinition {

	private final static Logger log = LoggerFactory.getLogger(EditNotationEvent.class);
	public static final String TOOLTIP_BAST_TEXT = "Edit method";

	private CompositionDocumentTypeManager compositionDocumentTypeManager;
	private PropertyNotationPanel propertyNotationPanel;
	private NotationEditorDialogFactory notationEditorDialogFactory;


	public EditNotationEvent() {
		super("/images/edit.png", TOOLTIP_BAST_TEXT);
		tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
	}

	@Override
	public void handle(ActionEvent event) {

		if (!compositionDocumentTypeManager.getCurrentDocument().isPresent()) {
			return;
		}

		int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
		Notation notation =  propertyNotationPanel.getNotation(index);
		if (notation != null) {
			notationEditorDialogFactory.editNotationShowDialog(notation, result -> {
				log.info("EditNotationButton - adding [{}]", result.toString());
				return compositionDocumentTypeManager.getCurrentDocument().get().exchangeNotationAfterEdit(notation, result);
				//TODO common this code from double click -
			});
		}
	}

	public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
		this.propertyNotationPanel = propertyNotationPanel;

		propertyNotationPanel.addListener(selectedNotation -> {
			disableProperty().set(!selectedNotation.isPresent());

			if (selectedNotation.isPresent()) {
				tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
			}
			else {
				tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
			}
		});
	}

	public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
		this.compositionDocumentTypeManager = compositionDocumentTypeManager;
	}

	public void setNotationEditorDialogFactory(NotationEditorDialogFactory notationEditorDialogFactory) {
		this.notationEditorDialogFactory = notationEditorDialogFactory;
	}
}
