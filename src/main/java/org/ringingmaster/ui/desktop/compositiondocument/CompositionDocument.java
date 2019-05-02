package org.ringingmaster.ui.desktop.compositiondocument;

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
import org.ringingmaster.engine.composition.DryRun;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Notation;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.ringingmaster.engine.composition.DryRun.DryRunResult.SUCCESS;
import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_PARTS_MAX;
import static org.ringingmaster.engine.notation.PlaceSetSequence.BY_NUMBER_THEN_NAME;

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
    private MutableComposition composition;
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

    public void init(MutableComposition composition) {
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

    @Deprecated
    public void setUpdatePoint(Supplier<String> updatePointName, boolean mutated) {
        if (mutated) {
            log.info("UPDATE: [{}]", updatePointName.get());
            setDirty(true);
        } else {
            log.debug("UPDATE: [{}], [{}]", updatePointName.get(), mutated);
        }
    }


    public MutableComposition getMutableComposition() {
        return composition;
    }

    public Composition getComposition() {
        return composition.get();
    }

    public NumberOfBells getNumberOfBells() {
        return composition.get().getNumberOfBells();
    }


    @Deprecated
    public List<Notation> getSortedAllNotations() {
        final List<Notation> sortedNotations = Lists.newArrayList(composition.get().getAllNotations());
        Collections.sort(sortedNotations, BY_NUMBER_THEN_NAME);
        return sortedNotations;
    }

    @Deprecated
    public List<Notation> getSortedValidNotations() {
        final List<Notation> sortedNotations = Lists.newArrayList(composition.get().getValidNotations());
        Collections.sort(sortedNotations, BY_NUMBER_THEN_NAME);
        return sortedNotations;
    }

    public boolean addNotation(Notation notationToAdd) {
        checkNotNull(notationToAdd);

        boolean mutated = false;
        DryRun dryRun = composition.dryRunAddNotation(notationToAdd);

        if (dryRun.result() == SUCCESS) {
            //mutated =
                    composition.addNotation(notationToAdd);
        } else {
            String message = dryRun.getMessages().stream().collect(Collectors.joining(System.lineSeparator()));
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

        boolean mutated = true;
                composition.removeNotation(notationToRemove);
        setUpdatePoint(() -> String.format("Remove method: %s", notationToRemove.getNameIncludingNumberOfBells()), mutated);

//	TODO			Also do checks thate the notation can be removed
//	TODO			Also what happens to active method.
    }

    public boolean exchangeNotationAfterEdit(Notation originalNotation, Notation replacementNotation) {
        checkNotNull(originalNotation);
        checkNotNull(replacementNotation);

        boolean mutated = false;
        DryRun dryRun = composition.dryRunExchangeNotation(originalNotation, replacementNotation);

        if (dryRun.result() == SUCCESS) {
            //mutated =
                    composition.exchangeNotation(originalNotation, replacementNotation);
        } else {
            String message = dryRun.getMessages().stream().collect(Collectors.joining(System.lineSeparator()));
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
            //mutated =
                    composition.setNonSplicedActiveNotation(notation);
        }
        setUpdatePoint(() -> String.format("Set active method: %s", notation.getNameIncludingNumberOfBells()), mutated);
    }

    public boolean isSpliced() {
        return composition.get().isSpliced();
    }

    public void setSpliced(boolean spliced) {
        boolean mutated = false;
        if (composition.get().isSpliced() != spliced) {
            composition.setSpliced(spliced);
        }
        setUpdatePoint(() -> (spliced ? "Set spliced" : "Set non spliced"), mutated);
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
