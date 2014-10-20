package com.concurrentperformance.ringingmaster.fxui.desktop.property;

import com.concurrentperformance.fxutils.propertyeditor.PropertyEditor;
import com.concurrentperformance.fxutils.propertyeditor.SelectionPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.TextPropertyValue;
import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManagerListener;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class PropertyWindow extends PropertyEditor implements DocumentManagerListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String SETUP_GROUP_NAME = "Setup";
	public static final String TITLE_PROPERTY_NAME = "Title";
	public static final String AUTHOR_PROPERTY_NAME = "Author";
	public static final String NUMBER_OF_BELLS_PROPERTY_NAME = "Number Of Bells";
	public static final String CALL_FROM_PROPERTY_NAME = "Call From";
	public static final String ACTIVE_METHOD_PROPERTY_NAME = "Active Method";
	public static final String CALL_TYPE_PROPERTY_NAME = "Call Type";
	public static final String PLAIN_LEAD_TOKEN_PROPERTY_NAME = "Plain Lead Token";

	public static final String ADVANCED_SETUP_GROUP_NAME = "Advanced Setup";

	public PropertyWindow() {
		DocumentManager.getInstance().addListener(this);

		buildSetupSection();
		buildAdvancedSetupSection();
	}

	@Override
	public void documentManager_updateDocument(TouchDocument touchDocument) {
		updateSetupSection(touchDocument);
		updateAdvancedSetupSection(touchDocument);
	}

	private void buildSetupSection() {
		add(SETUP_GROUP_NAME, new TextPropertyValue(TITLE_PROPERTY_NAME, new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				DocumentManager.getInstance().getCurrentDocument().setTitle(newValue);
			}
		}));

		add(SETUP_GROUP_NAME, new TextPropertyValue(AUTHOR_PROPERTY_NAME, new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				DocumentManager.getInstance().getCurrentDocument().setAuthor(newValue);
			}
		}));

		add(SETUP_GROUP_NAME, new SelectionPropertyValue(NUMBER_OF_BELLS_PROPERTY_NAME, new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				final NumberOfBells numberOfBells = NumberOfBells.values()[newValue.intValue()];
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setNumberOfBells(numberOfBells);
					}
				});

			}
		}));
		final List<String> numberOfBellItems = new ArrayList<>();
		for (NumberOfBells bells : NumberOfBells.values()) {
			numberOfBellItems.add(bells.getDisplayString());
		}
		((SelectionPropertyValue)findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setItems(numberOfBellItems);

		add(SETUP_GROUP_NAME, new SelectionPropertyValue(CALL_FROM_PROPERTY_NAME, new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				final Bell callFrom = Bell.values()[newValue.intValue()];
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setCallFrom(callFrom);
					}
				});

			}
		}));

		add(SETUP_GROUP_NAME, new SelectionPropertyValue(ACTIVE_METHOD_PROPERTY_NAME, new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setActiveNotation(newValue.intValue());
					}
				});

			}
		}));

		add(SETUP_GROUP_NAME, new SelectionPropertyValue(CALL_TYPE_PROPERTY_NAME, new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				final TouchType touchType = TouchType.values()[newValue.intValue()];
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setTouchType(touchType);
					}
				});

			}
		}));

		add(SETUP_GROUP_NAME, new TextPropertyValue(PLAIN_LEAD_TOKEN_PROPERTY_NAME, new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setPlainLeadToken(newValue);
					}
				});

			}
		}));
	}

	private void updateSetupSection(TouchDocument touchDocument) {
		final String title = touchDocument.getTitle();
		((TextPropertyValue)findPropertyByName(TITLE_PROPERTY_NAME)).setValue(title);

		final String author = touchDocument.getAuthor();
		((TextPropertyValue)findPropertyByName(AUTHOR_PROPERTY_NAME)).setValue(author);

		final NumberOfBells numberOfBells = touchDocument.getNumberOfBells();
		((SelectionPropertyValue)findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setSelectedIndex(numberOfBells.ordinal());

		final List<String> callFromItems = new ArrayList<>();
		for (Bell bell : Bell.values()) {
			if (bell.getZeroBasedBell() <= numberOfBells.getTenor().getZeroBasedBell()) {
				callFromItems.add(bell.getDisplayString());
			}
		}
		((SelectionPropertyValue)findPropertyByName(CALL_FROM_PROPERTY_NAME)).setItems(callFromItems);
		final Bell callFrom = touchDocument.getCallFrom();
		((SelectionPropertyValue)findPropertyByName(CALL_FROM_PROPERTY_NAME)).setSelectedIndex(callFrom.ordinal());

		final List<String> notationItems = touchDocument.getNotations();
		int selectedNotationIndex = touchDocument.getActiveNotationIndex();

		((SelectionPropertyValue)findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setItems(notationItems);
		((SelectionPropertyValue)findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setSelectedIndex(selectedNotationIndex);

		final List<String> touchTypes = new ArrayList<>();
		for (TouchType touchType : TouchType.values()) {
			touchTypes.add(touchType.getName());
		}
		((SelectionPropertyValue)findPropertyByName(CALL_TYPE_PROPERTY_NAME)).setItems(touchTypes);
		final TouchType touchType = touchDocument.getTouchType();
		((SelectionPropertyValue)findPropertyByName(CALL_TYPE_PROPERTY_NAME)).setSelectedIndex(touchType.ordinal());

		final String plainLeadToken = touchDocument.getPlainLeadToken();
		((TextPropertyValue)findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setValue(plainLeadToken);
	}



	private void buildAdvancedSetupSection() {
//		add(ADVANCED_SETUP_GROUP_NAME, new TextPropertyValue("Wrap Calls", cl));
//		final ObservableList<String> strings = FXCollections.observableArrayList(
//				"Option 1",
//				"Option 2",
//				"Option 3"
//		);
//
//		strings.addListener(new ListChangeListener<String>() {
//			@Override
//			public void onChanged(Change<? extends String> c) {
//				log.info(c.toString());
//			}
//		});
//	//	add(ADVANCED_SETUP_GROUP_NAME, new SelectionPropertyValue("Checking Type", strings));
	}

	private void updateAdvancedSetupSection(TouchDocument touchDocument) {


	}


}
