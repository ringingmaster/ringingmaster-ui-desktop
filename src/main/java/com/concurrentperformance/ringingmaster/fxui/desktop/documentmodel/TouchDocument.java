package com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.Notation;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilderHelper;
import com.concurrentperformance.ringingmaster.engine.touch.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;
import com.concurrentperformance.ringingmaster.engine.touch.impl.TouchBuilder;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.definitiongrid.DefinitionGridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.maingrid.MainGridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.ringingmaster.ui.common.TouchStyle;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Provides the interface between the engine {@code Touch} and the various
 * UI components.
 *
 * @author Lake
 */
public class TouchDocument extends ConcurrentListenable<TouchDocumentListener> implements Listenable<TouchDocumentListener> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Touch touch;
	private final TouchStyle touchStyle = new TouchStyle();

	private final MainGridModel mainGridModel;
	private final List<GridModel> definitionModels = new ArrayList<>();


	public TouchDocument() {

		//TODO all this must be persisted.
		touch = createDummyTouch();
		parseAndProve();

		configureDefinitionModels();

		mainGridModel = new MainGridModel(this, touch);
	}

	public String getTitle() {
		return touch.getTitle();
	}

	public void setTitle(String newTitle) {
		touch.setTitle(newTitle);
		fireDocumentContentChanged();
	}

	public String getAuthor() {
		return touch.getAuthor();
	}

	public void setAuthor(String author) {
		touch.setAuthor(author);
		fireDocumentContentChanged();
	}

	public NumberOfBells getNumberOfBells() {
		return touch.getNumberOfBells();
	}

	public void setNumberOfBells(NumberOfBells numberOfBells) {
		if (touch.getNumberOfBells() != numberOfBells) {
			StringBuilder message = new StringBuilder();
			message.append("Changing number of bells from ").append(touch.getNumberOfBells().getDisplayString())
					.append(" to ").append(numberOfBells.getDisplayString())
					.append(" will modify other properties: ").append(System.lineSeparator());
			int originalLength = message.length();

			int pointNumber = 1;
			if (touch.getCallFromBell().getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
				message.append(pointNumber++).append(") Call from bell will change from ")
						.append(touch.getCallFromBell().getZeroBasedBell() + 1)
						.append(" to ").append(numberOfBells.getTenor().getZeroBasedBell() + 1).append(".").append(System.lineSeparator());
			}

			final MethodRow existingInitialRow = touch.getInitialRow();
			final MethodRow newInitialRow = MethodBuilder.transformToNewNumberOfBells(existingInitialRow, numberOfBells);

			message.append(pointNumber++).append(") Start row will change from '")
					.append(existingInitialRow.getDisplayString(true))
					.append("' to '")
					.append(newInitialRow.getDisplayString(true))
					.append("'.").append(System.lineSeparator());

			if (touch.getTerminationSpecificRow().isPresent()) {
				final MethodRow existingTerminationRow = touch.getTerminationSpecificRow().get();
				final MethodRow newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);

				message.append(pointNumber++).append(") Termination row will change from '")
						.append(existingTerminationRow.getDisplayString(true))
						.append("' to '")
						.append(newTerminationRow.getDisplayString(true))
						.append("'.").append(System.lineSeparator());
			}

			if (!touch.isSpliced() &&
					touch.getSingleMethodActiveNotation() != null &&
					touch.getSingleMethodActiveNotation().getNumberOfWorkingBells().getBellCount() > numberOfBells.getBellCount()) {
				final List<NotationBody> filteredNotations = NotationBuilderHelper.filterNotations(touch.getAllNotations(), numberOfBells);
				message.append(pointNumber++).append(") Active method '")
						.append(touch.getSingleMethodActiveNotation().getNameIncludingNumberOfBells())
						.append("' ");
				if (filteredNotations.size() == 0) {
					message.append("will be unset. There is no suitable replacement.");
				}
				else {
					message.append("will change to '")
							.append(filteredNotations.get(0).getNameIncludingNumberOfBells())
					.append("'");
				}
				message.append(System.lineSeparator());
			}

			boolean doAction = false;

			if (message.length() > originalLength) {
				message.append(System.lineSeparator()).append("Do you wish to continue?");
			}

			if (doAction) {
				touch.setNumberOfBells(numberOfBells);
				parseAndProve();
			}
		}
		fireDocumentContentChanged();
	}

	public Bell getCallFrom() {
		return touch.getCallFromBell();
	}

	public void setCallFrom(Bell callFrom) {
		touch.setCallFromBell(callFrom);
		parseAndProve();
		fireDocumentContentChanged();
	}

	public List<String> getNotations() {
		final List<NotationBody> orderedNotations = getSortedNotationsBeingDisplayed();

		List<String> result = Lists.newArrayList();

		result.add("<Spliced>");

		for (int index = 0;index < orderedNotations.size();index++) {
			final NotationBody notation = orderedNotations.get(index);
			result.add(notation.getNameIncludingNumberOfBells());
		}

		return result;
	}

	private List<NotationBody> getSortedNotationsBeingDisplayed() {
		final List<NotationBody> sortedNotations = Lists.newArrayList(touch.getValidNotations());
		Collections.sort(sortedNotations, Notation.BY_NUMBER_THEN_NAME);
		return sortedNotations;
	}


	public int getActiveNotationIndex() {
		if (touch.isSpliced()) {
			return 0;
		}
		final NotationBody activeNotation = touch.getSingleMethodActiveNotation();
		final List<NotationBody> sortedNotationsBeingDisplayed = getSortedNotationsBeingDisplayed();
		for (int index = 0;index<sortedNotationsBeingDisplayed.size();index++) {
			final NotationBody notation = sortedNotationsBeingDisplayed.get(index);
			if (notation == activeNotation) {
				return index +1; // the 1 is the offset for the spliced row
			}
		}
		return -1;
	}

	public void setActiveNotation(int index) {
		if (index==0) {
			touch.setSpliced(true);
		}
		else {
			final List<NotationBody> sortedNotationsBeingDisplayed = getSortedNotationsBeingDisplayed();

			final NotationBody selectedNotation = sortedNotationsBeingDisplayed.get(index -1);// the -1 is the offset for the spliced row
			touch.setActiveNotation(selectedNotation);
		}
		parseAndProve();
		fireDocumentContentChanged();
	}


	public TouchType getTouchType() {
		return touch.getTouchType();
	}

	public void setTouchType(TouchType touchType) {
		touch.setTouchType(touchType);
		parseAndProve();
		fireDocumentContentChanged();
	}

	public String getPlainLeadToken() {
		return touch.getPlainLeadToken();
	}

	public void setPlainLeadToken(String plainLeadToken) {
		touch.setPlainLeadToken(plainLeadToken);
		parseAndProve();
		fireDocumentContentChanged();
	}

	public String getInitialRow() {
		return touch.getInitialRow().getDisplayString(true);
	}

	public void setInitialRow(String initialRowText) {
		if (initialRowText == null) {
			return;
		}

		if (getInitialRow().equals(initialRowText)) {
			return;
		}

		// first look for rounds token
		if (initialRowText.compareToIgnoreCase(MethodRow.ROUNDS_TOKEN) == 0) {
			final MethodRow rounds = MethodBuilder.buildRoundsRow(touch.getNumberOfBells());
			touch.setInitialRow(rounds);
			parseAndProve();
		}
		// Now check for valid row
		else {
			try {
				final MethodRow parsedRow = MethodBuilder.parse(touch.getNumberOfBells(), initialRowText);
				touch.setInitialRow(parsedRow);
				parseAndProve();
			}
			catch (RuntimeException e) {
				StringBuilder msg = new StringBuilder();
				msg.append("Chang initial row to '")
						.append(initialRowText)
						.append("' has failed:")
						.append(System.lineSeparator());
				msg.append(e.getMessage())
						.append(System.lineSeparator());
				msg.append("The original initial row '")
						.append(touch.getInitialRow().getDisplayString(true))
						.append("' will be restored.");

				Alert dialog = new Alert(Alert.AlertType.ERROR, msg.toString(), ButtonType.OK);
				dialog.setTitle("Change initial row failed");
				dialog.setHeaderText("Changing the initial row to '" + initialRowText + "' has failed.");
				dialog.getDialogPane().setMinHeight(100);
				dialog.getDialogPane().setMinWidth(400);
				dialog.showAndWait().filter(response -> response == ButtonType.OK);
			}
		}

		fireDocumentContentChanged();
	}

	public GridModel getMainGridModel() {
		return mainGridModel;
	}

	private void configureDefinitionModels() {

		final Set<TouchDefinition> definitions = touch.getDefinitions();

		for (TouchDefinition definition : definitions) {
			definitionModels.add(new DefinitionGridModel(this, definition));
		}
	}

	public List<GridModel> getDefinitionGridModels() {
		return definitionModels;
	}

	private void fireDocumentContentChanged() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (TouchDocumentListener touchDocumentListener : getListeners()) {
					touchDocumentListener.touchDocumentListener_documentContentChanged(TouchDocument.this);
				}
			}
		});
	}

	public TouchStyle getTouchStyle() {
		return touchStyle;
	}

	public void parseAndProve() {
		ProofManager.getInstance().parseAndProve(touch);
	}

	//TODO remove this
	private static Touch createDummyTouch() {

		Touch touch = TouchBuilder.getInstance(NumberOfBells.BELLS_6, 2, 2);
		touch.setTouchType(TouchType.LEAD_BASED);
		touch.addNotation(buildPlainBobMinor());
		touch.addNotation(buildLittleBobMinor());
		touch.addNotation(buildPlainBobMinimus());
		touch.addNotation(buildPlainBobMajor());

		touch.setTitle("My Touch");
		touch.setAuthor("by Stephen");

		touch.insertCharacter(0, 0, 0, '-');
		touch.insertCharacter(0, 1, 0, 's');
		touch.insertCharacter(0, 0, 1, 's');
		touch.insertCharacter(0, 1, 1, '-');
		touch.insertCharacter(1, 0, 0, 'p');
		touch.insertCharacter(1, 0, 1, ' ');
		touch.insertCharacter(1, 0, 2, '3');
		touch.insertCharacter(1, 0, 3, '*');


		touch.addDefinition("3*", "-s-");
		touch.addDefinition("tr", "sps");

		touch.setTerminationSpecificRow(MethodBuilder.buildRoundsRow(touch.getNumberOfBells()));
		return touch;
	}

	// TODO remove this
	private static NotationBody buildPlainBobMinor() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Plain Bob")
				.setFoldedPalindromeNotationShorthand("-16-16-16", "12")
				.addCall("Bob", "-", "14", true)
				.addCall("Single", "s", "1234", false)
				.setSpliceIdentifier("P")
				.build();
	}

	// TODO remove this
	private static NotationBody buildLittleBobMinor() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Little Bob")
				.setFoldedPalindromeNotationShorthand("-16-14", "12")
				.addCall("Bob", "-", "14", true)
				.addCall("Single", "s", "1234", false)
				.build();
	}

	// TODO remove this
	private static NotationBody buildPlainBobMinimus() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_4)
				.setName("Little Bob")
				.setFoldedPalindromeNotationShorthand("-14-14", "12")
				.addCall("Bob", "-", "14", true)
				.addCall("Single", "s", "1234", false)
				.build();
	}

	// TODO remove this
	private static NotationBody buildPlainBobMajor() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setName("Little Bob")
				.setFoldedPalindromeNotationShorthand("-18-18-18-18", "12")
				.addCall("Bob", "-", "14", true)
				.addCall("Single", "s", "1234", false)
				.build();
	}

	public int getColumnCount() {
		return touch.getColumnCount();
	}

	public int getRowCount() {
		return touch.getRowCount();
	}

	public Grid<TouchCell> allCellsView() {
		return touch.allCellsView();
	}

	public void incrementColumnCount() {
		touch.incrementColumnCount();
	}

	public void incrementRowCount() {
		touch.incrementRowCount();
	}

	public void insertCharacter(int column, int row, int index, char character) {
		touch.insertCharacter(column, row, index, character);
	}

	public void collapseEmptyRowsAndColumns() {
		touch.collapseEmptyRowsAndColumns();
	}
}
