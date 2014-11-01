package com.concurrentperformance.ringingmaster.fxui.desktop.property;

import com.concurrentperformance.fxutils.propertyeditor.CallbackStyle;
import com.concurrentperformance.fxutils.propertyeditor.IntegerPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.PropertyEditor;
import com.concurrentperformance.fxutils.propertyeditor.SelectionPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.TextPropertyValue;
import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
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

	public static final String START_GROUP_NAME = "Start";
	public static final String START_WITH_CHANGE_PROPERTY_NAME = "Start Change";
	public static final String START_AT_ROW_PROPERTY_NAME = "Start At Row";
	public static final String START_STROKE_PROPERTY_NAME = "Start Stroke";
	public static final String START_NOTATION_PROPERTY_NAME = "Start Notation";

	public static final String TERMINATION_GROUP_NAME = "Termination";
	public static final String TERMINATION_WITH_CHANGE_PROPERTY_NAME = "Termination Change";


	public PropertyWindow() {
		DocumentManager.getInstance().addListener(this);

		buildSetupSection();
		buildAdvancedSetupSection();
		buildStartSection();
		buildTerminationSection();
	}

	@Override
	public void documentManager_updateDocument(TouchDocument touchDocument) {
		updateSetupSection(touchDocument);
		updateAdvancedSetupSection(touchDocument);
		updateStartSection(touchDocument);
		updateTerminationSection(touchDocument);
	}

	private void buildSetupSection() {
		add(SETUP_GROUP_NAME, new TextPropertyValue(TITLE_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(TITLE_PROPERTY_NAME)).setListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				DocumentManager.getInstance().getCurrentDocument().setTitle(newValue);
			}
		}, CallbackStyle.EVERY_KEYSTROKE);

		add(SETUP_GROUP_NAME, new TextPropertyValue(AUTHOR_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(AUTHOR_PROPERTY_NAME)).setListener( new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				DocumentManager.getInstance().getCurrentDocument().setAuthor(newValue);
			}
		}, CallbackStyle.EVERY_KEYSTROKE);

		add(SETUP_GROUP_NAME, new SelectionPropertyValue(NUMBER_OF_BELLS_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setListener(new ChangeListener<Number>() {
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
		});
		final List<String> numberOfBellItems = new ArrayList<>();
		for (NumberOfBells bells : NumberOfBells.values()) {
			numberOfBellItems.add(bells.getDisplayString());
		}
		((SelectionPropertyValue)findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setItems(numberOfBellItems);

		add(SETUP_GROUP_NAME, new SelectionPropertyValue(CALL_FROM_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(CALL_FROM_PROPERTY_NAME)).setListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue <? extends Number> observable, Number oldValue, Number newValue) {
				final Bell callFrom = Bell.values()[newValue.intValue()];
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setCallFrom(callFrom);
					}
				});

			}
		});

		add(SETUP_GROUP_NAME, new SelectionPropertyValue(ACTIVE_METHOD_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setListener( new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setActiveNotation(newValue.intValue());
					}
				});

			}
		});

		add(SETUP_GROUP_NAME, new SelectionPropertyValue(CALL_TYPE_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(CALL_TYPE_PROPERTY_NAME)).setListener(new ChangeListener<Number>() {
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
		});

		add(SETUP_GROUP_NAME, new TextPropertyValue(PLAIN_LEAD_TOKEN_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setListener( new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setPlainLeadToken(newValue);
					}
				});

			}
		}, CallbackStyle.EVERY_KEYSTROKE);
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
		findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME).setDisable(touchType == TouchType.COURSE_BASED);
	}

	private void buildAdvancedSetupSection() {
		// TODO
	}

	private void updateAdvancedSetupSection(TouchDocument touchDocument) {
		//TODO

	}



	private void buildStartSection() {
		add(START_GROUP_NAME, new TextPropertyValue(START_WITH_CHANGE_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setStartChange(newValue);
					}
				});
			}
		}, CallbackStyle.WHEN_FINISHED);

		add(START_GROUP_NAME, new IntegerPropertyValue(START_AT_ROW_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setListener(new ChangeListener<Number>() {


			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setStartAtRow(newValue.intValue());
					}
				});

			}
		}, CallbackStyle.WHEN_FINISHED);

		add(SETUP_GROUP_NAME, new SelectionPropertyValue(START_STROKE_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(START_STROKE_PROPERTY_NAME)).setListener( new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						final Stroke startStroke = Stroke.values()[newValue.intValue()];
						DocumentManager.getInstance().getCurrentDocument().setStartStroke(startStroke);
					}
				});

			}
		});

		add(SETUP_GROUP_NAME, new TextPropertyValue(START_NOTATION_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(START_NOTATION_PROPERTY_NAME)).setListener( new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setStartNotation(newValue);
					}
				});

			}
		}, CallbackStyle.WHEN_FINISHED);
	}

	private void updateStartSection(TouchDocument touchDocument) {
		final String startChange = touchDocument.getStartChange();
		((TextPropertyValue)findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setValue(startChange);

		int startAtRow = touchDocument.getStartAtRow();
		((IntegerPropertyValue)findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setValue(startAtRow);

		final List<String> startAtStrokeItems = new ArrayList<>();
		for (Stroke stroke : Stroke.values()) {
			startAtStrokeItems.add(stroke.getDisplayString());
		}
		((SelectionPropertyValue)findPropertyByName(START_STROKE_PROPERTY_NAME)).setItems(startAtStrokeItems);
		final Stroke startStroke = touchDocument.getStartStroke();
		((SelectionPropertyValue)findPropertyByName(START_STROKE_PROPERTY_NAME)).setSelectedIndex(startStroke.ordinal());

		final String startNotation = touchDocument.getStartNotation();
		((TextPropertyValue)findPropertyByName(START_NOTATION_PROPERTY_NAME)).setValue(startNotation);
	}

	private void buildTerminationSection() {
		add(TERMINATION_GROUP_NAME, new TextPropertyValue(TERMINATION_WITH_CHANGE_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						DocumentManager.getInstance().getCurrentDocument().setTerminationChange(newValue);
					}
				});
			}
		}, CallbackStyle.WHEN_FINISHED);

	}

	private void updateTerminationSection(TouchDocument touchDocument) {
		final String terminationChange = touchDocument.getTerminationChange();
		((TextPropertyValue)findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setValue(terminationChange);

	}

}
