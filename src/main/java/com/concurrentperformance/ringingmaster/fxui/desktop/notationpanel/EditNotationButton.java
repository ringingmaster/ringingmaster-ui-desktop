package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor.NotationEditorDialogBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class EditNotationButton extends Button {

	private final static Logger log = LoggerFactory.getLogger(EditNotationButton.class);
	public static final String TOOLTIP_BAST_TEXT = "Edit method";
	public final Tooltip TOOLTIP = new Tooltip(TOOLTIP_BAST_TEXT);

	private static final Image IMAGE = new Image(EditNotationButton.class.getResourceAsStream("/images/method_edit.png"));

	private DocumentManager documentManager;
	private PropertyNotationPanel propertyNotationPanel;
	private NotationEditorDialogBuilder notationEditorDialogBuilder;


	public EditNotationButton() {
		super("", new ImageView(IMAGE));
		setTooltip(TOOLTIP);
		setDisable(true);
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
		this.propertyNotationPanel = propertyNotationPanel;

		propertyNotationPanel.addListener(selectedNotation -> {
			setDisable(!selectedNotation.isPresent());

			if (selectedNotation.isPresent()) {
				TOOLTIP.setText(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
			}
			else {
				TOOLTIP.setText(TOOLTIP_BAST_TEXT);
			}
		});

		setOnAction(event -> doEditCurrentSelectedNotation());

	}

	public void doEditCurrentSelectedNotation() {
		if (!documentManager.getCurrentDocument().isPresent()) {
			return;
		}

		int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
		NotationBody notation =  propertyNotationPanel.getNotation(index);
		if (notation != null) {
			notationEditorDialogBuilder.editNotationShowDialog(notation, result -> {
				log.info("EditNotationButton - adding", result.toString());
				documentManager.getCurrentDocument().get().exchangeNotationAfterEdit(notation, result);
				// TODO what checks do we need here?
				return true; //TODO common this code from double click -
			});
		}
	}

	public void setNotationEditorDialogBuilder(NotationEditorDialogBuilder notationEditorDialogBuilder) {
		this.notationEditorDialogBuilder = notationEditorDialogBuilder;
	}
}
