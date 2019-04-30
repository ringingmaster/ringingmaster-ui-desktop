package org.ringingmaster.ui.desktop.compositiondocument;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.reactivex.Observable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.maingrid.MainGridModel;
import org.ringingmaster.ui.desktop.documentmanager.DefaultDocument;
import org.ringingmaster.ui.desktop.documentmanager.Document;
import org.ringingmaster.ui.desktop.proof.ProofManager;
import org.ringingmaster.util.javafx.grid.canvas.GridPane;
import org.ringingmaster.util.javafx.grid.model.GridModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.ringingmaster.engine.composition.ObservableComposition.START_AT_ROW_MAX;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_LEADS_MAX;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_PARTS_MAX;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_ROWS_MAX;

/**
 * Provides the interface between the engine {@code Composition} and the various
 * UI components.
 *
 * @author Lake
 */

// TODO ALL business logic in this class should be in the engine.
public class CompositionDocument extends ScrollPane implements Document {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String SPLICED_TOKEN = "<Spliced>";

    //Raw Data
    private ObservableComposition composition;
    private final CompositionStyle compositionStyle = new CompositionStyle(); //TODO Eventually be Observable
    private Document documentDelegate = new DefaultDocument();

    private MainGridModel mainGridModel;
    private final List<GridModel> definitionModels = new ArrayList<>();

    private ProofManager proofManager;
    private final Parser parser = new Parser();


    // UI components
    private final TitlePane titlePane = new TitlePane();
    private final GridPane gridPane = new GridPane("Main");
    private final DefinitionPane definitionPane = new DefinitionPane();

    public CompositionDocument() {
        titlePane.setCompositionStyle(compositionStyle);
    }

    public void init(ObservableComposition composition) {
        layoutNodes();

        this.composition = composition;

        configureDefinitionModels();

        mainGridModel = new MainGridModel(this);

        //TODO is this lot updated?
        gridPane.setModel(mainGridModel);
        definitionPane.setModels(definitionModels);

        titlePane.init(composition);
    }

    public Observable<Composition> observableComposition() {
        return composition.observable();
    }

    public Observable<Parse> observableParse() {
        return composition.observable()
                .map(parser::apply);
    }

    private void layoutNodes() {
        VBox verticalLayout = new VBox(titlePane, gridPane, definitionPane);
        verticalLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        verticalLayout.setSpacing(20.0);

        setContent(verticalLayout);
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        setFitToHeight(true);
        setFitToWidth(true);
        setFocusTraversable(false);
    }

    public void setUpdatePoint(Supplier<String> updatePointName, boolean mutated) {
        if (mutated) {
            log.info("UPDATE: [{}]", updatePointName.get());
            setDirty(true);
        } else {
            log.debug("UPDATE: [{}], [{}]", updatePointName.get(), mutated);
        }
    }


    public ObservableComposition getObservableComposition() {
        return composition;
    }

    public Composition getComposition() {
        return composition.get();
    }

    //TODO remove
    private void updateUiComponents() {
        gridPane.gridModelListener_cellContentsChanged();
        definitionPane.contentsChanged();
    }

    public void setTitle(String title) {
        checkNotNull(title);
        composition.setTitle(title);
    }

    public void setAuthor(String author) {
        checkNotNull(author);
        composition.setAuthor(author);
    }

    public NumberOfBells getNumberOfBells() {
        return composition.get().getNumberOfBells();
    }

