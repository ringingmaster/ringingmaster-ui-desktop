package org.ringingmaster.ui.desktop.compositiondocument;

import com.google.common.collect.Lists;
import io.reactivex.Observable;
import javafx.stage.FileChooser;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.ui.desktop.documentmanager.CompositionPersistence;
import org.ringingmaster.ui.desktop.documentmanager.Document;
import org.ringingmaster.ui.desktop.documentmanager.DocumentManager;
import org.ringingmaster.ui.desktop.documentmanager.DocumentTypeManager;
import org.ringingmaster.ui.desktop.proof.ProofManager;
import org.ringingmaster.util.beanfactory.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.LEAD_BASED;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CompositionDocumentTypeManager implements DocumentTypeManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String DOCUMENT_TYPE_NAME = "Composition";
    private BeanFactory beanFactory;
    private ProofManager proofManager;
    private DocumentManager documentManager;
    private CompositionPersistence CompositionPersistence = new CompositionPersistence();
    private Optional<CompositionDocument> currentDocument = Optional.empty();

    private int docNumber = 0;

    public Observable<Optional<CompositionDocument>> observableActiveCompositionDocument() {
        return documentManager.observableActiveDocument()
                .map(activeDocument -> activeDocument.map(document -> (document instanceof CompositionDocument)?(CompositionDocument) document: null))
                .distinctUntilChanged();
    }

    public Observable<Optional<ObservableComposition>> observableActiveObservableComposition() {
        return observableActiveCompositionDocument()
                .map(opt -> opt.map(CompositionDocument::getObservableComposition));
    }

    public Observable<Optional<Composition>> observableComposition() {
        return observableActiveCompositionDocument().switchMap(compositionDocument -> {
            if (compositionDocument.isPresent()) {
                return compositionDocument.get().observableComposition().map(Optional::of);
            } else {
                return Observable.just(Optional.empty());
            }
        });
    }

    @Override
    public Document createNewDocument() {
        ObservableComposition composition = createEmptyComposition();
        final CompositionDocument compositionDocument = buildCompositionDocumentForComposition(composition);
        compositionDocument.setDocumentName("Untitled " + DOCUMENT_TYPE_NAME + " " + ++docNumber);
        compositionDocument.setDirty(true);
        return compositionDocument;
    }

    @Override
    public Document openDocument(Path path) {
        //TODO reactive
//        Composition composition = CompositionPersistence.load(path);
//        CompositionDocument compositionDocument = buildCompositionDocumentForComposition(composition);
//        compositionDocument.setPath(path);
//        return compositionDocument;
        return null;
    }

    @Override
    public void saveDocument(Document document) {
        CompositionDocument compositionDocument = (CompositionDocument) document;
        Path path = document.getPath();
        Composition composition = compositionDocument.getComposition();
        CompositionPersistence.save(path, composition);
        document.setDirty(false);
    }

    public Collection<FileChooser.ExtensionFilter> getFileChooserExtensionFilters() {
        return Lists.newArrayList(new FileChooser.ExtensionFilter("Composition Files", "*.composition"));
    }

    @Override
    public String getDocumentTypeName() {
        return DOCUMENT_TYPE_NAME;
    }


    private CompositionDocument buildCompositionDocumentForComposition(ObservableComposition composition) {
        final CompositionDocument compositionDocument = beanFactory.build(CompositionDocument.class);
        compositionDocument.init(composition);
        // this is needed to listen to document updates that are not changed with proof's.
        // example -> alter the start change to something that will fail, and this will
        // allow it to be put back to its original value.
        //TODO Reactive compositionDocument.addListener(compositionDoc -> fireUpdateDocument());
        compositionDocument.setDirty(false);
        return compositionDocument;
    }

    @Deprecated //Use the observables.
    public Optional<CompositionDocument> getCurrentDocument() {
        return currentDocument;
    }

    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;

        //TODO Temp notifications
        observableActiveCompositionDocument().subscribe(compositionDocument -> {

            currentDocument = compositionDocument;
            //currentDocument.parseAndProve();

            documentManager.updateTitles();
        });
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setProofManager(ProofManager proofManager) {
        this.proofManager = proofManager;
    }

    //TODO Make this return a new composition.
    private ObservableComposition createEmptyComposition() {
        ObservableComposition composition = new ObservableComposition();

        composition.setTitle("My Composition");
        composition.setAuthor("by Stephen");

        composition.setCompositionType(LEAD_BASED);
        composition.addNotation(buildPlainBobMinor());
        composition.addNotation(buildLittleBobMinor());
        composition.addNotation(buildPlainBobMinimus());
        composition.addNotation(buildPlainBobMajor());

        composition.addCharacters(MAIN_TABLE, 0, 0, "-s");
        composition.addCharacters(MAIN_TABLE, 1, 0, "p 3*");
        composition.addCharacters(MAIN_TABLE, 0, 1, "s-");

        composition.addDefinition("3*", "-s-");
        composition.addDefinition("tr", "sps");

        composition.setTerminationChange(MethodBuilder.buildRoundsRow(composition.get().getNumberOfBells()));

        return composition;
    }

    // TODO remove this
    private static Notation buildPlainBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Plain Bob")
                .setFoldedPalindromeNotationShorthand("-16-16-16", "12")
                .setCannedCalls()
                .setSpliceIdentifier("P")
                .build();
    }

    // TODO remove this
    private static Notation buildLittleBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("-16-14", "12")
                .setCannedCalls()
                .build();
    }

    // TODO remove this
    private static Notation buildPlainBobMinimus() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_4)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("-14-14", "12")
                .setCannedCalls()
                .build();
    }

    // TODO remove this
    public static Notation buildPlainBobMajor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_8)
                .setName("Plain Bob")
                .setFoldedPalindromeNotationShorthand("-18-18-18-18", "12")
                .addCall("MyCall", "C", "145678", true)
                .addCall("OtherCall", "O", "1234", false)
                .setSpliceIdentifier("X")
                .build();
    }
}
