package org.ringingmaster.ui.desktop.documentmanager;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.grid.Grid;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.container.TouchCell;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.ringingmaster.engine.touch.container.TouchDefinition;
import org.ringingmaster.engine.touch.container.impl.TouchBuilder;
import org.ringingmaster.persist.DocumentPersist;
import org.ringingmaster.persist.generated.v1.CallPersist;
import org.ringingmaster.persist.generated.v1.DefinitionPersist;
import org.ringingmaster.persist.generated.v1.NotationKeyPersist;
import org.ringingmaster.persist.generated.v1.ObjectFactory;
import org.ringingmaster.persist.generated.v1.StrokePersist;
import org.ringingmaster.persist.generated.v1.TouchCellPersist;
import org.ringingmaster.persist.generated.v1.TouchCellsPersist;
import org.ringingmaster.persist.generated.v1.TouchCheckingPersist;
import org.ringingmaster.persist.generated.v1.TouchNotationPersist;
import org.ringingmaster.persist.generated.v1.CompositionPersist;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CompositionPersistence {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private DocumentPersist documentPersist = new DocumentPersist();

	public void save(Path path, Touch touch) {

		CompositionPersist CompositionPersist = buildCompositionPersist(touch);

		try {
			documentPersist.writeTouch(CompositionPersist, path);
		} catch (IOException e) {
			log.error("TODO", e);
		} catch (JAXBException e) {
			log.error("TODO", e);
		}
	}

	public Touch load(Path path) {
		CompositionPersist CompositionPersist = documentPersist.readTouch(path);
		Touch touch = buildTouch(CompositionPersist);
		return touch;
	}

	CompositionPersist buildCompositionPersist(Touch touch) {
		CompositionPersist CompositionPersist = new CompositionPersist();

		CompositionPersist.setNumberOfBells(touch.getNumberOfBells().toInt());

		CompositionPersist.setTitle(touch.getTitle());

		CompositionPersist.setAuthor(touch.getAuthor());

		CompositionPersist.setTouchChecking(TouchCheckingPersist.fromValue(touch.getCheckingType().toString()));

		CompositionPersist.setCallFrom(touch.getCallFromBell().getZeroBasedBell() + 1);

		if (touch.getNonSplicedActiveNotation() != null) {
			NotationBody nonSplicedActiveNotation = touch.getNonSplicedActiveNotation();
			NotationKeyPersist notationKeyPersist = getNotationKeyPersist(nonSplicedActiveNotation);
			CompositionPersist.setNonSplicedActiveNotation(notationKeyPersist);
		}

		CompositionPersist.setSpliced(touch.isSpliced());

		CompositionPersist.setPlainLeadToken(touch.getPlainLeadToken());

		Set<TouchDefinition> definitions = touch.getDefinitions();
		for (TouchDefinition definition : definitions) {
			DefinitionPersist definitionPersist = buildDefinitionPersist(definition);
			CompositionPersist.getDefinition().add(definitionPersist);
		}

		for (NotationBody notation : touch.getAllNotations()) {
			TouchNotationPersist notationType = buildTouchNotationPersist(notation);
			CompositionPersist.getNotation().add(notationType);
		}

		Grid<TouchCell> touchCells = touch.allCellsView();

		TouchCellsPersist touchCellsPersist = new TouchCellsPersist();
		touchCellsPersist.setRows(touchCells.getRowCount());
		touchCellsPersist.setColumns(touchCells.getColumnCount());

		for (int rowIndex=0;rowIndex<touchCells.getRowCount();rowIndex++) {
			for (int columnIndex=0;columnIndex<touchCells.getColumnCount();columnIndex++) {
				TouchCell cell = touchCells.getCell(columnIndex, rowIndex);
				TouchCellPersist touchCellPersist = new TouchCellPersist();
				touchCellPersist.setRow(rowIndex);
				touchCellPersist.setColumn(columnIndex);
				touchCellPersist.setCharacters(cell.getCharacters());
				touchCellsPersist.getCell().add(touchCellPersist);
			}
		}

		CompositionPersist.setCells(touchCellsPersist);


		CompositionPersist.setStartChange(touch.getStartChange().getDisplayString(false));
		CompositionPersist.setStartRow(touch.getStartAtRow());
		CompositionPersist.setStartStroke(StrokePersist.fromValue(touch.getStartStroke().toString()));
		if (touch.getStartNotation().isPresent()) {
			CompositionPersist.setStartNotation((touch.getStartNotation().get().getRawNotationDisplayString(0, true)));
		}

		CompositionPersist.setTerminationMaxRows(touch.getTerminationMaxRows());
		if (touch.getTerminationMaxLeads().isPresent()) {
			CompositionPersist.setTerminationMaxLeads(touch.getTerminationMaxLeads().get());
		}
		if (touch.getTerminationMaxParts().isPresent()) {
			CompositionPersist.setTerminationMaxParts(touch.getTerminationMaxParts().get());
		}
		if (touch.getTerminationMaxCircularTouch().isPresent()) {
			CompositionPersist.setTerminationMaxCircularity(touch.getTerminationMaxCircularTouch().get());
		}
		if (touch.getTerminationChange().isPresent()) {
			CompositionPersist.setTerminationChange(touch.getTerminationChange().get().getDisplayString(false));
		}

		return CompositionPersist;
	}

	private NotationKeyPersist getNotationKeyPersist(NotationBody nonSplicedActiveNotation) {
		NotationKeyPersist notationKeyPersist = new ObjectFactory().createNotationKeyPersist();
		notationKeyPersist.setNumberOfWorkingBells(nonSplicedActiveNotation.getNumberOfWorkingBells().toInt());
		notationKeyPersist.setName(nonSplicedActiveNotation.getName());
		return notationKeyPersist;
	}

	private DefinitionPersist buildDefinitionPersist(TouchDefinition definition) {
		DefinitionPersist definitionPersist = new DefinitionPersist();
		definitionPersist.setCharacters(definition.getCharacters());
		definitionPersist.setShorthand(definition.getShorthand());
		return definitionPersist;
	}

	private TouchNotationPersist buildTouchNotationPersist(NotationBody notation) {
		TouchNotationPersist notationPersist = new TouchNotationPersist();
		notationPersist.setNumberOfWorkingBells(notation.getNumberOfWorkingBells().toInt());
		notationPersist.setName(notation.getName());
		notationPersist.setFoldedPalindrome(notation.isFoldedPalindrome());
		notationPersist.setNotation(notation.getRawNotationDisplayString(0, true));
		notationPersist.setNotation2(notation.getRawNotationDisplayString(1, true));

		if (notation.isCannedCalls()) {
			notationPersist.setUseCannedCalls(true);
		}
		else {
			Set<NotationCall> calls = notation.getCalls();
			for (NotationCall call : calls) {
				CallPersist callPersist = convertCall(call, (call == notation.getDefaultCall()));
				notationPersist.getCall().add(callPersist);
			}
		}
		notationPersist.setSplicedIdentifier(notation.getSpliceIdentifier());

		return notationPersist;
	}

	private CallPersist convertCall(NotationCall call, boolean defaultCall) {
		CallPersist callPersist = new CallPersist();
		callPersist.setName(call.getName());
		callPersist.setShorthand(call.getNameShorthand());
		callPersist.setNotation(call.getNotationDisplayString(true));
		callPersist.setDefault(defaultCall);

		return callPersist;
	}

	@VisibleForTesting
	protected Touch buildTouch(CompositionPersist CompositionPersist) {
		int rowCount = CompositionPersist.getCells().getRows();
		int columnCount = CompositionPersist.getCells().getColumns();
		NumberOfBells numberOfBells = NumberOfBells.valueOf(CompositionPersist.getNumberOfBells());
		Touch touch = TouchBuilder.getInstance(numberOfBells, columnCount, rowCount);

		touch.setTitle(CompositionPersist.getTitle());

		touch.setAuthor(CompositionPersist.getAuthor());

		touch.setTouchCheckingType(CheckingType.valueOf(CompositionPersist.getTouchChecking().toString()));

		touch.setCallFromBell(Bell.valueOf(CompositionPersist.getCallFrom() - 1));

		for (TouchNotationPersist CompositionPersistNotation : CompositionPersist.getNotation()) {
			NotationBody notation = buildNotation(CompositionPersistNotation);
			touch.addNotation(notation);
		}

		if (CompositionPersist.getNonSplicedActiveNotation() != null) {

			NotationKeyPersist nonSplicedActiveNotation = CompositionPersist.getNonSplicedActiveNotation();
			List<NotationBody> allNotations = touch.getAllNotations();
			for (NotationBody notation : allNotations) {
				if (notation.getName().equals(nonSplicedActiveNotation.getName()) &&
					(notation.getNumberOfWorkingBells().toInt() == nonSplicedActiveNotation.getNumberOfWorkingBells())) {
					touch.setNonSplicedActiveNotation(notation);
					break;
				}
			}
		}

		touch.setPlainLeadToken(CompositionPersist.getPlainLeadToken());

		for (DefinitionPersist definitionPersist : CompositionPersist.getDefinition()) {
			touch.addDefinition(definitionPersist.getShorthand(), definitionPersist.getCharacters());
		}

		for (TouchCellPersist touchCellPersist : CompositionPersist.getCells().getCell()) {
			int row = touchCellPersist.getRow();
			int column = touchCellPersist.getColumn();
			String characters = touchCellPersist.getCharacters();
			touch.addCharacters(column, row, characters);
		}

		touch.setStartChange(MethodBuilder.parse(touch.getNumberOfBells(), CompositionPersist.getStartChange()));
		touch.setStartAtRow(CompositionPersist.getStartRow());
		touch.setStartStroke(Stroke.valueOf(CompositionPersist.getStartStroke().name()));
		touch.setStartNotation(NotationBuilder.getInstance()
				.setNumberOfWorkingBells(touch.getNumberOfBells())
				.setUnfoldedNotationShorthand(Strings.nullToEmpty(CompositionPersist.getStartNotation()))
				.build());

		touch.setTerminationMaxRows(CompositionPersist.getTerminationMaxRows());
		if(CompositionPersist.getTerminationMaxLeads() != null) {
			touch.setTerminationMaxLeads(CompositionPersist.getTerminationMaxLeads());
		}
		if (CompositionPersist.getTerminationMaxParts() != null) {
			touch.setTerminationMaxParts(CompositionPersist.getTerminationMaxParts());
		}
		if (CompositionPersist.getTerminationMaxCircularity() != null) {
			touch.setTerminationMaxCircularTouch(CompositionPersist.getTerminationMaxCircularity());
		}
		if (CompositionPersist.getTerminationChange() != null) {
			touch.setTerminationChange(MethodBuilder.parse(touch.getNumberOfBells(), CompositionPersist.getTerminationChange()));
		}

		return touch;
	}

	private NotationBody buildNotation(TouchNotationPersist CompositionPersistNotation) {
		NotationBuilder notationBuilder = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.valueOf(CompositionPersistNotation.getNumberOfWorkingBells()))
				.setName(CompositionPersistNotation.getName());
		if (CompositionPersistNotation.isFoldedPalindrome()) {
			notationBuilder.setFoldedPalindromeNotationShorthand(CompositionPersistNotation.getNotation(), CompositionPersistNotation.getNotation2());
		}
		else {
			notationBuilder.setUnfoldedNotationShorthand(CompositionPersistNotation.getNotation());
		}

		if (CompositionPersistNotation.isUseCannedCalls() != null && CompositionPersistNotation.isUseCannedCalls()) {
			notationBuilder.setCannedCalls();
		}
		else {
			for (CallPersist callPersist : CompositionPersistNotation.getCall()) {
				notationBuilder.addCall(callPersist.getName(), callPersist.getShorthand(), callPersist.getNotation(), callPersist.isDefault());
			}
		}
		notationBuilder.setSpliceIdentifier(CompositionPersistNotation.getSplicedIdentifier());

		return notationBuilder.build();
	}
}