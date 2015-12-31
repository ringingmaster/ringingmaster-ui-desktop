package com.concurrentperformance.ringingmaster.ui.desktop.setuppanel;

import com.concurrentperformance.fxutils.propertyeditor.CallbackStyle;
import com.concurrentperformance.fxutils.propertyeditor.IntegerPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.PropertyEditor;
import com.concurrentperformance.fxutils.propertyeditor.SelectionPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.TextPropertyValue;
import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCheckingType;
import com.concurrentperformance.ringingmaster.ui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.ui.desktop.documentmodel.TouchDocumentTypeManager;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class PropertySetupWindow extends PropertyEditor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String TITLE_PROPERTY_NAME = "Title";
	public static final String AUTHOR_PROPERTY_NAME = "Author";
	public static final String NUMBER_OF_BELLS_PROPERTY_NAME = "Number Of Bells";
	public static final String CALL_FROM_PROPERTY_NAME = "Call From";
	public static final String ACTIVE_METHOD_PROPERTY_NAME = "Active Method";
	public static final String CHECKING_TYPE_PROPERTY_NAME = "Checking Type";
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

	private TouchDocumentTypeManager touchDocumentTypeManager;


	public void init() {

		buildSetupSection();
		buildAdvancedSetupSection();
		buildStartSection();
		buildTerminationSection();


		touchDocumentTypeManager.addListener(touchDocument -> {
			updateSetupSection(touchDocument);
			updateAdvancedSetupSection(touchDocument);
			updateStartSection(touchDocument);
			updateTerminationSection(touchDocument);
		});
	}

	private void buildSetupSection() {
		add( new TextPropertyValue(TITLE_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(TITLE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setTitle(newValue)), CallbackStyle.EVERY_KEYSTROKE);

		add( new TextPropertyValue(AUTHOR_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(AUTHOR_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setAuthor(newValue)), CallbackStyle.EVERY_KEYSTROKE);

		add( new SelectionPropertyValue(NUMBER_OF_BELLS_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() != -1) {
				final NumberOfBells numberOfBells = NumberOfBells.values()[newValue.intValue()];
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setNumberOfBells(numberOfBells));
			}
		});
		final List<String> numberOfBellItems = new ArrayList<>();
		for (NumberOfBells bells : NumberOfBells.values()) {
			numberOfBellItems.add(bells.getDisplayString());
		}
		((SelectionPropertyValue)findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setItems(numberOfBellItems);

		add( new SelectionPropertyValue(CALL_FROM_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(CALL_FROM_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() != -1) {
				final Bell callFrom = Bell.values()[newValue.intValue()];
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setCallFrom(callFrom));
			}
		});

		add( new SelectionPropertyValue(ACTIVE_METHOD_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> {
					int index = newValue.intValue();
					if (index == 0) {
						touchDocument.setSpliced(true);
					} else {
						final List<NotationBody> sortedNotationsBeingDisplayed = touchDocument.getSortedValidNotations();

						final NotationBody selectedNotation = sortedNotationsBeingDisplayed.get(index - 1);// the -1 is the offset for the spliced row
						touchDocument.setSingleMethodActiveNotation(selectedNotation);
					}
				}));

		add( new SelectionPropertyValue(CHECKING_TYPE_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(CHECKING_TYPE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() != -1) {
				final TouchCheckingType touchCheckingType = TouchCheckingType.values()[newValue.intValue()];
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setTouchCheckingType(touchCheckingType));
			}
		});

		add( new TextPropertyValue(PLAIN_LEAD_TOKEN_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setPlainLeadToken(newValue)), CallbackStyle.EVERY_KEYSTROKE);
	}

	private void updateSetupSection(Optional<TouchDocument> touchDocument) {

		final String title = touchDocument.isPresent()? touchDocument.get().getTitle():"";
		((TextPropertyValue)findPropertyByName(TITLE_PROPERTY_NAME)).setValue(title);

		final String author = touchDocument.isPresent()? touchDocument.get().getAuthor():"";
		((TextPropertyValue)findPropertyByName(AUTHOR_PROPERTY_NAME)).setValue(author);

		final int numberOfBellsIndex = touchDocument.isPresent()? touchDocument.get().getNumberOfBells().ordinal():-1;
		((SelectionPropertyValue)findPropertyByName(NUMBER_OF_BELLS_PROPERTY_NAME)).setSelectedIndex(numberOfBellsIndex);

		final List<String> callFromItems = new ArrayList<>();
		if (touchDocument.isPresent()) {
			NumberOfBells numberOfBells = touchDocument.get().getNumberOfBells();
			for (Bell bell : Bell.values()) {
				if (bell.getZeroBasedBell() <= numberOfBells.getTenor().getZeroBasedBell()) {
					callFromItems.add(bell.getDisplayString());
				}
			}
			((SelectionPropertyValue) findPropertyByName(CALL_FROM_PROPERTY_NAME)).setItems(callFromItems);
		}
		final int callFromIndex = touchDocument.isPresent()? touchDocument.get().getCallFrom().ordinal():-1;
		((SelectionPropertyValue)findPropertyByName(CALL_FROM_PROPERTY_NAME)).setSelectedIndex(callFromIndex);

		final List<String> validNotationItems = getValidNotations(touchDocument);
		int selectedNotationIndex = getActiveValidNotationIndex(touchDocument);
		((SelectionPropertyValue)findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setItems(validNotationItems);
		((SelectionPropertyValue)findPropertyByName(ACTIVE_METHOD_PROPERTY_NAME)).setSelectedIndex(selectedNotationIndex);

		final List<String> touchTypes = new ArrayList<>();
		for (TouchCheckingType touchCheckingType : TouchCheckingType.values()) {
			touchTypes.add(touchCheckingType.getName());
		}
		((SelectionPropertyValue)findPropertyByName(CHECKING_TYPE_PROPERTY_NAME)).setItems(touchTypes);
		final int touchTypeIndex = touchDocument.isPresent()? touchDocument.get().getTouchCheckingType().ordinal():-1;
		((SelectionPropertyValue)findPropertyByName(CHECKING_TYPE_PROPERTY_NAME)).setSelectedIndex(touchTypeIndex);

		final String plainLeadToken = touchDocument.isPresent()? touchDocument.get().getPlainLeadToken():"";
		((TextPropertyValue)findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME)).setValue(plainLeadToken);
		findPropertyByName(PLAIN_LEAD_TOKEN_PROPERTY_NAME).setDisable(touchDocument.isPresent() &&
																		touchDocument.get().getTouchCheckingType() == TouchCheckingType.COURSE_BASED);
	}

	public List<String> getValidNotations(Optional<TouchDocument> touchDocument) {
		List<String> result = Lists.newArrayList();

		if (touchDocument.isPresent()) {
			final List<NotationBody> orderedNotations = touchDocument.get().getSortedValidNotations();

			result.add(TouchDocument.SPLICED_TOKEN);

			for (int index = 0; index < orderedNotations.size(); index++) {
				final NotationBody notation = orderedNotations.get(index);
				result.add(notation.getNameIncludingNumberOfBells());
			}
		}

		return result;
	}

	private int getActiveValidNotationIndex(Optional<TouchDocument> touchDocument) {

		if (!touchDocument.isPresent()) {
			return -1;
		}

		if (touchDocument.get().isSpliced()) {
			return 0;
		}
		final NotationBody activeNotation = touchDocument.get().getSingleMethodActiveNotation();
		final List<NotationBody> sortedNotationsBeingDisplayed = touchDocument.get().getSortedValidNotations();
		for (int index = 0;index<sortedNotationsBeingDisplayed.size();index++) {
			final NotationBody notation = sortedNotationsBeingDisplayed.get(index);
			if (notation == activeNotation) {
				return index +1; // the 1 is the offset for the spliced row
			}
		}
		return -1;
	}

	private void buildAdvancedSetupSection() {
		// TODO
	}

	private void updateAdvancedSetupSection(Optional<TouchDocument> touchDocument) {
		//TODO

	}

	private void buildStartSection() {
		add(START_GROUP_NAME, new TextPropertyValue(START_WITH_CHANGE_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setStartChange(newValue)), CallbackStyle.WHEN_FINISHED);

		add(START_GROUP_NAME, new IntegerPropertyValue(START_AT_ROW_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setStartAtRow(newValue.intValue())), CallbackStyle.WHEN_FINISHED);

		add(START_GROUP_NAME, new SelectionPropertyValue(START_STROKE_PROPERTY_NAME));
		((SelectionPropertyValue)findPropertyByName(START_STROKE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() != -1) {
				final Stroke startStroke = Stroke.values()[newValue.intValue()];
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setStartStroke(startStroke));
			}
		});

		add(START_GROUP_NAME, new TextPropertyValue(START_NOTATION_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(START_NOTATION_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
			updateTouchDocumentIfPresent(touchDocument -> touchDocument.setStartNotation(newValue)), CallbackStyle.WHEN_FINISHED);

		showGroupByName(START_GROUP_NAME, false); // TODO save state in app

	}

	private void updateStartSection(Optional<TouchDocument> touchDocument) {
		final String startChange = touchDocument.isPresent()? touchDocument.get().getStartChange():"";
		((TextPropertyValue)findPropertyByName(START_WITH_CHANGE_PROPERTY_NAME)).setValue(startChange);

		int startAtRow = touchDocument.isPresent()? touchDocument.get().getStartAtRow():0;
		((IntegerPropertyValue)findPropertyByName(START_AT_ROW_PROPERTY_NAME)).setValue(startAtRow);

		final List<String> startAtStrokeItems = new ArrayList<>();
		for (Stroke stroke : Stroke.values()) {
			startAtStrokeItems.add(stroke.getDisplayString());
		}
		((SelectionPropertyValue)findPropertyByName(START_STROKE_PROPERTY_NAME)).setItems(startAtStrokeItems);
		final int startStroke = touchDocument.isPresent()? touchDocument.get().getStartStroke().ordinal():-1;
		((SelectionPropertyValue)findPropertyByName(START_STROKE_PROPERTY_NAME)).setSelectedIndex(startStroke);

		final String startNotation = touchDocument.isPresent()? touchDocument.get().getStartNotation():"";
		((TextPropertyValue)findPropertyByName(START_NOTATION_PROPERTY_NAME)).setValue(startNotation);
	}

	private void buildTerminationSection() {
		add(TERMINATION_GROUP_NAME, new TextPropertyValue(TERMINATION_WITH_CHANGE_PROPERTY_NAME));
		((TextPropertyValue)findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setTerminationChange(newValue)),CallbackStyle.WHEN_FINISHED);

		add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_ROW_LIMIT_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(TERMINATION_ROW_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setTerminationMaxRows(newValue == null ? 0 : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

		add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_LEAD_LIMIT_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(TERMINATION_LEAD_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setTerminationMaxLeads(newValue == null ? null : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

		add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_PART_LIMIT_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(TERMINATION_PART_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setTerminationMaxParts(newValue == null ? null : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

		add(TERMINATION_GROUP_NAME, new IntegerPropertyValue(TERMINATION_CIRCULAR_TOUCH_LIMIT_PROPERTY_NAME));
		((IntegerPropertyValue)findPropertyByName(TERMINATION_CIRCULAR_TOUCH_LIMIT_PROPERTY_NAME)).setListener((observable, oldValue, newValue) ->
				updateTouchDocumentIfPresent(touchDocument -> touchDocument.setTerminationCircularTouch(newValue == null ? null : newValue.intValue())), CallbackStyle.WHEN_FINISHED);

		showGroupByName(TERMINATION_GROUP_NAME, false); // TODO save state in app
	}

	private void updateTerminationSection(Optional<TouchDocument> touchDocument) {
		final String terminationChange = touchDocument.isPresent()? touchDocument.get().getTerminationChange():"";
		((TextPropertyValue)findPropertyByName(TERMINATION_WITH_CHANGE_PROPERTY_NAME)).setValue(terminationChange);

		int terminationRowLimit = touchDocument.isPresent()? touchDocument.get().getTerminationMaxRows():0;
		((IntegerPropertyValue)findPropertyByName(TERMINATION_ROW_LIMIT_PROPERTY_NAME)).setValue(terminationRowLimit);

		Integer terminationLeadLimit = touchDocument.isPresent()? touchDocument.get().getTerminationMaxLeads():null;
		((IntegerPropertyValue)findPropertyByName(TERMINATION_LEAD_LIMIT_PROPERTY_NAME)).setValue(terminationLeadLimit);

		Integer terminationPartLimit = touchDocument.isPresent()? touchDocument.get().getTerminationMaxParts():null;
		((IntegerPropertyValue)findPropertyByName(TERMINATION_PART_LIMIT_PROPERTY_NAME)).setValue(terminationPartLimit);

		Integer terminationCircularTouchLimit = touchDocument.isPresent()? touchDocument.get().getTerminationCircularTouch():null;
		((IntegerPropertyValue)findPropertyByName(TERMINATION_CIRCULAR_TOUCH_LIMIT_PROPERTY_NAME)).setValue(terminationCircularTouchLimit);

	}

	void updateTouchDocumentIfPresent(Consumer<TouchDocument> consumer) {
		Optional<TouchDocument> currentDocument = touchDocumentTypeManager.getCurrentDocument();
		if (currentDocument.isPresent()) {
			// The runLater is to prevent the UI from continuously applying the same wrong update when loosing focus
			// and switching focus to an error window.
			Platform.runLater(() -> consumer.accept(currentDocument.get()));
		}
	}

	public void setTouchDocumentTypeManager(TouchDocumentTypeManager touchDocumentTypeManager) {
		this.touchDocumentTypeManager = touchDocumentTypeManager;
	}
}