    public void setNumberOfBells(NumberOfBells numberOfBells) {
        checkNotNull(numberOfBells);
//TODO Drive this checking from the ObservableComposition - check can return an immutable composition
        //TODO Reactive


//        boolean mutated = false;
//        if (composition.get().getNumberOfBells() != numberOfBells) {
//            StringBuilder message = new StringBuilder();
//
//            int pointNumber = 1;
//            if (composition.get().getCallFromBell().getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
//                message.append(pointNumber++).append(") Call from bell will change from ")
//                        .append(composition.get().getCallFromBell().getZeroBasedBell() + 1)
//                        .append(" to ").append(numberOfBells.getTenor().getZeroBasedBell() + 1).append(".").append(System.lineSeparator());
//            }
//
//            final Row existingStartChange = composition.get().getStartChange();
//            final Row newInitialRow = MethodBuilder.transformToNewNumberOfBells(existingStartChange, numberOfBells);
//
//            message.append(pointNumber++).append(") Start change will change from '")
//                    .append(existingStartChange.getDisplayString(false))
//                    .append("' to '")
//                    .append(newInitialRow.getDisplayString(false))
//                    .append("'.").append(System.lineSeparator());
//
//            if (composition.get().getTerminationChange().isPresent()) {
//                final Row existingTerminationRow = composition.get().getTerminationChange().get();
//                final Row newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);
//
//                message.append(pointNumber++).append(") Termination row will change from '")
//                        .append(existingTerminationRow.getDisplayString(false))
//                        .append("' to '")
//                        .append(newTerminationRow.getDisplayString(false))
//                        .append("'.").append(System.lineSeparator());
//            }
//
//            if (!composition.isSpliced() &&
//                    composition.get().getNonSplicedActiveNotation() != null &&
//                    composition.get().getNonSplicedActiveNotation().getNumberOfWorkingBells().toInt() > numberOfBells.toInt()) {
//                final List<Notation> filteredNotations = NotationBuilderHelper.filterNotationsUptoNumberOfBells(composition.get().getAllNotations(), numberOfBells);
//                message.append(pointNumber++).append(") Active method '")
//                        .append(composition.get().getNonSplicedActiveNotation().getNameIncludingNumberOfBells())
//                        .append("' ");
//                if (filteredNotations.size() == 0) {
//                    message.append("will be unset. There is no suitable replacement.");
//                } else {
//                    message.append("will change to '")
//                            .append(filteredNotations.get(0).getNameIncludingNumberOfBells())
//                            .append("'");
//                }
//                message.append(System.lineSeparator());
//            }
//
//            if (composition.get().getStartNotation().isPresent()) {
//                final String notation = composition.get().getStartNotation().get().getNotationDisplayString(false);
//                Notation builtNotation = NotationBuilder.getInstance()
//                        .setNumberOfWorkingBells(numberOfBells)
//                        .setUnfoldedNotationShorthand(notation)
//                        .build();
//                String newNotation = builtNotation.getNotationDisplayString(false);
//                if (!newNotation.equals(notation)) {
//
//                    message.append(pointNumber++).append(") Start notation '")
//                            .append(notation)
//                            .append("' ")
//                            .append("will change to '")
//                            .append(newNotation)
//                            .append("'");
//                }
//            }
//
//            boolean doAction = false;
//
//            if (message.length() > 0) {
//                message.append(System.lineSeparator()).append("Do you wish to continue?");
//
//                StringBuilder preamble = new StringBuilder();
//                preamble.append("Changing number of bells from ").append(composition.get().getNumberOfBells().getDisplayString())
//                        .append(" to ").append(numberOfBells.getDisplayString())
//                        .append(" will modify other properties: ").append(System.lineSeparator());
//
//                Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, preamble.toString() + message.toString(), ButtonType.OK, ButtonType.CANCEL);
//                dialog.setTitle("Change number of bells");
//                dialog.setHeaderText("Change number of bells");
//                dialog.getDialogPane().setMinHeight(280);
//                dialog.getDialogPane().setMinWidth(620);
//                final java.util.Optional result = dialog.showAndWait().filter(response -> response == ButtonType.OK);
//                if (result.isPresent()) {
//                    doAction = true;
//                }
//            }
//
//            if (doAction) {
//                mutated = composition.setNumberOfBells(numberOfBells);
//            }
//        }
//        setUpdatePoint(() -> String.format("Set number of bells to: %s", numberOfBells.getDisplayString()), mutated);

    }

