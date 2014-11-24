package com.concurrentperformance.ringingmaster.fxui.desktop.property;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class PropertyWindow extends TabPane {

	private final PropertySetupWindow propertySetupWindow = new PropertySetupWindow();
	private final PropertyMethodsWindow propertyMethodsWindow = new PropertyMethodsWindow();

	public PropertyWindow() {
		createTab("Setup", propertySetupWindow);
		createTab("Methods", propertyMethodsWindow);
	}


	private void createTab(String name, Node node) {
		Tab tab = new Tab();
		tab.setText(name);
		tab.setContent(node);
		getTabs().add(tab);
	}
}
