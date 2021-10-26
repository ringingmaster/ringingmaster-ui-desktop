package org.ringingmaster.ui.desktop.methodrenderer;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;
import org.apache.commons.lang3.tuple.Pair;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Lead;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.ui.desktop.methodrenderer.dimension.Dimension;
import org.ringingmaster.ui.desktop.methodrenderer.dimension.DimensionBuilder;
import org.ringingmaster.ui.desktop.methodrenderer.methodstyle.BellStyle;
import org.ringingmaster.ui.desktop.methodrenderer.methodstyle.MethodStyle;
import org.ringingmaster.ui.desktop.methodrenderer.methodstyle.MethodStyleBuilder;
import org.ringingmaster.util.javafx.font.FontMetrics;
import org.ringingmaster.util.javafx.virtualcanvas.ViewportDrawer;
import org.ringingmaster.util.javafx.virtualcanvas.VirtualCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class MethodRendererPane extends VirtualCanvas {

    private final Logger log = LoggerFactory.getLogger(MethodRendererPane.class);

    private Optional<Method> method = Optional.empty();
    private Dimension dimension;
    private final MethodStyle style = new MethodStyleBuilder().defaultTextStyle().build(); //TODO Where do we store and select this? In the document??

    private final DimensionBuilder dimensionBuilder = new DimensionBuilder();


    private final ViewportDrawer methodLayer = (gc, viewport) -> {
        gc.setFontSmoothingType(FontSmoothingType.LCD); 
        clearBackground(gc);

        method.ifPresent(leads -> draw(leads, dimension, style, gc, viewport));
    };

    private final ViewportDrawer dummyLayer = (gc, viewport) -> {
        gc.setFontSmoothingType(FontSmoothingType.LCD);
        clearBackground(gc);

       //TODO  gc.strokeRect(100, 200, 300, 400);
    };


    public MethodRendererPane() {
        setLayers(methodLayer, dummyLayer);
    }


    public void setMethod(Observable<Optional<Method>> observableMethod) {

        observableMethod
                .map(method -> Pair.of(method, dimensionBuilder.build(method, style)))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(pair -> {
                    setVirtualSize(pair.getRight().getWidth(), pair.getRight().getHeight());
                    dimension = pair.getRight(); //TODO make stateless rather than writing to variables
                    method = pair.getLeft();//TODO make stateless rather than writing to variables
                    triggerVirtualCanvasRender();
                });
    }


    private void draw(Method method, Dimension dim, MethodStyle style, GraphicsContext gc, Bounds viewport) {

        for (int leadIndex = 0; leadIndex < method.getLeadCount(); leadIndex++) {
            Lead lead = method.getLead(leadIndex);

            Bounds leadBound = dim.getLeadBounds()[leadIndex];
            if (leadBound.intersects(viewport)) {
                drawHorizontalGrid(lead, dim, style, gc, leadBound);
                drawBellTextForLead(lead, dim, style, gc, leadBound);
                drawBellLinesForLead(lead, dim, style, gc, leadBound);
            }
        }
    }

    private void drawHorizontalGrid(Lead lead, Dimension dim, MethodStyle style, GraphicsContext gc, Bounds leadBound) {

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);

        for (int i=1;i<lead.getRowCount();i++) {

            int verticalPosition = (int) leadBound.getMinY() +
                    (dim.getRowVerticalSpacing() * i);

            gc.strokeLine(leadBound.getMinX(), verticalPosition,
                    leadBound.getMinX() + style.getBellHorizontalSpacing() * lead.getNumberOfBells().toInt(), verticalPosition);
        }
    }

    private void drawBellTextForLead(Lead lead, Dimension dim, MethodStyle style, GraphicsContext gc, Bounds leadBound) {
        FontMetrics fm = new FontMetrics(gc.getFont());

        Row firstRow = lead.getFirstRow();

        // We draw all of each bell in turn to give consistent overwriting.
        for (Bell bell : firstRow) {
            int verticalPosition = (int)leadBound.getMinY() +
                    dim.getRowVerticalSpacing() +
                    ((dim.getRowVerticalSpacing() - (int)fm.getLineHeight()) / 2) - (int)fm.getDescent()
                    ;
            BellStyle bellStyle = style.getBlendedBellStyle(bell, lead.getNumberOfBells());
            if (bellStyle.isShowingText()) {
                gc.setFill(bellStyle.getTextColor());
                String displayString = bell.getMnemonic();

                for (Row row : lead) {
                    int placeOfBell = row.getPlaceOfBell(bell);
                    int horizontalPosition = (int) leadBound.getMinX() + (dim.getBellHorizontalSpacing() * placeOfBell);

                    gc.fillText(displayString, horizontalPosition, verticalPosition);
                    verticalPosition += dim.getRowVerticalSpacing();
                }
            }
        }
    }

    private void drawBellLinesForLead(Lead lead, Dimension dim, MethodStyle style, GraphicsContext gc, Bounds leadBound) {

        Row firstRow = lead.getFirstRow();

        // cache the horizontal position .
        int[] horizontalPosition = new int[firstRow.getNumberOfBells().toInt()];
        for (int index = 0; index < horizontalPosition.length; index++) {
            horizontalPosition[index] = ((int) leadBound.getMinX()) +
                    (dim.getBellHorizontalSpacing() * index) +
                    (dim.getBellHorizontalSpacing() / 2);
        }

        for (Bell bell : firstRow) {
            BellStyle bellStyle = style.getBlendedBellStyle(bell, lead.getNumberOfBells());
            if (bellStyle.isShowingLine()) {

                int verticalPosition = (int) leadBound.getMinY() +
                        (dim.getRowVerticalSpacing() / 2);

                gc.setLineWidth(bellStyle.getLineWidth());
                gc.setStroke(bellStyle.getLineColor());
                gc.beginPath();

                int placeOfBell = firstRow.getPlaceOfBell(bell);
                gc.moveTo(horizontalPosition[placeOfBell], verticalPosition);

                for (int i = 1; i < lead.getRowCount(); i++) {
                    Row row = lead.getRow(i);
                    int nextPlaceOfBell = row.getPlaceOfBell(bell);
                    gc.lineTo(horizontalPosition[nextPlaceOfBell], verticalPosition + dim.getRowVerticalSpacing());
                    verticalPosition += dim.getRowVerticalSpacing();
                }

                gc.stroke();
            }
        }
    }

    private void clearBackground(GraphicsContext gc) {
        gc.clearRect(0, 0, getWidth(), getHeight());
    }
}
