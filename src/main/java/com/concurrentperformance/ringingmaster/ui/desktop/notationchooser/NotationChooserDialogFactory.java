package com.concurrentperformance.ringingmaster.ui.desktop.notationchooser;

import com.concurrentperformance.ringingmaster.ui.desktop.notationsearch.NotationLibraryManager;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationChooserDialogFactory {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Stage globalStage;
	private NotationLibraryManager notationLibraryManager;

	public void openNotationChooserDialog(Function<NotationChooserResults, Boolean> onSuccessHandler) {
		NotationChooserDialoga.showDialog(new NotationChooserResults(), globalStage, onSuccessHandler, notationLibraryManager);
	}

	public void setGlobalStage(Stage globalStage) {
		this.globalStage = globalStage;
	}

	public void setNotationLibraryManager(NotationLibraryManager notationLibraryManager) {
		this.notationLibraryManager = notationLibraryManager;
	}
}
