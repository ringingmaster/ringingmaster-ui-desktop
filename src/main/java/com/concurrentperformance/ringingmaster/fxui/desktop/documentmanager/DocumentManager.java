package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.util.beanfactory.BeanFactory;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Manages crating and switching of documents.
 *
 * @author Lake
 */
public class DocumentManager extends ConcurrentListenable<DocumentManagerListener> implements Listenable<DocumentManagerListener> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Optional<TouchDocument> currentDocument = Optional.empty();
	private TabPane documentWindow;
	private int docNumber = 0;

	private ProofManager proofManager;
	private BeanFactory beanFactory;

	public void init() {
		documentWindow.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		documentWindow.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			log.info("Tab " + newValue);
			if (newValue == null) {
				currentDocument = Optional.empty();
				proofManager.parseAndProve(null);
			}
			else {
				TouchDocument touchDocument = (TouchDocument) newValue.getContent();
				touchDocument.parseAndProve();
				currentDocument = Optional.of(touchDocument);
			}
			fireUpdateDocument();
		});
	}

	public void buildNewDocument() {
		final TouchDocument touchDocument = beanFactory.build(TouchDocument.class);
		// this is needed to listn to document updates that are not changed with proof's.
		// example -> alter the start change to something that will fail, and this will
		// allow it to be put back to its original value.
		touchDocument.addListener(touchDoc -> fireUpdateDocument());
		createTab("New Touch #" + ++docNumber, touchDocument);

		currentDocument = Optional.of(touchDocument);
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

	public Optional<TouchDocument> getCurrentDocument() {
		return currentDocument;
	}

	public void setDocumentWindow(TabPane documentWindow) {
		this.documentWindow = documentWindow;
	}

	public void setProofManager(ProofManager proofManager) {
		this.proofManager = proofManager;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
