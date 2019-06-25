package org.ringingmaster.ui.desktop.setuppanel;

import org.ringingmaster.engine.composition.DryRun;
import org.ringingmaster.engine.composition.MutableComposition;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetTerminationMaxPartCircularityHandler {

    void setTerminationMaxCircularity(MutableComposition composition, Integer terminationMaxPartCircularity) {
        if (terminationMaxPartCircularity == null) {
            composition.renotify();
            return;
        }

        DryRun dryRun = composition.dryRunSetTerminationMaxPartCircularity(terminationMaxPartCircularity);
        switch (dryRun.result()) {
            case SUGGESTED_ALTERATIVE:
                composition.setTerminationMaxPartCircularity((Integer)dryRun.getSuggestedAlternative());
                break;
            case SUCCESS:
                composition.setTerminationMaxPartCircularity(terminationMaxPartCircularity);
                break;
            case FAIL:
            case NO_CHANGE:
                composition.renotify();
                break;
        }
    }
}
