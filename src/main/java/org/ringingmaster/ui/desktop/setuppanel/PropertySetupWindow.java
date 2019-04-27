package org.ringingmaster.ui.desktop.setuppanel;

import com.google.common.collect.Lists;
import javafx.application.Platform;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.ui.desktop.documentmodel.CompositionDocument;
import org.ringingmaster.ui.desktop.documentmodel.CompositionDocumentTypeManager;
import org.ringingmaster.util.javafx.propertyeditor.CallbackStyle;
import org.ringingmaster.util.javafx.propertyeditor.IntegerPropertyValue;
import org.ringingmaster.util.javafx.propertyeditor.PropertyEditor;
import org.ringingmaster.util.javafx.propertyeditor.SelectionPropertyValue;
import org.ringingmaster.util.javafx.propertyeditor.TextPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;

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


    public void init() {

        buildSetupSection();
        buildAdvancedSetupSection();
        buildStartSection();
        buildTerminationSection();


        compositionDocumentTypeManager.addListener(compositionDocument -> {
            updateSetupSection(compositionDocument);
            updateAdvancedSetupSection(compositionDocument);
            updateStartSection(compositionDocument);
            updateTerminationSection(compositionDocument);
        });
    }

    private void buildSetupSection() {
        add(new TextPropertyValue(TITLE_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(TITLE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setTitle(newValue)), CallbackStyle.EVERY_KEYSTROKE);

        add(new TextPropertyValue(AUTHOR_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(AUTHOR_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setAuthor(newValue)), CallbackStyle.EVERY_KEYSTROKE);

        add(new SelectionPropertyValue(NUMBER_OF_BELLS_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                final NumberOfBells numberOfBells = NumberOfBells.values()[newValue.intValue()];
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setNumberOfBells(numberOfBells));
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
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setCallFrom(callFrom));
            }
        });

        add(new SelectionPropertyValue(ACTIVE_METHOD_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> {
                    int index = newValue.intValue();
                    if (index == 0) {
                        compositionDocument.setSpliced(true);
                    } else {
                        final List<Notation> sortedNotationsBeingDisplayed = compositionDocument.getSortedValidNotations();

                        final Notation selectedNotation = sortedNotationsBeingDisplayed.get(index - 1);// the -1 is the offset for the spliced row
                        compositionDocument.setSingleMethodActiveNotation(selectedNotation);
                    }
                }));

        add(new SelectionPropertyValue(CHECKING_TYPE_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(CHECKING_TYPE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                final CompositionType compositionType = CompositionType.values()[newValue.intValue()];
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setCompositionCompositionType(compositionType));
            }
        });

        add(new TextPropertyValue(PLAIN_LEAD_TOKEN_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setPlainLeadToken(newValue)), CallbackStyle.EVERY_KEYSTROKE);
    }

    private void updateSetupSection(Optional<CompositionDocument> compositionDocument) {

        final String title = compositionDocument.isPresent() ? compositionDocument.get().getTitle() : "";
        ((TextPropertyValue) findPropertyByName(TITLE_PROPERTY_NAME)).setValue(title);

        final String author = compositionDocument.isPresent() ? compositionDocument.get().getAuthor() : "";
        ((TextPropertyValue) findPropertyByName(AUTHOR_PROPERTY_NAME)).setValue(author);

        final int numberOfBellsIndex = compositionDocument.isPresent() ? compositionDocument.get().getNumberOfBells().ordinal() : -1;
        ((SelectionPropertyValue) findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setSelectedIndex(numberOfBellsIndex);

        final List<String> callFromItems = new ArrayList<>();
        if (compositionDocument.isPresent()) {
            NumberOfBells numberOfBells = compositionDocument.get().getNumberOfBells();
            for (Bell bell : Bell.values()) {
                if (bell.getZeroBasedBell() <= numberOfBells.getTenor().getZeroBasedBell()) {
                    callFromItems.add(bell.getDisplayString());
                }
            }
            ((SelectionPropertyValue) findPropertyByName(CALL_FROM_PROPERTY_NAME)).setItems(callFromItems);
        }
        final int callFromIndex = compositionDocument.isPresent() ? compositionDocument.get().getCallFrom().ordinal() : -1;
        ((SelectionPropertyValue) findPropertyByName(CALL_FROM_PROPERTY_NAME)).setSelectedIndex(callFromIndex);

        final List<String> validNotationItems = getValidNotations(compositionDocument);
        int selectedNotationIndex = getActiveValidNotationIndex(compositionDocument);
        ((SelectionPropertyValue) findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setItems(validNotationItems);
        ((SelectionPropertyValue) findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setSelectedIndex(selectedNotationIndex);

        final List<String> compositionTypes = new ArrayList<>();
        for (CompositionType compositionType : CompositionType.values()) {
            compositionTypes.add(compositionType.getName());
        }
        ((SelectionPropertyValue) findPropertyByName(CHECKING_TYPE_PROPERTY_NAME)).setItems(compositionTypes);
        final int compositionTypeIndex = compositionDocument.isPresent() ? compositionDocument.get().getCompositionCompositionType().ordinal() : -1;
        ((SelectionPropertyValue) findPropertyByName(CHECKING_TYPE_PROPERTY_NAME)).setSelectedIndex(compositionTypeIndex);

        final String plainLeadToken = compositionDocument.isPresent() ? compositionDocument.get().getPlainLeadToken() : "";
        ((TextPropertyValue) findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setValue(plainLeadToken);
        findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME).setDisable(compositionDocument.isPresent() &&
                compositionDocument.get().getCompositionCompositionType() == COURSE_BASED);
    }

    public List<String> getValidNotations(Optional<CompositionDocument> compositionDocument) {
        List<String> result = Lists.newArrayList();

        if (compositionDocument.isPresent()) {
            final List<Notation> orderedNotations = compositionDocument.get().getSortedValidNotations();

            result.add(CompositionDocument.SPLICED_TOKEN);

            for (int index = 0; index < orderedNotations.size(); index++) {
                final Notation notation = orderedNotations.get(index);
                result.add(notation.getNameIncludingNumberOfBells());
            }
        }

        return result;
    }

    private int getActiveValidNotationIndex(Optional<CompositionDocument> compositionDocument) {

        if (!compositionDocument.isPresent()) {
            return -1;
        }

        if (compositionDocument.get().isSpliced()) {
            return 0;
        }
        final Notation activeNotation = compositionDocument.get().getSingleMethodActiveNotation();
        final List<Notation> sortedNotationsBeingDisplayed = compositionDocument.get().getSortedValidNotations();
        for (int index = 0; index < sortedNotationsBeingDisplayed.size(); index++) {
            final Notation notation = sortedNotationsBeingDisplayed.get(index);
            if (notation == activeNotation) {
                return index + 1; // the 1 is the offset for the spliced row
            }
        }
        return -1;
    }

    private void buildAdvancedSetupSection() {
        // TODO
    }

    private void updateAdvancedSetupSection(Optional<CompositionDocument> compositionDocument) {
        //TODO

    }

    private void buildStartSection() {
        add(START_GROUP_NAME, new TextPropertyValue(START_WITH_CHANGE_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setStartChange(newValue)), CallbackStyle.WHEN_FINISHED);

        add(START_GROUP_NAME, new IntegerPropertyValue(START_AT_ROW_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setStartAtRow(newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        add(START_GROUP_NAME, new SelectionPropertyValue(START_STROKE_PROPERTY_NAME));
        ((SelectionPropertyValue) findPropertyByName(START_STROKE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                final Stroke startStroke = Stroke.values()[newValue.intValue()];
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setStartStroke(startStroke));
            }
        });

        add(START_GROUP_NAME, new TextPropertyValue(START_NOTATION_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(START_NOTATION_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setStartNotation(newValue)), CallbackStyle.WHEN_FINISHED);

        showGroupByName(START_GROUP_NAME, false); // TODO save state in app

    }

    private void updateStartSection(Optional<CompositionDocument> compositionDocument) {
        final String startChange = compositionDocument.isPresent() ? compositionDocument.get().getStartChange() : "";
        ((TextPropertyValue) findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setValue(startChange);

        int startAtRow = compositionDocument.isPresent() ? compositionDocument.get().getStartAtRow() : 0;
        ((IntegerPropertyValue) findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setValue(startAtRow);

        final List<String> startAtStrokeItems = new ArrayList<>();
        for (Stroke stroke : Stroke.values()) {
            startAtStrokeItems.add(stroke.getDisplayString());
        }
        ((SelectionPropertyValue) findPropertyByName(START_STROKE_PROPERTY_NAME)).setItems(startAtStrokeItems);
        final int startStroke = compositionDocument.isPresent() ? compositionDocument.get().getStartStroke().ordinal() : -1;
        ((SelectionPropertyValue) findPropertyByName(START_STROKE_PROPERTY_NAME)).setSelectedIndex(startStroke);

        final String startNotation = compositionDocument.isPresent() ? compositionDocument.get().getStartNotation() : "";
        ((TextPropertyValue) findPropertyByName(START_NOTATION_PROPERTY_NAME)).setValue(startNotation);
    }

    private void buildTerminationSection() {
        add(TERMINATION_GROUP_NAME, new TextPropertyValue(TERMINATION_WITH_CHANGE_PROPERTY_NAME));
        ((TextPropertyValue) findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setTerminationChange(newValue)), CallbackStyle.WHEN_FINISHED);

        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_ROW_LIMIT_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_ROW_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setTerminationMaxRows(newValue == null ? 0 : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_LEAD_LIMIT_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_LEAD_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setTerminationMaxLeads(newValue == null ? null : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_PART_LIMIT_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_PART_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setTerminationMaxParts(newValue == null ? null : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_CIRCULAR_COMPOSITION_LIMIT_PROPERTY_NAME));
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_CIRCULAR_COMPOSITION_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
                updateCompositionDocumentIfPresent(compositionDocument -> compositionDocument.setTerminationCircularComposition(newValue == null ? null : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

        showGroupByName(TERMINATION_GROUP_NAME, false); // TODO save state in app
    }

    private void updateTerminationSection(Optional<CompositionDocument> compositionDocument) {
        final String terminationChange = compositionDocument.isPresent() ? compositionDocument.get().getTerminationChange() : "";
        ((TextPropertyValue) findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setValue(terminationChange);

        int terminationRowLimit = compositionDocument.isPresent() ? compositionDocument.get().getTerminationMaxRows() : 0;
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_ROW_LIMIT_PROPERTY_NAME)).setValue(terminationRowLimit);

        Integer terminationLeadLimit = compositionDocument.isPresent() ? compositionDocument.get().getTerminationMaxLeads() : null;
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_LEAD_LIMIT_PROPERTY_NAME)).setValue(terminationLeadLimit);

        Integer terminationPartLimit = compositionDocument.isPresent() ? compositionDocument.get().getTerminationMaxParts() : null;
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_PART_LIMIT_PROPERTY_NAME)).setValue(terminationPartLimit);

        Integer terminationCircularCompositionLimit = compositionDocument.isPresent() ? compositionDocument.get().getTerminationCircularComposition() : null;
        ((IntegerPropertyValue) findPropertyByName(TERMINATION_CIRCULAR_COMPOSITION_LIMIT_PROPERTY_NAME)).setValue(terminationCircularCompositionLimit);

    }

    void updateCompositionDocumentIfPresent(Consumer<CompositionDocument> consumer) {
        Optional<CompositionDocument> currentDocument = compositionDocumentTypeManager.getCurrentDocument();
        if (currentDocument.isPresent()) {
            // The runLater is to prevent the UI from continuously applying the same wrong update when loosing focus
            // and switching focus to an error window.
            Platform.runLater(() -> consumer.accept(currentDocument.get()));
        }
    }

    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
        this.compositionDocumentTypeManager = compositionDocumentTypeManager;
    }
}
