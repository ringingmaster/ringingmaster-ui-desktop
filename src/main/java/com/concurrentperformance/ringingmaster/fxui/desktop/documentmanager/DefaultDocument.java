package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import javafx.scene.Node;

import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DefaultDocument implements Document {

	private Path path = null;
	private String documentName = null;

	@Override
	public boolean isSaved() {
		return path != null;
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public void setPath(Path path) {
		this.path = path;
		this.documentName = path.getFileName().toString();
	}

	@Override
	public void setDocumentName(String documentName) {
		checkState(path == null, "Once we have a path, then the document name is derived from that.");
		this.documentName = documentName;
	}

	@Override
	public String getNameForApplicationTitle() {
		if (path != null) {
			return path.toString();
			}
		else if (documentName != null) {
			return documentName;
		}
		else {
			throw new RuntimeException("Neither path nor document name are set.");
		}
	}

	@Override
	public String getNameForTab() {
		return documentName;
	}

}
