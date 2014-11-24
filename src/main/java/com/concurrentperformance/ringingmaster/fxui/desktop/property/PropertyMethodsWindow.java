package com.concurrentperformance.ringingmaster.fxui.desktop.property;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;



/**
 * TODO Comments
 *
 * @author Lake
 */
public class PropertyMethodsWindow extends VBox {

	private final Image newMethodImage = new Image(this.getClass().getResourceAsStream("/new_method.png"));
	private final Image deleteImage = new Image(this.getClass().getResourceAsStream("/delete.png"));

	public PropertyMethodsWindow() {
		getChildren().add(createToolbar());
	}


	private Node createToolbar() {
		ToolBar toolBar = new ToolBar();
		Button addMethod = new Button("", new ImageView(newMethodImage));
		Button removeMethod = new Button("", new ImageView(deleteImage));

		toolBar.getItems().addAll(addMethod, removeMethod);

		return toolBar;

	}
}
