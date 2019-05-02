package org.ringingmaster.ui.desktop.setuppanel;

import org.ringingmaster.engine.composition.DryRun;
import org.ringingmaster.engine.composition.MutableComposition;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetTerminationMaxRowsHandler {

    void setTerminationMaxRows(MutableComposition composition, Integer terminationMaxRows) {
        if (terminationMaxRows == null) {
            composition.renotify();
            return;
        }

        DryRun dryRun = composition.dryRunSetTerminationMaxRows(terminationMaxRows);
        switch (dryRun.result()) {
            case SUGGESTED_ALTERATIVE:
                composition.setTerminationMaxRows((Integer)dryRun.getSuggestedAlternative());
                break;
            case SUCCESS:
                composition.setTerminationMaxRows(terminationMaxRows);
                break;
            case FAIL:
            case NO_CHANGE:
                composition.renotify();
                break;
        }
    }
}
