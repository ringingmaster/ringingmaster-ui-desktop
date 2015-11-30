package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.fxutils.lifecycle.ShutdownService;
import com.concurrentperformance.fxutils.lifecycle.ShutdownServiceListener;
import com.concurrentperformance.fxutils.lifecycle.StartupService;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

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
	private ShutdownService shutdownService;
	private StartupService startupService;
	private DocumentTypeManager documentTypeManager; //TODO eventually this will be a Set of different document types. It can then control a menulist of document types when creating a new document

	public void init() {
		documentWindow.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		documentWindow.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			updateTitles();
			fireUpdateDocument();
		});

		startupService.addListener(this::startup);
		shutdownService.addListener(this::shutdown);
	}

	public void startup() {
		Preferences userPrefs = Preferences.userNodeForPackage(getClass());
		final int docCount = userPrefs.getInt("doc.count", 0);
		for (int tabIndex=0;tabIndex<docCount;tabIndex++) {
			Path path = Paths.get(userPrefs.get("doc." + tabIndex, ""));
			log.info("Loading document [{}]", path);
			final Document document = documentTypeManager.openDocument(path);
			buildTabForDocument(document);
		}

	}

	public ShutdownServiceListener.ShutdownOptions shutdown() {
		ObservableList<Tab> tabs = documentWindow.getTabs();
		for (Tab tab : tabs) {
			boolean closeTab = DocumentManager.this.closeDocumentTabAttempt(tab);
			if (!closeTab) {
				return ShutdownServiceListener.ShutdownOptions.PREVENT_SHUTDOWN;
			}
		}
		log.info("All touch documents saved");

		// Now save document list
		Preferences userPrefs = Preferences.userNodeForPackage(getClass());
		int docsToRestoreCount = 0;
		for (int tabIndex=0;tabIndex<tabs.size();tabIndex++) {
			Tab tab = tabs.get(tabIndex);
			Document document = getDocument(tab);
			if (document.getPath() != null) {
				userPrefs.put("doc." + docsToRestoreCount, document.getPath().toString());
				docsToRestoreCount++;
			}
		}
		userPrefs.putInt("doc.count", docsToRestoreCount);

		return ShutdownServiceListener.ShutdownOptions.ALLOW_SHUTDOWN;
	}

	private boolean closeDocumentTabAttempt(Tab tab) {
		Document document = getDocument(tab);

		log.info("Checking if [{}] is dirty", document.getNameForApplicationTitle());
		if (document.isDirty()) {
			//TODO it would be nice if the save dialog could have a don't save option instead of this additional Alert.
			log.info("Ask user if save required for [{}]", document.getNameForApplicationTitle());
			Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, "Your changes will be lost if you don't save them."  + System.lineSeparator(),
					ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
			dialog.setTitle("Save");
			dialog.setHeaderText("Do you want to save the changes made to the document:" + System.lineSeparator() +
					"'"  + document.getNameForApplicationTitle() + "'?");
			dialog.getDialogPane().setMinHeight(10);
			dialog.getDialogPane().setMinWidth(10);
			dialog.getDialogPane().setMaxWidth(500);
			Optional<ButtonType> buttonType = dialog.showAndWait();

			if (buttonType.get().equals(ButtonType.YES)) {
				log.info("User requested save for [{}]", document.getNameForApplicationTitle());
				boolean successfulSave = save(tab);
				if (!successfulSave) {
					return false;
				}
			}
			else if (buttonType.get().equals(ButtonType.CANCEL)) {
				log.info("User cancelled save for [{}]", document.getNameForApplicationTitle());
				return false;
			}
			else if (buttonType.get().equals(ButtonType.NO)) {
				log.info("User declined save for [{}]", document.getNameForApplicationTitle());
			}
		}
		return true;
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
		tab.setOnCloseRequest(event -> {
			log.info("Closing individual tab [{}]", document.getNameForApplicationTitle());
			boolean closeTab = closeDocumentTabAttempt(tab);
			if (!closeTab) {
				log.info("Prevent close of tab [{}]", document.getNameForApplicationTitle());
				// prevent the close by consuming the event
				event.consume();
			}
		});

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

	public void setStartupService(StartupService startupService) {
		this.startupService = startupService;
	}

	@Override
	public String toString() {
		return "DocumentManager{" +
				'}';
	}
}
