package com.concurrentperformance.ringingmaster.ui.desktop.notationsearch;

import com.concurrentperformance.ringingmaster.persist.DocumentPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.LibraryNotationPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.NotationLibraryPersist;
import com.concurrentperformance.util.radixtree.map.SuffixTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationLibraryManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String libraryLocation;

    private final CountDownLatch loaded = new CountDownLatch(1);
    private volatile List<LibraryNotationPersist> notations;
    private final SuffixTreeMap<LibraryNotationPersist> suffixTreeMap = new SuffixTreeMap<>();


    public void init() throws URISyntaxException {
        checkNotNull(libraryLocation);
        URL resource = NotationLibraryManager.class.getClassLoader().getResource(libraryLocation);

        Path path = Paths.get(resource.toURI());

        loadLibraryAsynch(path);
    }

    private void loadLibraryAsynch(final Path path) {
        Thread thread = new Thread(() -> {
            log.info(">>>> Notation Library load");
            NotationLibraryPersist notationLibraryPersist = new DocumentPersist().readNotationLibrary(path);
            notations = notationLibraryPersist.getNotation();
            log.info("<<<< Notation Library load");

            log.info(">>>> Build Notation Index");
            notations.stream()
                    .forEach(notation -> suffixTreeMap.put(notation.getName().toLowerCase(), notation));
            log.info("<<<< Build Notation Index");

            loaded.countDown();

        }, "Notation Library Loading");

        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public boolean isLoaded() {
        return loaded.getCount() == 0;
    }

    public List<LibraryNotationPersist> findNotation(String partialName) {
        ArrayList<LibraryNotationPersist> startingWith = suffixTreeMap.getStartingWith(partialName.toLowerCase(), new ArrayList<>());
        return  startingWith;
    }

    public void setLibraryLocation(String libraryLocation) {
        this.libraryLocation = libraryLocation;
    }
}
