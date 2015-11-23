package com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.Notation;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilderHelper;
import com.concurrentperformance.ringingmaster.engine.touch.container.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch.Mutated;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCheckingType;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.container.impl.ImmutableTouch;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DefaultDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.Document;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.definitiongrid.DefinitionGridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.maingrid.MainGridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.DefinitionPane;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.TitlePane;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas.GridPane;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.ringingmaster.ui.common.TouchStyle;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.concurrentperformance.ringingmaster.engine.touch.container.Touch.Mutated.*;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides the interface between the engine {@code Touch} and the various
 * UI components.
 *
 * @author Lake
 */
public class TouchDocument extends ScrollPane implements Listenable<TouchDocumentListener>, Document {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String SPLICED_TOKEN = "<Spliced>";

	//Raw Data
	private Touch touch;
	private final TouchStyle touchStyle = new TouchStyle();
	private Document documentDelegate = new DefaultDocument();

	private MainGridModel mainGridModel;
	private final List<GridModel> definitionModels = new ArrayList<>();

	private ProofManager proofManager;

	// UI components
	private final TitlePane titlePane = new TitlePane(this);
	private final GridPane gridPane = new GridPane("Main");
	private final DefinitionPane definitionPane = new DefinitionPane();

	private final ConcurrentListenable<TouchDocumentListener> listenerDelegate = new ConcurrentListenable<>();

	public TouchDocument() {
		titlePane.setTouchStyle(touchStyle);
	}

	public void init(Touch touch) {
		layoutNodes();

		this.touch = touch;

		parseAndProve();

		configureDefinitionModels();

		mainGridModel = new MainGridModel(this, touch);

		//TODO is this lot updated?
		gridPane.setModel(mainGridModel);
		definitionPane.setModels(definitionModels);
	}

	private void layoutNodes() {
		VBox verticalLayout = new VBox(titlePane, gridPane, definitionPane);
		verticalLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		verticalLayout.setSpacing(20.0);

		setContent(verticalLayout);
		setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		setFitToHeight(true);
		setFitToWidth(true);
		setFocusTraversable(false);
	}

	public Touch getTouch() {
		return new ImmutableTouch(touch);
	}

	private void updateUiComponents() {
		titlePane.setText(getTitle(), getAuthor());
	}

	public String getTitle() {
		return touch.getTitle();
	}

	public void setTitle(String title) {
		checkNotNull(title);

		Mutated mutated = UNCHANGED;
		if (!title.equals(touch.getTitle())) {
			mutated = touch.setTitle(title);
		}
		setUpdatePoint(() -> "Set Title", mutated);
	}

	public String getAuthor() {
		return touch.getAuthor();
	}

	public void setAuthor(String author) {
		checkNotNull(author);

		Mutated modified = UNCHANGED;
		if (!author.equals(touch.getAuthor())) {
			modified = touch.setAuthor(author);
		}
		setUpdatePoint(() -> "Set Author", modified);
	}

	public NumberOfBells getNumberOfBells() {
		return touch.getNumberOfBells();
	}

