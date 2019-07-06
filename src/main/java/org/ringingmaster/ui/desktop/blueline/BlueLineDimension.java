package org.ringingmaster.ui.desktop.blueline;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
public class BlueLineDimension {

    private final int width;
    private final int height;

    private final int rowHorizontalSpacing;
    private final int rowVerticalSpacing;

    private final int leadVerticalSpacing;

    public BlueLineDimension(int width, int height,
                             int rowHorizontalSpacing, int rowVerticalSpacing,
                             int leadVerticalSpacing) {

        this.width = width;
        this.height = height;

        this.rowHorizontalSpacing = rowHorizontalSpacing;
        this.rowVerticalSpacing = rowVerticalSpacing;

        this.leadVerticalSpacing = leadVerticalSpacing;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRowHorizontalSpacing() {
        return rowHorizontalSpacing;
    }

    public int getRowVerticalSpacing() {
        return rowVerticalSpacing;
    }

    public int getLeadVerticalSpacing() {
        return leadVerticalSpacing;
    }
}
