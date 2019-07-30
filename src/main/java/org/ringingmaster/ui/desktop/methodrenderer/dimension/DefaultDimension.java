package org.ringingmaster.ui.desktop.methodrenderer.dimension;

import javafx.geometry.Bounds;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
class DefaultDimension implements Dimension {

    private final int width;
    private final int height;

    private final int bellHorizontalSpacing;
    private final int rowVerticalSpacing;

    private final Bounds[] leadBounds;

    DefaultDimension(int width, int height,
                     int bellHorizontalSpacing, int rowVerticalSpacing,
                     Bounds[] leadBounds) {

        this.width = width;
        this.height = height;

        this.bellHorizontalSpacing = bellHorizontalSpacing;
        this.rowVerticalSpacing = rowVerticalSpacing;

        this.leadBounds = leadBounds;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getBellHorizontalSpacing() {
        return bellHorizontalSpacing;
    }

    @Override
    public int getRowVerticalSpacing() {
        return rowVerticalSpacing;
    }

    @Override
    public Bounds[] getLeadBounds() {
        return leadBounds;
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "width=" + width +
                ", height=" + height +
                ", bellHorizontalSpacing=" + bellHorizontalSpacing +
                ", rowVerticalSpacing=" + rowVerticalSpacing +
                ", leadBounds=" + Arrays.toString(leadBounds) +
                '}';
    }
}
