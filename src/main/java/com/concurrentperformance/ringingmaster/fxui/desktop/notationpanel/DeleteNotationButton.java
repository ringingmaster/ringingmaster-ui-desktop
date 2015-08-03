package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DeleteNotationButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	public static final String TOOLTIP_BAST_TEXT = "Remove method";
	public final Tooltip TOOLTIP = new Tooltip(TOOLTIP_BAST_TEXT);

	private static final Image IMAGE = new Image(DeleteNotationButton.class.getResourceAsStream("/images/remove.png"));

	private DocumentManager documentManager;


	public DeleteNotationButton() {
		super("", new ImageView(IMAGE));
		setTooltip(TOOLTIP);
		setDisable(true);
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
		propertyNotationPanel.addListener(selectedNotation -> {
			setDisable(!selectedNotation.isPresent());

			if (selectedNotation.isPresent()) {
				TOOLTIP.setText(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
			} else {
				TOOLTIP.setText(TOOLTIP_BAST_TEXT);
			}

		});

		setOnAction(event -> {
			int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
			NotationBody notation =  propertyNotationPanel.getNotation(index);
			Optional<TouchDocument> currentDocument = documentManager.getCurrentDocument();
			if (currentDocument.isPresent()) {
				currentDocument.get().removeNotation(notation);
			}
		});

	}
}
