package org.ringingmaster.ui.desktop.compositiondocument;

import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ringingmaster.engine.analyser.Analyser;
import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.engine.compiler.Compiler;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.blueline.BlueLineViewportDrawer;
import org.ringingmaster.ui.desktop.compositiondocument.composition.CompositionPane;
import org.ringingmaster.ui.desktop.compositiondocument.method.MethodPane;
import org.ringingmaster.ui.desktop.documentmanager.DefaultDocument;
import org.ringingmaster.ui.desktop.documentmanager.Document;
import org.ringingmaster.util.javafx.virtualcanvas.VirtualCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.ringingmaster.engine.notation.PlaceSetSequence.BY_NUMBER_THEN_NAME;

/**
 * Provides the interface between the engine {@code Composition} and the various
 * UI components.
 *
 * @author Steve Lake
 */

// TODO ALL business logic in this class should be in the engine.
public class CompositionDocument extends TabPane implements Document {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String TAB_STYLESHEET = "org/ringingmaster/ui/desktop/compositiondocument/tab.css";

    public static final String SPLICED_TOKEN = "<Spliced>";

    //Raw Data
    private MutableComposition composition;
    private final CompositionStyle compositionStyle = new CompositionStyle(); //TODO Eventually be Observable
    private Document documentDelegate = new DefaultDocument();


    private Observable<Parse> observableParse;
    private Observable<CompiledComposition> observableCompiledComposition;
    private Observable<Proof> observableProof;

    private final CompositionPane compositionPane = new CompositionPane();
    private final MethodPane methodPane = new MethodPane();


    public CompositionDocument() {
    }

    public void init(MutableComposition composition) {
        this.composition = composition;

        observableParse = composition.observable()
                .distinctUntilChanged()// need this because of renotification
                .map(new Parser()::apply)
                .replay(1)
                .autoConnect(1);

        observableCompiledComposition = observableParse
                .observeOn(Schedulers.computation())
                .map(new Compiler()::apply)
                .replay(1)
                .autoConnect(1);

        observableProof = observableCompiledComposition
                .observeOn(Schedulers.computation())
                .map(new Analyser()::apply)
                .replay(1)
                .autoConnect(1);

        composition.observable()
                .distinct()
                .subscribe(composition1 ->
                        setDirty(true));

        compositionPane.init(this);
        methodPane.init(this);

        setSide(Side.BOTTOM);
        getStylesheets().add(TAB_STYLESHEET);
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        VirtualCanvas virtualCanvas = new VirtualCanvas(new BlueLineViewportDrawer());

        virtualCanvas.setMinWidth(1500);
        virtualCanvas.setMinHeight(1500);


        getTabs().add(new Tab("TOOD", virtualCanvas));
        getTabs().add(new Tab("Blue Line", methodPane));
        getTabs().add(new Tab("Composition", compositionPane));

    }

    public Observable<Composition> observableComposition() {
        return composition.observable();
    }

    public Observable<Parse> observableParse() {
        return observableParse;
    }

    public Observable<CompiledComposition> observableCompiledComposition() {
        return observableCompiledComposition;
    }

    public Observable<Proof> observableProof() {
        return observableProof;
    }


    public MutableComposition getMutableComposition() {
        return composition;
    }

    public Composition getComposition() {
        return composition.get();
    }

    @Deprecated
    public List<Notation> getSortedAllNotations() {
        final List<Notation> sortedNotations = Lists.newArrayList(composition.get().getAllNotations());
        Collections.sort(sortedNotations, BY_NUMBER_THEN_NAME);
        return sortedNotations;
    }


    public CompositionStyle getCompositionStyle() {
        return compositionStyle;
    }

    @Override
    public boolean hasFileLocation() {
        return documentDelegate.hasFileLocation();
    }

    @Override
    public boolean isDirty() {
        return documentDelegate.isDirty();
    }

    @Override
    public Observable<Boolean> observableDirty() {
        return documentDelegate.observableDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        documentDelegate.setDirty(dirty);
    }

    @Override
    public Optional<Path> getPath() {
        return documentDelegate.getPath();
    }

    @Override
    public void setPath(Path path) {
        documentDelegate.setPath(path);
    }

    @Override
    public Observable<Optional<Path>> observablePath() {
        return documentDelegate.observablePath();
    }

    @Override
    public Observable<String> observableFallbackName() {
        return composition.observable().map(Composition::getTitle);
    }

    @Override
    public String getFallbackName() {
        return composition.get().getTitle();
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public String toString() {
        return "CompositionDocument{" +
                "path=" + getPath() +
                ", title=" + getFallbackName() +
                '}';
    }
}
