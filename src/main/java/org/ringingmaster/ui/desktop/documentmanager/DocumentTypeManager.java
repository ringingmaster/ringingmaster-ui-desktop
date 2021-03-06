package org.ringingmaster.ui.desktop.documentmanager;

import javafx.stage.FileChooser;

import java.nio.file.Path;
import java.util.Collection;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public interface DocumentTypeManager {
    Document createNewDocument();

    Document openDocument(Path path);

    void saveDocument(Document document);

    Collection<? extends FileChooser.ExtensionFilter> getFileChooserExtensionFilters();

    String getDocumentTypeName();
}
