package org.ringingmaster.ui.desktop.setuppanel;

import com.google.common.collect.Streams;
import javafx.application.Platform;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.ObservableComposition;
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

import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.util.javafx.propertyeditor.SelectionPropertyValue.UNDEFINED_INDEX;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class PropertySetupWindow extends PropertyEditor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String TITLE_PROPERTY_NAME = "Title";
    public static final String AUTHOR_PROPERTY_NAME = "Author";
    public static final String NUMBER_OF_BELLS_PROPERTY_NAME = "Number Of Bells";
    public static final String CALL_FROM_PROPERTY_NAME = "Call From";
    public static final String ACTIVE_METHOD_PROPERTY_NAME = "Active Method";
    public static final String CHECKING_TYPE_PROPERTY_NAME = "Checking Type";
    public static final String PLAIN_LEAD_TOKEN_PROPERTY_NAME = "Plain Lead Token";

    public static final String ADVANCED_SETUP_GROUP_NAME = "Advanced Setup";

    public static final String START_GROUP_NAME = "Start";
    public static final String START_WITH_CHANGE_PROPERTY_NAME = "Start Change";
    public static final String START_AT_ROW_PROPERTY_NAME = "Start At Row";
    public static final String START_STROKE_PROPERTY_NAME = "Start Stroke";
    public static final String START_NOTATION_PROPERTY_NAME = "Start Notation";

    public static final String TERMINATION_GROUP_NAME = "Termination";
    public static final String TERMINATION_WITH_CHANGE_PROPERTY_NAME = "Termination Change";
    public static final String TERMINATION_ROW_LIMIT_PROPERTY_NAME = "Row Limit";
    public static final String TERMINATION_LEAD_LIMIT_PROPERTY_NAME = "Lead Limit";
    public static final String TERMINATION_PART_LIMIT_PROPERTY_NAME = "Part Limit";
    public static final String TERMINATION_CIRCULAR_COMPOSITION_LIMIT_PROPERTY_NAME = "Circular Composition Limit";

    private CompositionDocumentTypeManager compositionDocumentTypeManager;

    private Optional<ObservableComposition> currentComposition = Optional.empty();

    public void init() {

        buildSetupSection();
        buildAdvancedSetupSection();
        buildStartSection();
        buildTerminationSection();

        compositionDocumentTypeManager.observableActiveObservableComposition().subscribe(observableComposition -> {
            currentComposition = observableComposition;
        });
        compositionDocumentTypeManager.observableComposition().subscribe(composition -> {
            updateSetupSection(composition);
            updateAdvancedSetupSection(composition);
            updateStartSection(composition);
            updateTerminationSection(composition);
        });
    }

    private void buildSetupSection() {
        add(new TextPropertyValue(TITLE_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(TITLE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setTitle(newValue)), CallbackStyle.EVERY_KEYSTROKE);

        add(new TextPropertyValue(AUTHOR_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(AUTHOR_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setAuthor(newValue)), CallbackStyle.EVERY_KEYSTROKE);

        add(new SelectionPropertyValue(NUMBER_OF_BELLS_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                final NumberOfBells numberOfBells = NumberOfBells.values()[newValue.intValue()];
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setNumberOfBells(numberOfBells));
            }
        });
        final List<String> numberOfBellItems = new ArrayList<>();
        for (NumberOfBells bells : NumberOfBells.values()) {
            numberOfBellItems.add(bells.getDisplayString());
        }
        ((SelectionPropertyValue) findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setItems(numberOfBellItems);

        add(new SelectionPropertyValue(CALL_FROM_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(CALL_FROM_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                final Bell callFrom = Bell.values()[newValue.intValue()];
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setCallFromBell(callFrom));
            }
        });

        add(new SelectionPropertyValue(ACTIVE_METHOD_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(compositionDocument -> {
                    int index = newValue.intValue();
                    if (index == 0) {
                        compositionDocument.setSpliced(true);
                    } else {
                        final List<Notation> sortedNotationsBeingDisplayed = getSortedNotations(compositionDocument.get());

                        final Notation selectedNotation = sortedNotationsBeingDisplayed.get(index - 1);// the -1 is the offset for the <spliced> row
                        compositionDocument.setNonSplicedActiveNotation(selectedNotation);
                    }
                }));

        add(new SelectionPropertyValue(CHECKING_TYPE_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(CHECKING_TYPE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                final CompositionType compositionType = CompositionType.values()[newValue.intValue()];
//TODO Reactive                updateCompositionIfPresent(compositionDocument -> compositionDocument.setCompositionCompositionType(compositionType));
            }
        });

        add(new TextPropertyValue(PLAIN_LEAD_TOKEN_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setPlainLeadToken(newValue)), CallbackStyle.EVERY_KEYSTROKE);
    }

    private void updateSetupSection(Optional<Composition> composition) {

        final String title = composition.isPresent() ? composition.get().getTitle() : "";
        ((TextPropertyValue) findPropertyByName(TITLE_PROPERTY_NAME)).setValue(title);

        final String author = composition.isPresent() ? composition.get().getAuthor() : "";
        ((TextPropertyValue) findPropertyByName(AUTHOR_PROPERTY_NAME)).setValue(author);

        final int numberOfBellsIndex = composition.map(composition1 -> composition1.getNumberOfBells().ordinal()).orElse(UNDEFINED_INDEX);
        ((SelectionPropertyValue) findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setSelectedIndex(numberOfBellsIndex);

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

        final List<String> validNotationItems = getSortedValidNotationNames(composition);
        int selectedNotationIndex = getActiveValidNotationIndex(composition);
        ((SelectionPropertyValue) findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setItems(validNotationItems);
        ((SelectionPropertyValue) findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setSelectedIndex(selectedNotationIndex);

        final List<String> compositionTypes = new ArrayList<>();
        for (CompositionType compositionType : CompositionType.values()) {
            compositionTypes.add(compositionType.getName());
        }
        ((SelectionPropertyValue) findPropertyByName(CHECKING_TYPE_PROPERTY_NAME)).setItems(compositionTypes);
        final int compositionTypeIndex = composition.map(value -> value.getCompositionType().ordinal()).orElse(UNDEFINED_INDEX);
        ((SelectionPropertyValue) findPropertyByName(CHECKING_TYPE_PROPERTY_NAME)).setSelectedIndex(compositionTypeIndex);

        final String plainLeadToken = composition.isPresent() ? composition.get().getPlainLeadToken() : "";
        ((TextPropertyValue) findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setValue(plainLeadToken);
        findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME).setDisable(composition.isPresent() &&
                composition.get().getCompositionType() == COURSE_BASED);
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

    private void buildAdvancedSetupSection() {
        // TODO
    }

    private void updateAdvancedSetupSection(Optional<Composition> composition) {
        //TODO

    }

    private void buildStartSection() {
        add(START_GROUP_NAME, new TextPropertyValue(START_WITH_CHANGE_PROPERTY_NAME));
//TODO Reactive        ((TextPropertyValue) findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
        //TODO Reactive updateCompositionIfPresent(compositionDocument -> compositionDocument.setStartChange(newValue)), CallbackStyle.WHEN_FINISHED);

        add(START_GROUP_NAME, new IntegerPropertyValue(START_AT_ROW_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setStartAtRow(newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        add(START_GROUP_NAME, new SelectionPropertyValue(START_STROKE_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(START_STROKE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                final Stroke startStroke = Stroke.values()[newValue.intValue()];
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setStartStroke(startStroke));
            }
        });

        add(START_GROUP_NAME, new TextPropertyValue(START_NOTATION_PROPERTY_NAME));
//TODO Reactive        ((TextPropertyValue) findPropertyByName(START_NOTATION_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
//TODO Reactive                updateCompositionIfPresent(compositionDocument -> compositionDocument.setStartNotation(newValue)), CallbackStyle.WHEN_FINISHED);

        showGroupByName(START_GROUP_NAME, false); // TODO save state in app

    }

    private void updateStartSection(Optional<Composition> compositionDocument) {
        final String startChange = compositionDocument.map(Composition::getStartChange)
                .map(row -> row.getDisplayString(true)).orElse("");
        ((TextPropertyValue) findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setValue(startChange);

        int startAtRow = compositionDocument.map(Composition::getStartAtRow).orElse(0);
        ((IntegerPropertyValue) findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setValue(startAtRow);

        final List<String> startAtStrokeItems = new ArrayList<>();
        for (Stroke stroke : Stroke.values()) {
            startAtStrokeItems.add(stroke.getDisplayString());
        }
        ((SelectionPropertyValue) findPropertyByName(START_STROKE_PROPERTY_NAME)).setItems(startAtStrokeItems);
        final int startStroke = compositionDocument.map(composition -> composition.getStartStroke().ordinal()).orElse(UNDEFINED_INDEX);
        ((SelectionPropertyValue) findPropertyByName(START_STROKE_PROPERTY_NAME)).setSelectedIndex(startStroke);


        final String startNotation = compositionDocument.flatMap(Composition::getStartNotation)
                .map(notation -> notation.getNotationDisplayString(false)).orElse("");
        ((TextPropertyValue) findPropertyByName(START_NOTATION_PROPERTY_NAME)).setValue(startNotation);
    }

    private void buildTerminationSection() {
        add(TERMINATION_GROUP_NAME, new TextPropertyValue(TERMINATION_WITH_CHANGE_PROPERTY_NAME));
//TODO Reactive        ((TextPropertyValue) findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
//TODO Reactive                updateCompositionIfPresent(compositionDocument -> compositionDocument.setTerminationChange(newValue)), CallbackStyle.WHEN_FINISHED);

        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_ROW_LIMIT_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_ROW_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setTerminationMaxRows(newValue == null ? 0 : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_LEAD_LIMIT_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_LEAD_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setTerminationMaxLeads(newValue == null ? null : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_PART_LIMIT_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_PART_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionIfPresent(compositionDocument -> compositionDocument.setTerminationMaxParts(newValue == null ? null : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_CIRCULAR_COMPOSITION_LIMIT_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_CIRCULAR_COMPOSITION_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
        updateCompositionIfPresent(compositionDocument -> compositionDocument.setTerminationMaxCircularComposition(newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        showGroupByName(TERMINATION_GROUP_NAME, false); // TODO save state in app
    }

    private void updateTerminationSection(Optional<Composition> compositionDocument) {
        final String terminationChange = compositionDocument.flatMap(Composition::getTerminationChange)
                .map(row -> row.getDisplayString(true)).orElse("");
        ((TextPropertyValue) findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setValue(terminationChange);

        int terminationRowLimit = compositionDocument.map(Composition::getTerminationMaxRows).orElse(0);
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_ROW_LIMIT_PROPERTY_NAME)).setValue(terminationRowLimit);

        Integer terminationLeadLimit = compositionDocument.flatMap(Composition::getTerminationMaxLeads).orElse(null);
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_LEAD_LIMIT_PROPERTY_NAME)).setValue(terminationLeadLimit);

        Integer terminationPartLimit = compositionDocument.flatMap(Composition::getTerminationMaxParts).orElse(null);
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_PART_LIMIT_PROPERTY_NAME)).setValue(terminationPartLimit);

        Integer terminationCircularCompositionLimit = compositionDocument.map(Composition::getTerminationMaxCircularity).orElse(null);
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_CIRCULAR_COMPOSITION_LIMIT_PROPERTY_NAME)).setValue(terminationCircularCompositionLimit);

    }

    void updateCompositionIfPresent(Consumer<ObservableComposition> consumer) {
        // The runLater is to prevent the UI from continuously applying the same wrong update when loosing focus
        // and switching focus to an error window.
        currentComposition.ifPresent(composition -> Platform.runLater(() -> consumer.accept(composition)));
    }

    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
        this.compositionDocumentTypeManager = compositionDocumentTypeManager;
    }
}
