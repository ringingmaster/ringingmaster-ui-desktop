package com.concurrentperformance.ringingmaster.ui.desktop.notationeditor;


import com.concurrentperformance.fxutils.dialog.EditMode;
import com.concurrentperformance.fxutils.dialog.SkeletalDialog;
import com.concurrentperformance.ringingmaster.ui.desktop.RingingMasterDesktopApp;
import com.google.common.collect.Lists;
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


public class CallEditorDialog extends SkeletalDialog<CallModel> {

	private final static Logger log = LoggerFactory.getLogger(CallEditorDialog.class);

	public static final String CALL_EDITOR_FXML = "CallEditor.fxml";

	@FXML
	private TextField callName;
	@FXML
	private TextField callShorthand;
	@FXML
	private TextField notation;


	public static void showDialog(EditMode editMode, CallModel model, Window owner, Function<CallModel, Boolean> onSuccessHandler) {
		new Launcher<CallModel>().showDialog(editMode, model, owner, CallEditorDialog.class.getResource(CALL_EDITOR_FXML),
				Lists.<String>newArrayList(RingingMasterDesktopApp.STYLESHEET), onSuccessHandler);
	}

	@Override
	protected void populateDialogFromModel(CallModel model) {
		callName.setText(model.getCallName());
		callShorthand.setText(model.getCallShorthand());
		notation.setText(model.getNotation());
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