package org.ringingmaster.ui.desktop.documentpanel;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.ringingmaster.ui.desktop.documentpanel.grid.canvas.GridPane;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridModel;

import java.util.List;

/**
 * A single pane that holds multiple definition panes. One for each definition.
 *
 * @author Lake
 */
public class DefinitionPane extends VBox {

    public DefinitionPane() {
        setSpacing(6.0);
    }

    public void setModels(List<GridModel> gridModels) {

        reconcileDefinitions(gridModels);

        for (int i = 0; i < gridModels.size(); i++) {
            ((GridPane) getChildren().get(i)).setModel(gridModels.get(i));
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

    public void contentsChanged() {
        for (Node node : getChildren()) {
            ((GridPane) node).gridModelListener_contentsChanged();
        }
    }
}
