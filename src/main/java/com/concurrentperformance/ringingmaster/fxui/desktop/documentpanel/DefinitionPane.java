package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas.GridPane;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModel;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DefinitionPane extends VBox {

	public DefinitionPane() {
		setSpacing(6.0);
	}

	public void setModels(List<GridModel> gridModels) {

		reconcileDefinitions(gridModels);

		for (int i=0;i<gridModels.size();i++) {
			((GridPane)getChildren().get(i)).setModel(gridModels.get(i));
		}
	}

	private void reconcileDefinitions(List<GridModel> gridModels) {
		if (getChildren().size() == gridModels.size()) {
			return;
		}

		while (getChildren().size() < gridModels.size()) {
			final GridPane gridPane = new GridPane("Definition");
			getChildren().add(gridPane);
		}

		while (getChildren().size() > gridModels.size()) {
			getChildren().remove(0);
		}
	}
}