    public void setCallFrom(Bell callFrom) {
        checkNotNull(callFrom);

        boolean mutated = false;
        if (composition.get().getCallFromBell() != callFrom) {
            mutated = composition.setCallFromBell(callFrom);
        }
        setUpdatePoint(() -> String.format("Set number of bells to: %s", callFrom.getDisplayString()), mutated);
    }

    @Deprecated
    public List<Notation> getSortedAllNotations() {
        final List<Notation> sortedNotations = Lists.newArrayList(composition.get().getAllNotations());
        Collections.sort(sortedNotations, Notation.BY_NUMBER_THEN_NAME);
        return sortedNotations;
    }

    @Deprecated
    public List<Notation> getSortedValidNotations() {
        final List<Notation> sortedNotations = Lists.newArrayList(composition.get().getValidNotations());
        Collections.sort(sortedNotations, Notation.BY_NUMBER_THEN_NAME);
        return sortedNotations;
    }

    public boolean addNotation(Notation notationToAdd) {
        checkNotNull(notationToAdd);

        boolean mutated = false;
        List<String> messages = composition.checkAddNotation(notationToAdd);

        if (messages.size() == 0) {
            mutated = composition.addNotation(notationToAdd);
        } else {
            String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
            dialog.setTitle("Can't add method");
            dialog.setHeaderText("Can't add '" + notationToAdd.getNameIncludingNumberOfBells() + "'");
            dialog.getDialogPane().setMinHeight(180);
            dialog.getDialogPane().setMinWidth(360);
            dialog.showAndWait();
        }

        setUpdatePoint(() -> String.format("Add method: %s", notationToAdd.getNameIncludingNumberOfBells()), mutated);
        return mutated;
    }

    public void removeNotation(Notation notationToRemove) {
        checkNotNull(notationToRemove);

        boolean mutated = composition.removeNotation(notationToRemove);
        setUpdatePoint(() -> String.format("Remove method: %s", notationToRemove.getNameIncludingNumberOfBells()), mutated);

//	TODO			Also do checks thate the notation can be removed
//	TODO			Also what happens to active method.
    }

    public boolean exchangeNotationAfterEdit(Notation originalNotation, Notation replacementNotation) {
        checkNotNull(originalNotation);
        checkNotNull(replacementNotation);

        boolean mutated = false;
        List<String> messages = composition.checkUpdateNotation(originalNotation, replacementNotation);

        if (messages.size() == 0) {
            mutated = composition.exchangeNotation(originalNotation, replacementNotation);
        } else {
            String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
            dialog.setTitle("Can't update method");
            dialog.setHeaderText("Can't update '" + originalNotation.getNameIncludingNumberOfBells() + "'" +
                    (originalNotation.getNameIncludingNumberOfBells().equals(replacementNotation.getNameIncludingNumberOfBells()) ? "" : " to '" + replacementNotation.getNameIncludingNumberOfBells() + "'"));
            dialog.getDialogPane().setMinHeight(180);
            dialog.getDialogPane().setMinWidth(360);
            dialog.showAndWait();
        }

        setUpdatePoint(() -> String.format("Update method: %s", replacementNotation.getNameIncludingNumberOfBells()), mutated);
        return mutated;
    }

    public Notation getSingleMethodActiveNotation() {
        return composition.get().getNonSplicedActiveNotation().get();
    }

    public void setSingleMethodActiveNotation(Notation notation) {
        boolean mutated = false;
        if (!notation.equals(composition.get().getNonSplicedActiveNotation())) {
            mutated = composition.setNonSplicedActiveNotation(notation);
        }
        setUpdatePoint(() -> String.format("Set active method: %s", notation.getNameIncludingNumberOfBells()), mutated);
    }

    public boolean isSpliced() {
        return composition.get().isSpliced();
    }

