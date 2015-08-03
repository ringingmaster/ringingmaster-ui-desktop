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
public class SetActiveNotationButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	public static final String TOOLTIP_BAST_TEXT = "Set active method";
	public final Tooltip tooltip = new Tooltip(TOOLTIP_BAST_TEXT);

	private static final Image IMAGE = new Image(SetActiveNotationButton.class.getResourceAsStream("/images/flag.png"));

	private DocumentManager documentManager;

	public SetActiveNotationButton() {
		super("", new ImageView(IMAGE));
		setTooltip(tooltip);
		setDisable(true);
	}

	public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
		propertyNotationPanel.addListener(selectedNotation -> {
				Optional<TouchDocument> currentDocument = documentManager.getCurrentDocument();
				setDisable(!selectedNotation.isPresent() ||
						   !currentDocument.isPresent() ||
						   !currentDocument.get().getSortedValidNotations().contains(selectedNotation.get()));

				setPressed(selectedNotation.isPresent() &&
						   currentDocument.isPresent() &&
						   currentDocument.get().getSingleMethodActiveNotation() == selectedNotation.get());

				if (selectedNotation.isPresent() &&
					currentDocument.isPresent()){
					if (!currentDocument.get().isSpliced() &&
							currentDocument.get().getSingleMethodActiveNotation() == selectedNotation.get()) {
						tooltip.setText("Set Spliced");
					} else {
						tooltip.setText(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
					}
				} else {
					tooltip.setText(TOOLTIP_BAST_TEXT);
				}
			}
		);

		setOnAction(event -> {
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
		});
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}
}
