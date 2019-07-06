package org.ringingmaster.ui.desktop.compositiondocument.method;

import javafx.scene.layout.GridPane;
import org.ringingmaster.ui.desktop.blueline.BlueLineViewportDrawer;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.util.javafx.virtualcanvas.VirtualCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class MethodPane extends GridPane {

    private final Logger log = LoggerFactory.getLogger(MethodPane.class);

//    private final BlueLinePane blueLinePane = new BlueLinePane();
    VirtualCanvas virtualCanvas;
    public MethodPane() {

        virtualCanvas = new VirtualCanvas(new BlueLineViewportDrawer());

        virtualCanvas.setMinWidth(1500);
        virtualCanvas.setMinHeight(1500);

        setMinWidth(500);
        setMinHeight(500);
        setWidth(500);
        setHeight(500);

        addColumn(0,virtualCanvas);



    }


    public void init(CompositionDocument compositionDocument) {

    }


    //    public void init (CompositionDocument  compositionDocument) {
////        layoutNodes();
////        blueLinePane.setMethod(compositionDocument.observableProof().map(p -> p.getCompiledComposition().getMethod()));
//    }
//
//    private void layoutNodes() {
//
//        VBox verticalLayout = new VBox(resizableCanvas);
//        verticalLayout.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
//        verticalLayout.setSpacing(20.0);
//
//        BorderPane border = new BorderPane(verticalLayout);
//        BorderPane.setMargin(verticalLayout, new javafx.geometry.Insets(20));
//        border.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
//
//        setContent(border);
//        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
//        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
//
//        setFitToHeight(true);
//        setFitToWidth(true);
//        setFocusTraversable(false);
//    }

}