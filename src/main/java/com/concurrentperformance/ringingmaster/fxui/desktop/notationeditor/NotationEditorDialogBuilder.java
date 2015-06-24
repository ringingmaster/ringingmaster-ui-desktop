package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.fxui.desktop.RingingMasterDesktopApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Function;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationEditorDialogBuilder {

	private static final Logger log = LoggerFactory.getLogger(NotationEditorDialogBuilder.class);

	public static void newNotationShowDialog(NumberOfBells numberOfWorkingBells, Function<NotationBody, Boolean> onSuccessHandler) {
		NotationBuilder builder = NotationBuilder.getInstance();
		builder.setNumberOfWorkingBells(numberOfWorkingBells);
		builder.setFoldedPalindromeNotationShorthand("");

		// We set everything to use the canned version.
		builder.setCannedCalls();

		showDialog(builder.build(), NotationEditorEditMode.ADD_NOTATION, onSuccessHandler);
	}

	public static void editNotationShowDialog(NotationBody notation, Function<NotationBody, Boolean> onSuccessHandler) {
		showDialog(notation, NotationEditorEditMode.EDIT_NOTATION, onSuccessHandler);
	}

	private static void showDialog(NotationBody notation, NotationEditorEditMode editMode, Function<NotationBody, Boolean> onSuccessHandler) {
		FXMLLoader fxmlLoader = new FXMLLoader(NotationEditorDialog.class.getResource("NotationEditorDialog.fxml"));

		try {
			Scene scene = new Scene(fxmlLoader.load());
			scene.getStylesheets().add(RingingMasterDesktopApp.STYLESHEET);

			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);

			NotationEditorDialog controller = fxmlLoader.getController();
			controller.init(editMode, stage, notation, onSuccessHandler);

			stage.showAndWait();
		} catch (IOException e) {
			log.error("Error initialising NotationEditorDialog", e);
		}
	}
}
