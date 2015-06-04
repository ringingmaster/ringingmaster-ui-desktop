package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DeleteMethodButton extends Button {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Image IMAGE = new Image(DeleteMethodButton.class.getResourceAsStream("/images/delete.png"));

	public DeleteMethodButton() {
		super("", new ImageView(IMAGE));

		setOnAction(event -> {
			NotationBody notation = PropertyMethodPanel.getInstance().getSelectedNotation();
			DocumentManager.getInstance().getCurrentDocument().removeNotation(notation);
		});

	}

}
