package com.concurrentperformance.ringingmaster.fxui.desktop.grid.canvas;

import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridModelListener;
import javafx.scene.layout.Pane;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class GridPane extends Pane implements GridModelListener {

	private final SelectionLayer selectionLayer = new SelectionLayer(this);
	private final MainDrawingLayer mainDrawingLayer = new MainDrawingLayer(this);
	private final InteractionLayer interactionLayer = new InteractionLayer(this);
	private GridDimension dimensions;
	private GridModel model;
	private final String name;


	public GridPane(String name) {
		// Order is important here to get the drawing in the correct Z Order.
		getChildren().add(selectionLayer);
		getChildren().add(mainDrawingLayer);
		getChildren().add(interactionLayer);
		this.name = name;
	}


	public void setModel(GridModel model) {
		if (this.model != null) {
			this.model.deRegisterListener(this);
		}
		this.model = checkNotNull(model);
		this.model.registerListener(this);
		gridModelListener_dimensionsChanged();
	}

	@Override
	public void gridModelListener_dimensionsChanged() {
		dimensions = new GridDimensionBuilder().setModel(model).build();

		setMinWidth(dimensions.getTableBottom());
		setMinHeight(dimensions.getTableBottom());

		mainDrawingLayer.setWidth(dimensions.getTableRight());
		mainDrawingLayer.setHeight(dimensions.getTableBottom());

		interactionLayer.setPrefSize(dimensions.getTableRight(), dimensions.getTableBottom());

		selectionLayer.setWidth(dimensions.getTableRight());
		selectionLayer.setHeight(dimensions.getTableBottom());

		selectionLayer.draw();
		mainDrawingLayer.draw();
		interactionLayer.draw();
	}

	@Override
	public void gridModelListener_caretMoved() {
		interactionLayer.showCaret();
		interactionLayer.draw();
	}

	@Override
	public void gridModelListener_selectionChanged() {
		selectionLayer.draw();
	}

	public GridDimension getDimensions() {
		return dimensions;
	}

	GridModel getModel() {
		return model;
	}

	public String getName() {
		return name;
	}
}
