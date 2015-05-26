package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
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

	public static NotationBody showDialog(NumberOfBells numberOfWorkingBells) {
		NotationBuilder builder = NotationBuilder.getInstance();
		builder.setNumberOfWorkingBells(numberOfWorkingBells);
		builder.setFoldedPalindromeNotationShorthand(""); 
		return showDialog(builder.build());
	}

	public static NotationBody showDialog(NotationBody notation) {
		FXMLLoader fxmlLoader = new FXMLLoader(NotationEditorDialog.class.getResource("NotationEditorDialog.fxml"));

		try {
			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setScene(new Scene(fxmlLoader.load()));
			stage.initModality(Modality.APPLICATION_MODAL);

			NotationEditorDialog controller = fxmlLoader.getController();
			controller.init(NotationEditorEditMode.EDIT_NOTATION, stage, notation);

			stage.showAndWait();

			return controller.getResult();

		} catch (IOException e) {
			log.error("Error initialising NotationEditorDialog", e);
			return null;
		}
	}
}
