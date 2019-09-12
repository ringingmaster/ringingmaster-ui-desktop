package org.ringingmaster.ui.desktop.methodrenderer.methodstyle;

import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import org.ringingmaster.engine.NumberOfBells;

import java.util.Arrays;
import java.util.Optional;

import static org.ringingmaster.ui.desktop.methodrenderer.methodstyle.MethodStyle.FROM_TENOR_BELL_STYLE_MAX;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class MethodStyleBuilder {

    private final BellStyle[] fromTreble = new BellStyle[NumberOfBells.MAX.toInt()] ;
    private final BellStyle[] fromTenor = new BellStyle[FROM_TENOR_BELL_STYLE_MAX];
    private final boolean[] fromTenorActive = new boolean[FROM_TENOR_BELL_STYLE_MAX];

    private int leadHorizontalSpacing = 30;
    private int bellHorizontalSpacing = 10;
    private int rowVerticalSpacing = 14;

    private Insets border = new Insets(15);
    private final Optional<Integer> leadsPerColumn = Optional.of(3);



    public MethodStyleBuilder defaultTextStyle() {
        BellStyle defaultBellStyle = new DefaultBellStyle(false, Color.BLACK, 1, true, Color.BLACK);

        Arrays.fill(fromTreble, defaultBellStyle);
        Arrays.fill(fromTenor, defaultBellStyle);

        fromTreble[0] = new DefaultBellStyle(true, Color.RED, 1, false, Color.RED);
        fromTreble[1] = new DefaultBellStyle(true, Color.BLUE, 2, false, Color.BLUE);



        return this;
    }

    public MethodStyleBuilder defaultLineStyle() {
        BellStyle defaultBellStyle = new DefaultBellStyle(false, Color.BLACK, 1, false, Color.BLACK);

        Arrays.fill(fromTreble, defaultBellStyle);
        Arrays.fill(fromTenor, defaultBellStyle);

        fromTreble[0] = new DefaultBellStyle(true, Color.RED, 1, false, Color.RED);
        fromTenor[0] = new DefaultBellStyle(true, Color.BLUE, 1, false, Color.BLUE);
        fromTenorActive[0] = true;

        rowVerticalSpacing = 5;

        return this;
    }

    public MethodStyle build() {
        return new DefaultMethodStyle(fromTreble, fromTenor, fromTenorActive,
            leadHorizontalSpacing, bellHorizontalSpacing, rowVerticalSpacing,
                border, leadsPerColumn);
    }

}
