package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.fxui.desktop.property.methods.AddMethodButton;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationEditorDialog extends Stage {

	private static Image INFO_IMAGE = new Image(AddMethodButton.class.getResourceAsStream("/images/info.png"));

	public NotationEditorDialog() {
		super(StageStyle.DECORATED);

		TabPane tabPane = getTabPane();
		Node buttonBar = getButtonBar();

		VBox vBox = new VBox();
		vBox.getChildren().addAll(tabPane, buttonBar);
		VBox.setVgrow(tabPane, Priority.ALWAYS);
		VBox.setVgrow(buttonBar, Priority.NEVER);

		setScene(new Scene(vBox));
		setTitle("Notation Editor - TODO ");
		initModality(Modality.APPLICATION_MODAL);

		show();
	}

	private TabPane getTabPane() {
		TabPane tabPane = new TabPane();

		Tab plainCourse = new Tab();
		plainCourse.setText("Plain Course");
		PlainCourse notationEditor = new PlainCourse();
		plainCourse.setContent(notationEditor);
		tabPane.getTabs().add(plainCourse);
		return tabPane;
	}

	public Node getButtonBar() {
		HBox buttonBar = new HBox();

		Button okButton = new Button("OK");
		Button cancelButton = new Button("Cancel");
		Pane spacer = new Pane();
		Button infoButton = new Button("", new ImageView(INFO_IMAGE));

		buttonBar.getChildren().addAll(infoButton, spacer, cancelButton, okButton);
		HBox.setHgrow(spacer,Priority.ALWAYS);

		buttonBar.setSpacing(20);

		return buttonBar;
	}
}
