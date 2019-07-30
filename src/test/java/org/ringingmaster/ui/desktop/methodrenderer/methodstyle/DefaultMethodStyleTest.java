package org.ringingmaster.ui.desktop.methodrenderer.methodstyle;

import javafx.scene.paint.Color;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;

import static org.junit.Assert.assertEquals;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class DefaultMethodStyleTest {

    @Test
    public void boundsCheck() {
        new DefaultMethodStyle(new BellStyle[30], new BellStyle[4], new boolean[4]);
    }

    @Test
    public void limitToNumberOfBellsEdgeCase() {
        DefaultMethodStyle methodStyle = new DefaultMethodStyle(new BellStyle[30], new BellStyle[4], new boolean[4]);

        methodStyle.getBlendedBellStyle(Bell.BELL_6, NumberOfBells.BELLS_6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void limitOfNumberOfBellsBreachedThrows() {
        DefaultMethodStyle methodStyle = new DefaultMethodStyle(new BellStyle[30], new BellStyle[4], new boolean[4]);

        methodStyle.getBlendedBellStyle(Bell.BELL_7, NumberOfBells.BELLS_6).getLineWidth();
    }

    @Test
    public void fromTenorNotActive() {
        BellStyle[] fromTreble = new BellStyle[30];
        for (int i = 0; i < fromTreble.length; i++) {
            fromTreble[i] = new DefaultBellStyle(Color.RED, i+1, Color.RED);
        }


        BellStyle[] fromTenor = new BellStyle[4];
        for (int i = 0; i < fromTenor.length; i++) {
            fromTenor[i] = new DefaultBellStyle(Color.GREEN, i + 100, Color.GREEN);
        }

        boolean[] fromTenorActive = new boolean[4];
        for (int i = 0; i < fromTenorActive.length; i++) {
            fromTenorActive[i] = false;
        }

        DefaultMethodStyle methodStyle = new DefaultMethodStyle(fromTreble, fromTenor, fromTenorActive);

        assertEquals(1, methodStyle.getBlendedBellStyle(Bell.BELL_1, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(2, methodStyle.getBlendedBellStyle(Bell.BELL_2, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(3, methodStyle.getBlendedBellStyle(Bell.BELL_3, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(4, methodStyle.getBlendedBellStyle(Bell.BELL_4, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(5, methodStyle.getBlendedBellStyle(Bell.BELL_5, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(6, methodStyle.getBlendedBellStyle(Bell.BELL_6, NumberOfBells.BELLS_6).getLineWidth());
    }

    @Test
    public void fromTenorActive() {
        BellStyle[] fromTreble = new BellStyle[30];
        for (int i = 0; i < fromTreble.length; i++) {
            fromTreble[i] = new DefaultBellStyle(Color.RED, i + 1, Color.RED);
        }


        BellStyle[] fromTenor = new BellStyle[4];
        for (int i = 0; i < fromTenor.length; i++) {
            fromTenor[i] = new DefaultBellStyle(Color.GREEN, i + 100, Color.GREEN);
        }

        boolean[] fromTenorActive = new boolean[] {true,false,false, true};

        DefaultMethodStyle methodStyle = new DefaultMethodStyle(fromTreble, fromTenor, fromTenorActive);

        assertEquals(1, methodStyle.getBlendedBellStyle(Bell.BELL_1, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(2, methodStyle.getBlendedBellStyle(Bell.BELL_2, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(103, methodStyle.getBlendedBellStyle(Bell.BELL_3, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(4, methodStyle.getBlendedBellStyle(Bell.BELL_4, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(5, methodStyle.getBlendedBellStyle(Bell.BELL_5, NumberOfBells.BELLS_6).getLineWidth());
        assertEquals(100, methodStyle.getBlendedBellStyle(Bell.BELL_6, NumberOfBells.BELLS_6).getLineWidth());

        assertEquals(1, methodStyle.getBlendedBellStyle(Bell.BELL_1, NumberOfBells.BELLS_30).getLineWidth());
        assertEquals(26, methodStyle.getBlendedBellStyle(Bell.BELL_26, NumberOfBells.BELLS_30).getLineWidth());
        assertEquals(103, methodStyle.getBlendedBellStyle(Bell.BELL_27, NumberOfBells.BELLS_30).getLineWidth());
        assertEquals(28, methodStyle.getBlendedBellStyle(Bell.BELL_28, NumberOfBells.BELLS_30).getLineWidth());
        assertEquals(29, methodStyle.getBlendedBellStyle(Bell.BELL_29, NumberOfBells.BELLS_30).getLineWidth());
        assertEquals(100, methodStyle.getBlendedBellStyle(Bell.BELL_30, NumberOfBells.BELLS_30).getLineWidth());

        assertEquals(1, methodStyle.getBlendedBellStyle(Bell.BELL_1, NumberOfBells.BELLS_3).getLineWidth());
        assertEquals(2, methodStyle.getBlendedBellStyle(Bell.BELL_2, NumberOfBells.BELLS_3).getLineWidth());
        assertEquals(100, methodStyle.getBlendedBellStyle(Bell.BELL_3, NumberOfBells.BELLS_3).getLineWidth());
    }

}