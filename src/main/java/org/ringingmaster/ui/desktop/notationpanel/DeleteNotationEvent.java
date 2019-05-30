package org.ringingmaster.ui.desktop.notationpanel;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.event.ActionEvent;
import org.apache.commons.lang3.tuple.Pair;
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
public class DeleteNotationEvent extends SkeletalEventDefinition implements EventDefinition {

    public static final String TOOLTIP_BASE_TEXT = "Remove method";

    private CompositionDocumentTypeManager compositionDocumentTypeManager;
    private PropertyNotationPanel propertyNotationPanel;

    private final BehaviorSubject<Boolean> handleEvent = BehaviorSubject.create();


    public DeleteNotationEvent() {
        super("/images/remove.png", TOOLTIP_BASE_TEXT);
        tooltipTextProperty().setValue(TOOLTIP_BASE_TEXT);
    }

    public void init() {
        // Disable
        propertyNotationPanel.observableSelectedNotation()
                .map(Optional::isPresent)
                .subscribe(present -> disableProperty().set(!present));

        //Tool Tip
        propertyNotationPanel.observableSelectedNotation()
                .subscribe(selectedNotation -> {
                    if (selectedNotation.isPresent()) {
                        tooltipTextProperty().setValue(TOOLTIP_BASE_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
                    } else {
                        tooltipTextProperty().setValue(TOOLTIP_BASE_TEXT);
                    }
                });

        // Edit notation
        Observable<Notation> handleEventNotation =
                handleEvent.withLatestFrom(propertyNotationPanel.observableSelectedNotation(), (aVoid, notation) -> notation)
                        .filter(Optional::isPresent)
                        .map(Optional::get);

        handleEventNotation
                .withLatestFrom(compositionDocumentTypeManager.observableActiveMutableComposition(), Pair::of)
                .filter(pair -> pair.getRight().isPresent())
                .map(pair -> Pair.of(pair.getLeft(), pair.getRight().get()))
                .subscribe(pair -> removeNotation(pair.getLeft(), pair.getRight()));

    }

    private void removeNotation(Notation notation, MutableComposition composition) {
        //	TODO			Also do checks thate the notation can be removed
//	TODO			Also what happens to active method.

        composition.removeNotation(notation);
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
