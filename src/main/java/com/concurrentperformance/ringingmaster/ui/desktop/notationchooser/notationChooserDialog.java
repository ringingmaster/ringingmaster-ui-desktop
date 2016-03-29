package com.concurrentperformance.ringingmaster.ui.desktop.notationchooser;

import com.concurrentperformance.fxutils.dialog.EditMode;
import com.concurrentperformance.fxutils.dialog.SkeletalDialog;
import com.concurrentperformance.ringingmaster.ui.desktop.RingingMasterDesktopApp;
import com.concurrentperformance.ringingmaster.ui.desktop.notationsearch.NotationLibraryManager;
import com.google.common.collect.Lists;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationChooserDialog extends SkeletalDialog<NotationChooserResults> {

    public static final String NOTATION_CHOOSER_FXML = "NotationEditorDialog.fxml";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private NotationLibraryManager notationLibraryManager;

    public static void showDialog(NotationChooserResults model, Window owner,
                                  Function<NotationChooserResults, Boolean> onSuccessHandler,
                                  NotationLibraryManager notationLibraryManager) {

        NotationChooserDialog notationChooserDialog = new DialogBuilder<NotationChooserResults, NotationChooserDialog>().buildDialog(EditMode.ADD, model, owner, NotationChooserDialog.class.getResource(NOTATION_CHOOSER_FXML),
                Lists.<String>newArrayList(RingingMasterDesktopApp.STYLESHEET), onSuccessHandler);
        notationChooserDialog.setNotationLibraryManager(checkNotNull(notationLibraryManager));
        notationChooserDialog.showAndWait();
    }


    @Override
    protected void populateDialogFromModel(NotationChooserResults notationChooserResults) {

    }

    @Override
    protected NotationChooserResults buildModelFromDialogData() {
        return null;
    }

    public NotationLibraryManager getNotationLibraryManager() {
        return checkNotNull(notationLibraryManager);
    }

    public void setNotationLibraryManager(NotationLibraryManager notationLibraryManager) {
        this.notationLibraryManager = notationLibraryManager;
    }
}
