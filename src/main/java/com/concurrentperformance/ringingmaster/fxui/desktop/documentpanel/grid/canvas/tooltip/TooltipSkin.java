package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas.tooltip;

/**
 * IMPORTANT: This is a copy of the tooltip from javafx.scene.control.Tooltip. It was checked in
 * as originally written so that any changes can be tracked - with a view to updating if the original
 * changes from java version to version.
 */

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;

/**
 * CSS based skin for Tooltip. It deals mostly with show hide logic for
 * Popup based controls, and specifically in this case for tooltip. It also
 * implements some of the Skin interface methods.
 *
 * TooltipContent class is the actual skin implementation of the tooltip.
 */
public class TooltipSkin implements Skin<Tooltip> {
	private Label tipLabel;

	private Tooltip tooltip;

	public TooltipSkin(Tooltip t) {
		this.tooltip = t;
		tipLabel = new Label();
		tipLabel.contentDisplayProperty().bind(t.contentDisplayProperty());
		tipLabel.fontProperty().bind(t.fontProperty());
		tipLabel.graphicProperty().bind(t.graphicProperty());
		tipLabel.graphicTextGapProperty().bind(t.graphicTextGapProperty());
		tipLabel.textAlignmentProperty().bind(t.textAlignmentProperty());
		tipLabel.textOverrunProperty().bind(t.textOverrunProperty());
		tipLabel.textProperty().bind(t.textProperty());
		tipLabel.wrapTextProperty().bind(t.wrapTextProperty());
		tipLabel.minWidthProperty().bind(t.minWidthProperty());
		tipLabel.prefWidthProperty().bind(t.prefWidthProperty());
		tipLabel.maxWidthProperty().bind(t.maxWidthProperty());
		tipLabel.minHeightProperty().bind(t.minHeightProperty());
		tipLabel.prefHeightProperty().bind(t.prefHeightProperty());
		tipLabel.maxHeightProperty().bind(t.maxHeightProperty());

		// RT-7512 - skin needs to have styleClass of the control
		// TODO - This needs to be bound together, not just set! Probably should
		// do the same for id and style as well.
		tipLabel.getStyleClass().setAll(t.getStyleClass());
		tipLabel.setStyle(t.getStyle());
		tipLabel.setId(t.getId());
	}

	@Override public Tooltip getSkinnable() {
		return tooltip;
	}

	@Override public Node getNode() {
		return tipLabel;
	}

	@Override public void dispose() {
		tooltip = null;
		tipLabel = null;
	}
}
