package com.concurrentperformance.ringingmaster.fxui.desktop.property;

import com.concurrentperformance.fxutils.propertyeditor.*;
import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManagerListener;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class PropertySetupWindow extends PropertyEditor implements DocumentManagerListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

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
	public static final String TERMINATION_ROW_LIMIT_PROPERTY_NAME = "Row Limit";
	public static final String TERMINATION_LEAD_LIMIT_PROPERTY_NAME = "Lead Limit";
	public static final String TERMINATION_PART_LIMIT_PROPERTY_NAME = "Part Limit";
	public static final String TERMINATION_CIRCULAR_TOUCH_LIMIT_PROPERTY_NAME = "Circular Touch Limit";


	public PropertySetupWindow() {
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
		add( new TextPropertyValue(TITLE_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(TITLE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				DocumentManager.getInstance().getCurrentDocument().setTitle(newValue), CallbackStyle.EVERY_KEYSTROKE);

		add( new TextPropertyValue(AUTHOR_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(AUTHOR_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				DocumentManager.getInstance().getCurrentDocument().setAuthor(newValue), CallbackStyle.EVERY_KEYSTROKE);

		add( new SelectionPropertyValue(NUMBER_OF_BELLS_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
			final NumberOfBells numberOfBells = NumberOfBells.values()[newValue.intValue()];
			Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setNumberOfBells(numberOfBells));

		});
		final List<String> numberOfBellItems = new ArrayList<>();
		for (NumberOfBells bells : NumberOfBells.values()) {
			numberOfBellItems.add(bells.getDisplayString());
		}
		((SelectionPropertyValue)findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setItems(numberOfBellItems);

		add( new SelectionPropertyValue(CALL_FROM_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(CALL_FROM_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
			final Bell callFrom = Bell.values()[newValue.intValue()];
			Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setCallFrom(callFrom));

		});

		add( new SelectionPropertyValue(ACTIVE_METHOD_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setActiveNotation(newValue.intValue())));

		add( new SelectionPropertyValue(CALL_TYPE_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(CALL_TYPE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
			final TouchType touchType = TouchType.values()[newValue.intValue()];
			Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setTouchType(touchType));

		});

		add( new TextPropertyValue(PLAIN_LEAD_TOKEN_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setPlainLeadToken(newValue)),
				CallbackStyle.EVERY_KEYSTROKE);
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
		((TextPropertyValue)findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setStartChange(newValue)),
				CallbackStyle.WHEN_FINISHED);

		add(START_GROUP_NAME, new IntegerPropertyValue(START_AT_ROW_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setStartAtRow(newValue.intValue())), CallbackStyle.WHEN_FINISHED);

		add(START_GROUP_NAME, new SelectionPropertyValue(START_STROKE_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(START_STROKE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				Platform.runLater(() -> {
			final Stroke startStroke = Stroke.values()[newValue.intValue()];
			DocumentManager.getInstance().getCurrentDocument().setStartStroke(startStroke);
		}));

		add(START_GROUP_NAME, new TextPropertyValue(START_NOTATION_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(START_NOTATION_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setStartNotation(newValue)),
				CallbackStyle.WHEN_FINISHED);

		showGroupByName(START_GROUP_NAME, false); // TODO save state in app

	}

	private void updateStartSection(TouchDocument touchDocument) {
		final String startChange = touchDocument.getStartChange();
		((TextPropertyValue)findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setValue(startChange);

		Integer startAtRow = touchDocument.getStartAtRow();
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
		((TextPropertyValue)findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setTerminationChange(newValue)),
				CallbackStyle.WHEN_FINISHED);

		add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_ROW_LIMIT_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(TERMINATION_ROW_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setTerminationMaxRows(newValue == null?null:newValue.intValue())),
				CallbackStyle.WHEN_FINISHED);

		add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_LEAD_LIMIT_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(TERMINATION_LEAD_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
						Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setTerminationMaxLeads(newValue == null ? null : newValue.intValue())),
				CallbackStyle.WHEN_FINISHED);

		add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_PART_LIMIT_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(TERMINATION_PART_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
						Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setTerminationMaxParts(newValue == null ? null : newValue.intValue())),
				CallbackStyle.WHEN_FINISHED);

		add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_CIRCULAR_TOUCH_LIMIT_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(TERMINATION_CIRCULAR_TOUCH_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
						Platform.runLater(() -> DocumentManager.getInstance().getCurrentDocument().setTerminationCircularTouch(newValue == null ? null : newValue.intValue())),
				CallbackStyle.WHEN_FINISHED);

		showGroupByName(TERMINATION_GROUP_NAME, false); // TODO save state in app
	}

	private void updateTerminationSection(TouchDocument touchDocument) {
		final String terminationChange = touchDocument.getTerminationChange();
		((TextPropertyValue)findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setValue(terminationChange);

		int terminationRowLimit = touchDocument.getTerminationMaxRows();
		((IntegerPropertyValue)findPropertyByName(TERMINATION_ROW_LIMIT_PROPERTY_NAME)).setValue(terminationRowLimit);

		Integer terminationLeadLimit = touchDocument.getTerminationMaxLeads();
		((IntegerPropertyValue)findPropertyByName(TERMINATION_LEAD_LIMIT_PROPERTY_NAME)).setValue(terminationLeadLimit);

		Integer terminationPartLimit = touchDocument.getTerminationMaxParts();
		((IntegerPropertyValue)findPropertyByName(TERMINATION_PART_LIMIT_PROPERTY_NAME)).setValue(terminationPartLimit);

		Integer terminationCircularTouchLimit = touchDocument.getTerminationCircularTouch();
		((IntegerPropertyValue)findPropertyByName(TERMINATION_CIRCULAR_TOUCH_LIMIT_PROPERTY_NAME)).setValue(terminationCircularTouchLimit);

	}

}
