package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationEditorDialog {

	protected NotationEditorEditMode editMode;
	protected Stage stage;
	protected String notationName;

	private List<NotationEditorTabController> tabControllers = new ArrayList<>();
	private Status statusController;

	@FXML
	private TabPane editorTabs;
	@FXML
	private TabPane statusTabs;
	@FXML
	protected Button okButton;

	void init(NotationEditorEditMode editMode, Stage stage, NotationBody notation) throws IOException {
		this.editMode = editMode;
		this.stage = stage;

		addEditorTabs();
		addStatusTabs();

		notationName = notation.getNameIncludingNumberOfBells();

		for (NotationEditorTabController tabController : tabControllers) {
			tabController.init(this, editMode);
		}

		buildDialogDataFromNotation(notation);

		checkNotationFromDialogData();
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


	public void checkNotationFromDialogData() {
		try {
			// build notation
			NotationBody notation = buildNotationFromDialogData();
			statusController.updateNotationStats(notation);
		}
		catch (Exception e) {
			statusController.updateNotationStats(e);
		}
	}

	public void rebuildNotationFromDialogData() {
		try {
			// build notation
			NotationBody notation = buildNotationFromDialogData();
			statusController.updateNotationStats(notation);
			buildDialogDataFromNotation(notation);
		}
		catch (Exception e) {
			statusController.updateNotationStats(e);
		}
	}

	private NotationBody buildNotationFromDialogData() {
		NotationBuilder notationBuilder = NotationBuilder.getInstance();
		for (NotationEditorTabController tabController : tabControllers) {
			tabController.buildNotationFromDialogData(notationBuilder);
		}
		return notationBuilder.build();
	}

	private void buildDialogDataFromNotation(NotationBody notation) {
		for (NotationEditorTabController tabController : tabControllers) {
			tabController.buildDialogDataFromNotation(notation);
		}
	}
}
