package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
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
public class SetActiveNotationButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	public static final String TOOLTIP_BAST_TEXT = "Set active method";
	public final Tooltip TOOLTIP = new Tooltip(TOOLTIP_BAST_TEXT);

	private static final Image IMAGE = new Image(SetActiveNotationButton.class.getResourceAsStream("/images/flag.png"));

	private DocumentManager documentManager;

	public SetActiveNotationButton() {
		super("", new ImageView(IMAGE));
		setTooltip(TOOLTIP);
	}

	public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
		propertyNotationPanel.addListener(selectedNotation -> {
					setDisable(!selectedNotation.isPresent() ||
							!documentManager.getCurrentDocument().getSortedValidNotations().contains(selectedNotation.get()));

					setPressed(selectedNotation.isPresent() &&
							documentManager.getCurrentDocument().getSingleMethodActiveNotation() == selectedNotation.get());

					if (selectedNotation.isPresent()) {
						if (!documentManager.getCurrentDocument().isSpliced() &&
								documentManager.getCurrentDocument().getSingleMethodActiveNotation() == selectedNotation.get()) {
							TOOLTIP.setText("Set Spliced");
						} else {
							TOOLTIP.setText(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
						}
					} else {
						TOOLTIP.setText(TOOLTIP_BAST_TEXT);
					}
				}
		);

		setOnAction(event -> {
			int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
			NotationBody selectedNotation = propertyNotationPanel.getNotation(index);

			if (selectedNotation != null) {
				if (documentManager.getCurrentDocument().isSpliced()) {
					documentManager.getCurrentDocument().setSpliced(false);
					documentManager.getCurrentDocument().setSingleMethodActiveNotation(selectedNotation);
				}
				else {
					// Not Spliced
					if (documentManager.getCurrentDocument().getSingleMethodActiveNotation() == selectedNotation) {
						documentManager.getCurrentDocument().setSpliced(true);
					}
					else {
						documentManager.getCurrentDocument().setSingleMethodActiveNotation(selectedNotation);
					}
				}
			}
		});
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}
}
