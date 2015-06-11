package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO Comments
 *
 * @author Lake
 */
public class PropertyNotationWindow extends VBox {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public PropertyNotationWindow() {
		Node toolbar = createToolbar();
		getChildren().add(toolbar);
		VBox.setVgrow(toolbar, Priority.NEVER);

		PropertyNotationPanel methodPanel = PropertyNotationPanel.getInstance();
		getChildren().add(methodPanel);
		VBox.setVgrow(methodPanel, Priority.ALWAYS);
	}


	private Node createToolbar() {
		ToolBar toolBar = new ToolBar();

		toolBar.getItems().addAll(new AddNotationButton(),
				new DeleteNotationButton(),
				new EditNotationButton());

		return toolBar;

	}
}
