package org.ringingmaster.ui.desktop.notationeditor;

import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.util.javafx.dialog.EditMode;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public interface NotationEditorTabController {

    String getTabName();

    void init(NotationEditorDialog parent, EditMode editMode);

    void buildDialogDataFromNotation(Notation notation);

    void buildNotationFromDialogData(NotationBuilder notationBuilder);
}
