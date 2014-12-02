package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationEditorDialog extends Stage {

	public NotationEditorDialog() {
		super(StageStyle.DECORATED);

		NotationEditor notationEditor = new NotationEditor();

		initModality(Modality.APPLICATION_MODAL);
		Scene scene = new Scene(notationEditor);
		setScene(scene);
		setTitle("Notation Editor - TODO ");

		show();
	}
}
