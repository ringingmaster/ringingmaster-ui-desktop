package com.concurrentperformance.ringingmaster.fxui.desktop.analysis;

import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AnalysisWindow extends TabPane {

	public enum Type { // TODO temp measure until a full windowing system can be put into place.
		LEFT,
		RIGHT
	}

	public AnalysisWindow(Type type) {
		setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		setSide(Side.BOTTOM);

		switch (type) {
			case LEFT:
				createTab("Status", new AnalysisStatusWindow());
				break;
			case RIGHT:
				createTab("Calls Made", new AnalysisCallsMadeWindow());
				break;
		}
	}

	private void createTab(String name, Node node) {
		Tab tab = new Tab();
		tab.setText(name);
		tab.setContent(node);
		getTabs().add(tab);
	}
}

