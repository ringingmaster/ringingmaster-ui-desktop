package org.ringingmaster.ui.desktop.documentpanel.grid.canvas;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class FontMetrics {
    final private Text internal;

    private float ascent;
    private float descent;
    private float lineHeight;


    public FontMetrics(Font fnt) {
        internal = new Text();
        internal.setFont(fnt);
        Bounds b= internal.getLayoutBounds();
        lineHeight= (float) b.getHeight();
        ascent= (float) -b.getMinY();
        descent=(float) b.getMaxY();
    }

    public float computeStringWidth(String txt)
    {
        internal.setText(txt);
        return (float) internal.getLayoutBounds().getWidth();
    }

    public float getAscent() {
        return ascent;
    }

    public float getDescent() {
        return descent;
    }

    public float getLineHeight() {
        return lineHeight;
    }
}