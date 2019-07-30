package org.ringingmaster.ui.desktop.methodrenderer.methodstyle;

import javafx.scene.paint.Color;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
class DefaultBellStyle implements BellStyle {

    private boolean showingLine;
    private final Color lineColor;
    private final int lineWidth;

    private boolean showingText;
    private final Color textColor;


    DefaultBellStyle(boolean showingLine, Color lineColor, int lineWidth, boolean showingText, Color textColor) {
        this.showingLine = showingLine;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;

        this.showingText = showingText;
        this.textColor = textColor;
    }

    @Override
    public boolean isShowingLine() {
        return showingLine;
    }

    @Override
    public Color getLineColor() {
        return lineColor;
    }

    @Override
    public int getLineWidth() {
        return lineWidth;
    }

    @Override
    public boolean isShowingText() {
        return showingText;
    }

    @Override
    public Color getTextColor() {
        return textColor;
    }
}
