package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.fxutils.events.EventDefinition;
import com.concurrentperformance.fxutils.events.SkeletalEventDefinition;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocumentTypeManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor.NotationEditorDialogBuilder;
import javafx.event.ActionEvent;
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

	private TouchDocumentTypeManager touchDocumentTypeManager;
	private PropertyNotationPanel propertyNotationPanel;
	private NotationEditorDialogBuilder notationEditorDialogBuilder;


	public EditNotationEvent() {
		super("/images/method_edit.png", TOOLTIP_BAST_TEXT);
		tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
	}

	@Override
	public void handle(ActionEvent event) {

		if (!touchDocumentTypeManager.getCurrentDocument().isPresent()) {
			return;
		}

		int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
		NotationBody notation =  propertyNotationPanel.getNotation(index);
		if (notation != null) {
			notationEditorDialogBuilder.editNotationShowDialog(notation, result -> {
				log.info("EditNotationButton - adding", result.toString());
				touchDocumentTypeManager.getCurrentDocument().get().exchangeNotationAfterEdit(notation, result);
				// TODO what checks do we need here?
				return true; //TODO common this code from double click -
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

	public void setTouchDocumentTypeManager(TouchDocumentTypeManager touchDocumentTypeManager) {
		this.touchDocumentTypeManager = touchDocumentTypeManager;
	}

	public void setNotationEditorDialogBuilder(NotationEditorDialogBuilder notationEditorDialogBuilder) {
		this.notationEditorDialogBuilder = notationEditorDialogBuilder;
	}
}