    public void setSpliced(boolean spliced) {
        boolean mutated = false;
        if (composition.get().isSpliced() != spliced) {
            mutated = composition.setSpliced(spliced);
        }
        setUpdatePoint(() -> (spliced ? "Set spliced" : "Set non spliced"), mutated);
    }

    public void setCompositionCompositionType(CompositionType compositionType) {
        checkNotNull(compositionType);

        boolean mutated = false;
        if (compositionType != composition.get().getCompositionType()) {
            mutated = composition.setCompositionType(compositionType);
        }
        setUpdatePoint(() -> String.format("Set Checking Type: %s", compositionType.getName()), mutated);
    }

    public void setPlainLeadToken(String plainLeadToken) {
        checkNotNull(plainLeadToken);

        boolean mutated = false;
        if (!plainLeadToken.equals(composition.get().getPlainLeadToken())) {
            mutated = composition.setPlainLeadToken(plainLeadToken);
        }
        setUpdatePoint(() -> String.format("Set Plain Lead Token: %s", plainLeadToken), mutated);
    }

    public String getStartChange() {
        return composition.get().getStartChange().getDisplayString(true);
    }

    public void setStartChange(String startChangeText) {
        checkNotNull(startChangeText);

        if (getStartChange().equals(startChangeText)) {
            return;
        }

        // first look for rounds token
        boolean mutated = false;
        String action = "Set Start Change";
        if (startChangeText.compareToIgnoreCase(Row.ROUNDS_TOKEN) == 0) {
            final Row rounds = MethodBuilder.buildRoundsRow(composition.get().getNumberOfBells());
            mutated = composition.setStartChange(rounds);
            action = ": " + Row.ROUNDS_TOKEN;
        }
        // Now check for valid row
        else {
            try {
                final Row parsedRow = MethodBuilder.parse(composition.get().getNumberOfBells(), startChangeText);
                mutated = composition.setStartChange(parsedRow);
                action += ": " + parsedRow.getDisplayString(true);
            } catch (RuntimeException e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Change start change to '")
                        .append(startChangeText)
                        .append("' has failed:")
                        .append(System.lineSeparator());
                msg.append(e.getMessage())
                        .append(System.lineSeparator());
                msg.append("The original start change '")
                        .append(composition.get().getStartChange().getDisplayString(true))
                        .append("' will be restored.");

                Alert dialog = new Alert(Alert.AlertType.ERROR, msg.toString(), ButtonType.OK);
                dialog.setTitle("Change start change failed");
                dialog.setHeaderText("Changing the start change to '" + startChangeText + "' has failed.");
                dialog.getDialogPane().setMinHeight(100);
                dialog.getDialogPane().setMinWidth(400);
                dialog.showAndWait().filter(response -> response == ButtonType.OK);
            }
        }

        String actionForLambda = action;
        setUpdatePoint(() -> actionForLambda, mutated);
    }

    public int getStartAtRow() {
        return composition.get().getStartAtRow();
    }

    public void setStartAtRow(int startAtRow) {
        if (startAtRow > START_AT_ROW_MAX) {
            startAtRow = START_AT_ROW_MAX;
        }
        if (startAtRow < 0) {
            startAtRow = 0;
        }
        boolean mutated = false;
        if (startAtRow != getStartAtRow()) {
            mutated = composition.setStartAtRow(startAtRow);
        }
        final int startAtRowForLambda = startAtRow;
        setUpdatePoint(() -> String.format("Set Start At Row: %d", startAtRowForLambda), mutated);
    }

    public void setStartStroke(Stroke startStroke) {
        checkNotNull(startStroke);

        boolean mutated = false;
        if (startStroke != composition.get().getStartStroke()) {
            mutated = composition.setStartStroke(startStroke);
        }
        setUpdatePoint(() -> String.format("Set Start Stroke: %s", startStroke), mutated);
    }

