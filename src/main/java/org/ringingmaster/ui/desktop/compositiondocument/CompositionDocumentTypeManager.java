package org.ringingmaster.ui.desktop.compositiondocument;

import com.google.common.collect.Lists;
import io.reactivex.Observable;
import javafx.stage.FileChooser;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.ui.desktop.documentmanager.Document;
import org.ringingmaster.ui.desktop.documentmanager.DocumentManager;
import org.ringingmaster.ui.desktop.documentmanager.DocumentTypeManager;
import org.ringingmaster.util.beanfactory.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;
import static org.ringingmaster.engine.composition.TerminationChange.Location.ANYWHERE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.LEAD_BASED;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class CompositionDocumentTypeManager implements DocumentTypeManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String DOCUMENT_TYPE_NAME = "Composition";
    private BeanFactory beanFactory;
    private DocumentManager documentManager;
    private CompositionPersistence compositionPersistence = new CompositionPersistence();
    private AtomicInteger docCount = new AtomicInteger(1);


    public Observable<Optional<CompositionDocument>> observableActiveCompositionDocument() {
        return documentManager.observableActiveDocument()
                .map(activeDocument -> activeDocument.map(document -> (document instanceof CompositionDocument)?(CompositionDocument) document: null))
                .distinctUntilChanged();
    }

    public Observable<Optional<MutableComposition>> observableActiveMutableComposition() {
        return observableActiveCompositionDocument()
                .map(opt -> opt.map(CompositionDocument::getMutableComposition));
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

    public Observable<Optional<Parse>> observableParse() {
        return observableActiveCompositionDocument().switchMap(compositionDocument -> {
            if (compositionDocument.isPresent()) {
                return compositionDocument.get().observableParse().map(Optional::of);
            } else {
                return Observable.just(Optional.empty());
            }
        });
    }

    public Observable<Optional<CompiledComposition>> observableCompiledComposition() {
        return observableActiveCompositionDocument().switchMap(compositionDocument -> {
            if (compositionDocument.isPresent()) {
                return compositionDocument.get().observableCompiledComposition().map(Optional::of);
            } else {
                return Observable.just(Optional.empty());
            }
        });
    }

    public Observable<Optional<Proof>> observableProof() {
        return observableActiveCompositionDocument().switchMap(compositionDocument -> {
            if (compositionDocument.isPresent()) {
                return compositionDocument.get().observableProof().map(Optional::of);
            } else {
                return Observable.just(Optional.empty());
            }
        });
    }


    @Override
    public Document createNewDocument() {
        MutableComposition composition = createEmptyComposition();
        final CompositionDocument compositionDocument = buildCompositionDocumentForComposition(composition);
        return compositionDocument;
    }

    @Override
    public Document openDocument(Path path) {
        MutableComposition composition = compositionPersistence.load(path);
        CompositionDocument compositionDocument = buildCompositionDocumentForComposition(composition);
        compositionDocument.setPath(path);
        return compositionDocument;
    }

    @Override
    public void saveDocument(Document document) {
        CompositionDocument compositionDocument = (CompositionDocument) document;
        Path path = document.getPath().get();
        Composition composition = compositionDocument.getComposition();
        compositionPersistence.save(path, composition);
        document.setDirty(false);
    }

    public Collection<FileChooser.ExtensionFilter> getFileChooserExtensionFilters() {
        return Lists.newArrayList(new FileChooser.ExtensionFilter("Composition Files", "*.composition"));
    }

    @Override
    public String getDocumentTypeName() {
        return DOCUMENT_TYPE_NAME;
    }


    private CompositionDocument buildCompositionDocumentForComposition(MutableComposition composition) {
        final CompositionDocument compositionDocument = beanFactory.build(CompositionDocument.class);
        compositionDocument.init(composition);
        return compositionDocument;
    }

    @Deprecated //Use the observables.
    public Optional<CompositionDocument> getCurrentDocument() {
        return null;
    }

    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    //TODO Make this return an empty composition.
    private MutableComposition createEmptyComposition() {
        MutableComposition composition = new MutableComposition();

        composition.setTitle("Untitled Composition " + docCount.getAndIncrement());
        composition.setAuthor(System.getProperty("user.name"));

        composition.setCompositionType(LEAD_BASED);
        composition.addNotation(buildPlainBobMinor());
        composition.addNotation(buildLittleBobMinor());
        composition.addNotation(buildPlainBobMinimus());
        composition.addNotation(buildPlainBobMajor());

        composition.addCharacters(COMPOSITION_TABLE, 0, 0, "-s");
        composition.addCharacters(COMPOSITION_TABLE, 1, 0, "p 3*");
        composition.addCharacters(COMPOSITION_TABLE, 0, 1, "s-");
        composition.addCharacters(COMPOSITION_TABLE, 1, 1, "[-os]");

        composition.addDefinition("3*", "-s-");
        composition.addDefinition("tr", "sps");

        composition.setTerminationChange(MethodBuilder.buildRoundsRow(composition.get().getNumberOfBells()), ANYWHERE);

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
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .build();
    }

    // TODO remove this
    private static Notation buildLittleBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("-16-14", "12")
                .setSpliceIdentifier("L")
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
    private static Notation buildPlainBobMajor() {
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
