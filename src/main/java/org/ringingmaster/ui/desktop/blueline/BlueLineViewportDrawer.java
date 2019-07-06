package org.ringingmaster.ui.desktop.blueline;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.ringingmaster.util.javafx.virtualcanvas.ViewportDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class BlueLineViewportDrawer implements ViewportDrawer {

    private final Logger log = LoggerFactory.getLogger(BlueLineViewportDrawer.class);

    @Override
    public void draw(GraphicsContext gc, double width, double height, double hOffset, double yOffset) {
        log.info("************** [{}], [{}]", hOffset, yOffset);



        gc.clearRect(0, 0, width, height);

        gc.setLineWidth(1.0);

        gc.setFill(Color.RED);
        gc.setStroke(Color.RED);

        gc.strokeLine(-100-hOffset,-100, 100-hOffset,300);

    }
}
