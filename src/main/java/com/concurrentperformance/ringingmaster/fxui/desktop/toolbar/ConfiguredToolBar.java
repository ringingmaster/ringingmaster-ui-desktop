package com.concurrentperformance.ringingmaster.fxui.desktop.toolbar;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.NewDocumentButton;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.OpenDocumentButton;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.SaveDocumentButton;
import com.concurrentperformance.ringingmaster.fxui.desktop.edit.CopyButton;
import com.concurrentperformance.ringingmaster.fxui.desktop.edit.CutButton;
import com.concurrentperformance.ringingmaster.fxui.desktop.edit.PasteButton;
import com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel.AddNotationButton;
import com.concurrentperformance.ringingmaster.fxui.desktop.undo.RedoButton;
import com.concurrentperformance.ringingmaster.fxui.desktop.undo.UndoButton;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class ConfiguredToolBar extends ToolBar {

	public ConfiguredToolBar() {
		getItems().addAll(
				new NewDocumentButton(),
				new OpenDocumentButton(),
				new SaveDocumentButton(),
				new Separator(Orientation.VERTICAL),
				new UndoButton(),
				new RedoButton(),
				new Separator(Orientation.VERTICAL),
				new CutButton(),
				new CopyButton(),
				new PasteButton(),
				new Separator(Orientation.VERTICAL),
				new AddNotationButton());
	}
}
