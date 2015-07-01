package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.fxutils.propertyeditor.LabelPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.PropertyEditor;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class PropertyNotationPanel extends PropertyEditor implements Listenable<PropertyNotationPanelListener> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private ConcurrentListenable<PropertyNotationPanelListener> listenableDelegate = new ConcurrentListenable<>();

	static PropertyNotationPanel INSTANCE = new PropertyNotationPanel();

	static PropertyNotationPanel getInstance() {
		return INSTANCE;
	}

	private PropertyNotationPanel() {
		DocumentManager.getInstance().addListener(touchDocument -> {
			if (hasMethodListChanged(touchDocument)) {
				rebuildMethodList(touchDocument);
			}
			updateMethodList(touchDocument);
			fireSelctionChange();

		});

		setOnDoubleClickListener(index -> EditNotationButton.doEditCurrentSelectedNotation());

		setVertSeparatorPosition(140.0);
		allowSelection(true);
		selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			log.info("Index set to " + newValue);
			fireSelctionChange();
		});
	}

	private void fireSelctionChange() {
		int selectedIndex = getSelectedIndex();
		Optional<NotationBody> selectedNotation = Optional.ofNullable(getNotation(selectedIndex));

		for (PropertyNotationPanelListener propertyNotationPanelListener : listenableDelegate.getListeners()) {
			propertyNotationPanelListener.propertyMethod_setSelectedNotation(selectedNotation);

		}
	}

	private void rebuildMethodList(TouchDocument touchDocument) {
		int selectedIndex = getSelectedIndex();
		clear();
		for (NotationBody notation : touchDocument.getSortedAllNotations()) {
			add(new LabelPropertyValue(getDisplayName(notation)));
		}

		if (selectedIndex >= 0) {
			if (selectedIndex >= sizeAll()) {
				selectedIndex = sizeAll()-1;
			}
		}
		if (selectedIndex >= 0) {
			setSelectedIndex(selectedIndex);
		}
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
				property.setDisable(true);
			}
			else if (spliced) {
				if (Strings.isNullOrEmpty(notation.getSpliceIdentifier())) {
					property.setValue("No splice letter");
					property.setDisable(true);

				} else {
					property.setValue(TouchDocument.SPLICED_TOKEN + " " + notation.getSpliceIdentifier());
					property.setDisable(false);
				}
			}
			else if (notation == touchDocument.getSingleMethodActiveNotation()) {
				property.setValue("<Active>");
				property.setDisable(false);
			}
			else {
				property.setValue("");
				property.setDisable(false);
			}
		}
	}

	private String getDisplayName(NotationBody notation) {
		return notation.getNameIncludingNumberOfBells();
	}

	@Override
	public void addListener(PropertyNotationPanelListener listener) {
		listenableDelegate.addListener(listener);
	}

	public NotationBody getNotation(int index) {
		List<NotationBody> sortedAllNotations = DocumentManager.getCurrentDocument().getSortedAllNotations();
		if (index >= 0 && index < sortedAllNotations.size()) {
			return sortedAllNotations.get(index);
		}

		return null;
	}
}
