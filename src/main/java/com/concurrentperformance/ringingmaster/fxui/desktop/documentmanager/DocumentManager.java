package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

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
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages crating and switching of documents.
 *
 * @author Lake
 */
public class DocumentManager extends ConcurrentListenable<DocumentManagerListener> implements Listenable<DocumentManagerListener> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Optional<DocTabHolder> currentDocTab = Optional.empty();
	private TabPane documentWindow;
	private Stage globalStage;
	private DocumentTypeManager documentTypeManager; //TODO eventually this will be a Set of different documents. It can then control a menulist of document types when creating a new document

	public void init() {
		documentWindow.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		documentWindow.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				currentDocTab = Optional.empty();
				setApplicationTitle(null);
			}
			else {
				currentDocTab = Optional.of(new DocTabHolder(newValue));
				setApplicationTitle(currentDocTab.get().document);
			}

			fireUpdateDocument();
		});

		globalStage.setOnCloseRequest(event -> {
			log.info("Attempt at shutting down");
			if (currentDocTab.isPresent()) {
				log.info("Prevent shut down");
				event.consume();
			}
			else {
				log.info("Allowing shut down");
			}
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
		if (currentDocTab.isPresent()) {
			Document document = currentDocTab.get().getDocument();
			Tab tab = currentDocTab.get().getTab();

			if (!document.isSaved()) {

				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save " + documentTypeManager.getDocumentTypeName() + " File");
				fileChooser.setInitialFileName(document.getNameForTab());
				fileChooser.getExtensionFilters().addAll(
						documentTypeManager.getFileChooserExtensionFilters());
				File selectedFile = fileChooser.showSaveDialog(globalStage);
				if (selectedFile == null) {
					return;
				}
				else {
					document.setPath(selectedFile.toPath());
				}
			}

			documentTypeManager.saveDocument(document);

			tab.setText(document.getNameForTab());
			setApplicationTitle(document);
		}
	}

	private void buildTabForDocument(Document document) {
		Tab tab = new Tab();
		tab.setText(document.getNameForTab());
		tab.setContent(document.getNode());
		documentWindow.getTabs().add(tab);
		documentWindow.getSelectionModel().select(tab);

		currentDocTab = Optional.of(new DocTabHolder(tab));
	}


	private void setApplicationTitle(Document document) {
		StringBuilder name = new StringBuilder();
		name.append("Ringingmaster Desktop");
		if (document != null) {
			name.append(" - [").append(document.getNameForApplicationTitle()).append("]");
		}

		globalStage.setTitle(name.toString());
	}

	private void fireUpdateDocument() {
		Document currentDocument = getCurrentDocument();

		for (DocumentManagerListener listener : getListeners()) {
			listener.documentManager_documentActivated(currentDocument);
		}
	}

	public Document getCurrentDocument() {
		return (currentDocTab.isPresent() ? currentDocTab.get().getDocument():null);
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

	private class DocTabHolder {
		private final Tab tab;
		private final Document document;


		private DocTabHolder(Tab tab) {
			this.tab = checkNotNull(tab);
			this.document = checkNotNull((Document) tab.getContent());
		}

		public Tab getTab() {
			return tab;
		}

		public Document getDocument() {
			return document;
		}
	}

}
