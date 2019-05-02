package org.ringingmaster.ui.desktop.setuppanel;

import org.ringingmaster.engine.composition.DryRun;
import org.ringingmaster.engine.composition.MutableComposition;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetTerminationMaxCircularityHandler {

    void setTerminationMaxCircularity(MutableComposition composition, Integer terminationMaxCircularity) {
        if (terminationMaxCircularity == null) {
            composition.renotify();
            return;
        }

        DryRun dryRun = composition.dryRunSetTerminationMaxCircularity(terminationMaxCircularity);
        switch (dryRun.result()) {
            case SUGGESTED_ALTERATIVE:
                composition.setTerminationMaxCircularity((Integer)dryRun.getSuggestedAlternative());
                break;
            case SUCCESS:
                composition.setTerminationMaxCircularity(terminationMaxCircularity);
                break;
            case FAIL:
            case NO_CHANGE:
                composition.renotify();
                break;
        }
    }
}
