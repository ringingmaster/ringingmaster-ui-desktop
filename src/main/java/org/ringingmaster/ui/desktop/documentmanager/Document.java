package org.ringingmaster.ui.desktop.documentmanager;

import io.reactivex.Observable;
import javafx.scene.Node;

import java.nio.file.Path;
import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public interface Document {

    boolean isDirty();

    Observable<Boolean> observableDirty();

    void setDirty(boolean dirty);


    boolean hasFileLocation();

    Optional<Path> getPath();

    void setPath(Path path);

    Observable<Optional<Path>> observablePath();


    default Observable<String> observableFallbackName() {
        return null;
    }

    default String getFallbackName() {
        return null;
    }


    default Node getNode() {
        return null;
    }

}
