package org.ringingmaster.ui.desktop.methodrenderer.dimension;

import javafx.geometry.Bounds;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public interface Dimension {

    int getWidth();

    int getHeight();

    int getBellHorizontalSpacing();

    int getRowVerticalSpacing();

    Bounds[] getLeadBounds();
}
