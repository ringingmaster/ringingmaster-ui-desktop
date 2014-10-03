package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DocumentManager {

	private static final DocumentManager INSTANCE = new DocumentManager();

	private final Set<DocumentManagerListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

	public static DocumentManager getInstance() {
		return INSTANCE;
	}

	private DocumentManager() {
	}

	public void addListener(DocumentManagerListener listener) {
		final boolean added = listeners.add(listener);
		checkState(added);
	}

	public void buildNewDocument() {
		TouchDocument document = new TouchDocument();

		for (DocumentManagerListener listener : listeners) {
			listener.documentManager_setDocument(document);
		}

	}
}
