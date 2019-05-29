package org.ringingmaster.ui.desktop.notationpanel;

import io.reactivex.subjects.BehaviorSubject;
import javafx.event.ActionEvent;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocumentTypeManager;
import org.ringingmaster.ui.desktop.notationeditor.NotationEditorDialogFactory;
import org.ringingmaster.ui.desktop.setuppanel.AddNotationHandler;
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
public class AddNotationEvent extends SkeletalEventDefinition implements EventDefinition {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private CompositionDocumentTypeManager compositionDocumentTypeManager;
    private PropertyNotationPanel propertyNotationPanel;
    private NotationEditorDialogFactory notationEditorDialogFactory;

    private final BehaviorSubject<Boolean> handleEvent = BehaviorSubject.create();


    public AddNotationEvent() {
        super("/images/add.png", "Add Method");
        tooltipTextProperty().setValue("Add a new method");
    }

    public void init() {
        // Disable
        compositionDocumentTypeManager.observableActiveCompositionDocument()
                .subscribe(document -> disableProperty().set(document.isEmpty()));

        // Add notation
        handleEvent
                .withLatestFrom(compositionDocumentTypeManager.observableActiveMutableComposition(), (click,mutableComposition) -> mutableComposition)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribe(this::addNotation);
    }

    private void addNotation(MutableComposition composition) {
        NumberOfBells numberOfBells = composition.get().getNumberOfBells();
        notationEditorDialogFactory.newNotationShowDialog(numberOfBells, result -> new AddNotationHandler().handle(composition, result));
    }

    @Override
    public void handle(ActionEvent event) {
        handleEvent.onNext(true);
    }

    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
        this.compositionDocumentTypeManager = compositionDocumentTypeManager;
    }

    public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
        this.propertyNotationPanel = propertyNotationPanel;
    }

    public void setNotationEditorDialogFactory(NotationEditorDialogFactory notationEditorDialogFactory) {
        this.notationEditorDialogFactory = notationEditorDialogFactory;
    }
}
