package org.ringingmaster.ui.desktop.notationpanel;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.event.ActionEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.pcollections.PSet;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocumentTypeManager;
import org.ringingmaster.util.javafx.events.EventDefinition;
import org.ringingmaster.util.javafx.events.SkeletalEventDefinition;

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class SetActiveNotationEvent extends SkeletalEventDefinition implements EventDefinition {

    public static final String TOOLTIP_BASE_TEXT = "Set active method";

    private CompositionDocumentTypeManager compositionDocumentTypeManager;
    private PropertyNotationPanel propertyNotationPanel;

    private final BehaviorSubject<Boolean> handleEvent = BehaviorSubject.create();


    public SetActiveNotationEvent() {
        super("/images/flag.png", TOOLTIP_BASE_TEXT);
        tooltipTextProperty().setValue(TOOLTIP_BASE_TEXT);
    }

    public void init() {
        // Disable
        Observable<Optional<PSet<Notation>>> validNotationsStream = compositionDocumentTypeManager.observableComposition().map(o -> o.map(Composition::getValidNotations));

        Observable.combineLatest(propertyNotationPanel.observableSelectedNotation(), validNotationsStream,
                (notation, validNotations) -> notation.isPresent() &&
                        validNotations.isPresent() &&
                        validNotations.get().contains(notation.get()))
                .subscribe(enabled -> disableProperty().set(!enabled));

        // Pressed
        Observable<Pair<Optional<Notation>, Optional<Composition>>> selectedNotationAndComposition = Observable.combineLatest(propertyNotationPanel.observableSelectedNotation(), compositionDocumentTypeManager.observableComposition(), Pair::of);
        selectedNotationAndComposition.subscribe(pair -> {
            Optional<Notation> selectedNotation = pair.getLeft();
            Optional<Composition> composition = pair.getRight();
            pressedProperty().set(
                    selectedNotation.isPresent() &&
                            composition.isPresent() &&
                            composition.get().getNonSplicedActiveNotation().equals(selectedNotation));
        });

        //Tooltip
        selectedNotationAndComposition.subscribe(pair -> {
            Optional<Notation> selectedNotation = pair.getLeft();
            Optional<Composition> composition = pair.getRight();
            if (selectedNotation.isPresent() &&
                    composition.isPresent()) {
                if (!composition.get().isSpliced() &&
                        composition.get().getNonSplicedActiveNotation().equals(selectedNotation)) {
                    tooltipTextProperty().setValue("Set Spliced");
                } else {
                    tooltipTextProperty().setValue(TOOLTIP_BASE_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
                }
            } else {
                tooltipTextProperty().setValue(TOOLTIP_BASE_TEXT);
            }
        });

        // Select notation
        Observable<Notation> handleEventNotation =
                handleEvent.withLatestFrom(propertyNotationPanel.observableSelectedNotation(), (aVoid, notation) -> notation)
                        .filter(Optional::isPresent)
                        .map(Optional::get);

        handleEventNotation
                .withLatestFrom(compositionDocumentTypeManager.observableActiveMutableComposition(), Pair::of)
                .filter(pair -> pair.getRight().isPresent())
                .map(pair -> Pair.of(pair.getLeft(), pair.getRight().get()))
                .subscribe(pair -> selectActiveOrSpliced(pair.getLeft(), pair.getRight()));

    }

    private void selectActiveOrSpliced(Notation notation, MutableComposition composition) {
        if (composition.get().isSpliced()) {
            composition.setNonSplicedActiveNotation(notation);
        }
        else {
            if (composition.get().getNonSplicedActiveNotation().isPresent() &&
                    composition.get().getNonSplicedActiveNotation().get() == notation) {
                composition.setSpliced(true);
            }
            else {
                composition.setNonSplicedActiveNotation(notation);
            }

        }
    }

    @Override
    public void handle(ActionEvent event) {
        handleEvent.onNext(true);
    }

    public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
        this.propertyNotationPanel = propertyNotationPanel;
    }

    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
        this.compositionDocumentTypeManager = compositionDocumentTypeManager;
    }
}
