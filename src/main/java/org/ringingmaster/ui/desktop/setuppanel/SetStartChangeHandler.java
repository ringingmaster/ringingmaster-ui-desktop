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
public class SetStartChangeHandler {

    public void setStartChange(MutableComposition composition, String startChangeText) {
        checkNotNull(startChangeText);

        // first look for rounds token
        if (startChangeText.compareToIgnoreCase(Row.ROUNDS_TOKEN) == 0) {
            final Row rounds = MethodBuilder.buildRoundsRow(composition.get().getNumberOfBells());
            composition.setStartChange(rounds);
        }
        // Now check for valid row
        else {
            try {
                final Row parsedRow = MethodBuilder.parse(composition.get().getNumberOfBells(), startChangeText);
                composition.setStartChange(parsedRow);
            } catch (RuntimeException e) {

                String msg = "Changing the start change to '" +
                        startChangeText +
                        "' has failed:" +
                        System.lineSeparator() +
                        e.getMessage() +
                        System.lineSeparator() +
                        "The original start change '" +
                        composition.get().getStartChange().getDisplayString(true) +
                        "' will be restored.";
                Alert dialog = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
                dialog.setTitle("Setting start change failed");
                dialog.setHeaderText("Setting the start change to '" + startChangeText + "' has failed.");
                dialog.getDialogPane().setMinHeight(100);
                dialog.getDialogPane().setMinWidth(400);
                dialog.showAndWait().filter(response -> response == ButtonType.OK);
                composition.renotify();
            }
        }
    }
}
