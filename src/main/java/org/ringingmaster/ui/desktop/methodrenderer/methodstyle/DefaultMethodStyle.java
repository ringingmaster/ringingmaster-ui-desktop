package org.ringingmaster.ui.desktop.methodrenderer.methodstyle;

import javafx.geometry.Insets;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@Immutable
class DefaultMethodStyle implements MethodStyle {

    private final BellStyle[] fromTreble;
    private final BellStyle[] fromTenor;
    private final boolean[] fromTenorActive;

    private final int leadHorizontalSpacing;
    private final int bellHorizontalSpacing;
    private final int rowVerticalSpacing;

    private final Insets border;
    private final Optional<Integer> leadsPerColumn;

    DefaultMethodStyle(BellStyle[] fromTreble, BellStyle[] fromTenor, boolean[] fromTenorActive,
                       int leadHorizontalSpacing, int bellHorizontalSpacing, int rowVerticalSpacing,
                       Insets border, Optional<Integer> leadsPerColumn) {
        this.fromTreble = checkNotNull(fromTreble);
        checkArgument(fromTreble.length == NumberOfBells.MAX.toInt());
        for (int i=0;i<fromTreble.length;i++) {
            checkArgument(fromTreble[i] != null);
        }

        this.fromTenor = checkNotNull(fromTenor);
        checkArgument(fromTenor.length == FROM_TENOR_BELL_STYLE_MAX);
        for (int i=0;i<fromTenor.length;i++) {
            checkArgument(fromTenor[i] != null);
        }

        this.fromTenorActive = checkNotNull(fromTenorActive);
        checkArgument(fromTenorActive.length == FROM_TENOR_BELL_STYLE_MAX);

        checkArgument(leadHorizontalSpacing > 0);
        this.leadHorizontalSpacing = leadHorizontalSpacing;

        checkArgument(bellHorizontalSpacing > 0);
        this.bellHorizontalSpacing = bellHorizontalSpacing;

        checkArgument(rowVerticalSpacing > 0);
        this.rowVerticalSpacing = rowVerticalSpacing;

        this.border = checkNotNull(border);

        this.leadsPerColumn = checkNotNull(leadsPerColumn);
        leadsPerColumn.ifPresent(leadCount -> checkState(leadCount > 0));
    }


    @Override
    public Insets getBorder() {
        return border;
    }

    @Override
    public int getLeadHorizontalSpacing() {
        return leadHorizontalSpacing;
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
    public Optional<Integer> getLeadsPerColumn() {
        return Optional.of(3);
    }

    @Override
    public BellStyle getFromTrebleBellStyle(int indexFromTreble) {
        return fromTreble[checkElementIndex(indexFromTreble, fromTreble.length)];
    }

    @Override
    public BellStyle getFromTenorBellStyle(int indexFromTenor) {
        return fromTenor[checkElementIndex(indexFromTenor, fromTenor.length)];
    }

    @Override
    public BellStyle getBlendedBellStyle(Bell bell, NumberOfBells numberOfBells) {
        checkArgument(bell.getZeroBasedBell() < numberOfBells.toInt());
        int fromTenorIndex = numberOfBells.toInt() - bell.getZeroBasedBell() - 1;
        if (fromTenorIndex < FROM_TENOR_BELL_STYLE_MAX &&
                fromTenorActive[fromTenorIndex]) {
            return fromTenor[fromTenorIndex];
        }
        return fromTreble[bell.getZeroBasedBell()];
    }

}
