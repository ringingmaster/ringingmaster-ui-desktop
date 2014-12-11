package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.util.Set;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class Calls {

	@FXML private TableView<CallModel> callsList;

	public void setNotation(NotationBody notation) {

		ObservableList<CallModel> items = callsList.getItems();
		items.clear();
		Set<NotationCall> calls = notation.getCalls();
		NotationCall defaultCall = notation.getDefaultCall();
		for (NotationCall call : calls) {
			items.add(new CallModel(
					call.getName(),
					call.getNameShorthand(),
					call.getNotationDisplayString(false),
					(call == defaultCall)?"default":""));
		}
	}
}