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

/**
 * TODO Comments
 *
 * @author Lake
 */


public class PlainCourse extends SkeletalNotationEditorTabController implements NotationEditorTabController {

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

	@Override
	public String getTabName() {
		return "Plain Course"   ;
	}

	@Override
	public void init(NotationEditorDialog parent, NotationEditorEditMode editMode) {
		super.init(parent, editMode);

		name.setOnKeyReleased(this::keyPressUpdater);

		shorthand.focusedProperty().addListener(this::focusLostUpdater);

		this.notation.setOnKeyReleased(this::keyPressUpdater);

		notationSearchButton.setTooltip(new Tooltip("Search for method to populate editor."));

		leadend.setOnKeyReleased(this::keyPressUpdater);

		for (NumberOfBells numberOfBells : NumberOfBells.values()) {
			this.numberOfBells.getItems().add(numberOfBells);
		}
		numberOfBells.getSelectionModel().selectedItemProperty().addListener(this::numberOfBellsUpdater);
		numberOfBells.focusedProperty().addListener(this::focusLostUpdater);

		asymmetric.selectedProperty().addListener(this::asymmetricUpdater);
	}

	@Override
	public void buildDialogDataFromNotation(NotationBody notation) {
		name.setText(notation.getName());
		shorthand.setText(notation.getSpliceIdentifier());
		this.notation.setText(notation.getRawNotationDisplayString(true));
		leadend.setText(notation.getRawLeadEndDisplayString(true));
		numberOfBells.getSelectionModel().select(notation.getNumberOfWorkingBells());
	}

	public void focusLostUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (Objects.equal(Boolean.FALSE, newValue)) {
			parent.buildNotationFromDialogData();
		}
	}

	public void numberOfBellsUpdater(ObservableValue<? extends NumberOfBells> observable, NumberOfBells oldValue, NumberOfBells newValue) {
		parent.buildNotationFromDialogData();
	}

	public void keyPressUpdater(KeyEvent event) {
		parent.buildNotationFromDialogData();
	}

	public void asymmetricUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		leadend.setVisible(!newValue);
		leadendLabel.setVisible(!newValue);
		GridPane.setColumnSpan(this.notation, (newValue)?5:4);
		parent.buildNotationFromDialogData();
	}

	@Override
	public void buildNotationFromDialogData(NotationBuilder notationBuilder) {
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