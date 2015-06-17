package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.TouchPanel;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DocumentManager extends ConcurrentListenable<DocumentManagerListener> implements Listenable<DocumentManagerListener> {

	private static final DocumentManager INSTANCE = new DocumentManager();

	private TouchDocument currentDocument = null;
	private TabPane docTabPane = new TabPane();


	public static DocumentManager getInstance() {
		return INSTANCE;
	}

	private DocumentManager() {
	}

	public static void buildNewDocument() {
		INSTANCE.doBuildNewDocument();
	}

	private void doBuildNewDocument() {
		createTab("My Touch", new TouchPanel());

		currentDocument = new TouchDocument();
		currentDocument.addListener(touchDocument -> fireUpdateDocument());
		fireUpdateDocument();
	}



	private void createTab(String name, Node node) {
		Tab tab = new Tab();
		tab.setText(name);
		tab.setContent(node);
		docTabPane.getTabs().add(tab);
	}

	private void fireUpdateDocument() {
		for (DocumentManagerListener listener : getListeners()) {
			listener.documentManager_updateDocument(currentDocument);
		}
	}

	public static TouchDocument getCurrentDocument() {
		return INSTANCE.currentDocument;
	}


	public Node getDocPane() {

		return docTabPane;
	}
}
