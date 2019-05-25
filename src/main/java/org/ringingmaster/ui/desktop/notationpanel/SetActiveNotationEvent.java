package org.ringingmaster.ui.desktop.notationpanel;

import io.reactivex.Observable;
import javafx.event.ActionEvent;
import org.pcollections.PSet;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
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

    public static final String TOOLTIP_BAST_TEXT = "Set active method";

    private CompositionDocumentTypeManager compositionDocumentTypeManager;
    private PropertyNotationPanel propertyNotationPanel;

    public SetActiveNotationEvent() {
        super("/images/flag.png", TOOLTIP_BAST_TEXT);
        tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
    }

    public void init() {
        // Disable
        Observable<Optional<PSet<Notation>>> validNotationsStream = compositionDocumentTypeManager.observableComposition().map(o -> o.map(Composition::getValidNotations));
        Observable.combineLatest(propertyNotationPanel.observableSelectedNotation(), validNotationsStream,
                (notation, validNotations) -> notation.isPresent() &&
                        validNotations.isPresent() &&
                        validNotations.get().contains(notation.get()))
                .subscribe(enabled -> disableProperty().set(!enabled));


        //Tooltip
        propertyNotationPanel.observableSelectedNotation()
                .subscribe(selectedNotation -> {
                    if (selectedNotation.isPresent()) {??? Need to bring the logic from below.

                        tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
                    } else {
                        tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
                    }
                });

    }

        @Override
    public void handle(ActionEvent event) {
        int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
        Notation selectedNotation = propertyNotationPanel.getNotation(index);

        if (selectedNotation != null) {
            if (compositionDocumentTypeManager.getCurrentDocument().get().isSpliced()) {
                compositionDocumentTypeManager.getCurrentDocument().get().setSpliced(false);
                compositionDocumentTypeManager.getCurrentDocument().get().setSingleMethodActiveNotation(selectedNotation);
            } else {
                // Not Spliced
                if (compositionDocumentTypeManager.getCurrentDocument().get().getSingleMethodActiveNotation() == selectedNotation) {
                    compositionDocumentTypeManager.getCurrentDocument().get().setSpliced(true);
                } else {
                    compositionDocumentTypeManager.getCurrentDocument().get().setSingleMethodActiveNotation(selectedNotation);
                }
            }
        }
    }

    public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
        this.propertyNotationPanel = propertyNotationPanel;

        propertyNotationPanel.addListener(selectedNotation -> {
                    Optional<CompositionDocument> currentDocument = compositionDocumentTypeManager.getCurrentDocument();

                    pressedProperty().set(selectedNotation.isPresent() &&
                            currentDocument.isPresent() &&
                            currentDocument.get().getSingleMethodActiveNotation() == selectedNotation.get());

                    if (selectedNotation.isPresent() &&
                            currentDocument.isPresent()) {
                        if (!currentDocument.get().isSpliced() &&
                                currentDocument.get().getSingleMethodActiveNotation() == selectedNotation.get()) {
                            tooltipTextProperty().setValue("Set Spliced");
                        } else {
                            tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
                        }
                    } else {
                        tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
                    }
                }
        );

    }

    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
        this.compositionDocumentTypeManager = compositionDocumentTypeManager;
    }
}
