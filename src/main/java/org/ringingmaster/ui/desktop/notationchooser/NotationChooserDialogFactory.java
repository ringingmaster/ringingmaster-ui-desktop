package org.ringingmaster.ui.desktop.notationchooser;

import javafx.stage.Stage;
import org.ringingmaster.ui.desktop.notationsearch.NotationLibraryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class NotationChooserDialogFactory {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Stage globalStage;
    private NotationLibraryManager notationLibraryManager;

    public void openNotationChooserDialog(Function<NotationChooserResults, Boolean> onSuccessHandler) {
        NotationChooserDialog.showDialog(new NotationChooserResults(), globalStage, onSuccessHandler, notationLibraryManager);
    }

    public void setGlobalStage(Stage globalStage) {
        this.globalStage = globalStage;
    }

    public void setNotationLibraryManager(NotationLibraryManager notationLibraryManager) {
        this.notationLibraryManager = notationLibraryManager;
    }
}
