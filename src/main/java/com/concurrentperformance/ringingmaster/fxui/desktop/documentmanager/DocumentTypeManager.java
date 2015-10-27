package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import javafx.stage.FileChooser;

import java.nio.file.Path;
import java.util.Collection;

/**
 * TODO Comments
 *
 * @author Lake
 */
public interface DocumentTypeManager {
	Document createNewDocument();
	Document openDocument(Path path);
	void saveDocument(Document document);

	Collection<? extends FileChooser.ExtensionFilter> getFileChooserExtensionFilters();

	String getDocumentTypeName();
}