package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.TouchPanel;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

	private static final DocumentManager INSTANCE = new DocumentManager();

	private TouchDocument currentDocument = null;
	private TabPane docTabPane = new TabPane();
	private int docNumber = 0;


	public static DocumentManager getInstance() {
		return INSTANCE;
	}

	private DocumentManager() {
		docTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		docTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				log.info("Got Tab " + newValue.toString());
			}
		});
	}

	public static void buildNewDocument() {
		INSTANCE.doBuildNewDocument();
	}

	private void doBuildNewDocument() {
		createTab("New Touch #" + ++docNumber, new TouchPanel());

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
