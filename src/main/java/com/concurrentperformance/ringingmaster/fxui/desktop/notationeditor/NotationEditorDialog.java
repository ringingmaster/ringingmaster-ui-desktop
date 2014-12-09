package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

	private PlainCourse plainCourseController;
	private Calls callsController;

	@FXML
	private TabPane tabs;

	public static NotationEditorDialog createInstance(NotationBody notationBody) {
		FXMLLoader fxmlLoader = new FXMLLoader(NotationEditorDialog.class.getResource("/com/concurrentperformance/ringingmaster/fxui/desktop/notationeditor/NotationEditorDialog.fxml"));

		try {
			Stage stage = new Stage(StageStyle.DECORATED);

			stage.setScene(new Scene(fxmlLoader.load()));
			stage.setTitle("Notation Editor - TODO ");
			stage.initModality(Modality.APPLICATION_MODAL);

			NotationEditorDialog controller = fxmlLoader.getController();
			controller.init();
			controller.setNotationBody(notationBody);

			stage.show();

			return controller;

		} catch (IOException e) {
			log.error("Error initialising NotationEditorDialog", e);
			return null;
		}
	}

	private void setNotationBody(NotationBody notationBody) {
		plainCourseController.setNotation(notationBody);
	}

	private void init() throws IOException {
		addPlainCourseTab(tabs);
		addCallsTab(tabs);
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

}
