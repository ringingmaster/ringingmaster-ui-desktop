package org.ringingmaster.ui.desktop.notationeditor;

import org.ringingmaster.fxutils.dialog.EditMode;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.ui.desktop.notationsearch.NotationLibraryManager;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationEditorDialogFactory {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Stage globalStage;
	private NotationLibraryManager notationLibraryManager;


	public void newNotationShowDialog(NumberOfBells numberOfWorkingBells, Function<NotationBody, Boolean> onSuccessHandler) {
		NotationBuilder builder = NotationBuilder.getInstance();
		builder.setNumberOfWorkingBells(numberOfWorkingBells);
		builder.setFoldedPalindromeNotationShorthand("");

		// We set everything to use the canned version.
		builder.setCannedCalls();

		NotationEditorDialog.showDialog(EditMode.ADD, builder.build(), globalStage, onSuccessHandler, notationLibraryManager);
	}

	public void editNotationShowDialog(NotationBody notation, Function<NotationBody, Boolean> onSuccessHandler) {
		NotationEditorDialog.showDialog(EditMode.EDIT, notation, globalStage, onSuccessHandler, notationLibraryManager);
	}

	public void setGlobalStage(Stage globalStage) {
		this.globalStage = globalStage;
	}

	public void setNotationLibraryManager(NotationLibraryManager notationLibraryManager) {
		this.notationLibraryManager = notationLibraryManager;
	}
}
