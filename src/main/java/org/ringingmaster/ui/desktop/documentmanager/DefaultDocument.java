package org.ringingmaster.ui.desktop.documentmanager;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class DefaultDocument implements Document {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private BehaviorSubject<Optional<Path>> path = BehaviorSubject.createDefault(Optional.empty());
    private BehaviorSubject<Boolean> dirty = BehaviorSubject.createDefault(false);

    public DefaultDocument() {
        observableDirty().subscribe(dirty ->
            log.info("Document [{}] is [{}]", getPath(), dirty ? "dirty" : "clean"));
    }

    @Override
    public boolean hasFileLocation() {
        return path.getValue().isPresent();
    }

    @Override
    public boolean isDirty() {
        return dirty.getValue();
    }

    @Override
    public Observable<Boolean> observableDirty() {
        return dirty.distinctUntilChanged();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty.onNext(dirty);
    }

    @Override
    public Optional<Path> getPath() {
        return path.getValue();
    }

    @Override
    public void setPath(Path path) {
        this.path.onNext(Optional.of(path));
    }

    @Override
    public Observable<Optional<Path>> observablePath() {
        return path;
    }
}
