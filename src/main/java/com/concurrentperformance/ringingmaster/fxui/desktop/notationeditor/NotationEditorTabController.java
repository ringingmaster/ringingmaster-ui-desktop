package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;

/**
 * TODO Comments
 *
 * @author Lake
 */
public interface NotationEditorTabController {

	String getTabName();

	void init(NotationBody notationBody, NotationEditorDialog notationEditorDialog, NotationEditorDialog.EditMode editMode);

	void build(NotationBuilder notationBuilder);
}
