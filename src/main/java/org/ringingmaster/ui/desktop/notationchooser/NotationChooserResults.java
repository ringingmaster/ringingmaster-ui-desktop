package org.ringingmaster.ui.desktop.notationchooser;

import org.ringingmaster.persist.generated.v1.LibraryNotationPersist;

import java.util.Collections;
import java.util.List;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationChooserResults {

    private List<LibraryNotationPersist> notations = Collections.emptyList();

    public List<LibraryNotationPersist> getNotations() {
        return notations;
    }

    public void setNotations(List<LibraryNotationPersist> notations) {
        this.notations = notations;
    }
}
