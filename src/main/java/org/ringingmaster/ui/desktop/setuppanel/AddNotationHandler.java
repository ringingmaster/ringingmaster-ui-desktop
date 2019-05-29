package org.ringingmaster.ui.desktop.setuppanel;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.ringingmaster.engine.composition.DryRun;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Notation;

import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.ringingmaster.engine.composition.DryRun.DryRunResult.SUCCESS;

public class AddNotationHandler {

    public boolean handle(MutableComposition composition, Notation notationToAdd) {
        checkNotNull(notationToAdd);

        DryRun dryRun = composition.dryRunAddNotation(notationToAdd);

        if (dryRun.result() == SUCCESS) {
            composition.addNotation(notationToAdd);
            return true;
        } else {
            String message = dryRun.getMessages().stream().collect(Collectors.joining(System.lineSeparator()));
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
            dialog.setTitle("Can't add method");
            dialog.setHeaderText("Can't add '" + notationToAdd.getNameIncludingNumberOfBells() + "'");
            dialog.getDialogPane().setMinHeight(180);
            dialog.getDialogPane().setMinWidth(360);
            dialog.showAndWait();
            return false;
        }
    }
}
