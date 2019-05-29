package org.ringingmaster.ui.desktop.notationpanel;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.event.ActionEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocumentTypeManager;
import org.ringingmaster.ui.desktop.notationeditor.NotationEditorDialogFactory;
import org.ringingmaster.ui.desktop.setuppanel.ExchangeNotationHandler;
import org.ringingmaster.util.javafx.events.EventDefinition;
import org.ringingmaster.util.javafx.events.SkeletalEventDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class EditNotationEvent extends SkeletalEventDefinition implements EventDefinition {

    private final static Logger log = LoggerFactory.getLogger(EditNotationEvent.class);
    public static final String TOOLTIP_BAST_TEXT = "Edit method";

    private CompositionDocumentTypeManager compositionDocumentTypeManager;
    private PropertyNotationPanel propertyNotationPanel;
    private NotationEditorDialogFactory notationEditorDialogFactory;

    private final BehaviorSubject<Boolean> handleEvent = BehaviorSubject.create();


    public EditNotationEvent() {
        super("/images/edit.png", TOOLTIP_BAST_TEXT);
        tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
    }

    public void init() {
        //Disable
        propertyNotationPanel.observableSelectedNotation()
                .subscribe(selectedNotation -> disableProperty().set(selectedNotation.isEmpty()));

        //Tool Tip
        propertyNotationPanel.observableSelectedNotation()
                .subscribe(selectedNotation -> {
                    if (selectedNotation.isPresent()) {
                        tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
                    } else {
                        tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
                    }
                });

        // Edit notation
        Observable<Notation> handleEventNotation =
                handleEvent.withLatestFrom(propertyNotationPanel.observableSelectedNotation(), (aVoid, notation) -> notation)
                .filter(Optional::isPresent)
                .map(Optional::get);

        Observable.merge(handleEventNotation, propertyNotationPanel.observableDoubleClickedNotation())
                .withLatestFrom(compositionDocumentTypeManager.observableActiveMutableComposition(), Pair::of)
                .filter(pair -> pair.getRight().isPresent())
                .map(pair -> Pair.of(pair.getLeft(), pair.getRight().get()))
                .subscribe(pair -> editNotation(pair.getLeft(), pair.getRight()));
    }

    @Override
    public void handle(ActionEvent event) {
        handleEvent.onNext(true);
    }

    private void editNotation(Notation notation, MutableComposition composition) {
        notationEditorDialogFactory.editNotationShowDialog(notation, result -> new ExchangeNotationHandler().handle(composition, notation, result));
    }

    public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
        this.propertyNotationPanel = propertyNotationPanel;
    }

    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
        this.compositionDocumentTypeManager = compositionDocumentTypeManager;
    }

    public void setNotationEditorDialogFactory(NotationEditorDialogFactory notationEditorDialogFactory) {
        this.notationEditorDialogFactory = notationEditorDialogFactory;
    }
}
