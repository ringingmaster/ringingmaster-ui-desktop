package org.ringingmaster.ui.desktop.setuppanel;

import org.ringingmaster.engine.composition.DryRun;
import org.ringingmaster.engine.composition.MutableComposition;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetTerminationMaxPartsHandler {

    void setTerminationMaxRows(MutableComposition composition, Integer terminationMaxParts) {
        if (terminationMaxParts == null || terminationMaxParts < 1) {
            composition.removeTerminationMaxParts();
            return;
        }

        DryRun dryRun = composition.dryRunSetTerminationMaxParts(terminationMaxParts);
        switch (dryRun.result()) {
            case SUGGESTED_ALTERATIVE:
                composition.setTerminationMaxParts((Integer)dryRun.getSuggestedAlternative());
                break;
            case SUCCESS:
                composition.setTerminationMaxParts(terminationMaxParts);
                break;
            case FAIL:
            case NO_CHANGE:
                composition.renotify();
                break;
        }
    }
}
