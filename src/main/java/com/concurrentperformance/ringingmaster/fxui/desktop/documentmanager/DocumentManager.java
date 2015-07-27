package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.TouchPanel;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class DocumentManager extends ConcurrentListenable<DocumentManagerListener> implements Listenable<DocumentManagerListener> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private TouchDocument currentDocument = null;
	private TabPane documentWindow;
	private int docNumber = 0;

	private ProofManager proofManager;

	public void init() {
		documentWindow.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		documentWindow.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			log.info("Got Tab [{}]", newValue);
			//TODO
		});
	}

	public void buildNewDocument() {
		TouchPanel touchPanel = new TouchPanel();;// TODO SPRING should this be a spring builder?
		touchPanel.setDocumentManager(this);
		createTab("New Touch #" + ++docNumber, touchPanel);

		currentDocument = new TouchDocument(proofManager);// TODO SPRING should this be a spring builder?
		currentDocument.addListener(touchDocument -> fireUpdateDocument());
		fireUpdateDocument();
	}


	private void createTab(String name, Node node) {
		Tab tab = new Tab();
		tab.setText(name);
		tab.setContent(node);
		documentWindow.getTabs().add(tab);
	}

	private void fireUpdateDocument() {
		for (DocumentManagerListener listener : getListeners()) {
			listener.documentManager_updateDocument(currentDocument);
		}
	}

	public TouchDocument getCurrentDocument() {
		return currentDocument;
	}

	public void setDocumentWindow(TabPane documentWindow) {
		this.documentWindow = documentWindow;
	}

	public void setProofManager(ProofManager proofManager) {
		this.proofManager = proofManager;
	}
}
