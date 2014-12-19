package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.google.common.base.Objects;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class PlainCourse {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@FXML
	private TextField name;
	@FXML
	private Button notationSearchButton;
	@FXML
	private TextField shorthand;
	@FXML
	private ComboBox<NumberOfBells> numberOfBells;
	@FXML
	private CheckBox asymmetric;
	@FXML
	private TextField notation;
	@FXML
	private TextField leadend;
	@FXML
	private Label leadendLabel;

	private NotationEditorDialog parent;

	public void init(NotationBody notation, NotationEditorDialog parent, NotationEditorDialog.EditMode editMode) {
		checkNotNull(notation);
		checkState(this.parent == null, "Don't init more than once");
		this.parent = checkNotNull(parent);

		name.setText(notation.getName());
		name.setOnKeyReleased(this::keyPressUpdater);

		shorthand.setText(notation.getSpliceIdentifier());
		shorthand.focusedProperty().addListener(this::focusLostUpdater);

		this.notation.setText(notation.getRawNotationDisplayString(true));
		this.notation.setOnKeyReleased(this::keyPressUpdater);

		notationSearchButton.setTooltip(new Tooltip("Search for method to populate editor."));

		leadend.setText(notation.getRawLeadEndDisplayString(true));
		leadend.setOnKeyReleased(this::keyPressUpdater);

		for (NumberOfBells numberOfBells : NumberOfBells.values()) {
			this.numberOfBells.getItems().add(numberOfBells);
		}
		numberOfBells.getSelectionModel().select(notation.getNumberOfWorkingBells());
		numberOfBells.getSelectionModel().selectedItemProperty().addListener(this::numberOfBellsUpdater);
		numberOfBells.focusedProperty().addListener(this::focusLostUpdater);

		asymmetric.selectedProperty().addListener(this::asymmetricUpdater);
	}

	public void focusLostUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (Objects.equal(Boolean.FALSE, newValue)) {
			parent.update();
		}
	}

	public void numberOfBellsUpdater(ObservableValue<? extends NumberOfBells> observable, NumberOfBells oldValue, NumberOfBells newValue) {
		parent.update();
	}

	public void keyPressUpdater(KeyEvent event) {
		parent.update();
	}

	public void asymmetricUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		leadend.setVisible(!newValue);
		leadendLabel.setVisible(!newValue);
		GridPane.setColumnSpan(this.notation, (newValue)?5:4);
		parent.update();
	}

	public void build(NotationBuilder notationBuilder) {
		notationBuilder.setName(name.getText());
		notationBuilder.setNumberOfWorkingBells(numberOfBells.getSelectionModel().getSelectedItem());
		boolean selected = asymmetric.isSelected();
		if (selected) {
			notationBuilder.setUnfoldedNotationShorthand(notation.getText());
		} else {
			notationBuilder.setFoldedPalindromeNotationShorthand(notation.getText(), leadend.getText());
		}
	}
}