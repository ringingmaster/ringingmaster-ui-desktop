package org.ringingmaster.ui.desktop.notationeditor;


import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.util.javafx.dialog.EditMode;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */


public class CallPointRow extends SkeletalNotationEditorTabController implements NotationEditorTabController {

    @Override
    public String getTabName() {
        return "Row Call Points";
    }

    @Override
    public void init(NotationEditorDialog parent, EditMode editMode) {
        super.init(parent, editMode);

    }

    @Override
    public void buildDialogDataFromNotation(Notation notation) {

    }

    @Override
    public void buildNotationFromDialogData(NotationBuilder notationBuilder) {

    }
}