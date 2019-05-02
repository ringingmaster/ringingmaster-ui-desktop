package org.ringingmaster.ui.desktop.setuppanel;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.DryRun;
import org.ringingmaster.engine.composition.MutableComposition;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.ringingmaster.engine.composition.DryRun.DryRunResult.SUCCESS;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetNumberOfBellsHandler {

    public void handle(MutableComposition composition, NumberOfBells numberOfBells) {
        DryRun dryRun = composition.dryRunSetNumberOfBells(numberOfBells);
        if (dryRun.result() == SUCCESS) {
            StringBuilder preamble = new StringBuilder();
            preamble.append("Changing number of bells from ").append(composition.get().getNumberOfBells().getDisplayString())
                    .append(" to ").append(numberOfBells.getDisplayString())
                    .append(" will modify other properties: ").append(System.lineSeparator());

            List<String> messages = dryRun.getMessages();
            messages.add(System.lineSeparator());
            messages.add("Do you wish to continue?");
            String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));

            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, preamble.toString() + message, ButtonType.OK, ButtonType.CANCEL);
            dialog.setTitle("Change number of bells");
            dialog.setHeaderText("Change number of bells");
            dialog.getDialogPane().setMinHeight(280);
            dialog.getDialogPane().setMinWidth(620);
            final Optional result = dialog.showAndWait().filter(response -> response == ButtonType.OK);
            if (result.isPresent()) {
                composition.setNumberOfBells(numberOfBells);
            }
            else {
                composition.renotify();
            }
        }
    }
}
