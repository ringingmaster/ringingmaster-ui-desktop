package org.ringingmaster.ui.desktop.blueline;

import io.reactivex.Observable;
import javafx.scene.layout.Pane;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.util.javafx.virtualcanvas.VirtualCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class BlueLinePane extends Pane {

    private final Logger log = LoggerFactory.getLogger(BlueLinePane.class);

    private final RowNumbersLayer rowNumbersLayer = new RowNumbersLayer();
    private final BlueLineDimensionBuilder blueLineDimensionBuilder = new BlueLineDimensionBuilder();
    private final VirtualCanvas virtualCanvas;


    public BlueLinePane() {


        virtualCanvas = new VirtualCanvas(new BlueLineViewportDrawer());

        virtualCanvas.setMinWidth(500);
        virtualCanvas.setMinHeight(500);



//        ResizableCanvas sc = new ResizableCanvas();
//        getChildren().add(rowNumbersLayer);
//        getChildren().add(sc);

        setMinWidth(500);
        setMinHeight(500);
    }

    public void setMethod(Observable<Optional<Method>> observableMethod) {

//        observableMethod
//                .map(method -> Pair.of(method, blueLineDimensionBuilder.build(method)))
//                .observeOn(JavaFxScheduler.platform())
//                .subscribe(pair -> {
//                    setMinHeight(pair.getRight().getHeight());
//                    setMinWidth(pair.getRight().getWidth());
//                    rowNumbersLayer.draw(pair.getLeft(), pair.getRight());
//                });
    }
}
