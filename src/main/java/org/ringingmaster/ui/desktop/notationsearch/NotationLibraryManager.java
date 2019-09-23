package org.ringingmaster.ui.desktop.notationsearch;

import org.ringingmaster.persist.NotationLibraryPersister;
import org.ringingmaster.persist.generated.v1.LibraryNotationPersist;
import org.ringingmaster.persist.generated.v1.NotationLibraryPersist;
import org.ringingmaster.util.radixtree.map.SuffixTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Steve Lake
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
            long start = System.currentTimeMillis();
            log.info(">>>> Notation Library load");
            NotationLibraryPersist notationLibraryPersist = new NotationLibraryPersister().readNotationLibrary(path);
            notations = notationLibraryPersist.getNotation();
            log.info("<<<< Notation Library load. Loaded [{}] notations in [{}]ms", notations.size(), System.currentTimeMillis() - start);

            long startIndex = System.currentTimeMillis();
            log.info(">>>> Build Notation Index");
            notations.forEach(notation -> suffixTreeMap.put(notation.getName().toLowerCase(), notation));
            log.info("<<<< Build Notation Index in [{}]ms", System.currentTimeMillis() - startIndex);

            loaded.countDown();

        }, "Notation Library Loading");

        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public boolean isLoaded() {
        return loaded.getCount() == 0;
    }

    public List<LibraryNotationPersist> findNotationSuggestions(String partialName) {
        try {
            loaded.await();
        } catch (InterruptedException e) {
            log.error("", e);
            return Collections.emptyList();
        }
        ArrayList<LibraryNotationPersist> startingWith = suffixTreeMap.getStartingWith(partialName.toLowerCase(), new ArrayList<>());
        return startingWith;
    }

    public void setLibraryLocation(String libraryLocation) {
        this.libraryLocation = libraryLocation;
    }
}
