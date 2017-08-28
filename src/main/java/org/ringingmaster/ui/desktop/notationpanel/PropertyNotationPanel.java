package org.ringingmaster.ui.desktop.notationpanel;

import org.ringingmaster.util.javafx.namevaluepair.NameValuePairModel;
import org.ringingmaster.util.javafx.namevaluepair.NameValuePairTable;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.ui.desktop.documentmodel.TouchDocument;
import org.ringingmaster.ui.desktop.documentmodel.TouchDocumentTypeManager;
import org.ringingmaster.util.listener.ConcurrentListenable;
import org.ringingmaster.util.listener.Listenable;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
		touchDocumentTypeManager.addListener(this::updateToReflectDocument);

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
		Set<String> namesInDocument = allNotations.stream().map(this::getDisplayName).collect(Collectors.toSet());
		Set<String> namesInUI = getItems().stream().map(nameValuePairModel -> nameValuePairModel.getName().getText()).collect(Collectors.toSet());

		return Sets.symmetricDifference(namesInDocument, namesInUI).size() != 0;
	}

	private void updateMethodList(Optional<TouchDocument> touchDocument) {
		if (!touchDocument.isPresent()) {
			return;
		}

		List<NotationBody> allNotations = touchDocument.get().getSortedAllNotations();
		boolean spliced = touchDocument.get().isSpliced();

		for (NotationBody notation : allNotations) {
			String name = getDisplayName(notation);

			if (notation.getNumberOfWorkingBells().toInt() > touchDocument.get().getNumberOfBells().toInt()) {
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
