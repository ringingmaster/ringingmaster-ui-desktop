package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
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
	private CheckBox useDefault;
	@FXML
	private TextField leadHeadCode;

	@Override
	public String getTabName() {
		return "Calls";
	}

	@Override
	public void init(NotationEditorDialog parent, NotationEditorEditMode editMode) {
		super.init(parent, editMode);

		useDefault.selectedProperty().addListener(this::useDefaultUpdater);
	}

	public void useDefaultUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		callsList.setDisable(newValue);
		parent.checkNotationFromDialogData();
	}

	@Override
	public void buildDialogDataFromNotation(NotationBody notation) {
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

		leadHeadCode.setText(notation.getLeadHeadCode());
	}

	@Override
	public void buildNotationFromDialogData(NotationBuilder notationBuilder) {
		for (CallModel call : callsList.getItems()) {
			notationBuilder.addCall(call.getCallName(),
									call.getCallShorthand(),
									call.getNotation(),
									(DEFAULT_CALL_TOKEN.equals(call.getSelected())));
		}
	}
}