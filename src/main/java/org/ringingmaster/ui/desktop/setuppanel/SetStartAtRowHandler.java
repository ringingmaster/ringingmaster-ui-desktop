package org.ringingmaster.ui.desktop.setuppanel;

import org.ringingmaster.engine.composition.DryRun;
import org.ringingmaster.engine.composition.MutableComposition;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetStartAtRowHandler {

    void setStartRow(MutableComposition composition, Integer startAtRow) {
        if (startAtRow == null) {
            composition.renotify();
            return;
        }

        DryRun dryRun = composition.dryRunSetStartAtRow(startAtRow);
        switch (dryRun.result()) {
            case SUGGESTED_ALTERATIVE:
                composition.setStartAtRow((Integer)dryRun.getSuggestedAlternative());
                break;
            case SUCCESS:
                composition.setStartAtRow(startAtRow);
                break;
            case FAIL:
            case NO_CHANGE:
                composition.renotify();
                break;
        }
    }
}
