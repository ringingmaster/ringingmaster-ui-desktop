package org.ringingmaster.ui.desktop.notationpanel;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocumentTypeManager;
import org.ringingmaster.util.javafx.namevaluepair.NameValuePairModel;
import org.ringingmaster.util.javafx.namevaluepair.NameValuePairTable;
import org.ringingmaster.util.listener.ConcurrentListenable;
import org.ringingmaster.util.listener.Listenable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class PropertyNotationPanel extends NameValuePairTable implements Listenable<PropertyNotationPanelListener> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ConcurrentListenable<PropertyNotationPanelListener> listenableDelegate = new ConcurrentListenable<>();

    private CompositionDocumentTypeManager compositionDocumentTypeManager;
    private EditNotationEvent editNotationEvent;

    public PropertyNotationPanel() {
        //TODO add an empty table hint i.e. setPlaceholder(new Label("No Calls Defined"));
    }

    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
        this.compositionDocumentTypeManager = compositionDocumentTypeManager;
        compositionDocumentTypeManager.observableActiveCompositionDocument().subscribe(this::updateToReflectDocument);

        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editNotationEvent.handle(null);
            }
        });

        getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            fireSelectionChange();
        });
    }

    private void updateToReflectDocument(Optional<CompositionDocument> compositionDocument) {
        if (hasMethodListChanged(compositionDocument)) {
            rebuildMethodList(compositionDocument);
        }
        updateMethodList(compositionDocument);
        fireSelectionChange();
    }

    private void fireSelectionChange() {

        int selectedIndex = getSelectionModel().getSelectedIndex();
        Optional<Notation> selectedNotation = Optional.ofNullable(getNotation(selectedIndex));

        for (PropertyNotationPanelListener propertyNotationPanelListener : listenableDelegate.getListeners()) {
            propertyNotationPanelListener.propertyMethod_setSelectedNotation(selectedNotation);

        }
    }

    private void rebuildMethodList(Optional<CompositionDocument> compositionDocument) {
        int selectedIndex = getSelectionModel().getSelectedIndex();
        getItems().clear();
        if (!compositionDocument.isPresent()) {
            return;
        }

        for (Notation notation : compositionDocument.get().getSortedAllNotations()) {
            getItems().add(new NameValuePairModel(getDisplayName(notation)));
        }

        if (selectedIndex >= 0) {
            if (selectedIndex >= getItems().size()) {
                selectedIndex = getItems().size() - 1;
            }
        }
        if (selectedIndex >= 0) {
            getSelectionModel().select(selectedIndex);
        }
    }

    private boolean hasMethodListChanged(Optional<CompositionDocument> compositionDocument) {
        if (!compositionDocument.isPresent()) {
            return (getItems().size() != 0);
        }
        List<Notation> allNotations = compositionDocument.get().getSortedAllNotations();
        Set<String> namesInDocument = allNotations.stream().map(this::getDisplayName).collect(Collectors.toSet());
        Set<String> namesInUI = getItems().stream().map(nameValuePairModel -> nameValuePairModel.getName().getText()).collect(Collectors.toSet());

        return Sets.symmetricDifference(namesInDocument, namesInUI).size() != 0;
    }

    private void updateMethodList(Optional<CompositionDocument> compositionDocument) {
        if (!compositionDocument.isPresent()) {
            return;
        }

        List<Notation> allNotations = compositionDocument.get().getSortedAllNotations();
        boolean spliced = compositionDocument.get().isSpliced();

        for (Notation notation : allNotations) {
            String name = getDisplayName(notation);

            if (notation.getNumberOfWorkingBells().toInt() > compositionDocument.get().getNumberOfBells().toInt()) {
                updateDisplayProperty(name, "Too many bells", true);
            } else if (spliced) {
                if (Strings.isNullOrEmpty(notation.getSpliceIdentifier())) {
                    updateDisplayProperty(name, "No splice letter", true);

                } else {
                    updateDisplayProperty(name, CompositionDocument.SPLICED_TOKEN + " " + notation.getSpliceIdentifier(), false);
                }
            } else if (notation == compositionDocument.get().getSingleMethodActiveNotation()) {
                updateDisplayProperty(name, "<Active>", false);
            } else {
                updateDisplayProperty(name, "", false);
            }
        }
    }

    private String getDisplayName(Notation notation) {
        return notation.getNameIncludingNumberOfBells();
    }


    public Notation getNotation(int index) {
        if (!compositionDocumentTypeManager.getCurrentDocument().isPresent()) {
            return null;
        }

        List<Notation> sortedAllNotations = compositionDocumentTypeManager.getCurrentDocument().get().getSortedAllNotations();
        if (index >= 0 && index < sortedAllNotations.size()) {
            return sortedAllNotations.get(index);
        }

        return null;
    }

    @Override
    public void addListener(PropertyNotationPanelListener listener) {
        listenableDelegate.addListener(listener);
    }

    public void setEditNotationEvent(EditNotationEvent editNotationEvent) {
        this.editNotationEvent = editNotationEvent;
    }
}
