package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.fxutils.dialog.EditMode;
import com.concurrentperformance.fxutils.dialog.SkeletalDialog;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class CallEditor extends SkeletalDialog<CallModel> {

	private final static Logger log = LoggerFactory.getLogger(CallEditor.class);

	public static final String CALL_EDITOR_FXML = "CallEditor.fxml";

	@FXML
	private TextField callName;
	@FXML
	private TextField callShorthand;
	@FXML
	private TextField notation;


	public static void showDialog(EditMode editMode, CallModel model, Window owner, Function<CallModel, Boolean> onSuccessHandler) {
		new Launcher().showDialog(editMode, model, owner, CALL_EDITOR_FXML, onSuccessHandler);
	}

	@Override
	protected void buildDialogDataFromModel(CallModel model) {
		callName.setText(model.getCallName());
		callShorthand.setText(model.getCallName());
		notation.setText(model.getCallName());
	}

	@Override
	protected CallModel buildModelFromDialogData() {
		CallModel callModel = new CallModel();
		callModel.setCallName(callName.getText());
		callModel.setCallShorthand(callShorthand.getText());
		callModel.setNotation(notation.getText());
		return callModel;
	}
}