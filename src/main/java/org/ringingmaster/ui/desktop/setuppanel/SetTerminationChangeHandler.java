package org.ringingmaster.ui.desktop.setuppanel;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.TerminationChange;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.method.Row;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.ringingmaster.engine.composition.TerminationChange.Location.ANYWHERE;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetTerminationChangeHandler {

    public void setTerminationChange(MutableComposition composition, String terminationChangeText) {
        checkNotNull(terminationChangeText);

        TerminationChange.Location existingLocation = composition.get().getTerminationChange().map(TerminationChange::getLocation).orElse(ANYWHERE);

        // first look for removal
        if (terminationChangeText.isEmpty()) {
            composition.removeTerminationChange();
        }
        // then look for rounds token
        else if (terminationChangeText.toLowerCase().contains(Row.ROUNDS_TOKEN.toLowerCase())) {
            final Row rounds = MethodBuilder.buildRoundsRow(composition.get().getNumberOfBells());
            composition.setTerminationChange(rounds, existingLocation);
        }
        // Now check for valid row
        else {
            try {
                final Row parsedRow = MethodBuilder.parse(composition.get().getNumberOfBells(), terminationChangeText);
                composition.setTerminationChange(parsedRow, existingLocation);
            } catch (RuntimeException e) {

                String msg = "Changing the termination change to '" +
                        terminationChangeText +
                        "' has failed:" +
                        System.lineSeparator() +
                        e.getMessage() +
                        System.lineSeparator() +
                        "The original termination change " +
                        composition.get().getTerminationChange().map(row -> "'" + row.getChange().getDisplayString(true) + "' ").orElse("") + //TODO Add handler for Location
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

    public void setTerminationLocation(MutableComposition composition, TerminationChange.Location location) {
        checkNotNull(location);

        Row existingTerminationChange = composition.get().getTerminationChange().map(TerminationChange::getChange).orElse(
                MethodBuilder.buildRoundsRow(composition.get().getNumberOfBells())
        );

        composition.setTerminationChange(existingTerminationChange, location);

    }
}