    public void setStartNotation(String startNotation) {
        boolean mutated;
        String action;

        if (Strings.isNullOrEmpty(startNotation)) {
            mutated = composition.removeStartNotation();
            action = "Remove Start Notation";
        } else {
            Notation builtNotation = NotationBuilder.getInstance()
                    .setNumberOfWorkingBells(composition.get().getNumberOfBells())
                    .setUnfoldedNotationShorthand(startNotation)
                    .build();

            if (builtNotation != null && builtNotation.size() > 0) {
                mutated = composition.setStartNotation(builtNotation);
                action = "Set Start Notation: " + builtNotation.getNotationDisplayString(true);
            } else {
                mutated = composition.removeStartNotation();
                action = "Remove Start Notation";
            }
        }

        setUpdatePoint(() -> action, mutated);
    }


    public String getTerminationChange() {
        final Optional<Row> terminationChange = composition.get().getTerminationChange();
        if (terminationChange.isPresent()) {
            return terminationChange.get().getDisplayString(true);
        } else {
            return "";
        }
    }

    public void setTerminationChange(String terminationChangeText) {
        checkNotNull(terminationChangeText);

        if (getTerminationChange().equals(terminationChangeText)) {
            return;
        }

        boolean mutated = false;
        String action;
        // first look for removal
        if (terminationChangeText.isEmpty()) {
            mutated = composition.removeTerminationChange();
            action = "Remove Termination Change";
        }
        // then look for rounds token
        else if (terminationChangeText.compareToIgnoreCase(Row.ROUNDS_TOKEN) == 0) {
            final Row rounds = MethodBuilder.buildRoundsRow(composition.get().getNumberOfBells());
            mutated = composition.setTerminationChange(rounds);
            action = "Set Termination Change: " + Row.ROUNDS_TOKEN;
        }
        // Now check for valid row
        else {
            try {
                final Row parsedRow = MethodBuilder.parse(composition.get().getNumberOfBells(), terminationChangeText);
                mutated = composition.setTerminationChange(parsedRow);
                action = "Set Termination Change: " + parsedRow.getDisplayString(true);
            } catch (RuntimeException e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Change termination change to '")
                        .append(terminationChangeText)
                        .append("' has failed:")
                        .append(System.lineSeparator());
                msg.append(e.getMessage())
                        .append(System.lineSeparator());
                msg.append("The original termination change '")
                        .append(composition.get().getTerminationChange().isPresent() ? composition.get().getTerminationChange().get().getDisplayString(true) : "")
                        .append("' will be restored.");

                Alert dialog = new Alert(Alert.AlertType.ERROR, msg.toString(), ButtonType.OK);
                dialog.setTitle("Change termination change failed");
                dialog.setHeaderText("Changing the termination change to '" + terminationChangeText + "' has failed.");
                dialog.getDialogPane().setMinHeight(100);
                dialog.getDialogPane().setMinWidth(400);
                dialog.showAndWait().filter(response -> response == ButtonType.OK);
                action = "error";
            }
        }

        String actionForLambda = action;
        setUpdatePoint(() -> actionForLambda, mutated);
    }

    public int getTerminationMaxRows() {
        return composition.get().getTerminationMaxRows();
    }

    public void setTerminationMaxRows(int terminationMaxRows) {
        if (terminationMaxRows > TERMINATION_MAX_ROWS_MAX ||
                terminationMaxRows < 1) {
            terminationMaxRows = TERMINATION_MAX_ROWS_MAX;
        }

        boolean mutated = false;
        if (terminationMaxRows != getTerminationMaxRows()) {
            mutated = composition.setTerminationMaxRows(terminationMaxRows);
        }

        int terminationMaxRowsForLambda = terminationMaxRows;
        setUpdatePoint(() -> String.format("Set Row Limit: %d", terminationMaxRowsForLambda), mutated);
    }

