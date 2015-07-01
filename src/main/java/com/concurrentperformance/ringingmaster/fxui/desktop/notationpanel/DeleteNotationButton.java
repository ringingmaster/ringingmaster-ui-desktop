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
public class DeleteNotationButton extends Button implements PropertyNotationPanelListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	public static final String TOOLTIP_BAST_TEXT = "Remove method";
	public final Tooltip TOOLTIP = new Tooltip(TOOLTIP_BAST_TEXT);

	private static Image IMAGE = new Image(DeleteNotationButton.class.getResourceAsStream("/images/remove.png"));

	public DeleteNotationButton() {
		super("", new ImageView(IMAGE));
		setTooltip(TOOLTIP);

		PropertyNotationPanel.getInstance().addListener(this);

		setOnAction(event -> {
			int index = PropertyNotationPanel.getInstance().getSelectedIndex();
			NotationBody notation =  PropertyNotationPanel.getInstance().getNotation(index);
			DocumentManager.getCurrentDocument().removeNotation(notation);
		});
	}


	@Override
	public void propertyMethod_setSelectedNotation(Optional<NotationBody> selectedNotation) {
		setDisable(!selectedNotation.isPresent());

		if (selectedNotation.isPresent()) {
			TOOLTIP.setText(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
		}
		else {
			TOOLTIP.setText(TOOLTIP_BAST_TEXT);
		}

	}
}
