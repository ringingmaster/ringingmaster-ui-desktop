package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas.GridPane;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModel;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class TouchPanel extends ScrollPane {


	private final TitlePane titlePane = new TitlePane();
	private final GridPane gridPane = new GridPane("Main");
	private final DefinitionPane definitionPane = new DefinitionPane();

	public TouchPanel() {

		VBox verticalLayout = new VBox(titlePane, gridPane, definitionPane);
		verticalLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		verticalLayout.setSpacing(20.0);

		setContent(verticalLayout);
		setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		setFitToHeight(true);
		setFitToWidth(false);
		setFocusTraversable(false);

		DocumentManager.getInstance().addListener(document -> {
			titlePane.setText(document.getTitle(), document.getAuthor());

			GridModel gridModel = document.getMainGridModel();
			gridPane.setModel(gridModel);

			List<GridModel> gridModels = document.getDefinitionGridModels();
			definitionPane.setModels(gridModels);
		});
	}

	@Override
	public void requestFocus() {
		// Prevent grabbing focus
		//super.requestFocus();
	}

}
