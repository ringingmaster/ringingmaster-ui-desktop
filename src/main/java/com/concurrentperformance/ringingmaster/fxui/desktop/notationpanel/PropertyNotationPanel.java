package com.concurrentperformance.ringingmaster.fxui.desktop.notationpanel;

import com.concurrentperformance.fxutils.namevaluepair.NameValuePairModel;
import com.concurrentperformance.fxutils.namevaluepair.NameValuePairTable;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocumentTypeManager;
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

	private TouchDocumentTypeManager touchDocumentTypeManager;
	private EditNotationEvent editNotationEvent;

	public PropertyNotationPanel() {
		//TODO add an empty table hint i.e. setPlaceholder(new Label("No Calls Defined"));
	}

	public void setTouchDocumentTypeManager(TouchDocumentTypeManager touchDocumentTypeManager) {
		this.touchDocumentTypeManager = touchDocumentTypeManager;
		touchDocumentTypeManager.addListener(document -> updateToReflectDocument(document));

		setOnMouseClicked(event -> {
			if(event.getClickCount() == 2) {
				editNotationEvent.handle(null);
			}
		});

		getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			fireSelectionChange();
		});
	}

	private void updateToReflectDocument(Optional<TouchDocument> touchDocument) {
		if (hasMethodListChanged(touchDocument)) {
			rebuildMethodList(touchDocument);
		}
		updateMethodList(touchDocument);
		fireSelectionChange();
	}

	private void fireSelectionChange() {

		int selectedIndex = getSelectionModel().getSelectedIndex();
		Optional<NotationBody> selectedNotation = Optional.ofNullable(getNotation(selectedIndex));

		for (PropertyNotationPanelListener propertyNotationPanelListener : listenableDelegate.getListeners()) {
			propertyNotationPanelListener.propertyMethod_setSelectedNotation(selectedNotation);

		}
	}

	private void rebuildMethodList(Optional<TouchDocument> touchDocument) {
		int selectedIndex = getSelectionModel().getSelectedIndex();
		getItems().clear();
		if (!touchDocument.isPresent()) {
			return;
		}

		for (NotationBody notation : touchDocument.get().getSortedAllNotations()) {
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

	private boolean hasMethodListChanged(Optional<TouchDocument> touchDocument) {
		if (!touchDocument.isPresent()) {
			return (getItems().size() != 0);
		}
		List<NotationBody> allNotations = touchDocument.get().getSortedAllNotations();
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

	private void updateMethodList(Optional<TouchDocument> touchDocument) {
		if (!touchDocument.isPresent()) {
			return;
		}

		List<NotationBody> allNotations = touchDocument.get().getSortedAllNotations();
		boolean spliced = touchDocument.get().isSpliced();

		for (NotationBody notation : allNotations) {
			String name = getDisplayName(notation);

			if (notation.getNumberOfWorkingBells().getBellCount() > touchDocument.get().getNumberOfBells().getBellCount()) {
				updateDisplayProperty(name, "Too many bells", true);
			}
			else if (spliced) {
				if (Strings.isNullOrEmpty(notation.getSpliceIdentifier())) {
					updateDisplayProperty(name, "No splice letter", true);

				} else {
					updateDisplayProperty(name, TouchDocument.SPLICED_TOKEN + " " + notation.getSpliceIdentifier(), false);
				}
			}
			else if (notation == touchDocument.get().getSingleMethodActiveNotation()) {
				updateDisplayProperty(name, "<Active>", false);
			}
			else {
				updateDisplayProperty(name,"", false);
			}
		}
	}

	private String getDisplayName(NotationBody notation) {
		return notation.getNameIncludingNumberOfBells();
	}


	public NotationBody getNotation(int index) {
		if (!touchDocumentTypeManager.getCurrentDocument().isPresent()) {
			return null;
		}

		List<NotationBody> sortedAllNotations = touchDocumentTypeManager.getCurrentDocument().get().getSortedAllNotations();
		if (index >= 0 && index < sortedAllNotations.size()) {
			return sortedAllNotations.get(index);
		}

		return null;
	}

	@Override
	public void addListener(PropertyNotationPanelListener listener) {
		listenableDelegate.addListener(listener);
	}

	public void setEditNotationEvent(EditNotationEvent editNotationEvent) {
		this.editNotationEvent = editNotationEvent;
	}
}
