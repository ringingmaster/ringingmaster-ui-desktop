package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.fxutils.shutdown.ShutdownService;
import com.concurrentperformance.fxutils.shutdown.ShutdownServiceVeto;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages crating and switching of documents.
 *
 * @author Lake
 */
public class DocumentManager extends ConcurrentListenable<DocumentManagerListener> implements Listenable<DocumentManagerListener> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private TabPane documentWindow;
	private Stage globalStage;
	ShutdownService shutdownService;
	private DocumentTypeManager documentTypeManager; //TODO eventually this will be a Set of different documents. It can then control a menulist of document types when creating a new document

	public void init() {
		documentWindow.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		documentWindow.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			updateTitles();

			fireUpdateDocument();
		});

		shutdownService.addListener(() -> {
			for (Tab tab : documentWindow.getTabs()) {
				Document document = getDocument(tab);

				log.info("Checking if [{}] is dirty", document.getNameForTab());
				if (document.isDirty()) {

					log.info("Requesting save for [{}]", document.getNameForTab());
					boolean successfulSave = save(tab);
					if (!successfulSave) {
						return ShutdownServiceVeto.ShutdownOptions.PREVENT_SHUTDOWN;
					}
				}
			}
			return ShutdownServiceVeto.ShutdownOptions.ALLOW_SHUTDOWN;
		});
	}

	public void newDocument() {
		Document document =  documentTypeManager.createNewDocument();
		buildTabForDocument(document);
	}

	public void openDocument() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open " + documentTypeManager.getDocumentTypeName() + " File");
		fileChooser.getExtensionFilters().addAll(
				documentTypeManager.getFileChooserExtensionFilters());
		List<File> files = fileChooser.showOpenMultipleDialog(globalStage);
		if (files != null) {
			for (File file : files) {
				Path path = file.toPath();
				final Document document = documentTypeManager.openDocument(path);
				document.setPath(path);
				buildTabForDocument(document);
			}
		}
	}

	public void saveCurrentDocument() {
		Tab currentTab = getCurrentTab();
		if (currentTab != null) {
			save(currentTab);
		}
	}


	/**
	 * Save the document associated with the tab, prompting for a new file location
	 * if one does not exist
	 *
	 * @param tab
	 * @return true if save successful
	 */
	private boolean save(Tab tab) {
		Document document = getDocument(tab);

		if (!document.hasFileLocation()) {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save " + documentTypeManager.getDocumentTypeName() + " File");
			fileChooser.setInitialFileName(document.getNameForTab());
			fileChooser.getExtensionFilters().addAll(
					documentTypeManager.getFileChooserExtensionFilters());
			File selectedFile = fileChooser.showSaveDialog(globalStage);
			if (selectedFile == null) {
				return false;
			}
			else {
				document.setPath(selectedFile.toPath());
			}
		}

		documentTypeManager.saveDocument(document);
		updateTitles();

		return true;
	}

	private void buildTabForDocument(Document document) {
		Tab tab = new Tab();
		tab.setContent(document.getNode());
		documentWindow.getTabs().add(tab);
		documentWindow.getSelectionModel().select(tab);

		updateTitles();
	}

	public void updateTitles() {
		StringBuilder applicationTitle = new StringBuilder();
		applicationTitle.append("Ringingmaster Desktop");

		Tab tab = getCurrentTab();
		if (tab != null) {
			Document document = getDocument(tab);
			boolean dirty = document.isDirty();
			tab.setText(document.getNameForTab() + (dirty?"*":""));
			applicationTitle.append(" - [")
					.append(document.getNameForApplicationTitle())
					.append(dirty?"*":"")
					.append("]");
		}

		globalStage.setTitle(applicationTitle.toString());
	}


	private void fireUpdateDocument() {
		Document currentDocument = getCurrentDocument();

		for (DocumentManagerListener listener : getListeners()) {
			listener.documentManager_documentActivated(currentDocument);
		}
	}

	public Document getCurrentDocument() {
		Tab selectedItem = getCurrentTab();
		if (selectedItem == null) {
			return null;
		}
		return getDocument(selectedItem);
	}

	private Tab getCurrentTab() {
		return documentWindow.getSelectionModel().getSelectedItem();
	}

	private Document getDocument(Tab tab) {

		return checkNotNull((Document) tab.getContent());
	}

	public void setDocumentWindow(TabPane documentWindow) {
		this.documentWindow = documentWindow;
	}

	public void setGlobalStage(Stage globalStage) {
		this.globalStage = globalStage;
	}

	public void setDocumentTypeManager(DocumentTypeManager documentTypeManager) {
		this.documentTypeManager = documentTypeManager;
	}

	public void setShutdownService(ShutdownService shutdownService) {
		this.shutdownService = shutdownService;
	}
}
