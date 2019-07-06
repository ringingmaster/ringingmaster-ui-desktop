package org.ringingmaster.ui.desktop.blueline;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ResizableCanvas extends Canvas {

    private final Logger log = LoggerFactory.getLogger(ResizableCanvas.class);

    public ResizableCanvas() {
        super(600,600);

        log.info("**************A");


        /*
         * Make sure the canvas draws its content again when its size
         * changes.
         */
        widthProperty().addListener(it -> draw(0,0));
        heightProperty().addListener(it -> draw(0,0));

        setWidth(500);
        setWidth(500);
    }

    /*
     * Draw a chart based on the data provided by the model.
     */
    public void draw(double hOffset, double yOffset) {

        log.info("************** [{}], [{}]", hOffset, yOffset);
        GraphicsContext gc = getGraphicsContext2D();

    }
}
