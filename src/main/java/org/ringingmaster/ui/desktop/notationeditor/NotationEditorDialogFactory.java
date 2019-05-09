package org.ringingmaster.ui.desktop.notationeditor;

import javafx.stage.Stage;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.ui.desktop.notationsearch.NotationLibraryManager;
import org.ringingmaster.util.javafx.dialog.EditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class NotationEditorDialogFactory {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Stage globalStage;
    private NotationLibraryManager notationLibraryManager;


    public void newNotationShowDialog(NumberOfBells numberOfWorkingBells, Function<Notation, Boolean> onSuccessHandler) {
        NotationBuilder builder = NotationBuilder.getInstance();
        builder.setNumberOfWorkingBells(numberOfWorkingBells);
        builder.setFoldedPalindromeNotationShorthand("");

        // We set everything to use the canned version.
        builder.setCannedCalls();

        NotationEditorDialog.showDialog(EditMode.ADD, builder.build(), globalStage, onSuccessHandler, notationLibraryManager);
    }

    public void editNotationShowDialog(Notation notation, Function<Notation, Boolean> onSuccessHandler) {
        NotationEditorDialog.showDialog(EditMode.EDIT, notation, globalStage, onSuccessHandler, notationLibraryManager);
    }

    public void setGlobalStage(Stage globalStage) {
        this.globalStage = globalStage;
    }

    public void setNotationLibraryManager(NotationLibraryManager notationLibraryManager) {
        this.notationLibraryManager = notationLibraryManager;
    }
}
