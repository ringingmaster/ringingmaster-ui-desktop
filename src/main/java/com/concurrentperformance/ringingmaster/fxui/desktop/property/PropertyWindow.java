package com.concurrentperformance.ringingmaster.fxui.desktop.property;

import com.concurrentperformance.ringingmaster.fxui.desktop.property.methods.PropertyNotationWindow;
import com.concurrentperformance.ringingmaster.fxui.desktop.property.setup.PropertySetupWindow;
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
	private final PropertyNotationWindow propertyNotationWindow = new PropertyNotationWindow();

	public PropertyWindow() {
		createTab("Methods", propertyNotationWindow);
		createTab("Setup", propertySetupWindow);
	}


	private void createTab(String name, Node node) {
		Tab tab = new Tab();
		tab.setText(name);
		tab.setContent(node);
		getTabs().add(tab);
	}
}
