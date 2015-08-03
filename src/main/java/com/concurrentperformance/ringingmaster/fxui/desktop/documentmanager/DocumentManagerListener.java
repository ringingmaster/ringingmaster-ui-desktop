package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;

import java.util.Optional;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public interface DocumentManagerListener {

	void documentManager_updateDocument(Optional<TouchDocument> touchDocument);
}
