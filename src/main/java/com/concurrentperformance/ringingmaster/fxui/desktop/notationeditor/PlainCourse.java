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
	private TextField notation1;
	@FXML
	private TextField notation2;
	@FXML
	private Label notation1Label;
	@FXML
	private Label notation2Label;


	@Override
	public String getTabName() {
		return "Plain Course";
	}

	@Override
	public void init(NotationEditorDialog parent, NotationEditorEditMode editMode) {
		super.init(parent, editMode);

		name.setOnKeyReleased(this::keyPressUpdater);

		shorthand.focusedProperty().addListener(this::focusLostUpdater);

		this.notation1.setOnKeyReleased(this::keyPressUpdater);
		this.notation1.focusedProperty().addListener(this::focusLostUpdater);

		notationSearchButton.setTooltip(new Tooltip("Search for method to populate editor."));

		notation2.setOnKeyReleased(this::keyPressUpdater);
		notation2.focusedProperty().addListener(this::focusLostUpdater);

		for (NumberOfBells numberOfBells : NumberOfBells.values()) {
			this.numberOfBells.getItems().add(numberOfBells);
		}
		numberOfBells.getSelectionModel().selectedItemProperty().addListener(this::numberOfBellsUpdater);
		numberOfBells.focusedProperty().addListener(this::focusLostUpdater);

		asymmetric.selectedProperty().addListener(this::asymmetricUpdater);
		asymmetric.focusedProperty().addListener(this::focusLostUpdater);
	}

	@Override
	public void buildDialogDataFromNotation(NotationBody notation) {
		name.setText(notation.getName());
		shorthand.setText(notation.getSpliceIdentifier());
		//checkState(false, "TODO This all needs reqorking and renaming in terms of rotation sets. - lead end does not cut it as a name. read the pdf that comes with the method library");
		this.notation1.setText(notation.getRawNotationDisplayString(0, true));
		notation2.setText(notation.getRawNotationDisplayString(1, true));
		numberOfBells.getSelectionModel().select(notation.getNumberOfWorkingBells());
	}

	public void focusLostUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (Objects.equal(Boolean.FALSE, newValue)) {
			parent.rebuildNotationFromDialogData();
		}
	}

	public void numberOfBellsUpdater(ObservableValue<? extends NumberOfBells> observable, NumberOfBells oldValue, NumberOfBells newValue) {
		parent.checkNotationFromDialogData();
	}

	public void keyPressUpdater(KeyEvent event) {
		parent.checkNotationFromDialogData();
	}

	public void asymmetricUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		notation1Label.setText((newValue)?"Notation:":"Notation: Rotation 1:");
		notation2.setVisible(!newValue);
		notation2Label.setVisible(!newValue);
		GridPane.setColumnSpan(this.notation1, (newValue)?5:3);
		parent.checkNotationFromDialogData();
	}

	@Override
	public void buildNotationFromDialogData(NotationBuilder notationBuilder) {
		notationBuilder.setName(name.getText());
		notationBuilder.setNumberOfWorkingBells(numberOfBells.getSelectionModel().getSelectedItem());
		boolean selected = asymmetric.isSelected();
		if (selected) {
			notationBuilder.setUnfoldedNotationShorthand(notation1.getText());
		} else {
			notationBuilder.setFoldedPalindromeNotationShorthand(notation1.getText(), notation2.getText());
		}
	}
}