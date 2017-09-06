package org.ringingmaster.ui.desktop.documentmodel;

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
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.grid.Grid;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.touch.container.TouchCell;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.newcontainer.Touch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.ringingmaster.ui.common.TouchStyle;
import org.ringingmaster.ui.desktop.documentmanager.DefaultDocument;
import org.ringingmaster.ui.desktop.documentmanager.Document;
import org.ringingmaster.ui.desktop.documentmodel.maingrid.MainGridModel;
import org.ringingmaster.ui.desktop.documentpanel.DefinitionPane;
import org.ringingmaster.ui.desktop.documentpanel.TitlePane;
import org.ringingmaster.ui.desktop.documentpanel.grid.canvas.GridPane;
import org.ringingmaster.ui.desktop.documentpanel.grid.model.GridModel;
import org.ringingmaster.ui.desktop.proof.ProofManager;
import org.ringingmaster.util.listener.ConcurrentListenable;
import org.ringingmaster.util.listener.Listenable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private ObservableTouch touch;
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

	public void init(ObservableTouch touch) {
		layoutNodes();

		this.touch = touch;

		parseAndProve();

		configureDefinitionModels();

		mainGridModel = new MainGridModel(this);

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
		return touch.get();
	}

	private void updateUiComponents() {
		titlePane.setText(getTitle(), getAuthor());
		gridPane.gridModelListener_contentsChanged();
		definitionPane.contentsChanged();
	}

	public String getTitle() {
		return touch.get().getTitle();
	}

	public void setTitle(String title) {
		touch.setTitle(title);

//		setUpdatePoint(() -> "Set Title", mutated);
	}

	public String getAuthor() {
		return touch.get().getAuthor();
	}

	public void setAuthor(String author) {
		touch.setAuthor(author);

		//setUpdatePoint(() -> "Set Author", modified);
	}

	public NumberOfBells getNumberOfBells() {
		return touch.get().getNumberOfBells();
	}

	public void setNumberOfBells(NumberOfBells numberOfBells) {
		touch.setNumberOfBells(numberOfBells);
//		checkNotNull(numberOfBells);
////TODO Drive this checking from the ObservableTouch - check can return an immutable touch
//
//		Mutated mutated = UNCHANGED;
//		if (touch.get().getNumberOfBells() != numberOfBells) {
//			StringBuilder message = new StringBuilder();
//
//			int pointNumber = 1;
//			if (touch.get().getCallFromBell().getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
//				message.append(pointNumber++).append(") Call from bell will change from ")
//						.append(touch.get().getCallFromBell().getZeroBasedBell() + 1)
//						.append(" to ").append(numberOfBells.getTenor().getZeroBasedBell() + 1).append(".").append(System.lineSeparator());
//			}
//
//			final MethodRow existingStartChange = touch.get().getStartChange();
//			final MethodRow newInitialRow = MethodBuilder.transformToNewNumberOfBells(existingStartChange, numberOfBells);
//
//			message.append(pointNumber++).append(") Start change will change from '")
//					.append(existingStartChange.getDisplayString(false))
//					.append("' to '")
//					.append(newInitialRow.getDisplayString(false))
//					.append("'.").append(System.lineSeparator());
//
//			if (touch.get().getTerminationChange().isPresent()) {
//				final MethodRow existingTerminationRow = touch.get().getTerminationChange().get();
//				final MethodRow newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);
//
//				message.append(pointNumber++).append(") Termination row will change from '")
//						.append(existingTerminationRow.getDisplayString(false))
//						.append("' to '")
//						.append(newTerminationRow.getDisplayString(false))
//						.append("'.").append(System.lineSeparator());
//			}
//
//			if (!touch.isSpliced() &&
//					touch.get().getNonSplicedActiveNotation() != null &&
//					touch.get().getNonSplicedActiveNotation().getNumberOfWorkingBells().toInt() > numberOfBells.toInt()) {
//				final List<NotationBody> filteredNotations = NotationBuilderHelper.filterNotationsUptoNumberOfBells(touch.get().getAllNotations(), numberOfBells);
//				message.append(pointNumber++).append(") Active method '")
//						.append(touch.get().getNonSplicedActiveNotation().getNameIncludingNumberOfBells())
//						.append("' ");
//				if (filteredNotations.size() == 0) {
//					message.append("will be unset. There is no suitable replacement.");
//				}
//				else {
//					message.append("will change to '")
//							.append(filteredNotations.get(0).getNameIncludingNumberOfBells())
//					.append("'");
//				}
//				message.append(System.lineSeparator());
//			}
//
//			if (touch.get().getStartNotation().isPresent()) {
//				final String notation = touch.get().getStartNotation().get().getNotationDisplayString(false);
//				NotationBody builtNotation = NotationBuilder.getInstance()
//						.setNumberOfWorkingBells(numberOfBells)
//						.setUnfoldedNotationShorthand(notation)
//						.build();
//				String newNotation = builtNotation.getNotationDisplayString(false);
//				if (!newNotation.equals(notation)) {
//
//					message.append(pointNumber++).append(") Start notation '")
//							.append(notation)
//							.append("' ")
//					.append("will change to '")
//					.append(newNotation)
//					.append("'");
//				}
//			}
//
//			boolean doAction = false;
//
//			if (message.length() > 0) {
//				message.append(System.lineSeparator()).append("Do you wish to continue?");
//
//				StringBuilder preamble = new StringBuilder();
//				preamble.append("Changing number of bells from ").append(touch.get().getNumberOfBells().getDisplayString())
//						.append(" to ").append(numberOfBells.getDisplayString())
//						.append(" will modify other properties: ").append(System.lineSeparator());
//
//				Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, preamble.toString() + message.toString(), ButtonType.OK, ButtonType.CANCEL);
//				dialog.setTitle("Change number of bells");
//				dialog.setHeaderText("Change number of bells");
//				dialog.getDialogPane().setMinHeight(280);
//				dialog.getDialogPane().setMinWidth(620);
//				final java.util.Optional result = dialog.showAndWait().filter(response -> response == ButtonType.OK);
//				if (result.isPresent()) {
//					doAction = true;
//				}
//			}
//
//			if (doAction) {
//				mutated = touch.setNumberOfBells(numberOfBells);
//			}
//		}



				//	setUpdatePoint(()->String.format("Set number of bells to: %s", numberOfBells.getDisplayString()), mutated);
	}

	public Bell getCallFrom() {
		return touch.get().getCallFromBell();
	}

	public void setCallFrom(Bell callFrom) {
		checkNotNull(callFrom);
		touch.setCallFromBell(callFrom);

//		setUpdatePoint(() -> String.format("Set number of bells to: %s", callFrom.getDisplayString()), mutated);
	}

	public List<NotationBody> getSortedAllNotations() {
		final List<NotationBody> sortedNotations = Lists.newArrayList(touch.get().getAllNotations());
		sortedNotations.sort(Notation.BY_NUMBER_THEN_NAME);
		return sortedNotations;
	}

	public List<NotationBody> getSortedValidNotations() {
		final List<NotationBody> sortedNotations = Lists.newArrayList(touch.get().getValidNotations());
		Collections.sort(sortedNotations, Notation.BY_NUMBER_THEN_NAME);
		return sortedNotations;
	}

	public void addNotation(NotationBody notationToAdd) {
		//TODO should this be in the calling action ?
		List<String> messages = touch.checkAddNotation(notationToAdd);

		if (messages.size() == 0) {
			touch.addNotation(notationToAdd);
		} else {
			String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
			Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
			dialog.setTitle("Can't add method");
			dialog.setHeaderText("Can't add '" + notationToAdd.getNameIncludingNumberOfBells() + "'");
			dialog.getDialogPane().setMinHeight(180);
			dialog.getDialogPane().setMinWidth(360);
			dialog.showAndWait();
		}

//		setUpdatePoint(() -> String.format("Add method: %s", notationToAdd.getNameIncludingNumberOfBells()), mutated);
	}

	public void removeNotation(NotationBody notationToRemove) {
//		checkNotNull(notationToRemove);

//		setUpdatePoint(() -> String.format("Remove method: %s", notationToRemove.getNameIncludingNumberOfBells()), mutated);

//	TODO			Also do checks thate the notation can be removed
//	TODO			Also what happens to active method.
	}

	public void exchangeNotationAfterEdit(NotationBody originalNotation, NotationBody replacementNotation) {


		List<String> messages = touch.checkUpdateNotation(originalNotation, replacementNotation);

		//TODO should this be in the calling action ?
		if (messages.size() == 0) {
			touch.updateNotation(originalNotation, replacementNotation);
		}
		else {
			String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
			Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
			dialog.setTitle("Can't update method");
			dialog.setHeaderText("Can't update '" + originalNotation.getNameIncludingNumberOfBells() + "'" +
					(originalNotation.getNameIncludingNumberOfBells().equals(replacementNotation.getNameIncludingNumberOfBells())?"":" to '" + replacementNotation.getNameIncludingNumberOfBells() + "'"  ));
			dialog.getDialogPane().setMinHeight(180);
			dialog.getDialogPane().setMinWidth(360);
			dialog.showAndWait();
		}

//		setUpdatePoint(() -> String.format("Update method: %s", replacementNotation.getNameIncludingNumberOfBells()), mutated);
	}

	public NotationBody getNonSplicedActiveNotation() {
		return touch.get().getNonSplicedActiveNotation().get();
	}

	public void setSingleMethodActiveNotation(NotationBody notation) {
		touch.setNonSplicedActiveNotation(notation);

//		setUpdatePoint(() -> String.format("Set active method: %s", notation.getNameIncludingNumberOfBells()), mutated);
	}

	public boolean isSpliced() {
		return touch.get().isSpliced();
	}

	public void setSpliced(boolean spliced) {
		touch.setSpliced(spliced);

//		setUpdatePoint(() -> (spliced?"Set spliced":"Set non spliced"), mutated);
	}

	public CheckingType getTouchCheckingType() {
		return touch.get().getCheckingType();
	}

	public void setTouchCheckingType(CheckingType checkingType) {
		touch.setTouchCheckingType(checkingType);

//		setUpdatePoint(() -> String.format("Set Checking Type: %s", checkingType.getName()), mutated);
	}

	public String getPlainLeadToken() {
		return touch.get().getPlainLeadToken();
	}

	public void setPlainLeadToken(String plainLeadToken) {
		touch.setPlainLeadToken(plainLeadToken);

//		setUpdatePoint(() -> String.format("Set Plain Lead Token: %s", plainLeadToken), mutated);
	}

	public String getStartChange() {
		return touch.get().getStartChange().getDisplayString(true);
	}

	public void setStartChange(String startChangeText) {
//TODO//		checkNotNull(startChangeText);
//
//		if (getStartChange().equals(startChangeText)) {
//			return;
//		}
//
//		// first look for rounds token
//		org.ringingmaster.engine.touch.container.Touch.Mutated mutated = UNCHANGED;
//		String action = "Set Start Change";
//		if (startChangeText.compareToIgnoreCase(MethodRow.ROUNDS_TOKEN) == 0) {
//			final MethodRow rounds = MethodBuilder.buildRoundsRow(touch.get().getNumberOfBells());
//			mutated = touch.setStartChange(rounds);
//			action = ": " + MethodRow.ROUNDS_TOKEN;
//		}
//		// Now check for valid row
//		else {
//			try {
//				final MethodRow parsedRow = MethodBuilder.parse(touch.get().getNumberOfBells(), startChangeText);
//				mutated = touch.setStartChange(parsedRow);
//				action += ": " + parsedRow.getDisplayString(true);
//			}
//			catch (RuntimeException e) {
//				StringBuilder msg = new StringBuilder();
//				msg.append("Change start change to '")
//						.append(startChangeText)
//						.append("' has failed:")
//						.append(System.lineSeparator());
//				msg.append(e.getMessage())
//						.append(System.lineSeparator());
//				msg.append("The original start change '")
//						.append(touch.get().getStartChange().getDisplayString(true))
//						.append("' will be restored.");
//
//				Alert dialog = new Alert(Alert.AlertType.ERROR, msg.toString(), ButtonType.OK);
//				dialog.setTitle("Change start change failed");
//				dialog.setHeaderText("Changing the start change to '" + startChangeText + "' has failed.");
//				dialog.getDialogPane().setMinHeight(100);
//				dialog.getDialogPane().setMinWidth(400);
//				dialog.showAndWait().filter(response -> response == ButtonType.OK);
//			}
//		}
//
//		String actionForLambda = action;
//		setUpdatePoint(() -> actionForLambda, mutated);
	}

	public int getStartAtRow() {
		return touch.get().getStartAtRow();
	}

	public void setStartAtRow(int startAtRow) {
		if (startAtRow > ObservableTouch.START_AT_ROW_MAX) {
			startAtRow = ObservableTouch.START_AT_ROW_MAX;
		}
		if (startAtRow < 0) {
			startAtRow = 0;
		}

		touch.setStartAtRow(startAtRow);

//		final int startAtRowForLambda = startAtRow;
//		setUpdatePoint(() -> String.format("Set Start At Row: %d", startAtRowForLambda), mutated);
	}


	public Stroke getStartStroke() {
		return touch.get().getStartStroke();
	}

	public void setStartStroke(Stroke startStroke) {
		touch.setStartStroke(startStroke);

//		setUpdatePoint(() -> String.format("Set Start Stroke: %s", startStroke), mutated);
	}

	public void setStartNotation(String startNotation) {
//TODO		Mutated mutated;
//		String action;
//
//		if (Strings.isNullOrEmpty(startNotation)) {
//			mutated = touch.removeStartNotation();
//			action = "Remove Start Notation";
//		}
//		else {
//			NotationBody builtNotation = NotationBuilder.getInstance()
//				.setNumberOfWorkingBells(touch.get().getNumberOfBells())
//				.setUnfoldedNotationShorthand(startNotation)
//				.build();
//
//			if (builtNotation != null && builtNotation.getRowCount() > 0) {
//				mutated = touch.setStartNotation(builtNotation);
//				action = "Set Start Notation: " + builtNotation.getNotationDisplayString(true);
//			}
//			else {
//				mutated = touch.removeStartNotation();
//				action = "Remove Start Notation";
//			}
//		}
//
//		setUpdatePoint(() -> action, mutated);
	}

	public String getStartNotation() {
		final Optional<NotationBody> startNotation = touch.get().getStartNotation();
		if (startNotation.isPresent()) {
			return startNotation.get().getNotationDisplayString(false);
		}
		else {
			return "";
		}
	}

	public String getTerminationChange() {
		final Optional<MethodRow> terminationChange = touch.get().getTerminationChange();
		if (terminationChange.isPresent()) {
			return terminationChange.get().getDisplayString(true);
		}
		else {
			return "";
		}
	}

	public void setTerminationChange(String terminationChangeText) {
//TODO		checkNotNull(terminationChangeText);
//
//		if (getTerminationChange().equals(terminationChangeText)) {
//			return;
//		}
//
//		Mutated mutated = UNCHANGED;
//		String action;
//		// first look for removal
//		if (terminationChangeText.isEmpty()) {
//			mutated = touch.removeTerminationChange();
//			action = "Remove Termination Change";
//		}
//		// then look for rounds token
//		else if (terminationChangeText.compareToIgnoreCase(MethodRow.ROUNDS_TOKEN) == 0) {
//			final MethodRow rounds = MethodBuilder.buildRoundsRow(touch.get().getNumberOfBells());
//			mutated = touch.setTerminationChange(rounds);
//			action = "Set Termination Change: " + MethodRow.ROUNDS_TOKEN;
//		}
//		// Now check for valid row
//		else {
//			try {
//				final MethodRow parsedRow = MethodBuilder.parse(touch.get().getNumberOfBells(), terminationChangeText);
//				mutated = touch.setTerminationChange(parsedRow);
//				action = "Set Termination Change: " + parsedRow.getDisplayString(true);
//			}
//			catch (RuntimeException e) {
//				StringBuilder msg = new StringBuilder();
//				msg.append("Change termination change to '")
//						.append(terminationChangeText)
//						.append("' has failed:")
//						.append(System.lineSeparator());
//				msg.append(e.getMessage())
//						.append(System.lineSeparator());
//				msg.append("The original termination change '")
//						.append(touch.get().getTerminationChange().isPresent()? touch.get().getTerminationChange().get().getDisplayString(true):"")
//						.append("' will be restored.");
//
//				Alert dialog = new Alert(Alert.AlertType.ERROR, msg.toString(), ButtonType.OK);
//				dialog.setTitle("Change termination change failed");
//				dialog.setHeaderText("Changing the termination change to '" + terminationChangeText + "' has failed.");
//				dialog.getDialogPane().setMinHeight(100);
//				dialog.getDialogPane().setMinWidth(400);
//				dialog.showAndWait().filter(response -> response == ButtonType.OK);
//				action = "error";
//			}
//		}
//
//		String actionForLambda = action;
//		setUpdatePoint(() -> actionForLambda, mutated);
	}

	public int getTerminationMaxRows() {
		return touch.get().getTerminationMaxRows();
	}

	public void setTerminationMaxRows(int terminationMaxRows) {
//TODO		if (terminationMaxRows > Touch.TERMINATION_MAX_ROWS_MAX ||
//			terminationMaxRows < 1) {
//			terminationMaxRows = Touch.TERMINATION_MAX_ROWS_MAX;
//		}
//
//		Mutated mutated = UNCHANGED;
//		if (terminationMaxRows != getTerminationMaxRows()) {
//			mutated = touch.setTerminationMaxRows(terminationMaxRows);
//		}
//
//		int terminationMaxRowsForLambda = terminationMaxRows;
//		setUpdatePoint(() -> String.format("Set Row Limit: %d", terminationMaxRowsForLambda), mutated);
	}

	public Integer getTerminationMaxLeads() {
		return touch.get().getTerminationMaxLeads().orElse(null);
	}

	public void setTerminationMaxLeads(Integer terminationMaxLeads) {
//TODO		Mutated mutated;
//		String action;
//		if (terminationMaxLeads == null ||
//			terminationMaxLeads < 1) {
//			action = "Remove Lead Limit";
//			mutated = touch.removeTerminationMaxLeads();
//		}
//		else {
//			if (terminationMaxLeads > Touch.TERMINATION_MAX_LEADS_MAX) {
//				terminationMaxLeads = Touch.TERMINATION_MAX_LEADS_MAX;
//			}
//
//			action = String.format("Set Lead Limit: %d", terminationMaxLeads);
//			mutated = touch.setTerminationMaxLeads(terminationMaxLeads);
//		}
//		String actionForLambda = action;
//		setUpdatePoint(() -> actionForLambda, mutated);
	}

	public Integer getTerminationMaxParts() {
		return touch.get().getTerminationMaxParts().orElse(null);
	}

	public void setTerminationMaxParts(Integer terminationMaxParts) {
//TODO		Mutated mutated;
//		String action;
//		if (terminationMaxParts == null ||
//				terminationMaxParts < 1){
//			action = "Remove Part Limit";
//			mutated = touch.removeTerminationMaxParts();
//		}
//		else {
//			if (terminationMaxParts > Touch.TERMINATION_MAX_PARTS_MAX) {
//				terminationMaxParts = Touch.TERMINATION_MAX_PARTS_MAX;
//			}
//
//			action = String.format("Set Part Limit: %d", terminationMaxParts);
//			mutated = touch.setTerminationMaxParts(terminationMaxParts);
//		}
//
//		String actionForLambda = action;
//		setUpdatePoint(() -> actionForLambda, mutated);
	}

	public int getTerminationCircularTouch() {
		return touch.get().getTerminationMaxCircularity();
	}

	public void setTerminationCircularTouch(Integer terminationCircularTouch) {
//		Mutated mutated;
//		String action;
//		if (terminationCircularTouch == null ||
//				terminationCircularTouch < 1) {
//			action = "Remove Circular Touch Limit";
//			mutated = touch.removeTerminationMaxCircularTouch();
//		}
//		else {
//			if (terminationCircularTouch > Touch.TERMINATION_CIRCULAR_TOUCH_MAX) {
//				terminationCircularTouch = Touch.TERMINATION_CIRCULAR_TOUCH_MAX;
//			}
//
//			action = String.format("Set Circular Touch Limit: %d", terminationCircularTouch);
//			mutated = touch.setTerminationMaxCircularTouch(terminationCircularTouch);
//		}
//		String actionForLambda = action;
//		setUpdatePoint(() -> actionForLambda, mutated);
	}

	private void configureDefinitionModels() {
//
//		final List<TouchDefinition> definitions = new ArrayList<>(touch.get().getDefinitions());
//		Collections.sort(definitions, TouchDefinition.BY_SHORTHAND);
//
//		for (TouchDefinition definition : definitions) {
//			definitionModels.add(new DefinitionGridModel(this, definition));
//		}
	}

	public List<GridModel> getDefinitionGridModels() {
		return definitionModels;
	}

	public TouchStyle getTouchStyle() {
		return touchStyle;
	}

	public int getColumnCount() {
		return touch.get().getColumnCount();
	}

	public int getRowCount() {
		return touch.get().getRowCount();
	}

	public Grid<TouchCell> allCellsView() {
		return null; //TODO touch.allCellsView();
	}

	public void incrementColumnCount() {
		//TODO	touch.incrementColumnCount();
	}

	public void incrementRowCount() {
	//TODO	touch.incrementRowCount();
	}

	public void insertCharacter(int column, int row, int index, String character) {
		touch.insertCharacters(column, row, index, character);
	}

	public void collapseEmptyRowsAndColumns() {
	//TODO	touch.collapseEmptyRowsAndColumns();
	}

	@Override
	public void addListener(TouchDocumentListener listener) {
		listenerDelegate.addListener(listener);
	}

//TODO	public void setUpdatePoint(Supplier<String> updatePointName, Mutated mutated) {
//		if (mutated == MUTATED) {
//			log.info("UPDATE: [{}]", updatePointName.get());
//			parseAndProve();
//			setDirty(true);
//		}
//		else {
//			log.debug("UPDATE: [{}], [{}]", updatePointName.get(), mutated);
//		}
//		fireDocumentContentChanged();
//	}

	public void parseAndProve() {
		//TODOproofManager.parseAndProve(touch);
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
