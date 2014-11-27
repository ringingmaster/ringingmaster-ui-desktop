package com.concurrentperformance.ringingmaster.fxui.desktop.property;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

	private final Image newMethodImage = new Image(this.getClass().getResourceAsStream("/images/new_method.png"));
	private final Image deleteImage = new Image(this.getClass().getResourceAsStream("/images/delete.png"));

	public PropertyMethodsWindow() {
		getChildren().add(createToolbar());
		getChildren().add(new PropertyMethodPanel());
	}


	private Node createToolbar() {
		ToolBar toolBar = new ToolBar();
		Button addMethod = new Button("", new ImageView(newMethodImage));
		Button removeMethod = new Button("", new ImageView(deleteImage));
		addMethod.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				log.info(event.toString());
				NotationEditor notationEditor = new NotationEditor();
				notationEditor.setText("Hello World!");
				getChildren().add(notationEditor);
			}


		});

		toolBar.getItems().addAll(addMethod, removeMethod);

		return toolBar;

	}
}