    public void setTerminationMaxLeads(Integer terminationMaxLeads) {
        boolean mutated;
        String action;
        if (terminationMaxLeads == null ||
                terminationMaxLeads < 1) {
            action = "Remove Lead Limit";
            mutated = composition.removeTerminationMaxLeads();
        } else {
            if (terminationMaxLeads > TERMINATION_MAX_LEADS_MAX) {
                terminationMaxLeads = TERMINATION_MAX_LEADS_MAX;
            }

            action = String.format("Set Lead Limit: %d", terminationMaxLeads);
            mutated = composition.setTerminationMaxLeads(terminationMaxLeads);
        }
        String actionForLambda = action;
        setUpdatePoint(() -> actionForLambda, mutated);
    }

    public void setTerminationMaxParts(Integer terminationMaxParts) {
        boolean mutated;
        String action;
        if (terminationMaxParts == null ||
                terminationMaxParts < 1) {
            action = "Remove Part Limit";
            mutated = composition.removeTerminationMaxParts();
        } else {
            if (terminationMaxParts > TERMINATION_MAX_PARTS_MAX) {
                terminationMaxParts = TERMINATION_MAX_PARTS_MAX;
            }

            action = String.format("Set Part Limit: %d", terminationMaxParts);
            mutated = composition.setTerminationMaxParts(terminationMaxParts);
        }

        String actionForLambda = action;
        setUpdatePoint(() -> actionForLambda, mutated);
    }

    public void setTerminationCircularComposition(Integer terminationCircularComposition) {
        //TODO Reactive
//        boolean mutated;
//        String action;
//        if (terminationCircularComposition == null ||
//                terminationCircularComposition < 1) {
//            action = "Remove Circular Composition Limit";
//            mutated = composition.re.removeTerminationMaxCircularComposition();
//        } else {
//            if (terminationCircularComposition > Composition.TERMINATION_CIRCULAR_COMPOSITION_MAX) {
//                terminationCircularComposition = Composition.TERMINATION_CIRCULAR_COMPOSITION_MAX;
//            }
//
//            action = String.format("Set Circular Composition Limit: %d", terminationCircularComposition);
//            mutated = composition.setTerminationMaxCircularComposition(terminationCircularComposition);
//        }
//        String actionForLambda = action;
//        setUpdatePoint(() -> actionForLambda, mutated);
    }

    private void configureDefinitionModels() {
        //TODO Reactive
//        final List<CompositionDefinition> definitions = new ArrayList<>(composition.get().getDefinitions());
//        Collections.sort(definitions, CompositionDefinition.BY_SHORTHAND);
//
//        for (CompositionDefinition definition : definitions) {
//            definitionModels.add(new DefinitionGridModel(this, definition));
//        }
    }

    public List<GridModel> getDefinitionGridModels() {
        return definitionModels;
    }

    public CompositionStyle getCompositionStyle() {
        return compositionStyle;
    }

    public int getColumnSize() {
          return composition.get().allCompositionCells().getColumnSize();
    }

    public int getRowSize() {
        return composition.get().allCompositionCells().getRowSize();
    }

    public void setProofManager(ProofManager proofManager) {
        this.proofManager = proofManager;
    }

    @Override
    public boolean hasFileLocation() {
        return documentDelegate.hasFileLocation();
    }

    @Override
    public boolean isDirty() {
        return documentDelegate.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        documentDelegate.setDirty(dirty);
    }

    @Override
    public Path getPath() {
        return documentDelegate.getPath();
    }

    @Override
    public void setPath(Path path) {
        documentDelegate.setPath(path);
    }

    @Override
    public void setDocumentName(String documentName) {
        documentDelegate.setDocumentName(documentName);
    }

    @Override
    public String getNameForApplicationTitle() {
        return documentDelegate.getNameForApplicationTitle();
    }

    @Override
    public String getNameForTab() {
        return documentDelegate.getNameForTab();
    }

    @Override
    public Node getNode() {
        return this;
    }
}
