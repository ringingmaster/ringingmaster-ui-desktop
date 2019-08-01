package org.ringingmaster.ui.desktop.methodrenderer.dimension;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.ringingmaster.engine.method.Lead;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.ui.desktop.methodrenderer.methodstyle.MethodStyle;

import java.util.Arrays;
import java.util.Optional;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class DimensionBuilder {


    public Dimension build(Optional<Method> optionalMethod, MethodStyle style) {

        Method method = optionalMethod.get();

        BoundingBox[] leadBounds = calculationLeadBounds(method, style);

        int width =  (int)leadBounds[leadBounds.length-1].getMaxX() +
                (int)(style.getBorder().getLeft() + style.getBorder().getRight());

        int height = (int)Arrays.stream(leadBounds)
                .mapToDouble(Bounds::getMaxY)
                .max()
                .orElse(10) +
                    (int)(style.getBorder().getTop() + style.getBorder().getBottom());

        return new DefaultDimension(width, height,
                style.getBellHorizontalSpacing(), style.getRowVerticalSpacing(),
                leadBounds);
    }

    private BoundingBox[] calculationLeadBounds(Method method, MethodStyle style) {
        BoundingBox[] leadBounds = new BoundingBox[method.getLeadCount()];
        int width = method.getNumberOfBells().toInt() * style.getBellHorizontalSpacing();

        int top = (int)style.getBorder().getTop();
        int left = (int)style.getBorder().getLeft();
        int leadIndexForColumn = 0;


        for (int leadIndex=0;leadIndex<method.getLeadCount();leadIndex++) {
            Lead lead = method.getLead(leadIndex);
            int height = (lead.getRowCount()) * style.getRowVerticalSpacing();
            leadBounds[leadIndex] = new BoundingBox(left, top, width, height);

            leadIndexForColumn++;

            if (style.getLeadsPerColumn().isPresent() &&
                    leadIndexForColumn >= style.getLeadsPerColumn().get()) {
                // Roll over columns
                leadIndexForColumn = 0;
                top = (int)style.getBorder().getLeft();
                left += (width + style.getLeadHorizontalSpacing());
            }
            else {
                top += (lead.getRowCount()-1) * style.getRowVerticalSpacing();
            }
        }
        return leadBounds;
    }
}
