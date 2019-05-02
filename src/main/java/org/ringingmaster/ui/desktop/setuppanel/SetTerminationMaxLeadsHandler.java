package org.ringingmaster.ui.desktop.setuppanel;

import org.ringingmaster.engine.composition.DryRun;
import org.ringingmaster.engine.composition.MutableComposition;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class SetTerminationMaxLeadsHandler {

    void setTerminationMaxRows(MutableComposition composition, Integer terminationMaxLeads) {
        if (terminationMaxLeads == null || terminationMaxLeads < 1) {
            composition.removeTerminationMaxLeads();
            return;
        }

        DryRun dryRun = composition.dryRunSetTerminationMaxLeads(terminationMaxLeads);
        switch (dryRun.result()) {
            case SUGGESTED_ALTERATIVE:
                composition.setTerminationMaxLeads((Integer)dryRun.getSuggestedAlternative());
                break;
            case SUCCESS:
                composition.setTerminationMaxLeads(terminationMaxLeads);
                break;
            case FAIL:
            case NO_CHANGE:
                composition.renotify();
                break;
        }
    }
}
