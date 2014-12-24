package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationEditorDialogBuilder {

	private static final Logger log = LoggerFactory.getLogger(NotationEditorDialogBuilder.class);

	public static NotationEditorDialog createInstance(NotationBody notation) {
		FXMLLoader fxmlLoader = new FXMLLoader(NotationEditorDialog.class.getResource("NotationEditorDialog.fxml"));

		try {
			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setScene(new Scene(fxmlLoader.load()));
			stage.initModality(Modality.APPLICATION_MODAL);

			NotationEditorDialog controller = fxmlLoader.getController();
			controller.init(NotationEditorEditMode.EDIT_NOTATION, stage, notation);

			stage.show();

			return controller;

		} catch (IOException e) {
			log.error("Error initialising NotationEditorDialog", e);
			return null;
		}
	}
}
