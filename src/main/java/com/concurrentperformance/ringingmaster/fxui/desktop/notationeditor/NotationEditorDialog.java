package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.fxui.desktop.util.ColorManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
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
	private String notationName;

	@FXML
	private TabPane tabs;

	@FXML
	private Label status;

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

		addPlainCourseTab(tabs);
		addCallsTab(tabs);

		notationName = notationBody.getNameIncludingNumberOfBells();
		plainCourseController.init(notationBody, this);
		callsController.setNotation(notationBody);

		update();
	}

	private void addPlainCourseTab(TabPane tabPane) throws IOException {
		Tab plainCourseTab = new Tab();
		plainCourseTab.setText("Plain Course");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/PlainCourse.fxml"));
		GridPane plainCourse = fxmlLoader.load();
		plainCourseController = fxmlLoader.getController();

		plainCourseTab.setContent(plainCourse);
		tabPane.getTabs().add(plainCourseTab);
	}

	private void addCallsTab(TabPane tabPane) throws IOException {
		Tab callsCourseTab = new Tab();
		callsCourseTab.setText("Plain Course");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/Calls.fxml"));
		GridPane calls = fxmlLoader.load();
		callsController = fxmlLoader.getController();

		callsCourseTab.setContent(calls);
		tabPane.getTabs().add(callsCourseTab);
	}

	public void update() {
		try {
			NotationBuilder notationBuilder = NotationBuilder.getInstance();

			plainCourseController.build(notationBuilder);
			NotationBody notation = notationBuilder.build();
			status.setText("Method OK: " + notation.getNameIncludingNumberOfBells());
			stage.setTitle(editMode.getEditText() + " " + notation.getNameIncludingNumberOfBells());
		}
		catch (Exception e) {
			status.setText("Error: " + System.lineSeparator()  + "\t" + e.getMessage());
			status.setBackground(new Background(new BackgroundFill(ColorManager.getErrorHighlight(), CornerRadii.EMPTY, Insets.EMPTY)));
			stage.setTitle(editMode.getEditText() + " " + notationName);
		}
	}
}
