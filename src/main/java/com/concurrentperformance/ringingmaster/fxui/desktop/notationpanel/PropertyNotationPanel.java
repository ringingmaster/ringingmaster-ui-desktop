package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.fxutils.namevaluepair.NameValueColumnDescriptor;
import com.concurrentperformance.fxutils.namevaluepair.NameValuePairModel;
import com.concurrentperformance.fxutils.namevaluepair.NameValuePairTable;
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
public class PropertyNotationPanel extends NameValuePairTable implements Listenable<PropertyNotationPanelListener> {

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
			fireSelectionChange();

		});

		setOnMouseClicked(event -> {
			if(event.getClickCount() == 2) {
				EditNotationButton.doEditCurrentSelectedNotation();
			}
		});

		getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			fireSelectionChange();
		});
	}

	private void fireSelectionChange() {

		int selectedIndex = getSelectionModel().getSelectedIndex();
		Optional<NotationBody> selectedNotation = Optional.ofNullable(getNotation(selectedIndex));

		for (PropertyNotationPanelListener propertyNotationPanelListener : listenableDelegate.getListeners()) {
			propertyNotationPanelListener.propertyMethod_setSelectedNotation(selectedNotation);

		}
	}

	private void rebuildMethodList(TouchDocument touchDocument) {
		int selectedIndex = getSelectionModel().getSelectedIndex();
		getItems().clear();
		for (NotationBody notation : touchDocument.getSortedAllNotations()) {
			getItems().add(new NameValuePairModel(getDisplayName(notation)));
		}

		if (selectedIndex >= 0) {
			if (selectedIndex >= getItems().size()) {
				selectedIndex = getItems().size()-1;
			}
		}
		if (selectedIndex >= 0) {
			getSelectionModel().select(selectedIndex);
		}
	}

	private boolean hasMethodListChanged(TouchDocument touchDocument) {
		List<NotationBody> allNotations = touchDocument.getSortedAllNotations();
		for (NotationBody notation : allNotations) {
			String name = getDisplayName(notation);
			if (getItems().stream()
					.filter(columnDescriptor -> columnDescriptor.getName().getText().equals(name))
					.count() < 1 ) {
				return true;
			}
		}

		return (allNotations.size() != getItems().size());
	}

	private void updateMethodList(TouchDocument touchDocument) {
		List<NotationBody> allNotations = touchDocument.getSortedAllNotations();
		boolean spliced = touchDocument.isSpliced();

		for (NotationBody notation : allNotations) {
			String name = getDisplayName(notation);

			if (notation.getNumberOfWorkingBells().getBellCount() > touchDocument.getNumberOfBells().getBellCount()) {
				updateDisplayProperty(name, "Too many bells", true);
			}
			else if (spliced) {
				if (Strings.isNullOrEmpty(notation.getSpliceIdentifier())) {
					updateDisplayProperty(name, "No splice letter", true);

				} else {
					updateDisplayProperty(name, TouchDocument.SPLICED_TOKEN + " " + notation.getSpliceIdentifier(), false);
				}
			}
			else if (notation == touchDocument.getSingleMethodActiveNotation()) {
				updateDisplayProperty(name, "<Active>", false);
			}
			else {
				updateDisplayProperty(name,"", false);
			}
		}
	}

	private void updateDisplayProperty(String propertyName, String value, boolean disabled) {
		getItems().stream()
				.filter(columnDescriptor -> columnDescriptor.getName().getText().equals(propertyName))
				.forEach(pair -> {pair.setValue(new NameValueColumnDescriptor(value, null, disabled));
								  pair.setName(new NameValueColumnDescriptor(propertyName, null, disabled));
				});
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
