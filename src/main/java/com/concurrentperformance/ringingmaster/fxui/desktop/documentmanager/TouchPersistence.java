package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.touch.container.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCheckingType;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.container.impl.TouchBuilder;
import com.concurrentperformance.ringingmaster.persist.DocumentPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.CallPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.DefinitionPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.NotationKeyPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.ObjectFactory;
import com.concurrentperformance.ringingmaster.persist.generated.v1.StrokePersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.TouchCellPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.TouchCellsPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.TouchCheckingPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.TouchNotationPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.TouchPersist;
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
public class TouchPersistence {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private DocumentPersist documentPersist = new DocumentPersist();

	public void persist(Path path, Touch touch) {

		TouchPersist touchPersist = buildTouchPersist(touch);

		try {
			documentPersist.writeTouch(touchPersist, path);
		} catch (IOException e) {
			log.error("TODO", e);
		} catch (JAXBException e) {
			log.error("TODO", e);
		}
	}

	TouchPersist buildTouchPersist(Touch touch) {
		TouchPersist touchPersist = new TouchPersist();

		touchPersist.setNumberOfBells(touch.getNumberOfBells().getBellCount());

		touchPersist.setTitle(touch.getTitle());

		touchPersist.setAuthor(touch.getAuthor());

		touchPersist.setTouchChecking(TouchCheckingPersist.fromValue(touch.getTouchCheckingType().toString()));

		touchPersist.setCallFrom(touch.getCallFromBell().getZeroBasedBell() + 1);

		if (touch.getNonSplicedActiveNotation() != null) {
			NotationBody nonSplicedActiveNotation = touch.getNonSplicedActiveNotation();
			NotationKeyPersist notationKeyPersist = getNotationKeyPersist(nonSplicedActiveNotation);
			touchPersist.setNonSplicedActiveNotation(notationKeyPersist);
		}

		touchPersist.setSpliced(touch.isSpliced());

		touchPersist.setPlainLeadToken(touch.getPlainLeadToken());

		Set<TouchDefinition> definitions = touch.getDefinitions();
		for (TouchDefinition definition : definitions) {
			DefinitionPersist definitionPersist = buildDefinitionPersist(definition);
			touchPersist.getDefinition().add(definitionPersist);
		}

		for (NotationBody notation : touch.getAllNotations()) {
			TouchNotationPersist notationType = buildTouchNotationPersist(notation);
			touchPersist.getNotation().add(notationType);
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

		touchPersist.setCells(touchCellsPersist);


		touchPersist.setStartChange(touch.getStartChange().getDisplayString(false));
		touchPersist.setStartRow(touch.getStartAtRow());
		touchPersist.setStartStroke(StrokePersist.fromValue(touch.getStartStroke().toString()));
		if (touch.getStartNotation().isPresent()) {
			touchPersist.setStartNotation((touch.getStartNotation().get().getRawNotationDisplayString(0, true)));
		}

		touchPersist.setTerminationMaxRows(touch.getTerminationMaxRows());
		if (touch.getTerminationMaxLeads().isPresent()) {
			touchPersist.setTerminationMaxLeads(touch.getTerminationMaxLeads().get());
		}
		if (touch.getTerminationMaxParts().isPresent()) {
			touchPersist.setTerminationMaxParts(touch.getTerminationMaxParts().get());
		}
		if (touch.getTerminationMaxCircularTouch().isPresent()) {
			touchPersist.setTerminationMaxCircularTouch(touch.getTerminationMaxCircularTouch().get());
		}
		if (touch.getTerminationChange().isPresent()) {
			touchPersist.setTerminationChange(touch.getTerminationChange().get().getDisplayString(false));
		}

		return touchPersist;
	}

	private NotationKeyPersist getNotationKeyPersist(NotationBody nonSplicedActiveNotation) {
		NotationKeyPersist notationKeyPersist = new ObjectFactory().createNotationKeyPersist();
		notationKeyPersist.setNumberOfWorkingBells(nonSplicedActiveNotation.getNumberOfWorkingBells().getBellCount());
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
		notationPersist.setNumberOfWorkingBells(notation.getNumberOfWorkingBells().getBellCount());
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


	Touch buildTouch(TouchPersist touchPersist) {
		int rowCount = touchPersist.getCells().getRows();
		int columnCount = touchPersist.getCells().getColumns();
		NumberOfBells numberOfBells = NumberOfBells.valueOf(touchPersist.getNumberOfBells());
		Touch touch = TouchBuilder.getInstance(numberOfBells, columnCount, rowCount);

		touch.setTitle(touchPersist.getTitle());

		touch.setAuthor(touchPersist.getAuthor());

		touch.setTouchCheckingType(TouchCheckingType.valueOf(touchPersist.getTouchChecking().toString()));

		touch.setCallFromBell(Bell.valueOf(touchPersist.getCallFrom() - 1));

		for (TouchNotationPersist touchPersistNotation : touchPersist.getNotation()) {
			NotationBody notation = buildNotation(touchPersistNotation);
			touch.addNotation(notation);
		}

		if (touchPersist.getNonSplicedActiveNotation() != null) {

			NotationKeyPersist nonSplicedActiveNotation = touchPersist.getNonSplicedActiveNotation();
			List<NotationBody> allNotations = touch.getAllNotations();
			for (NotationBody notation : allNotations) {
				if (notation.getName().equals(nonSplicedActiveNotation.getName()) &&
					(notation.getNumberOfWorkingBells().getBellCount() == nonSplicedActiveNotation.getNumberOfWorkingBells())) {
					touch.setNonSplicedActiveNotation(notation);
					break;
				}
			}
		}

		touch.setPlainLeadToken(touchPersist.getPlainLeadToken());

		for (DefinitionPersist definitionPersist : touchPersist.getDefinition()) {
			touch.addDefinition(definitionPersist.getShorthand(), definitionPersist.getCharacters());
		}

		for (TouchCellPersist touchCellPersist : touchPersist.getCells().getCell()) {
			int row = touchCellPersist.getRow();
			int column = touchCellPersist.getColumn();
			String characters = touchCellPersist.getCharacters();
			touch.addCharacters(column, row, characters);
		}

		touch.setStartChange(MethodBuilder.parse(touch.getNumberOfBells(), touchPersist.getStartChange()));
		touch.setStartAtRow(touchPersist.getStartRow());
		touch.setStartStroke(Stroke.valueOf(touchPersist.getStartStroke().name()));
		touch.setStartNotation(NotationBuilder.getInstance()
				.setNumberOfWorkingBells(touch.getNumberOfBells())
				.setUnfoldedNotationShorthand(touchPersist.getStartNotation())
				.build());

		touch.setTerminationMaxRows(touchPersist.getTerminationMaxRows());
		touch.setTerminationMaxLeads(touchPersist.getTerminationMaxLeads());
		touch.setTerminationMaxParts(touchPersist.getTerminationMaxParts());
		touch.setTerminationMaxCircularTouch(touchPersist.getTerminationMaxCircularTouch());
		touch.setTerminationChange(MethodBuilder.parse(touch.getNumberOfBells(), touchPersist.getTerminationChange()));

		return touch;
	}

	private NotationBody buildNotation(TouchNotationPersist touchPersistNotation) {
		NotationBuilder notationBuilder = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.valueOf(touchPersistNotation.getNumberOfWorkingBells()))
				.setName(touchPersistNotation.getName());
		if (touchPersistNotation.isFoldedPalindrome()) {
			notationBuilder.setFoldedPalindromeNotationShorthand(touchPersistNotation.getNotation(), touchPersistNotation.getNotation2());
		}
		else {
			notationBuilder.setUnfoldedNotationShorthand(touchPersistNotation.getNotation());
		}

		if (touchPersistNotation.isUseCannedCalls() != null && touchPersistNotation.isUseCannedCalls()) {
			notationBuilder.setCannedCalls();
		}
		else {
			for (CallPersist callPersist : touchPersistNotation.getCall()) {
				notationBuilder.addCall(callPersist.getName(), callPersist.getShorthand(), callPersist.getNotation(), callPersist.isDefault());
			}
		}
		notationBuilder.setSpliceIdentifier(touchPersistNotation.getSplicedIdentifier());

		return notationBuilder.build();
	}
}