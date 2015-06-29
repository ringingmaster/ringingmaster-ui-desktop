package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.fxutils.components.PressableButton;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Set;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class Call extends SkeletalNotationEditorTabController implements NotationEditorTabController {

	public static final String DEFAULT_CALL_TOKEN = "<default>";

	@FXML
	private TableView<CallModel> callsList;
	@FXML
	private CheckBox cannedCalls;
	@FXML
	private Button addCallButton;
	@FXML
	private Button removeCallButton;
	@FXML
	private PressableButton defaultCallButton;

	@FXML
	private TextField leadHeadCode;

	@Override
	public String getTabName() {
		return "Calls";
	}

	@Override
	public void init(NotationEditorDialog parent, NotationEditorEditMode editMode) {
		super.init(parent, editMode);

		cannedCalls.selectedProperty().addListener(this::useCannedCallsUpdater);
		cannedCalls.selectedProperty().addListener(parent::rebuildNotationUpdater);

		callsList.setPlaceholder(new Label("No Calls Defined"));
		callsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			updateDefaultCallButtonDisableState();
		});
		leadHeadCode.setDisable(true);

	}

	public void useCannedCallsUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		callsList.setDisable(newValue);
		addCallButton.setDisable(newValue);
		removeCallButton.setDisable(newValue);
		updateDefaultCallButtonDisableState();
	}

	private void updateDefaultCallButtonDisableState() {
		defaultCallButton.setDisable(cannedCalls.isSelected() ||
									 callsList.getSelectionModel().selectedItemProperty().get() == null);
		defaultCallButton.setPressedState(!cannedCalls.isSelected() &&
				callsList.getSelectionModel().selectedItemProperty().get() != null &&
				DEFAULT_CALL_TOKEN.equals(callsList.getSelectionModel().selectedItemProperty().get().getSelected()));
	}

	@Override
	public void buildDialogDataFromNotation(NotationBody notation) {
		cannedCalls.setSelected(notation.isCannedCalls());

		int selectedIndex = callsList.getSelectionModel().selectedIndexProperty().get();
		ObservableList<CallModel> items = callsList.getItems();
		items.clear();
		Set<NotationCall> calls = notation.getCalls();
		NotationCall defaultCall = notation.getDefaultCall();

		for (NotationCall call : calls) {
			items.add(new CallModel(
					call.getName(),
					call.getNameShorthand(),
					call.getNotationDisplayString(false),
					(call == defaultCall)? DEFAULT_CALL_TOKEN :""));
		}
		//TODO Would be really nice to keep the selected item. callsList.getSelectionModel().selectedIndexProperty(selectedIndex);

		leadHeadCode.setText(notation.getLeadHeadCode());
	}

	@Override
	public void buildNotationFromDialogData(NotationBuilder notationBuilder) {

		if (cannedCalls.isSelected()) {
			notationBuilder.setCannedCalls();
		}
		else {
			for (CallModel call : callsList.getItems()) {
				notationBuilder.addCall(call.getCallName(),
						call.getCallShorthand(),
						call.getNotation(),
						(DEFAULT_CALL_TOKEN.equals(call.getSelected())));
			}
		}
	}

	@FXML
	private void onAddCall() {

	}

	@FXML
	private void onRemoveCall() {

	}

	@FXML
	private void onDefaultCall() {
		int selectedIndex = callsList.getSelectionModel().selectedIndexProperty().get();
		if (selectedIndex == -1) {
			return;
		}

		for (int index = 0;index<callsList.getItems().size();index++) {
			callsList.getItems().get(index).setSelected((index == selectedIndex)?DEFAULT_CALL_TOKEN:"");
		}
		parent.rebuildNotationFromDialogData();
	}
}