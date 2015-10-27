package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import javafx.scene.Node;

import java.nio.file.Path;

/**
 * TODO Comments
 *
 * @author Lake
 */
public interface Document {

	boolean isSaved();
	Path getPath();
	void setPath(Path path);
	void setDocumentName(String documentName);

	String getNameForApplicationTitle();
	String getNameForTab();

	default Node getNode() {return null;};
}