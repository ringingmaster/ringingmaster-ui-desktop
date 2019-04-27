package org.ringingmaster.ui.desktop.documentpanel.grid.canvas;


import com.google.errorprone.annotations.Immutable;

import java.util.Arrays;

/**
 * TODO
 *
 * @author Stephen
 */
@Immutable
public class CharacterGroupMeasurement {

    private final double maxHeight;
    private final double bottomGap;
    private final double horizontalPadding;
    private final double totalCharacterWidth;
    private final double[] characterWidths;

    CharacterGroupMeasurement(final double maxHeight, final double maxDescent, final double[] characterWidths, final double horizontalPadding) {
        this.maxHeight = maxHeight;
        this.bottomGap = maxDescent;
        this.horizontalPadding = horizontalPadding;
        this.characterWidths = characterWidths;
        this.totalCharacterWidth = calculateTotalCharacterWidth(characterWidths);
    }

    private double calculateTotalCharacterWidth(double[] characterWidths) {
        double totalCharacterWidth = 0;
        for (double characterWidth : characterWidths) {
            totalCharacterWidth += characterWidth;
        }
        return totalCharacterWidth;
    }

    double getMaxHeight() {
        return maxHeight;
    }

    double getBottomGap() {
        return bottomGap;
    }

    public double[] getCharacterWidths() {
        return characterWidths;
    }

    public double getHorizontalPadding() {
        return horizontalPadding;
    }

    double getTotalWidth() {
        return totalCharacterWidth + (horizontalPadding * 2);
    }

    @Override
    public String toString() {
        return "CharacterGroupMeasurement{" +
                "maxHeight=" + maxHeight +
                ", bottomGap=" + bottomGap +
                ", totalCharacterWidth=" + totalCharacterWidth +
                ", characterWidths=" + Arrays.toString(characterWidths) +
                ", horizontalPadding=" + horizontalPadding +
                '}';
    }
}