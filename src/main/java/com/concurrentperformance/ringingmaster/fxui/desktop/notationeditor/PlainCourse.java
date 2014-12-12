package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.google.common.base.Objects;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
	private TextField shorthand;
	@FXML
	private ComboBox numberOfBells;
	@FXML
	private CheckBox asymmetric;
	@FXML
	private TextField notation;
	@FXML
	private TextField leadend;

	private NotationEditorDialog parent;

	public void init(NotationBody notation, NotationEditorDialog parent) {
		checkNotNull(notation);
		checkState(this.parent == null, "Don't init more than once");
		this.parent = checkNotNull(parent);

		name.setText(notation.getName());
		name.focusedProperty().addListener(this::focusLostUpdater);

		shorthand.setText(notation.getSpliceIdentifier());
		shorthand.focusedProperty().addListener(this::focusLostUpdater);

		this.notation.setText(notation.getRawNotationDisplayString(true));
		this.notation.focusedProperty().addListener(this::focusLostUpdater);

		leadend.setText(notation.getRawLeadEndDisplayString(true));
		leadend.focusedProperty().addListener(this::focusLostUpdater);

		for (NumberOfBells numberOfBells : NumberOfBells.values()) {
			this.numberOfBells.getItems().add(numberOfBells);
		}
		numberOfBells.getSelectionModel().select(notation.getNumberOfWorkingBells());
		numberOfBells.focusedProperty().addListener(this::focusLostUpdater);
	}

	public void focusLostUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (Objects.equal(Boolean.FALSE, newValue)) {
			parent.update();
		}
	}

	public void build(NotationBuilder notationBuilder) {
		notationBuilder.setName(name.getText());
		boolean selected = asymmetric.isSelected();
		if (selected) {
			notationBuilder.setFoldedPalindromeNotationShorthand(notation.getText(), leadend.getText());
		}
		else {
			notationBuilder.setUnfoldedNotationShorthand(notation.getText());
		}
	}
}