package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import javafx.scene.control.Button;
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

	private static Image IMAGE = new Image(DeleteNotationButton.class.getResourceAsStream("/images/delete.png"));

	public DeleteNotationButton() {
		super("", new ImageView(IMAGE));
		PropertyNotationPanel.getInstance().addListener(this);

		setOnAction(event -> {
			int index = PropertyNotationPanel.getInstance().getSelectedIndex();
			NotationBody notation =  PropertyNotationPanel.getInstance().getNotation(index);
			DocumentManager.getInstance().getCurrentDocument().removeNotation(notation);
		});
	}


	@Override
	public void propertyMethod_setSelectedNotation(Optional<NotationBody> selectedNotation) {
		setDisable(!selectedNotation.isPresent());
	}
}
