package com.concurrentperformance.ringingmaster.ui.desktop.notationeditor;

import com.concurrentperformance.fxutils.dialog.EditMode;
import com.concurrentperformance.fxutils.dialog.SkeletalDialog;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.ui.desktop.RingingMasterDesktopApp;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationEditorDialog extends SkeletalDialog<NotationBody> {

	public static final String NOTATION_EDITOR_FXML = "NotationEditorDialog.fxml";

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	protected NotationBody lastGoodNotation;

	private List<NotationEditorTabController> tabControllers = new ArrayList<>();
	private Status statusController;

	@FXML
	private TabPane editorTabs;
	@FXML
	private TabPane statusTabs;
	@FXML
	protected Button okButton;

	public static void showDialog(EditMode editMode, NotationBody model, Window owner,
	                              Function<NotationBody, Boolean> onSuccessHandler) {
		new Launcher<NotationBody>().showDialog(editMode, model, owner, NotationEditorDialog.class.getResource(NOTATION_EDITOR_FXML),
				Lists.<String>newArrayList(RingingMasterDesktopApp.STYLESHEET), onSuccessHandler);
	}

	protected void initialiseDialog(EditMode editMode, NotationBody notation) {
		try {
			addEditorTabs();
			addStatusTabs();

			for (NotationEditorTabController tabController : tabControllers) {
				tabController.init(this, editMode);
			}
		} catch (IOException e) {
			log.error("Error building dialog structure for [" + notation + "]", e);
		}
	}

	private void addEditorTabs() throws IOException {
		addEditorTab("PlainCourse.fxml");
		addEditorTab("Call.fxml");
		addEditorTab("CallPointRow.fxml");
		addEditorTab("CallPointMethod.fxml");
		addEditorTab("CallPointAggregate.fxml");
		addEditorTab("SplicePoint.fxml");
		addEditorTab("LeadLinePoint.fxml");
		addEditorTab("CourseHeadPoint.fxml");
	}

	private void addEditorTab(String fxmlFile) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
		Node content = fxmlLoader.load();
		NotationEditorTabController controller = fxmlLoader.getController();
		tabControllers.add(controller);

		Tab tab = new Tab();
		tab.setText(controller.getTabName());
		tab.setContent(content);

		editorTabs.getTabs().add(tab);
	}

	private void addStatusTabs() throws IOException {
		//Status
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Status.fxml"));
		Node content = fxmlLoader.load();
		statusController = fxmlLoader.getController();
		statusController.init(this);

		Tab statusTab = new Tab();
		statusTab.setText("Status");
		statusTab.setContent(content);

		statusTabs.getTabs().add(statusTab);

		//Diagram
		Tab diagramTab = new Tab();
		diagramTab.setText("Diagram - TODO");
		//diagramTab.setContent(TODO);

		statusTabs.getTabs().add(diagramTab);
	}


	public void roundTripDialogDataOnFocusLossUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (Objects.equal(Boolean.FALSE, newValue)) {
			roundTripDialogDataToModelToDialogData();
		}
	}

	public void roundTripDialogDataUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		roundTripDialogDataToModelToDialogData();
	}

	protected void roundTripDialogDataToModelToDialogData() {
		NotationBody notation = buildModelFromDialogData();
		if (notation != null) {
			populateDialogFromModel(notation);
		}
	}

	protected NotationBody buildModelFromDialogData() {
		try {
			NotationBuilder notationBuilder = NotationBuilder.getInstance();
			for (NotationEditorTabController tabController : tabControllers) {
				tabController.buildNotationFromDialogData(notationBuilder);
			}

			NotationBody notationBody = notationBuilder.build();
			lastGoodNotation = notationBody;
			statusController.updateNotationStats(notationBody);
			return notationBody;
		}
		catch (Exception e) {
			log.info("Problem with checking notation [{}]", e.getMessage());
			log.debug("", e);
			statusController.updateNotationStats(e);
			return null;
		}
	}

	protected void populateDialogFromModel(NotationBody notation) {
		for (NotationEditorTabController tabController : tabControllers) {
			tabController.buildDialogDataFromNotation(notation);
		}
	}

}