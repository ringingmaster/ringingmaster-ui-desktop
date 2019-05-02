package org.ringingmaster.ui.desktop.setuppanel;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.method.Row;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetTerminationChangeHandler {

    public void setTerminationChange(MutableComposition composition, String terminationChangeText) {
        checkNotNull(terminationChangeText);

        // first look for removal
        if (terminationChangeText.isEmpty()) {
            composition.removeTerminationChange();
        }
        // then look for rounds token
        else if (terminationChangeText.compareToIgnoreCase(Row.ROUNDS_TOKEN) == 0) {
            final Row rounds = MethodBuilder.buildRoundsRow(composition.get().getNumberOfBells());
            composition.setTerminationChange(rounds);
        }
        // Now check for valid row
        else {
            try {
                final Row parsedRow = MethodBuilder.parse(composition.get().getNumberOfBells(), terminationChangeText);
                composition.setTerminationChange(parsedRow);
            } catch (RuntimeException e) {

                String msg = "Changing the termination change to '" +
                        terminationChangeText +
                        "' has failed:" +
                        System.lineSeparator() +
                        e.getMessage() +
                        System.lineSeparator() +
                        "The original termination change " +
                        composition.get().getTerminationChange().map(row -> "'" + row.getDisplayString(true) + "' ").orElse("") +
                        "will be restored.";
                Alert dialog = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
                dialog.setTitle("Setting termination change failed");
                dialog.setHeaderText("Setting the termination change to '" + terminationChangeText + "' has failed.");
                dialog.getDialogPane().setMinHeight(100);
                dialog.getDialogPane().setMinWidth(400);
                dialog.showAndWait().filter(response -> response == ButtonType.OK);
                composition.renotify();
            }
        }
    }
}
