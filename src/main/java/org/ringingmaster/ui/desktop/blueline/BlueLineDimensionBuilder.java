package org.ringingmaster.ui.desktop.blueline;

import org.ringingmaster.engine.method.Method;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class BlueLineDimensionBuilder {


    BlueLineDimension build(Optional<Method> method) {

        int rowVerticalSpacing = 13;
        int leadVerticalSpacing = 5;
        int rowHorizontalSpacing = 10;

        int height = (method.get().getRowCount() * rowVerticalSpacing) *
                (method.get().getLeadCount() * leadVerticalSpacing);
        int width = method.get().getNumberOfBells().toInt() * rowHorizontalSpacing;


        return new BlueLineDimension(width, height,
                rowHorizontalSpacing, rowVerticalSpacing,
                leadVerticalSpacing);
    }
}
