package org.ringingmaster.ui.desktop.documentmanager;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.ringingmaster.util.javafx.lifecycle.ShutdownService;
import org.ringingmaster.util.javafx.lifecycle.ShutdownServiceListener;
import org.ringingmaster.util.javafx.lifecycle.StartupService;
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
 * @author Steve Lake
 */
public class DocumentManager  {

    public static final String APPLICATION_TITLE = "Ringingmaster Desktop";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private TabPane documentWindow;
    private Stage globalStage;
    private ShutdownService shutdownService;
    private StartupService startupService;
    private DocumentTypeManager documentTypeManager; //TODO eventually this will be a Set of different document types. It can then control a menulist of document types when creating a new document

    private final BehaviorSubject<Optional<Document>> observableActiveDocument = BehaviorSubject.createDefault(Optional.empty());


    public void init() {
        documentWindow.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        documentWindow.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            observableActiveDocument.onNext(Optional.ofNullable(newTab).map(this::getDocument));
        });

        startupService.addListener(this::openPreviouslyOpenDocuments);
        shutdownService.addListener(this::shutdown);

        Observable<Optional<Path>> observablePath = observableActiveDocument
                .switchMap(document -> document.map(Document::observablePath).orElse(Observable.just(Optional.empty())));

        Observable<Boolean> observableDirty = observableActiveDocument
                .switchMap(document -> document.map(Document::observableDirty).orElse(Observable.just(false)));

        Observable.combineLatest(observablePath, observableDirty, (optionalPath, dirty) ->
                optionalPath
                        .map(path -> APPLICATION_TITLE + " - [" + path.toString() + (dirty ? " *" : "") + "]")
                        .orElse(APPLICATION_TITLE))//TODO Reactive need the fallback doc name when no save path
                .subscribe(title -> globalStage.setTitle(title));

    }


    public Observable<Optional<Document>> observableActiveDocument() {
        return observableActiveDocument;
    }

    public void openPreviouslyOpenDocuments() {
        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        final int docCount = userPrefs.getInt("doc.count", 0);
        log.info("Reading from user prefs [{}]>[{}]", "doc.count" , docCount);

        for (int tabIndex = 0; tabIndex < docCount; tabIndex++) {
            Path path = Paths.get(userPrefs.get("doc." + tabIndex, ""));
            log.info("Reading from user prefs [{}]>[{}]", "doc." + tabIndex , path);
            try {
                final Document document = documentTypeManager.openDocument(path);
                buildTabForDocument(document);
                document.setDirty(false);
            } catch (RuntimeException e) {
                log.error("Failed to open document [" + path + "] during openPreviouslyOpenDocuments", e);
            }
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
        log.info("All composition documents saved");

        // Now save document list
        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        int docsToRestoreCount = 0;
        for (int tabIndex = 0; tabIndex < tabs.size(); tabIndex++) {
            Tab tab = tabs.get(tabIndex);
            Document document = getDocument(tab);
            if (document.getPath().isPresent()) {
                log.info("Writing to user prefs [{}]>[{}]", "doc." + docsToRestoreCount, document.getPath().get().toString());

                userPrefs.put("doc." + docsToRestoreCount, document.getPath().get().toString());
                docsToRestoreCount++;
            }
        }

        log.info("Writing to user prefs [{}]>[{}]", "doc.count", docsToRestoreCount);
        userPrefs.putInt("doc.count", docsToRestoreCount);

        return ShutdownServiceListener.ShutdownOptions.ALLOW_SHUTDOWN;
    }

    private boolean closeDocumentTabAttempt(Tab tab) {
        Document document = getDocument(tab);

        log.info("Checking if [{}] is dirty", document);
        if (document.isDirty()) {
            //TODO it would be nice if the save dialog could have a don't save option instead of this additional Alert.
            log.info("Ask user if save required for [{}]", document);
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, "Your changes will be lost if you don't save them." + System.lineSeparator(),
                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            dialog.setTitle("Save");
            dialog.setHeaderText("Do you want to save the changes made to the document:" + System.lineSeparator() +
                    "'" + document.getPath().orElse(Path.of("")).toString() + "' ?");
            dialog.getDialogPane().setMinHeight(10);
            dialog.getDialogPane().setMinWidth(10);
            dialog.getDialogPane().setMaxWidth(500);
            Optional<ButtonType> buttonType = dialog.showAndWait();

            if (buttonType.get().equals(ButtonType.YES)) {
                log.info("User requested save for [{}]", document);
                boolean successfulSave = save(tab);
                return successfulSave;
            } else if (buttonType.get().equals(ButtonType.CANCEL)) {
                log.info("User cancelled save for [{}]", document);
                return false;
            } else if (buttonType.get().equals(ButtonType.NO)) {
                log.info("User declined save for [{}]", document);
            }
        }
        return true;
    }

    public void createNewDocument() {
        Document document = documentTypeManager.createNewDocument();
        buildTabForDocument(document);
        document.setDirty(false);
    }

    public void chooseAndOpenDocument() {
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
            fileChooser.setInitialFileName(document.getFallbackName());
            fileChooser.getExtensionFilters().addAll(
                    documentTypeManager.getFileChooserExtensionFilters());
            File selectedFile = fileChooser.showSaveDialog(globalStage);
            if (selectedFile == null) {
                return false;
            } else {
                document.setPath(selectedFile.toPath());
            }
        }

        documentTypeManager.saveDocument(document);

        return true;
    }

    private void buildTabForDocument(Document document) {
        Tab tab = new Tab();

        Observable.combineLatest(document.observablePath(), document.observableFallbackName(), document.observableDirty(),
                (optionalPath, fallbackName, dirty) -> optionalPath
                        .map(path -> path.getFileName().toString() + (dirty ? " *" : "")).orElseGet(() -> fallbackName + (dirty ? " *" : "")))
                .subscribe(tab::setText);


        tab.setContent(document.getNode());
        documentWindow.getTabs().add(tab);
        documentWindow.getSelectionModel().select(tab);
        tab.setOnCloseRequest(event -> {
            log.info("Closing individual tab [{}]", document);
            boolean closeTab = closeDocumentTabAttempt(tab);
            if (!closeTab) {
                log.info("Prevent close of tab [{}]", document);
                // prevent the close by consuming the event
                event.consume();
            }
        });

        //TODO remove??  updateApplicationTitle();
    }


    private Tab     getCurrentTab() {
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
