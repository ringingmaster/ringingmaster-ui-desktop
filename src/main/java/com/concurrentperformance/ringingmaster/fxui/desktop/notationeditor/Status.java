package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.helper.PlainCourseHelper;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.fxui.desktop.util.ColorManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class Status {

	private NotationEditorDialog parent;

	@FXML
	private TableView<StatusModel> status;

	public void init(NotationEditorDialog parent) {
		this.parent = parent;
		hideStatusHeaders();
	}

	void hideStatusHeaders() {
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

	void updateNotationStats(NotationBody notation) {
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
		parent.stage.setTitle(parent.editMode.getEditText() + ": " + notation.getNameIncludingNumberOfBells());
		parent.okButton.setDisable(false);
	}

	void updateNotationStats(Exception e) {
		ObservableList<StatusModel> items = status.getItems();
		items.clear();
		items.add(new StatusModel("error",e.getMessage()));
		status.setBackground(new Background(new BackgroundFill(ColorManager.getErrorHighlight(), CornerRadii.EMPTY, Insets.EMPTY)));
		parent.stage.setTitle(parent.editMode.getEditText() + ": " + parent.notationName);
		parent.okButton.setDisable(true);
	}
}
