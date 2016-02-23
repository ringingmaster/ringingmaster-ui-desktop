package com.concurrentperformance.ringingmaster.ui.desktop.notationeditor;


import com.concurrentperformance.fxutils.dialog.EditMode;
import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.google.common.collect.Lists;
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
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


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
	private TextField spliceIndntifier;
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
	public void init(NotationEditorDialog parent, EditMode editMode) {
		super.init(parent, editMode);

		TextFields.bindAutoCompletion(name, (Callback<AutoCompletionBinding.ISuggestionRequest, Collection<Object>>)
				suggestion -> {
            log.info("Getting suggestions for [{}]", suggestion.getUserText() );
					if (suggestion.getUserText().contains("S"))
            			return Lists.newArrayList("STephen");
					else
						return Lists.newArrayList("Judith", "William");
        });

		name.setOnKeyReleased(this::keyPressUpdater);

		spliceIndntifier.setOnKeyReleased(this::keyPressUpdater);
		spliceIndntifier.focusedProperty().addListener(parent::roundTripDialogDataOnFocusLossUpdater);

		this.notation1.setOnKeyReleased(this::keyPressUpdater);
		this.notation1.focusedProperty().addListener(parent::roundTripDialogDataOnFocusLossUpdater);

		notationSearchButton.setTooltip(new Tooltip("Search for method to populate editor."));

		notation2.setOnKeyReleased(this::keyPressUpdater);
		notation2.focusedProperty().addListener(parent::roundTripDialogDataOnFocusLossUpdater);

		for (NumberOfBells numberOfBells : NumberOfBells.values()) {
			this.numberOfBells.getItems().add(numberOfBells);
		}
		numberOfBells.getSelectionModel().selectedItemProperty().addListener(this::numberOfBellsUpdater);
		numberOfBells.focusedProperty().addListener(parent::roundTripDialogDataOnFocusLossUpdater);

		asymmetric.selectedProperty().addListener(this::asymmetricUpdater);
		asymmetric.focusedProperty().addListener(parent::roundTripDialogDataOnFocusLossUpdater);
	}

	@Override
	public void buildDialogDataFromNotation(NotationBody notation) {
		name.setText(notation.getName());
		spliceIndntifier.setText(notation.getSpliceIdentifier());
		notation1.setText(notation.getRawNotationDisplayString(0, true));
		notation2.setText(notation.getRawNotationDisplayString(1, true));
		numberOfBells.getSelectionModel().select(notation.getNumberOfWorkingBells());
		asymmetric.setSelected(!notation.isFoldedPalindrome());
	}

	public void numberOfBellsUpdater(ObservableValue<? extends NumberOfBells> observable, NumberOfBells oldValue, NumberOfBells newValue) {
		parent.buildModelFromDialogData();
	}

	public void keyPressUpdater(KeyEvent event) {
		parent.buildModelFromDialogData();
	}

	public void asymmetricUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		notation1Label.setText((newValue) ? "Notation:" : "Notation: Rotation 1:");
		notation2.setVisible(!newValue);
		notation2Label.setVisible(!newValue);
		GridPane.setColumnSpan(this.notation1, (newValue)?5:3);
		parent.buildModelFromDialogData();
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
		notationBuilder.setSpliceIdentifier(spliceIndntifier.getText());
	}
}