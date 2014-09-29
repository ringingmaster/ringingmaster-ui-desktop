package com.concurrentperformance.ringingmaster.fxui.desktop.panel;

import com.concurrentperformance.ringingmaster.fxui.desktop.document.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.canvas.GridPane;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridModel;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class TouchPanel extends ScrollPane {

	private final Text title = new Text();
	private final GridPane gridPane = new GridPane("Main");
	private final DefinitionPane definitionPane = new DefinitionPane();

	public TouchPanel() {
		title.setFont(new Font(20));
		title.setFocusTraversable(false);

		VBox verticalLayout = new VBox(title, gridPane, definitionPane);
		verticalLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		verticalLayout.setSpacing(20.0);

		setContent(verticalLayout);
		setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		setFitToHeight(true);
		setFitToWidth(false);
	//	setStyle(".scroll-pane > .viewport  { -fx-background-color: red; }");
	}

	@Override
	public void requestFocus() {
		// Prevent grabbing focus
		//super.requestFocus();
	}

	public void bind(TouchDocument document) {
		title.textProperty().bind(document.getTitleProperty());

		GridModel gridModel = document.getMainGridModel();
		gridPane.setModel(gridModel);

		List<GridModel> gridModels = document.getDefinitionGridModels();
		definitionPane.setModels(gridModels);

		setFocusTraversable(false);
	}

}
