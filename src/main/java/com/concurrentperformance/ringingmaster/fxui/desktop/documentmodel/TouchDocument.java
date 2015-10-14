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
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCheckingType;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.container.impl.ImmutableTouch;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides the interface between the engine {@code Touch} and the various
 * UI components.
 *
 * @author Lake
 */
public class TouchDocument extends ScrollPane implements Listenable<TouchDocumentListener> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String SPLICED_TOKEN = "<Spliced>";

	//Raw Data
	private Touch touch;
	private final TouchStyle touchStyle = new TouchStyle();
	private Optional<Path> path = Optional.empty();

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

	public Touch getTouch() {
		return new ImmutableTouch(touch);
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

	private void updateUiComponents() {
		titlePane.setText(getTitle(), getAuthor());
	}

	public String getTitle() {
		return touch.getTitle();
	}

	public void setTitle(String newTitle) {
		if (!newTitle.equals(touch.getTitle())) {
			touch.setTitle(newTitle);
		}
		fireDocumentContentChanged();
	}

	public String getAuthor() {
		return touch.getAuthor();
	}

	public void setAuthor(String author) {
		if (!author.equals(touch.getAuthor())) {
			touch.setAuthor(author);
		}
		fireDocumentContentChanged();
	}

	public NumberOfBells getNumberOfBells() {
		return touch.getNumberOfBells();
	}

	public void setNumberOfBells(NumberOfBells numberOfBells) {
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
		if (touch.getCallFromBell() != callFrom) {
			touch.setCallFromBell(callFrom);
			parseAndProve();
		}
		fireDocumentContentChanged();
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

	public boolean addNotation(NotationBody notationToAdd) {

		List<String> messages = touch.checkAddNotation(notationToAdd);

		if (messages.size() == 0) {
			touch.addNotation(notationToAdd);
			parseAndProve();
			fireDocumentContentChanged();
			return true;
		}
		else {
			String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
			Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message.toString(), ButtonType.OK);
			dialog.setTitle("Can't add notation");
			dialog.setHeaderText("Can't add '" + notationToAdd.getNameIncludingNumberOfBells() + "'");
			dialog.getDialogPane().setMinHeight(280);
			dialog.getDialogPane().setMinWidth(560);
			dialog.showAndWait();

			fireDocumentContentChanged();
			return false;
		}
	}

	public void removeNotation(NotationBody notationToRemove) {
		touch.removeNotation(notationToRemove);
		parseAndProve();
		fireDocumentContentChanged();

//	TODO			Also do checks thate the notation can be removed
//	TODO			Also what happens to active method.
	}

	public void exchangeNotationAfterEdit(NotationBody originalNotation, NotationBody replacementNotation) {
		touch.exchangeNotation(originalNotation, replacementNotation);
		parseAndProve();
		fireDocumentContentChanged();

		// TODO do we need checks here???
	}

	public NotationBody getSingleMethodActiveNotation() {
		return touch.getNonSplicedActiveNotation();
	}

	public void setSingleMethodActiveNotation(NotationBody notation) {
		if (!notation.equals(touch.getNonSplicedActiveNotation())) {
			touch.setNonSplicedActiveNotation(notation);
			parseAndProve();
		}
		fireDocumentContentChanged();
	}

	public boolean isSpliced() {
		return touch.isSpliced();
	}

	public void setSpliced(boolean spliced) {
		if (touch.isSpliced() != spliced) {
			touch.setSpliced(spliced);
			parseAndProve();
		}
		fireDocumentContentChanged();
	}

	public TouchCheckingType getTouchCheckingType() {
		return touch.getTouchCheckingType();
	}

	public void setTouchCheckingType(TouchCheckingType touchCheckingType) {
		checkNotNull(touchCheckingType);
		if (touchCheckingType != touch.getTouchCheckingType()) {
			touch.setTouchCheckingType(touchCheckingType);
			parseAndProve();
		}
		fireDocumentContentChanged();
	}

	public String getPlainLeadToken() {
		return touch.getPlainLeadToken();
	}

	public void setPlainLeadToken(String plainLeadToken) {
		checkNotNull(plainLeadToken);
		if (!plainLeadToken.equals(touch.getPlainLeadToken())) {
			touch.setPlainLeadToken(plainLeadToken);
			parseAndProve();
		}
		fireDocumentContentChanged();
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
		if (startChangeText.compareToIgnoreCase(MethodRow.ROUNDS_TOKEN) == 0) {
			final MethodRow rounds = MethodBuilder.buildRoundsRow(touch.getNumberOfBells());
			touch.setStartChange(rounds);
			parseAndProve();
		}
		// Now check for valid row
		else {
			try {
				final MethodRow parsedRow = MethodBuilder.parse(touch.getNumberOfBells(), startChangeText);
				touch.setStartChange(parsedRow);
				parseAndProve();
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

		fireDocumentContentChanged();
	}

	public int getStartAtRow() {
		return touch.getStartAtRow();
	}


	public void setStartAtRow(Integer startAtRow) {
		if (startAtRow != null) {
			if (startAtRow > Touch.START_AT_ROW_MAX) {
				startAtRow = Touch.START_AT_ROW_MAX;
			}
			if (startAtRow < 0) {
				startAtRow = 0;
			}
			if (!startAtRow.equals(getStartAtRow())) {
				touch.setStartAtRow(startAtRow);
				parseAndProve();
			}
		}
		fireDocumentContentChanged();
	}

	public Stroke getStartStroke() {
		return touch.getStartStroke();
	}

	public void setStartStroke(Stroke startStroke) {
		if (startStroke != touch.getStartStroke()) {
			touch.setStartStroke(startStroke);
			parseAndProve();
		}
		fireDocumentContentChanged();
	}

	public void setStartNotation(String startNotation) {
		if (Strings.isNullOrEmpty(startNotation)) {
			touch.removeStartNotation();
		}
		else {
			NotationBody builtNotation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(touch.getNumberOfBells())
				.setUnfoldedNotationShorthand(startNotation)
				.build();

			if (builtNotation != null && builtNotation.getRowCount() > 0) {
				touch.setStartNotation(builtNotation);
			}
			else {
				touch.removeStartNotation();
			}
		}

		parseAndProve();
		fireDocumentContentChanged();
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

		if (terminationChangeText.isEmpty()) {
			touch.removeTerminationChange();
			parseAndProve();
		}
		// first look for rounds token
		else if (terminationChangeText.compareToIgnoreCase(MethodRow.ROUNDS_TOKEN) == 0) {
			final MethodRow rounds = MethodBuilder.buildRoundsRow(touch.getNumberOfBells());
			touch.setTerminationChange(rounds);
			parseAndProve();
		}
		// Now check for valid row
		else {
			try {
				final MethodRow parsedRow = MethodBuilder.parse(touch.getNumberOfBells(), terminationChangeText);
				touch.setTerminationChange(parsedRow);
				parseAndProve();
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
			}
		}

		fireDocumentContentChanged();
	}

	public int getTerminationMaxRows() {
		return touch.getTerminationMaxRows();
	}

	public void setTerminationMaxRows(Integer terminationMaxRows) {
		if (terminationMaxRows != null) {
			if (terminationMaxRows > Touch.TERMINATION_MAX_ROWS_MAX) {
				terminationMaxRows = Touch.TERMINATION_MAX_ROWS_MAX;
			}
			if (terminationMaxRows < 1) {
				terminationMaxRows = 1;
			}
			if (!terminationMaxRows.equals(getTerminationMaxRows())) {
				touch.setTerminationMaxRows(terminationMaxRows);
				parseAndProve();
			}
		}

		fireDocumentContentChanged();
	}

	public Integer getTerminationMaxLeads() {
		return touch.getTerminationMaxLeads().orElse(null);
	}

	public void setTerminationMaxLeads(Integer terminationMaxLeads) {
		if (terminationMaxLeads != null) {
			if (terminationMaxLeads > Touch.TERMINATION_MAX_LEADS_MAX) {
				terminationMaxLeads = Touch.TERMINATION_MAX_LEADS_MAX;
			}
			if (terminationMaxLeads < 1) {
				terminationMaxLeads = 1;
			}
			if (!terminationMaxLeads.equals(getTerminationMaxLeads())) {
				touch.setTerminationMaxLeads(terminationMaxLeads);
				parseAndProve();
			}
		}
		else {
			if (touch.getTerminationMaxLeads() != null) {
				touch.removeTerminationMaxLeads();
				parseAndProve();
			}
		}
		fireDocumentContentChanged();
	}

	public Integer getTerminationMaxParts() {
		return touch.getTerminationMaxParts().orElse(null);
	}

	public void setTerminationMaxParts(Integer terminationMaxParts) {
		if (terminationMaxParts != null) {
			if (terminationMaxParts > Touch.TERMINATION_MAX_PARTS_MAX) {
				terminationMaxParts = Touch.TERMINATION_MAX_PARTS_MAX;
			}
			if (terminationMaxParts < 1) {
				terminationMaxParts = 1;
			}
			if (!terminationMaxParts.equals(getTerminationMaxParts())) {
				touch.setTerminationMaxParts(terminationMaxParts);
				parseAndProve();
			}
		}
		else {
			if (touch.getTerminationMaxParts() != null) {
				touch.removeTerminationMaxParts();
				parseAndProve();
			}
		}
		fireDocumentContentChanged();
	}

	public Integer getTerminationCircularTouch() {
		return touch.getTerminationMaxCircularTouch().orElse(null);
	}

	public void setTerminationCircularTouch(Integer terminationCircularTouch) {
		if (terminationCircularTouch != null) {
			if (terminationCircularTouch > Touch.TERMINATION_CIRCULAR_TOUCH_MAX) {
				terminationCircularTouch = Touch.TERMINATION_CIRCULAR_TOUCH_MAX;
			}
			if (terminationCircularTouch < 1) {
				terminationCircularTouch = 1;
			}
			if (!terminationCircularTouch.equals(getTerminationCircularTouch())) {

				touch.setTerminationMaxCircularTouch(terminationCircularTouch);
				parseAndProve();
			}
		}
		else {
			if (touch.getTerminationMaxCircularTouch() != null) {
				touch.removeTerminationMaxCircularTouch();
				parseAndProve();
			}
		}
		fireDocumentContentChanged();
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

	public TouchStyle getTouchStyle() {
		return touchStyle;
	}

	public void parseAndProve() {
		proofManager.parseAndProve(touch);
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

	public Optional<Path> getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = Optional.of(path);
	}
}
