package com.concurrentperformance.ringingmaster.ui.desktop.documentmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DefaultDocument implements Document {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Path path;
	private String documentName;
	private boolean dirty;

	@Override
	public boolean hasFileLocation() {
		return path != null;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean dirty) {
		log.info("Setting document [{}] [{}]", getNameForTab(), dirty?"dirty":"clean");
		this.dirty = dirty;
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
