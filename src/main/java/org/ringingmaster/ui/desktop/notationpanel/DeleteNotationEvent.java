package org.ringingmaster.ui.desktop.notationpanel;

import javafx.event.ActionEvent;
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
public class DeleteNotationEvent extends SkeletalEventDefinition implements EventDefinition {

    public static final String TOOLTIP_BAST_TEXT = "Remove method";

    private CompositionDocumentTypeManager compositionDocumentTypeManager;
    private PropertyNotationPanel propertyNotationPanel;


    public DeleteNotationEvent() {
        super("/images/remove.png", TOOLTIP_BAST_TEXT);
        tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
    }

    @Override
    public void handle(ActionEvent event) {
        int index = propertyNotationPanel.getSelectionModel().getSelectedIndex();
        Notation notation = propertyNotationPanel.getNotation(index);
        Optional<CompositionDocument> currentDocument = compositionDocumentTypeManager.getCurrentDocument();
        if (currentDocument.isPresent()) {
            currentDocument.get().removeNotation(notation);
        }
    }

    public void setPropertyNotationPanel(PropertyNotationPanel propertyNotationPanel) {
        this.propertyNotationPanel = propertyNotationPanel;

        propertyNotationPanel.addListener(selectedNotation -> {
            disableProperty().set(!selectedNotation.isPresent());

            if (selectedNotation.isPresent()) {
                tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT + " '" + selectedNotation.get().getNameIncludingNumberOfBells() + "'");
            } else {
                tooltipTextProperty().setValue(TOOLTIP_BAST_TEXT);
            }

        });
    }

    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
        this.compositionDocumentTypeManager = compositionDocumentTypeManager;
    }
}
