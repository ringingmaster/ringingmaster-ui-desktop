package com.concurrentperformance.ringingmaster.fxui.desktop.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class ConfiguredMenuBar extends MenuBar {

	public ConfiguredMenuBar() {
		// --- Menu File
		Menu menuFile = new Menu("File");
		menuFile.getItems().add(0,new MenuItem("Stephen"));

		getMenus().addAll(menuFile);

		setUseSystemMenuBar(true);
	}
}
