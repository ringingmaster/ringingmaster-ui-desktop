package org.ringingmaster.ui.desktop.methodrenderer.methodstyle;

import javafx.scene.paint.Color;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public interface BellStyle {

    boolean isShowingLine();
    Color getLineColor();
    int getLineWidth();

    boolean isShowingText();
    Color getTextColor();
}
