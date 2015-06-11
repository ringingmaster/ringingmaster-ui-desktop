package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DocumentManager extends ConcurrentListenable<DocumentManagerListener> implements Listenable<DocumentManagerListener> {

	private static final DocumentManager INSTANCE = new DocumentManager();


	private TouchDocument currentDocument = null;

	public static DocumentManager getInstance() {
		return INSTANCE;
	}

	private DocumentManager() {
	}

	public static void buildNewDocument() {
		INSTANCE.doBuildNewDocument();
	}

	private void doBuildNewDocument() {
		currentDocument = new TouchDocument();
		currentDocument.addListener(touchDocument -> fireUpdateDocument());
		fireUpdateDocument();
	}

	private void fireUpdateDocument() {
		for (DocumentManagerListener listener : getListeners()) {
			listener.documentManager_updateDocument(currentDocument);
		}
	}

	public static TouchDocument getCurrentDocument() {
		return INSTANCE.currentDocument;

	}
}
