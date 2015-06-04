package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import javafx.scene.Node;
import javafx.scene.control.Button;
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
public class PropertyMethodsWindow extends VBox {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public PropertyMethodsWindow() {
		Node toolbar = createToolbar();
		getChildren().add(toolbar);
		VBox.setVgrow(toolbar, Priority.NEVER);

		PropertyMethodPanel methodPanel = PropertyMethodPanel.getInstance();
		getChildren().add(methodPanel);
		VBox.setVgrow(methodPanel, Priority.ALWAYS);
	}


	private Node createToolbar() {
		ToolBar toolBar = new ToolBar();
		Button addMethod = new AddMethodButton();
		Button deleteMethod = new DeleteMethodButton();

		toolBar.getItems().addAll(addMethod, deleteMethod);

		return toolBar;

	}
}
