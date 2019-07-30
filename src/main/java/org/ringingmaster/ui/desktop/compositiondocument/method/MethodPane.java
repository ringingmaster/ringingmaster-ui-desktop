package org.ringingmaster.ui.desktop.compositiondocument.method;

import io.reactivex.Observable;
import javafx.scene.layout.BorderPane;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.ui.desktop.methodrenderer.MethodRendererPane;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class MethodPane extends BorderPane {

    private final MethodRendererPane methodRendererPane = new MethodRendererPane();

    public MethodPane() {

        setCenter(methodRendererPane);

    }

    public void init(CompositionDocument compositionDocument) {

        Observable<Optional<Method>> observableMethod = compositionDocument.observableCompiledComposition()
                .map(CompiledComposition::getMethod);

        methodRendererPane.setMethod(observableMethod);
    }
}
