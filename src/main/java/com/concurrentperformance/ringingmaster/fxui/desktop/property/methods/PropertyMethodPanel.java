package com.concurrentperformance.ringingmaster.fxui.desktop.property.methods;

import com.concurrentperformance.fxutils.propertyeditor.LabelPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.PropertyEditor;
import com.concurrentperformance.fxutils.propertyeditor.PropertyValue;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManagerListener;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.util.ColorManager;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class PropertyMethodPanel extends PropertyEditor implements DocumentManagerListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	static PropertyMethodPanel INSTANCE = new PropertyMethodPanel();

	static PropertyMethodPanel getInstance() {
		return INSTANCE;
	}

	private PropertyMethodPanel() {
		DocumentManager.getInstance().addListener(this);
		setVertSeparatorPosition(140.0);
		allowSelection(true);
	}

	@Override
	public void documentManager_updateDocument(TouchDocument touchDocument) {
		if (hasMethodListChanged(touchDocument)) {
			rebuildMethodList(touchDocument);
		}

		updateMethodList(touchDocument);
	}

	private void rebuildMethodList(TouchDocument touchDocument) {
		clear();
		for (NotationBody notation : touchDocument.getSortedAllNotations()) {
			add(new LabelPropertyValue(getDisplayName(notation)));
		}
	}

	public NotationBody getSelectedNotation() {

		PropertyValue selected = getSelected();
		if (selected != null) {
			String name = selected.getName();
			for (NotationBody possibleNotation : DocumentManager.getInstance().getCurrentDocument().getSortedAllNotations()) {
				String possibleName = getDisplayName(possibleNotation);
				if (name.equals(possibleName)) {
					return possibleNotation;
				}
			}
		}

		return null;
	}

	private boolean hasMethodListChanged(TouchDocument touchDocument) {
		List<NotationBody> allNotations = touchDocument.getSortedAllNotations();
		for (NotationBody notation : allNotations) {
			String name = getDisplayName(notation);
			if (findPropertyByName(name) == null) {
				return true;
			}
		}

		return (allNotations.size() != sizeAll());
	}

	private void updateMethodList(TouchDocument touchDocument) {
		List<NotationBody> allNotations = touchDocument.getSortedAllNotations();
		boolean spliced = touchDocument.isSpliced();

		for (NotationBody notation : allNotations) {
			String name = getDisplayName(notation);
			LabelPropertyValue property = (LabelPropertyValue)findPropertyByName(name);

			if (notation.getNumberOfWorkingBells().getBellCount() > touchDocument.getNumberOfBells().getBellCount()) {
				property.setValue("Too many bells");
				property.setColor(ColorManager.getWarnHighlight());
				property.setDisable(true);
			}
			else if (spliced) {
				if (Strings.isNullOrEmpty(notation.getSpliceIdentifier())) {
					property.setValue("No splice letter");
					property.setColor(ColorManager.getWarnHighlight());
					property.setDisable(true);

				} else {
					property.setValue(TouchDocument.SPLICED_TOKEN + " " + notation.getSpliceIdentifier());
					property.setColor(ColorManager.getClearHighlight());
					property.setDisable(false);
				}
			}
			else if (notation == touchDocument.getSingleMethodActiveNotation()) {
				property.setValue("<Active>");
				property.setColor(ColorManager.getClearHighlight());
				property.setDisable(false);
			}
			else {
				property.setValue("");
				property.setColor(ColorManager.getClearHighlight());
				property.setDisable(false);
			}
		}
	}

	private String getDisplayName(NotationBody notation) {
		return notation.getNameIncludingNumberOfBells();
	}
}