	public void setNumberOfBells(NumberOfBells numberOfBells) {
		checkNotNull(numberOfBells);

		Mutated mutated = UNCHANGED;
		if (touch.getNumberOfBells() != numberOfBells) {
			StringBuilder message = new StringBuilder();

			int pointNumber = 1;
			if (touch.getCallFromBell().getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
				message.append(pointNumber++).append(") Call from bell will change from ")
						.append(touch.getCallFromBell().getZeroBasedBell() + 1)
						.append(" to ").append(numberOfBells.getTenor().getZeroBasedBell() + 1).append(".").append(System.lineSeparator());
			}

			final MethodRow existingStartChange = touch.getStartChange();
			final MethodRow newInitialRow = MethodBuilder.transformToNewNumberOfBells(existingStartChange, numberOfBells);

			message.append(pointNumber++).append(") Start change will change from '")
					.append(existingStartChange.getDisplayString(false))
					.append("' to '")
					.append(newInitialRow.getDisplayString(false))
					.append("'.").append(System.lineSeparator());

			if (touch.getTerminationChange().isPresent()) {
				final MethodRow existingTerminationRow = touch.getTerminationChange().get();
				final MethodRow newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);

				message.append(pointNumber++).append(") Termination row will change from '")
						.append(existingTerminationRow.getDisplayString(false))
						.append("' to '")
						.append(newTerminationRow.getDisplayString(false))
						.append("'.").append(System.lineSeparator());
			}

			if (!touch.isSpliced() &&
					touch.getNonSplicedActiveNotation() != null &&
					touch.getNonSplicedActiveNotation().getNumberOfWorkingBells().getBellCount() > numberOfBells.getBellCount()) {
				final List<NotationBody> filteredNotations = NotationBuilderHelper.filterNotations(touch.getAllNotations(), numberOfBells);
				message.append(pointNumber++).append(") Active method '")
						.append(touch.getNonSplicedActiveNotation().getNameIncludingNumberOfBells())
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

			if (touch.getStartNotation().isPresent()) {
				final String notation = touch.getStartNotation().get().getNotationDisplayString(false);
				NotationBody builtNotation = NotationBuilder.getInstance()
						.setNumberOfWorkingBells(numberOfBells)
						.setUnfoldedNotationShorthand(notation)
						.build();
				String newNotation = builtNotation.getNotationDisplayString(false);
				if (!newNotation.equals(notation)) {

					message.append(pointNumber++).append(") Start notation '")
							.append(notation)
							.append("' ")
					.append("will change to '")
					.append(newNotation)
					.append("'");
				}
			}

			boolean doAction = false;

			if (message.length() > 0) {
				message.append(System.lineSeparator()).append("Do you wish to continue?");

				StringBuilder preamble = new StringBuilder();
				preamble.append("Changing number of bells from ").append(touch.getNumberOfBells().getDisplayString())
						.append(" to ").append(numberOfBells.getDisplayString())
						.append(" will modify other properties: ").append(System.lineSeparator());

				Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, preamble.toString() + message.toString(), ButtonType.OK, ButtonType.CANCEL);
				dialog.setTitle("Change number of bells");
				dialog.setHeaderText("Change number of bells");
				dialog.getDialogPane().setMinHeight(280);
				dialog.getDialogPane().setMinWidth(620);
				final java.util.Optional result = dialog.showAndWait().filter(response -> response == ButtonType.OK);
				if (result.isPresent()) {
					doAction = true;
				}
			}

			if (doAction) {
				mutated = touch.setNumberOfBells(numberOfBells);
			}
		}
		setUpdatePoint(()->String.format("Set number of bells to: %s", numberOfBells.getDisplayString()), mutated);
	}

	public Bell getCallFrom() {
		return touch.getCallFromBell();
	}

	public void setCallFrom(Bell callFrom) {
		checkNotNull(callFrom);

		Mutated mutated = UNCHANGED;
		if (touch.getCallFromBell() != callFrom) {
			mutated = touch.setCallFromBell(callFrom);
		}
		setUpdatePoint(() -> String.format("Set number of bells to: %s", callFrom.getDisplayString()), mutated);
	}

	public List<NotationBody> getSortedAllNotations() {
		final List<NotationBody> sortedNotations = Lists.newArrayList(touch.getAllNotations());
		Collections.sort(sortedNotations, Notation.BY_NUMBER_THEN_NAME);
		return sortedNotations;
	}

	public List<NotationBody> getSortedValidNotations() {
		final List<NotationBody> sortedNotations = Lists.newArrayList(touch.getValidNotations());
		Collections.sort(sortedNotations, Notation.BY_NUMBER_THEN_NAME);
		return sortedNotations;
	}

	public Mutated addNotation(NotationBody notationToAdd) {
		checkNotNull(notationToAdd);

		Mutated mutated = UNCHANGED;
		List<String> messages = touch.checkAddNotation(notationToAdd);

		if (messages.size() == 0) {
			mutated = touch.addNotation(notationToAdd);
		}
		else {
			String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
			Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
			dialog.setTitle("Can't add method");
			dialog.setHeaderText("Can't add '" + notationToAdd.getNameIncludingNumberOfBells() + "'");
			dialog.getDialogPane().setMinHeight(280);
			dialog.getDialogPane().setMinWidth(560);
			dialog.showAndWait();
		}

		setUpdatePoint(() -> String.format("Add method: %s", notationToAdd.getNameIncludingNumberOfBells()), mutated);
		return mutated;
	}

	public void removeNotation(NotationBody notationToRemove) {
		checkNotNull(notationToRemove);

		Mutated mutated = touch.removeNotation(notationToRemove);
		setUpdatePoint(() -> String.format("Remove method: %s", notationToRemove.getNameIncludingNumberOfBells()), mutated);

//	TODO			Also do checks thate the notation can be removed
//	TODO			Also what happens to active method.
	}

	public void exchangeNotationAfterEdit(NotationBody originalNotation, NotationBody replacementNotation) {
		Mutated mutated = touch.exchangeNotation(originalNotation, replacementNotation);
		setUpdatePoint(() -> String.format("Update method: %s", replacementNotation.getNameIncludingNumberOfBells()), mutated);

		// TODO do we need checks here???
	}

	public NotationBody getSingleMethodActiveNotation() {
		return touch.getNonSplicedActiveNotation();
	}

	public void setSingleMethodActiveNotation(NotationBody notation) {
		Mutated mutated = UNCHANGED;
		if (!notation.equals(touch.getNonSplicedActiveNotation())) {
			mutated = touch.setNonSplicedActiveNotation(notation);
		}
		setUpdatePoint(() -> String.format("Set active method: %s", notation.getNameIncludingNumberOfBells()), mutated);
	}

	public boolean isSpliced() {
		return touch.isSpliced();
	}

	public void setSpliced(boolean spliced) {
		Mutated mutated = UNCHANGED;
		if (touch.isSpliced() != spliced) {
			mutated = touch.setSpliced(spliced);
		}
		setUpdatePoint(() -> (spliced?"Set spliced":"Set non spliced"), mutated);
	}

	public TouchCheckingType getTouchCheckingType() {
		return touch.getTouchCheckingType();
	}

	public void setTouchCheckingType(TouchCheckingType touchCheckingType) {
		checkNotNull(touchCheckingType);

		Mutated mutated = UNCHANGED;
		if (touchCheckingType != touch.getTouchCheckingType()) {
			mutated = touch.setTouchCheckingType(touchCheckingType);
		}
		setUpdatePoint(() -> String.format("Set Checking Type: %s", touchCheckingType.getName()), mutated);
	}

	public String getPlainLeadToken() {
		return touch.getPlainLeadToken();
	}

	public void setPlainLeadToken(String plainLeadToken) {
		checkNotNull(plainLeadToken);

		Mutated mutated = UNCHANGED;
		if (!plainLeadToken.equals(touch.getPlainLeadToken())) {
			mutated = touch.setPlainLeadToken(plainLeadToken);
		}
		setUpdatePoint(() -> String.format("Set Plain Lead Token: %s", plainLeadToken), mutated);
	}

	public String getStartChange() {
		return touch.getStartChange().getDisplayString(true);
	}

	public void setStartChange(String startChangeText) {
		checkNotNull(startChangeText);

		if (getStartChange().equals(startChangeText)) {
			return;
		}

		// first look for rounds token
		Mutated mutated = UNCHANGED;
		String action = "Set Start Change";
		if (startChangeText.compareToIgnoreCase(MethodRow.ROUNDS_TOKEN) == 0) {
			final MethodRow rounds = MethodBuilder.buildRoundsRow(touch.getNumberOfBells());
			mutated = touch.setStartChange(rounds);
			action = ": " + MethodRow.ROUNDS_TOKEN;
		}
		// Now check for valid row
		else {
			try {
				final MethodRow parsedRow = MethodBuilder.parse(touch.getNumberOfBells(), startChangeText);
				mutated = touch.setStartChange(parsedRow);
				action += ": " + parsedRow.getDisplayString(true);
			}
			catch (RuntimeException e) {
				StringBuilder msg = new StringBuilder();
				msg.append("Change start change to '")
						.append(startChangeText)
						.append("' has failed:")
						.append(System.lineSeparator());
				msg.append(e.getMessage())
						.append(System.lineSeparator());
				msg.append("The original start change '")
						.append(touch.getStartChange().getDisplayString(true))
						.append("' will be restored.");

				Alert dialog = new Alert(Alert.AlertType.ERROR, msg.toString(), ButtonType.OK);
				dialog.setTitle("Change start change failed");
				dialog.setHeaderText("Changing the start change to '" + startChangeText + "' has failed.");
				dialog.getDialogPane().setMinHeight(100);
				dialog.getDialogPane().setMinWidth(400);
				dialog.showAndWait().filter(response -> response == ButtonType.OK);
			}
		}

		String actionForLambda = action;
		setUpdatePoint(() -> actionForLambda, mutated);
	}

	public int getStartAtRow() {
		return touch.getStartAtRow();
	}

	public void setStartAtRow(int startAtRow) {
		if (startAtRow > Touch.START_AT_ROW_MAX) {
			startAtRow = Touch.START_AT_ROW_MAX;
		}
		if (startAtRow < 0) {
			startAtRow = 0;
		}

		Mutated mutated = UNCHANGED;
		if (startAtRow != getStartAtRow()) {
			mutated = touch.setStartAtRow(startAtRow);
		}
		final int startAtRowForLambda = startAtRow;
		setUpdatePoint(() -> String.format("Set Start At Row: %d", startAtRowForLambda), mutated);
	}


	public Stroke getStartStroke() {
		return touch.getStartStroke();
	}

	public void setStartStroke(Stroke startStroke) {
		checkNotNull(startStroke);

		Mutated mutated = UNCHANGED;
		if (startStroke != touch.getStartStroke()) {
			mutated = touch.setStartStroke(startStroke);
		}
		setUpdatePoint(() -> String.format("Set Start Stroke: %s", startStroke), mutated);
	}

	public void setStartNotation(String startNotation) {
		Mutated mutated;
		String action;

		if (Strings.isNullOrEmpty(startNotation)) {
			mutated = touch.removeStartNotation();
			action = "Remove Start Notation";
		}
		else {
			NotationBody builtNotation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(touch.getNumberOfBells())
				.setUnfoldedNotationShorthand(startNotation)
				.build();

			if (builtNotation != null && builtNotation.getRowCount() > 0) {
				mutated = touch.setStartNotation(builtNotation);
				action = "Set Start Notation: " + builtNotation.getNotationDisplayString(true);
			}
			else {
				mutated = touch.removeStartNotation();
				action = "Remove Start Notation";
			}
		}

		setUpdatePoint(() -> action, mutated);
	}

	public String getStartNotation() {
		final Optional<NotationBody> startNotation = touch.getStartNotation();
		if (startNotation.isPresent()) {
			return startNotation.get().getNotationDisplayString(false);
		}
		else {
			return "";
		}
	}

	public String getTerminationChange() {
		final Optional<MethodRow> terminationChange = touch.getTerminationChange();
		if (terminationChange.isPresent()) {
			return terminationChange.get().getDisplayString(true);
		}
		else {
			return "";
		}
	}

	public void setTerminationChange(String terminationChangeText) {
		checkNotNull(terminationChangeText);

		if (getTerminationChange().equals(terminationChangeText)) {
			return;
		}

		Mutated mutated = UNCHANGED;
		String action;
		// first look for removal
		if (terminationChangeText.isEmpty()) {
			mutated = touch.removeTerminationChange();
			action = "Remove Termination Change";
		}
		// then look for rounds token
		else if (terminationChangeText.compareToIgnoreCase(MethodRow.ROUNDS_TOKEN) == 0) {
			final MethodRow rounds = MethodBuilder.buildRoundsRow(touch.getNumberOfBells());
			mutated = touch.setTerminationChange(rounds);
			action = "Set Termination Change: " + MethodRow.ROUNDS_TOKEN;
		}
		// Now check for valid row
		else {
			try {
				final MethodRow parsedRow = MethodBuilder.parse(touch.getNumberOfBells(), terminationChangeText);
				mutated = touch.setTerminationChange(parsedRow);
				action = "Set Termination Change: " + parsedRow.getDisplayString(true);
			}
			catch (RuntimeException e) {
				StringBuilder msg = new StringBuilder();
				msg.append("Change termination change to '")
						.append(terminationChangeText)
						.append("' has failed:")
						.append(System.lineSeparator());
				msg.append(e.getMessage())
						.append(System.lineSeparator());
				msg.append("The original termination change '")
						.append(touch.getTerminationChange().isPresent()? touch.getTerminationChange().get().getDisplayString(true):"")
						.append("' will be restored.");

				Alert dialog = new Alert(Alert.AlertType.ERROR, msg.toString(), ButtonType.OK);
				dialog.setTitle("Change termination change failed");
				dialog.setHeaderText("Changing the termination change to '" + terminationChangeText + "' has failed.");
				dialog.getDialogPane().setMinHeight(100);
				dialog.getDialogPane().setMinWidth(400);
				dialog.showAndWait().filter(response -> response == ButtonType.OK);
				action = "error";
			}
		}

		String actionForLambda = action;
		setUpdatePoint(() -> actionForLambda, mutated);
	}

	public int getTerminationMaxRows() {
		return touch.getTerminationMaxRows();
	}

	public void setTerminationMaxRows(int terminationMaxRows) {
		if (terminationMaxRows > Touch.TERMINATION_MAX_ROWS_MAX ||
			terminationMaxRows < 1) {
			terminationMaxRows = Touch.TERMINATION_MAX_ROWS_MAX;
		}

		Mutated mutated = UNCHANGED;
		if (terminationMaxRows != getTerminationMaxRows()) {
			mutated = touch.setTerminationMaxRows(terminationMaxRows);
		}

		int terminationMaxRowsForLambda = terminationMaxRows;
		setUpdatePoint(() -> String.format("Set Row Limit: %d", terminationMaxRowsForLambda), mutated);
	}

	public Integer getTerminationMaxLeads() {
		return touch.getTerminationMaxLeads().orElse(null);
	}

	public void setTerminationMaxLeads(Integer terminationMaxLeads) {
		Mutated mutated;
		String action;
		if (terminationMaxLeads == null ||
			terminationMaxLeads < 1) {
			action = "Remove Lead Limit";
			mutated = touch.removeTerminationMaxLeads();
		}
		else {
			if (terminationMaxLeads > Touch.TERMINATION_MAX_LEADS_MAX) {
				terminationMaxLeads = Touch.TERMINATION_MAX_LEADS_MAX;
			}

			action = String.format("Set Lead Limit: %d", terminationMaxLeads);
			mutated = touch.setTerminationMaxLeads(terminationMaxLeads);
		}
		String actionForLambda = action;
		setUpdatePoint(() -> actionForLambda, mutated);
	}

	public Integer getTerminationMaxParts() {
		return touch.getTerminationMaxParts().orElse(null);
	}

	public void setTerminationMaxParts(Integer terminationMaxParts) {
		Mutated mutated;
		String action;
		if (terminationMaxParts == null ||
				terminationMaxParts < 1){
			action = "Remove Part Limit";
			mutated = touch.removeTerminationMaxParts();
		}
		else {
			if (terminationMaxParts > Touch.TERMINATION_MAX_PARTS_MAX) {
				terminationMaxParts = Touch.TERMINATION_MAX_PARTS_MAX;
			}

			action = String.format("Set Part Limit: %d", terminationMaxParts);
			mutated = touch.setTerminationMaxParts(terminationMaxParts);
		}

		String actionForLambda = action;
		setUpdatePoint(() -> actionForLambda, mutated);
	}

	public Integer getTerminationCircularTouch() {
		return touch.getTerminationMaxCircularTouch().orElse(null);
	}

	public void setTerminationCircularTouch(Integer terminationCircularTouch) {
		Mutated mutated;
		String action;
		if (terminationCircularTouch == null ||
				terminationCircularTouch < 1) {
			action = "Remove Circular Touch Limit";
			mutated = touch.removeTerminationMaxCircularTouch();
		}
		else {
			if (terminationCircularTouch > Touch.TERMINATION_CIRCULAR_TOUCH_MAX) {
				terminationCircularTouch = Touch.TERMINATION_CIRCULAR_TOUCH_MAX;
			}

			action = String.format("Set Circular Touch Limit: %d", terminationCircularTouch);
			mutated = touch.setTerminationMaxCircularTouch(terminationCircularTouch);
		}
		String actionForLambda = action;
		setUpdatePoint(() -> actionForLambda, mutated);
	}

	private void configureDefinitionModels() {

		final List<TouchDefinition> definitions = new ArrayList<>(touch.getDefinitions());
		Collections.sort(definitions, TouchDefinition.BY_SHORTHAND);

		for (TouchDefinition definition : definitions) {
			definitionModels.add(new DefinitionGridModel(this, definition));
		}
	}

	public List<GridModel> getDefinitionGridModels() {
		return definitionModels;
	}

	public TouchStyle getTouchStyle() {
		return touchStyle;
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

	@Override
	public void addListener(TouchDocumentListener listener) {
		listenerDelegate.addListener(listener);
	}

	public void setUpdatePoint(Supplier<String> updatePointName, Mutated mutated) {
		if (mutated == MUTATED) {
			log.info("UPDATE: [{}], [{}]", updatePointName.get(), mutated);
			parseAndProve();
			setDirty(true);
		}
		else {
			log.debug("UPDATE: [{}], [{}]", updatePointName.get(), mutated);
		}
		fireDocumentContentChanged();
	}

	public void parseAndProve() {
		proofManager.parseAndProve(touch);
	}

	private void fireDocumentContentChanged() {
		Platform.runLater(() -> {
			updateUiComponents();

			for (TouchDocumentListener touchDocumentListener : listenerDelegate.getListeners()) {
				touchDocumentListener.touchDocumentListener_documentContentChanged(TouchDocument.this);
			}
		});
	}

	public void setProofManager(ProofManager proofManager) {
		this.proofManager = proofManager;
	}

	@Override
	public boolean hasFileLocation() {
		return documentDelegate.hasFileLocation();
	}

	@Override
	public boolean isDirty() {
		return documentDelegate.isDirty();
	}

	@Override
	public void setDirty(boolean dirty) {
		documentDelegate.setDirty(dirty);
	}

	@Override
	public Path getPath() {
		return documentDelegate.getPath();
	}

	@Override
	public void setPath(Path path) {
		documentDelegate.setPath(path);
	}

	@Override
	public void setDocumentName(String documentName) {
		documentDelegate.setDocumentName(documentName);
	}

	@Override
	public String getNameForApplicationTitle() {
		return documentDelegate.getNameForApplicationTitle();
	}

	@Override
	public String getNameForTab() {
		return documentDelegate.getNameForTab();
	}

	@Override
	public Node getNode() {
		return this;
	}
}
