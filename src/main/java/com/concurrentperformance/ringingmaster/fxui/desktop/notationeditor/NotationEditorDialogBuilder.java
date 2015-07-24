package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public void newNotationShowDialog(NumberOfBells numberOfWorkingBells, Function<NotationBody, Boolean> onSuccessHandler) {
		NotationBuilder builder = NotationBuilder.getInstance();
		builder.setNumberOfWorkingBells(numberOfWorkingBells);
		builder.setFoldedPalindromeNotationShorthand("");

		// We set everything to use the canned version.
		builder.setCannedCalls();

		showDialog(builder.build(), NotationEditorEditMode.ADD_NOTATION, onSuccessHandler);
	}

	public void editNotationShowDialog(NotationBody notation, Function<NotationBody, Boolean> onSuccessHandler) {
		showDialog(notation, NotationEditorEditMode.EDIT_NOTATION, onSuccessHandler);
	}

	private void showDialog(NotationBody notation, NotationEditorEditMode editMode, Function<NotationBody, Boolean> onSuccessHandler) {
		FXMLLoader fxmlLoader = new FXMLLoader(NotationEditorDialog.class.getResource("NotationEditorDialog.fxml"));

		try {
			Scene scene = new Scene(fxmlLoader.load());
			NotationEditorDialog controller = fxmlLoader.getController();
			controller.init(editMode, scene, notation, onSuccessHandler);
		} catch (IOException e) {
			log.error("Error initialising NotationEditorDialog", e);
		}
	}
}
