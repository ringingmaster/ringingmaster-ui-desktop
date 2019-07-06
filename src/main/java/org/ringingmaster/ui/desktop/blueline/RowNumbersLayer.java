package org.ringingmaster.ui.desktop.blueline;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Lead;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class RowNumbersLayer extends Canvas {

    private final Logger log = LoggerFactory.getLogger(RowNumbersLayer.class);

    public void draw(Optional<Method> method, BlueLineDimension dimension) {
        setWidth(dimension.getWidth());
        setHeight(dimension.getHeight());

        GraphicsContext gc = getGraphicsContext2D();
        gc.setFontSmoothingType(FontSmoothingType.LCD);
        clearBackground(gc);

        if (method.isPresent()) {
            drawRowNumbers(method.get(), dimension, gc);
        }
    }

    private void drawRowNumbers(Method method, BlueLineDimension dim, GraphicsContext gc) {

        int verticalPosition = dim.getRowVerticalSpacing();

        for (Lead lead : method) {
            for (Row row : lead) {
                int horizontalPosition = dim.getRowHorizontalSpacing();
                for (Bell bell : row) {
                    switch (bell) {
                        case BELL_1:
                            gc.setFill(Color.BLUE);
                            break;
                        case BELL_2:
                            gc.setFill(Color.RED);
                            break;
                        default:
                            gc.setFill(Color.BLACK);
                    }

                    String displayString = bell.getMnemonic();

                    gc.fillText(displayString, horizontalPosition, verticalPosition);
                    horizontalPosition += dim.getRowHorizontalSpacing();
                }
                verticalPosition += dim.getRowVerticalSpacing();
            }
            verticalPosition += dim.getLeadVerticalSpacing();
        }
    }

    private void clearBackground(GraphicsContext gc) {
        gc.clearRect(0, 0, getWidth(), getHeight());
    }

}