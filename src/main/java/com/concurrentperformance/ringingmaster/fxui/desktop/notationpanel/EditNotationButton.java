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

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class EditNotationButton extends Button implements PropertyNotationPanelListener {

	private final static Logger log = LoggerFactory.getLogger(EditNotationButton.class);
	public static final String TOOLTIP_BAST_TEXT = "Edit method";
	public final Tooltip TOOLTIP = new Tooltip(TOOLTIP_BAST_TEXT);

	private static Image IMAGE = new Image(EditNotationButton.class.getResourceAsStream("/images/method_edit.png"));

	public EditNotationButton() {
		super("", new ImageView(IMAGE));
		setTooltip(TOOLTIP);

		PropertyNotationPanel.getInstance().addListener(this);

		setOnAction(event -> doEditCurrentSelectedNotation());
	}

	public static void doEditCurrentSelectedNotation() {
		int index = PropertyNotationPanel.getInstance().getSelectionModel().getSelectedIndex();
		NotationBody notation =  PropertyNotationPanel.getInstance().getNotation(index);
		if (notation != null) {
			NotationEditorDialogBuilder.editNotationShowDialog(notation, result -> {
				log.info("EditNotationButton - adding", result.toString());
				DocumentManager.getCurrentDocument().exchangeNotationAfterEdit(notation, result);
				// TODO what checks do we need here?
				return true; //TODO common this code from double click -
			});
		}
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
