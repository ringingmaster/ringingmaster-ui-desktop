package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
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
public class SetActiveNotationButton extends Button implements PropertyNotationPanelListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	public static final String TOOLTIP_BAST_TEXT = "Set active method";
	public final Tooltip TOOLTIP = new Tooltip(TOOLTIP_BAST_TEXT);

	private static Image IMAGE = new Image(SetActiveNotationButton.class.getResourceAsStream("/images/flag.png"));

	public SetActiveNotationButton() {
		super("", new ImageView(IMAGE));
		setTooltip(TOOLTIP);

		PropertyNotationPanel.getInstance().addListener(this);

		setOnAction(event -> {
			int index = PropertyNotationPanel.getInstance().getSelectionModel().getSelectedIndex();
			NotationBody selectedNotation = PropertyNotationPanel.getInstance().getNotation(index);

			if (selectedNotation != null) {
				if (DocumentManager.getCurrentDocument().isSpliced()) {
					DocumentManager.getCurrentDocument().setSpliced(false);
					DocumentManager.getCurrentDocument().setSingleMethodActiveNotation(selectedNotation);
				}
				else {
					// Not Spliced
					if (DocumentManager.getCurrentDocument().getSingleMethodActiveNotation() == selectedNotation) {
						DocumentManager.getCurrentDocument().setSpliced(true);
					}
					else {
						DocumentManager.getCurrentDocument().setSingleMethodActiveNotation(selectedNotation);
					}
				}
			}
		});
	}


	@Override
	public void propertyMethod_setSelectedNotation(Optional<NotationBody> selectedNotation) {
		setDisable(!selectedNotation.isPresent() ||
				!DocumentManager.getCurrentDocument().getSortedValidNotations().contains(selectedNotation.get()));

		setPressed(selectedNotation.isPresent() &&
				DocumentManager.getCurrentDocument().getSingleMethodActiveNotation() == selectedNotation.get());

		if (selectedNotation.isPresent()) {
			if (!DocumentManager.getCurrentDocument().isSpliced() &&
					DocumentManager.getCurrentDocument().getSingleMethodActiveNotation() == selectedNotation.get()) {
				TOOLTIP.setText("Set Spliced");
			}
			else {
				TOOLTIP.setText(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
			}
		}
		else {
			TOOLTIP.setText(TOOLTIP_BAST_TEXT);
		}


	}
}
