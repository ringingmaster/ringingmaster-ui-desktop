package org.ringingmaster.ui.desktop.setuppanel;

import com.google.common.collect.Streams;
import javafx.application.Platform;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocumentTypeManager;
import org.ringingmaster.util.javafx.propertyeditor.CallbackStyle;
import org.ringingmaster.util.javafx.propertyeditor.IntegerPropertyValue;
import org.ringingmaster.util.javafx.propertyeditor.PropertyEditor;
import org.ringingmaster.util.javafx.propertyeditor.SelectionPropertyValue;
import org.ringingmaster.util.javafx.propertyeditor.TextPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_PART_CIRCULARITY_INITIAL_VALUE;
import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_ROWS_INITIAL_VALUE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.util.javafx.propertyeditor.SelectionPropertyValue.UNDEFINED_INDEX;

/**
 * TODO comments ???
 *
 * @author Steve Lake
 */
public class PropertySetupWindow extends PropertyEditor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String TITLE_PROPERTY_NAME = "Title";
    public static final String AUTHOR_PROPERTY_NAME = "Author";
    public static final String NUMBER_OF_BELLS_PROPERTY_NAME = "Number Of Bells";
    public static final String CALL_FROM_PROPERTY_NAME = "Call From";
    public static final String ACTIVE_METHOD_PROPERTY_NAME = "Active Method";
    public static final String COMPOSITION_TYPE_PROPERTY_NAME = "Composition Type";
    public static final String PLAIN_LEAD_TOKEN_PROPERTY_NAME = "Plain Lead Token";

    public static final String ADVANCED_SETUP_GROUP_NAME = "Advanced Setup";

    public static final String START_GROUP_NAME = "Start";
    public static final String START_WITH_CHANGE_PROPERTY_NAME = "Start Change";
    public static final String START_AT_ROW_PROPERTY_NAME = "Start At Row";
    public static final String START_STROKE_PROPERTY_NAME = "Start Stroke";
    public static final String START_NOTATION_PROPERTY_NAME = "Start Notation";

    public static final String TERMINATION_GROUP_NAME = "Termination";
    public static final String TERMINATION_CHANGE_PROPERTY_NAME = "Change";
    public static final String TERMINATION_MAX_ROWS_PROPERTY_NAME = "Row Limit";
    public static final String TERMINATION_MAX_LEADS_PROPERTY_NAME = "Lead Limit";
    public static final String TERMINATION_MAX_PARTS_PROPERTY_NAME = "Part Limit";
    public static final String TERMINATION_MAX_CIRCULARITY_PROPERTY_NAME = "Circularity Limit";

    private CompositionDocumentTypeManager compositionTypeManager;

    private Optional<MutableComposition> currentComposition = Optional.empty();

    public void init() {

        buildTitle();
        buildAuthor();
        buildNumberOfBells();
        buildCallFrom();
        buildActiveMethod();
        buildCheckingType();
        buildPlainLeadToken();

        buildStartChange();
        buildStartAtRow();
        buildStartStroke();
        buildStartNotation();
        showGroupByName(START_GROUP_NAME, false); // TODO save state in app

        buildTerminationChange();
        buildTerminationMaxRows();
        buildTerminstionMaxLeads();
        buildTerminationMaxParts();
        buildTerminationMaxCircularity();
        showGroupByName(TERMINATION_GROUP_NAME, false); // TODO save state in app


        compositionTypeManager.observableActiveMutableComposition().subscribe(mutableComposition -> {
            currentComposition = mutableComposition;
        });
        compositionTypeManager.observableComposition().subscribe(composition -> {
            updateTitle(composition);
            updateAuthor(composition);
            updateNumberOfBells(composition);
            updateCallFrom(composition);
            updateActiveMethod(composition);
            updateCheckingType(composition);
            updatePlainLeadToken(composition);

            updateStartChange(composition);
            updateStartAtRow(composition);
            updateStroke(composition);
            updateStartNotation(composition);

            updateTerminationChange(composition);
            updateTerminationMaxRows(composition);
            updateTerminationMaxLeads(composition);
            updateTerminationMaxParts(composition);
            updateTerminationMaxCircularity(composition);
        });
    }

    void updateCompositionIfPresent(Consumer<MutableComposition> consumer) {
        // The runLater is to prevent the UI from continuously applying the same wrong update when loosing focus
        // and switching focus to an error window.
        currentComposition.ifPresent(composition -> Platform.runLater(() -> consumer.accept(composition)));
    }


    private void buildTitle() {
        add(new TextPropertyValue(TITLE_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(TITLE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> composition.setTitle(newValue)), CallbackStyle.EVERY_KEYSTROKE);
    }

    private void updateTitle(Optional<Composition> composition) {
        final String title = composition.map(Composition::getTitle).orElse("");
        ((TextPropertyValue) findPropertyByName(TITLE_PROPERTY_NAME)).setValue(title);
    }


    private void buildAuthor() {
        add(new TextPropertyValue(AUTHOR_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(AUTHOR_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> composition.setAuthor(newValue)), CallbackStyle.EVERY_KEYSTROKE);
    }

    private void updateAuthor(Optional<Composition> composition) {
        final String author = composition.map(Composition::getAuthor).orElse("");
        ((TextPropertyValue) findPropertyByName(AUTHOR_PROPERTY_NAME)).setValue(author);
    }


    private void buildNumberOfBells() {
        add(new SelectionPropertyValue(NUMBER_OF_BELLS_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != UNDEFINED_INDEX) {
                final NumberOfBells numberOfBells = NumberOfBells.values()[newValue.intValue()];
                updateCompositionIfPresent(composition -> new SetNumberOfBellsHandler().handle(composition, numberOfBells));
            }
        });
        final List<String> numberOfBellItems = new ArrayList<>();
        for (NumberOfBells bells : NumberOfBells.values()) {
            numberOfBellItems.add(bells.getDisplayString());
        }
        ((SelectionPropertyValue) findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setItems(numberOfBellItems);
    }

    private void updateNumberOfBells(Optional<Composition> composition) {
        final int numberOfBellsIndex = composition.map(composition1 -> composition1.getNumberOfBells().ordinal()).orElse(UNDEFINED_INDEX);
        ((SelectionPropertyValue) findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setSelectedIndex(numberOfBellsIndex);
    }


    private void buildCallFrom() {
        add(new SelectionPropertyValue(CALL_FROM_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(CALL_FROM_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != UNDEFINED_INDEX) {
                final Bell callFrom = Bell.values()[newValue.intValue()];
                updateCompositionIfPresent(composition -> composition.setCallFromBell(callFrom));
            }
        });
    }

    private void updateCallFrom(Optional<Composition> composition) {
        final List<String> callFromItems = new ArrayList<>();
        if (composition.isPresent()) {
            NumberOfBells numberOfBells = composition.get().getNumberOfBells();
            for (Bell bell : Bell.values()) {
                if (bell.getZeroBasedBell() <= numberOfBells.getTenor().getZeroBasedBell()) {
                    callFromItems.add(bell.getDisplayString());
                }
            }
            ((SelectionPropertyValue) findPropertyByName(CALL_FROM_PROPERTY_NAME)).setItems(callFromItems);
        }

        final int callFromIndex = composition.map(value -> value.getCallFromBell().ordinal()).orElse(UNDEFINED_INDEX);
        ((SelectionPropertyValue) findPropertyByName(CALL_FROM_PROPERTY_NAME)).setSelectedIndex(callFromIndex);
    }


    private void buildActiveMethod() {
        add(new SelectionPropertyValue(ACTIVE_METHOD_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> {
                    int index = newValue.intValue();
                    if (index == 0) {
                        composition.setSpliced(true);
                    } else {
                        final List<Notation> sortedNotationsBeingDisplayed = getSortedNotations(composition.get());

                        final Notation selectedNotation = sortedNotationsBeingDisplayed.get(index - 1);// the -1 is the offset for the <spliced> row
                        composition.setNonSplicedActiveNotation(selectedNotation);
                    }
                }));
    }

    private void updateActiveMethod(Optional<Composition> composition) {
        final List<String> validNotationItems = getSortedValidNotationNames(composition);
        int selectedNotationIndex = getActiveValidNotationIndex(composition);
        ((SelectionPropertyValue) findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setItems(validNotationItems);
        ((SelectionPropertyValue) findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setSelectedIndex(selectedNotationIndex);
    }

    private int getActiveValidNotationIndex(Optional<Composition> composition) {

        if (!composition.isPresent()) {
            return UNDEFINED_INDEX;
        }

        if (composition.get().isSpliced()) {
            return 0;
        }

        final List<String> validNotationNames = getSortedValidNotationNames(composition);
        final String activeNotationName = composition.get().getNonSplicedActiveNotation()
                .map(Notation::getNameIncludingNumberOfBells).orElse("");
        return validNotationNames.indexOf(activeNotationName);
    }

    private List<String> getSortedValidNotationNames(Optional<Composition> composition) {
        return composition.map(composition1 ->
                Streams.concat(Stream.of(CompositionDocument.SPLICED_TOKEN),
                        getSortedNotations(composition1).stream().map(Notation::getNameIncludingNumberOfBells))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private List<Notation> getSortedNotations(Composition composition) {
        return composition.getValidNotations().stream()
                .sorted(Notation.BY_NUMBER_THEN_NAME)
                .collect(Collectors.toList());
    }


    private void buildCheckingType() {
        add(new SelectionPropertyValue(COMPOSITION_TYPE_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(COMPOSITION_TYPE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != UNDEFINED_INDEX) {
                final CompositionType compositionType = CompositionType.values()[newValue.intValue()];
                updateCompositionIfPresent(composition -> composition.setCompositionType(compositionType));
            }
        });
    }

    private void updateCheckingType(Optional<Composition> composition) {
        final List<String> compositionTypes = new ArrayList<>();
        for (CompositionType compositionType : CompositionType.values()) {
            compositionTypes.add(compositionType.getName());
        }
        ((SelectionPropertyValue) findPropertyByName(COMPOSITION_TYPE_PROPERTY_NAME)).setItems(compositionTypes);
        final int compositionTypeIndex = composition.map(value -> value.getCompositionType().ordinal()).orElse(UNDEFINED_INDEX);
        ((SelectionPropertyValue) findPropertyByName(COMPOSITION_TYPE_PROPERTY_NAME)).setSelectedIndex(compositionTypeIndex);
    }


    private void buildPlainLeadToken() {
        add(new TextPropertyValue(PLAIN_LEAD_TOKEN_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> composition.setPlainLeadToken(newValue)), CallbackStyle.EVERY_KEYSTROKE);
    }

    private void updatePlainLeadToken(Optional<Composition> composition) {
        final String plainLeadToken = composition.isPresent() ? composition.get().getPlainLeadToken() : "";
        ((TextPropertyValue) findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setValue(plainLeadToken);
        findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME).setDisable(composition.isPresent() &&
                composition.get().getCompositionType() == COURSE_BASED);
    }


    private void buildStartChange() {
        add(START_GROUP_NAME, new TextPropertyValue(START_WITH_CHANGE_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> new SetStartChangeHandler().setStartChange(composition, newValue)), CallbackStyle.WHEN_FINISHED);
    }

    private void updateStartChange(Optional<Composition> composition) {
        final String startChange = composition.map(Composition::getStartChange)
                .map(row -> row.getDisplayString(true)).orElse("");
        ((TextPropertyValue) findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setValue(startChange);
    }


    private void buildStartAtRow() {
        add(START_GROUP_NAME, new IntegerPropertyValue(START_AT_ROW_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> new SetStartAtRowHandler().setStartRow(composition, newValue)), CallbackStyle.WHEN_FINISHED);
    }

    private void updateStartAtRow(Optional<Composition> composition) {
        int startAtRow = composition.map(Composition::getStartAtRow).orElse(0);
        ((IntegerPropertyValue) findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setValue(startAtRow);
    }


    private void buildStartStroke() {
        add(START_GROUP_NAME, new SelectionPropertyValue(START_STROKE_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(START_STROKE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != UNDEFINED_INDEX) {
                final Stroke startStroke = Stroke.values()[newValue.intValue()];
                updateCompositionIfPresent(composition -> composition.setStartStroke(startStroke));
            }
        });
    }

    private void updateStroke(Optional<Composition> composition) {
        final List<String> startAtStrokeItems = new ArrayList<>();
        for (Stroke stroke : Stroke.values()) {
            startAtStrokeItems.add(stroke.getDisplayString());
        }
        ((SelectionPropertyValue) findPropertyByName(START_STROKE_PROPERTY_NAME)).setItems(startAtStrokeItems);
        final int startStroke = composition.map(composition1 -> composition1.getStartStroke().ordinal()).orElse(UNDEFINED_INDEX);
        ((SelectionPropertyValue) findPropertyByName(START_STROKE_PROPERTY_NAME)).setSelectedIndex(startStroke);
    }


    private void buildStartNotation() {
        add(START_GROUP_NAME, new TextPropertyValue(START_NOTATION_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(START_NOTATION_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> new SetStartNotationHandler().setStartNotation(composition,newValue)), CallbackStyle.WHEN_FINISHED);

        showGroupByName(START_GROUP_NAME, false); // TODO save state in app
    }

    private void updateStartNotation(Optional<Composition> composition) {
        final String startNotation = composition.flatMap(Composition::getStartNotation)
                .map(notation -> notation.getNotationDisplayString(false)).orElse("");
        ((TextPropertyValue) findPropertyByName(START_NOTATION_PROPERTY_NAME)).setValue(startNotation);
    }


    private void buildTerminationChange() {
        add(TERMINATION_GROUP_NAME, new TextPropertyValue(TERMINATION_CHANGE_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(TERMINATION_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> new SetTerminationChangeHandler().setTerminationChange(composition, newValue)), CallbackStyle.WHEN_FINISHED);
    }

    private void updateTerminationChange(Optional<Composition> composition) {
        final String terminationChange = composition.flatMap(Composition::getTerminationChange)
                .map(row -> row.getChange().getDisplayString(true)).orElse("");
        ((TextPropertyValue) findPropertyByName(TERMINATION_CHANGE_PROPERTY_NAME)).setValue(terminationChange);
    }


    private void buildTerminationMaxRows() {
        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_MAX_ROWS_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_MAX_ROWS_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> new SetTerminationMaxRowsHandler().setTerminationMaxRows(composition, newValue)), CallbackStyle.WHEN_FINISHED);
    }

    private void updateTerminationMaxRows(Optional<Composition> composition) {
        int terminationMaxRows = composition.map(Composition::getTerminationMaxRows).orElse(TERMINATION_MAX_ROWS_INITIAL_VALUE);
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_MAX_ROWS_PROPERTY_NAME)).setValue(terminationMaxRows);
    }


    private void buildTerminstionMaxLeads() {
        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_MAX_LEADS_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_MAX_LEADS_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> new SetTerminationMaxLeadsHandler().setTerminationMaxRows(composition, newValue)), CallbackStyle.WHEN_FINISHED);
    }

    private void updateTerminationMaxLeads(Optional<Composition> composition) {
        Integer terminationLeadLimit = composition.flatMap(Composition::getTerminationMaxLeads).orElse(null);
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_MAX_LEADS_PROPERTY_NAME)).setValue(terminationLeadLimit);
    }


    private void buildTerminationMaxParts() {
        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_MAX_PARTS_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_MAX_PARTS_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> new SetTerminationMaxPartsHandler().setTerminationMaxRows(composition, newValue)), CallbackStyle.WHEN_FINISHED);
    }

    private void updateTerminationMaxParts(Optional<Composition> composition) {
        Integer terminationPartLimit = composition.flatMap(Composition::getTerminationMaxParts).orElse(null);
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_MAX_PARTS_PROPERTY_NAME)).setValue(terminationPartLimit);
    }


    private void buildTerminationMaxCircularity() {
        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_MAX_CIRCULARITY_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_MAX_CIRCULARITY_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(composition -> new SetTerminationMaxPartCircularityHandler().setTerminationMaxCircularity(composition, newValue)), CallbackStyle.WHEN_FINISHED);
    }

    private void updateTerminationMaxCircularity(Optional<Composition> composition) {
        int terminationMaxPartCircularity = composition.map(Composition::getTerminationMaxPartCircularity).orElse(TERMINATION_MAX_PART_CIRCULARITY_INITIAL_VALUE);
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_MAX_CIRCULARITY_PROPERTY_NAME)).setValue(terminationMaxPartCircularity);
    }


    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionTypeManager) {
        this.compositionTypeManager = compositionTypeManager;
    }
}
