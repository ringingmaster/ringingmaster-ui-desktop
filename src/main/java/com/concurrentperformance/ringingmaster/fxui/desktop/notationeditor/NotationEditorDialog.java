package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.fxui.desktop.util.ColorManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
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

	private NotationEditorEditMode editMode;
	private Stage stage;
	private List<NotationEditorTabController> tabControllers = new ArrayList<>();
	private String notationName;

	@FXML
	private TabPane tabs;
	@FXML
	private TableView<StatusModel> status;
	@FXML
	private Button okButton;

	void init(NotationEditorEditMode editMode, Stage stage, NotationBody notation) throws IOException {
		this.editMode = editMode;
		this.stage = stage;

		addAllTabs();

		notationName = notation.getNameIncludingNumberOfBells();

		for (NotationEditorTabController tabController : tabControllers) {
			tabController.init(this, editMode);
		}

		hideStatusHeaders();

		buildDialogDataFromNotation(notation);

		checkNotationFromDialogData();
	}

	private void addAllTabs() throws IOException {
		addTab("PlainCourse.fxml");
		addTab("Call.fxml");
		addTab("CallPointRow.fxml");
		addTab("CallPointMethod.fxml");
		addTab("CallPointAggregate.fxml");
		addTab("SplicePoint.fxml");
		addTab("LeadLinePoint.fxml");
		addTab("CourseHeadPoint.fxml");
	}

	private void addTab(String fxmlFile) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
		Node content = fxmlLoader.load();
		NotationEditorTabController controller = fxmlLoader.getController();
		tabControllers.add(controller);

		Tab tab = new Tab();
		tab.setText(controller.getTabName());
		tab.setContent(content);

		tabs.getTabs().add(tab);
	}

	private void hideStatusHeaders() {
		// Hide the status headers
		status.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				// Get the table header
				Pane header = (Pane) status.lookup("TableHeaderRow");
				if (header != null && header.isVisible()) {
					header.setMaxHeight(0);
					header.setMinHeight(0);
					header.setPrefHeight(0);
					header.setVisible(false);
					header.setManaged(false);
				}
			}
		});
	}

	public void checkNotationFromDialogData() {
		try {
			// build notation
			NotationBody notation = buildNotationFromDialogData();
			updateNotationStats(notation);
		}
		catch (Exception e) {
			updateNotationStats(e);
		}
	}

	public void rebuildNotationFromDialogData() {
		try {
			// build notation
			NotationBody notation = buildNotationFromDialogData();
			updateNotationStats(notation);
			buildDialogDataFromNotation(notation);
		}
		catch (Exception e) {
			updateNotationStats(e);
		}
	}

	private NotationBody buildNotationFromDialogData() {
		NotationBuilder notationBuilder = NotationBuilder.getInstance();
		for (NotationEditorTabController tabController : tabControllers) {
			tabController.buildNotationFromDialogData(notationBuilder);
		}
		return notationBuilder.build();
	}

	private void updateNotationStats(NotationBody notation) {
		ObservableList<StatusModel> items = status.getItems();

		// Test build a plain course to see if it is possible.
		Proof plainCourseProof = PlainCourseHelper.buildPlainCourse(notation, "Checking new notation", true);
		Method plainCourse = plainCourseProof.getCreatedMethod();

		items.clear();
		items.add(new StatusModel("name", notation.getNameIncludingNumberOfBells()));
		items.add(new StatusModel("notation", notation.getNotationDisplayString(true)));
		items.add(new StatusModel("notation type", notation.isFoldedPalindrome()?"symmetric":"asymmetric"));
		items.add(new StatusModel("plain course", plainCourseProof.getAnalysis().isTrueTouch()?"true":"false"));
		items.add(new StatusModel("changes in plain lead", Integer.toString(plainCourse.getLead(0).getRowCount())));
		items.add(new StatusModel("changes in plain course", Integer.toString(plainCourse.getRowCount())));
		items.add(new StatusModel("leads in plain course", Integer.toString(plainCourse.getLeadCount())));
		items.add(new StatusModel("number of bells in the hunt", Integer.toString(plainCourse.getNumberOfBellsInHunt())));
		items.add(new StatusModel("number of calls defined", Integer.toString(notation.getCalls().size())));

		status.setBackground(new Background(new BackgroundFill(ColorManager.getClearHighlight(), CornerRadii.EMPTY, Insets.EMPTY)));
		stage.setTitle(editMode.getEditText() + " " + notation.getNameIncludingNumberOfBells());
		okButton.setDisable(false);
	}

	private void updateNotationStats(Exception e) {
		ObservableList<StatusModel> items = status.getItems();
		items.clear();
		items.add(new StatusModel("error",e.getMessage()));
		status.setBackground(new Background(new BackgroundFill(ColorManager.getErrorHighlight(), CornerRadii.EMPTY, Insets.EMPTY)));
		stage.setTitle(editMode.getEditText() + " " + notationName);
		okButton.setDisable(true);
	}

	private void buildDialogDataFromNotation(NotationBody notation) {
		for (NotationEditorTabController tabController : tabControllers) {
			tabController.buildDialogDataFromNotation(notation);
		}
	}
}
