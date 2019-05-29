package org.ringingmaster.ui.desktop.notationpanel;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.ringingmaster.engine.composition.Composition;
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

    private final BehaviorSubject<Integer> observableSelectedIndex = BehaviorSubject.createDefault(-1);
    private final BehaviorSubject<Integer> observableDoubleClickIndex = BehaviorSubject.create();
    private Observable<Optional<Notation>> selectedNotation; //TODO Reactive combine into RxJav streams
    private Observable<Notation> doubleClickedNotation; //TODO Reactive combine into RxJava streams


    public PropertyNotationPanel() {
        //TODO add an empty table hint i.e. setPlaceholder(new Label("No Calls Defined"));
    }

    public void init() {

        compositionDocumentTypeManager.observableComposition()
                .subscribe(this::updateToReflectDocument);

        getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            observableSelectedIndex.onNext(newValue.intValue());
        });

        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Integer index = observableSelectedIndex.getValue();
                observableDoubleClickIndex.onNext(index);
            }
        });

        Observable<Optional<List<Notation>>> observableSortedNotations = compositionDocumentTypeManager.observableComposition()
                .map(composition -> composition.map(this::getSortedNotations)); //TODO can we put getSortedNotations here?

        selectedNotation = Observable.combineLatest(observableSelectedIndex, observableSortedNotations,
                (index, notations) -> notations.map(notations1 -> (index == -1)?null:notations1.get(index)));

        doubleClickedNotation = observableDoubleClickIndex
                .withLatestFrom(selectedNotation, (i, s) -> s)
                .filter(Optional::isPresent)
                .map(Optional::get);

    }

    public Observable<Optional<Notation>> observableSelectedNotation() {
        return selectedNotation;
    }

    public Observable<Notation> observableDoubleClickedNotation() {
        return doubleClickedNotation;
    }

    private void updateToReflectDocument(Optional<Composition> composition) {
        if (hasMethodListChanged(composition)) {
            rebuildMethodList(composition);
        }
        updateMethodList(composition);
    }

    private boolean hasMethodListChanged(Optional<Composition> optionalComposition) {
        return optionalComposition
                .map(composition -> {
                    Set<String> namesInDocument = getSortedNotations(composition).stream().map(this::getStandardDisplayName).collect(Collectors.toSet());
                    Set<String> namesInUI = getItems().stream().map(nameValuePairModel -> nameValuePairModel.getName().getText()).collect(Collectors.toSet());
                    return Sets.symmetricDifference(namesInDocument, namesInUI).size() != 0;
                })
                .orElse(getItems().size() != 0);
    }

    private void rebuildMethodList(Optional<Composition> composition) {
        int previousSelectedIndex = getSelectionModel().getSelectedIndex();
        getItems().clear();
        if (composition.isEmpty()) {
            return;
        }

        for (Notation notation : getSortedNotations(composition.get())) {
            getItems().add(new NameValuePairModel(getStandardDisplayName(notation)));
        }

        if (previousSelectedIndex >= 0) {
            if (previousSelectedIndex >= getItems().size()) {
                previousSelectedIndex = getItems().size() - 1;
            }
        }
        if (previousSelectedIndex >= 0) {
            getSelectionModel().select(previousSelectedIndex);
        }
    }

    private void updateMethodList(Optional<Composition> composition) {
        if (composition.isEmpty()) {
            return;
        }

        List<Notation> allNotations = getSortedNotations(composition.get());

        for (Notation notation : allNotations) {
            String name = getStandardDisplayName(notation);

            if (notation.getNumberOfWorkingBells().compareTo(composition.get().getNumberOfBells()) > 0) {
                updateDisplayProperty(name, "Too many bells (> " + composition.get().getNumberOfBells() + ")", true);
            }
            else if (composition.get().isSpliced()) {
                if (Strings.isNullOrEmpty(notation.getSpliceIdentifier())) {
                    updateDisplayProperty(name, "No splice letter", true);
                }
                else {
                    updateDisplayProperty(name, CompositionDocument.SPLICED_TOKEN + " " + notation.getSpliceIdentifier(), false);
                }
            }
            else if (notation == composition.get().getNonSplicedActiveNotation().get()) {
                updateDisplayProperty(name, "<Active>", false);
            }
            else {
                updateDisplayProperty(name, "", false);
            }
        }
    }

    private List<Notation> getSortedNotations(Composition composition) {
        return composition.getAllNotations().stream()
                .sorted(Notation.BY_NUMBER_THEN_NAME)
                .collect(Collectors.toList());
    }

    //We do this for consistency between create and update
    private String getStandardDisplayName(Notation notation) {
        return notation.getNameIncludingNumberOfBells();
    }

    @Deprecated
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

    public void setCompositionDocumentTypeManager(CompositionDocumentTypeManager compositionDocumentTypeManager) {
        this.compositionDocumentTypeManager = compositionDocumentTypeManager;
    }
}
