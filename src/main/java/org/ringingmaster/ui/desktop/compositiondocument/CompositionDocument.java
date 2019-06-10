package org.ringingmaster.ui.desktop.compositiondocument;

import com.google.common.collect.Lists;
import io.reactivex.Observable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
import org.ringingmaster.ui.desktop.compositiondocument.gridmodel.DefinitionGridModel;
import org.ringingmaster.ui.desktop.compositiondocument.gridmodel.MainGridModel;
import org.ringingmaster.ui.desktop.documentmanager.DefaultDocument;
import org.ringingmaster.ui.desktop.documentmanager.Document;
import org.ringingmaster.util.javafx.color.ColorUtil;
import org.ringingmaster.util.javafx.grid.canvas.GridPane;
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
public class CompositionDocument extends ScrollPane implements Document {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String STYLESHEET = "org/ringingmaster/ui/desktop/documentpanel/titlepane.css"; //TODO using wrong CSS

    public static final String SPLICED_TOKEN = "<Spliced>";

    //Raw Data
    private MutableComposition composition;
    private final CompositionStyle compositionStyle = new CompositionStyle(); //TODO Eventually be Observable
    private Document documentDelegate = new DefaultDocument();

    private MainGridModel mainGridModel;
    private DefinitionGridModel definitionModel;

    private Observable<Parse> observableParse;
    private Observable<CompiledComposition> observableCompiledComposition;
    private Observable<Proof> observableProof;


    // UI components
    private final TitlePane titlePane = new TitlePane();
    private final GridPane gridPane = new GridPane("Main");
    private final TextField definitionText = new TextField("Definitions"); //TODO separate out
    private final GridPane definitionPane = new GridPane("Definition");

    public CompositionDocument() {
        titlePane.setCompositionStyle(compositionStyle);
    }

    public void init(MutableComposition composition) {

        this.composition = composition;
        observableParse = composition.observable()
                .distinctUntilChanged()// need this because of renotification
                .map(new Parser()::apply)
                .replay(1)
                .autoConnect(1);

        observableCompiledComposition = observableParse
                .map(new Compiler()::apply)
                .replay(1)
                .autoConnect(1);

        observableProof = observableCompiledComposition
                .map(new Analyser()::apply)
                .replay(1)
                .autoConnect(1);

        composition.observable()
                .distinct()
                .subscribe(composition1 ->
                        setDirty(true));

        layoutNodes();

        titlePane.init(composition);

        mainGridModel = new MainGridModel(this);
        gridPane.setModel(mainGridModel);

        //TODO package in its own class
        definitionText.getStylesheets().add(STYLESHEET);
        definitionText.setFont(new Font(14));
        // TODO make style reactive
        Color titleColor = compositionStyle.getColour(CompositionStyle.CompositionStyleColor.DEFINITION);
        definitionText.setStyle("-fx-text-inner-color: " + ColorUtil.toWeb(titleColor) + ";");

        definitionModel = new DefinitionGridModel(this);
        definitionPane.setModel(definitionModel);
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

    private void layoutNodes() {

        HBox definitionLayout = new HBox(10, new Pane(), definitionPane);

        VBox verticalLayout = new VBox(titlePane, gridPane, new Pane(), definitionText, definitionLayout);
        verticalLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        verticalLayout.setSpacing(20.0);

        BorderPane border = new BorderPane(verticalLayout);
        BorderPane.setMargin(verticalLayout, new Insets(20));
        border.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        setContent(border);
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        setFitToHeight(true);
        setFitToWidth(true);
        setFocusTraversable(false);
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
