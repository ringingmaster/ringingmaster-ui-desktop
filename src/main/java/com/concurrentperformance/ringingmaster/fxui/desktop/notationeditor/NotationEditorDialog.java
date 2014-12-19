package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.proof.Proof;
import com.concurrentperformance.ringingmaster.fxui.desktop.util.ColorManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationEditorDialog {

	private static final Logger log = LoggerFactory.getLogger(NotationEditorDialog.class);

	enum EditMode {
		ADD_NOTATION("Add"),
		EDIT_NOTATION("Editing")
		;

		private final String editText;

		EditMode(String editText) {
			this.editText = editText;
		}

		public String getEditText() {
			return editText;
		}
	};
	private EditMode editMode;

	private Stage stage;
	private PlainCourse plainCourseController;
	private Calls callsController;
	private CallPointRow callPointRowController;
	private CallPointMethod callPointMethodController;
	private CallPointAggregate callPointAggregateController;
	private SplicePoint splicePointController;
	private LeadLinePoint leadLinePointController;
	private CourseHeadPoint courseHeadPointController;

	private String notationName;

	@FXML
	private TabPane tabs;
	@FXML
	private TableView<StatusModel> status;
	@FXML
	private Button okButton;

	public static NotationEditorDialog createInstance(NotationBody notationBody) {
		FXMLLoader fxmlLoader = new FXMLLoader(NotationEditorDialog.class.getResource("/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/NotationEditorDialog.fxml"));

		try {
			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setScene(new Scene(fxmlLoader.load()));
			stage.initModality(Modality.APPLICATION_MODAL);

			NotationEditorDialog controller = fxmlLoader.getController();
			controller.init(EditMode.EDIT_NOTATION, stage, notationBody);

			stage.show();

			return controller;

		} catch (IOException e) {
			log.error("Error initialising NotationEditorDialog", e);
			return null;
		}
	}

	private void init(EditMode editMode, Stage stage, NotationBody notationBody) throws IOException {
		this.editMode = editMode;
		this.stage = stage;

		plainCourseController = addTab(tabs, "Plain Course", "/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/PlainCourse.fxml");
		callsController = addTab(tabs, "Calls", "/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/Calls.fxml");
		callPointRowController = addTab(tabs, "Row Call Points", "/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/CallPointRow.fxml");
		callPointMethodController = addTab(tabs, "Method Call Points", "/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/CallPointMethod.fxml");
		callPointAggregateController = addTab(tabs, "Aggregate Call Points", "/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/CallPointAggregate.fxml");
		splicePointController = addTab(tabs, "Aggregate Call Points", "/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/SplicePoint.fxml");
		leadLinePointController = addTab(tabs, "Lead Line Points", "/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/LeadLinePoint.fxml");
		courseHeadPointController = addTab(tabs, "Course Head Point", "/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/CourseHeadPoint.fxml");

		notationName = notationBody.getNameIncludingNumberOfBells();
		plainCourseController.init(notationBody, this, editMode);
		callsController.setNotation(notationBody);


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

		update();
	}

	private <T> T addTab(TabPane tabPane, String name, String fxmlFile) throws IOException {
		Tab tab = new Tab();
		tab.setText(name);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
		Node content = fxmlLoader.load();
		tab.setContent(content);
		tabPane.getTabs().add(tab);
		return fxmlLoader.getController();
	}

	public void update() {
		ObservableList<StatusModel> items = status.getItems();

		try {
			NotationBuilder notationBuilder = NotationBuilder.getInstance();

			plainCourseController.build(notationBuilder);
			NotationBody notation = notationBuilder.build();

			// Test build a plain course to see if it is possible.
			Proof plainCourseProof = PlainCourseHelper.buildPlainCourse(notation, "Checking new notation", true);
			Method plainCourse = plainCourseProof.getCreatedMethod();

			items.clear();
			items.add(new StatusModel("name", notation.getNameIncludingNumberOfBells()));
			items.add(new StatusModel("notation", notation.getNotationDisplayString(true)));
			items.add(new StatusModel("notation type", notation.isFoldedPalindrome()?"asymmetric":"asymmetric"));
			items.add(new StatusModel("plain course", plainCourseProof.getAnalysis().isTrueTouch()?"true":"false"));
			items.add(new StatusModel("changes in plain lead", Integer.toString(plainCourse.getLead(0).getRowCount())));
			items.add(new StatusModel("changes in plain course", Integer.toString(plainCourse.getRowCount())));
			items.add(new StatusModel("leads in plain course", Integer.toString(plainCourse.getLeadCount())));
			items.add(new StatusModel("number of bells in the hunt", "TODO"));
			items.add(new StatusModel("number of calls defined", Integer.toString(notation.getCalls().size())));

			status.setBackground(new Background(new BackgroundFill(ColorManager.getClearHighlight(), CornerRadii.EMPTY, Insets.EMPTY)));
			stage.setTitle(editMode.getEditText() + " " + notation.getNameIncludingNumberOfBells());
			okButton.setDisable(false);
		}
		catch (Exception e) {
			items.clear();
			items.add(new StatusModel("error",e.getMessage()));
			status.setBackground(new Background(new BackgroundFill(ColorManager.getErrorHighlight(), CornerRadii.EMPTY, Insets.EMPTY)));
			stage.setTitle(editMode.getEditText() + " " + notationName);
			okButton.setDisable(true);
		}
	}
}
