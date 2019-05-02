package org.ringingmaster.ui.desktop.documentmanager;

import com.google.common.annotations.VisibleForTesting;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Call;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.persist.DocumentPersist;
import org.ringingmaster.persist.generated.v1.CallPersist;
import org.ringingmaster.persist.generated.v1.CompositionNotationPersist;
import org.ringingmaster.persist.generated.v1.CompositionPersist;
import org.ringingmaster.persist.generated.v1.NotationKeyPersist;
import org.ringingmaster.persist.generated.v1.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CompositionPersistence {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private DocumentPersist documentPersist = new DocumentPersist();

    public void save(Path path, Composition composition) {

        CompositionPersist CompositionPersist = buildCompositionPersist(composition);

        try {
            documentPersist.writeComposition(CompositionPersist, path);
        } catch (IOException e) {
            log.error("TODO", e);
        } catch (JAXBException e) {
            log.error("TODO", e);
        }
    }

    public MutableComposition load(Path path) {
        CompositionPersist CompositionPersist = documentPersist.readComposition(path);
        MutableComposition composition = buildComposition(CompositionPersist);
        return composition;
    }

    CompositionPersist buildCompositionPersist(Composition composition) {
        //TODO Reactive
//		CompositionPersist CompositionPersist = new CompositionPersist();
//
//		CompositionPersist.setNumberOfBells(composition.getNumberOfBells().toInt());
//
//		CompositionPersist.setTitle(composition.getTitle());
//
//		CompositionPersist.setAuthor(composition.getAuthor());
//
//		CompositionPersist.setCompositionChecking(CompositionCheckingPersist.fromValue(composition.getCompositionType().toString()));
//
//		CompositionPersist.setCallFrom(composition.getCallFromBell().getZeroBasedBell() + 1);
//
//		if (composition.getNonSplicedActiveNotation() != null) {
//			Notation nonSplicedActiveNotation = composition.getNonSplicedActiveNotation();
//			NotationKeyPersist notationKeyPersist = getNotationKeyPersist(nonSplicedActiveNotation);
//			CompositionPersist.setNonSplicedActiveNotation(notationKeyPersist);
//		}
//
//		CompositionPersist.setSpliced(composition.isSpliced());
//
//		CompositionPersist.setPlainLeadToken(composition.getPlainLeadToken());
//
//		Set<CompositionDefinition> definitions = composition.getDefinitions();
//		for (CompositionDefinition definition : definitions) {
//			DefinitionPersist definitionPersist = buildDefinitionPersist(definition);
//			CompositionPersist.getDefinition().add(definitionPersist);
//		}
//
//		for (Notation notation : composition.getAllNotations()) {
//			CompositionNotationPersist notationType = buildCompositionNotationPersist(notation);
//			CompositionPersist.getNotation().add(notationType);
//		}
//
//		Grid<CompositionCell> compositionCells = composition.allCellsView();
//
//		CompositionCellsPersist compositionCellsPersist = new CompositionCellsPersist();
//		compositionCellsPersist.setRows(compositionCells.getRowCount());
//		compositionCellsPersist.setColumns(compositionCells.getColumnCount());
//
//		for (int rowIndex=0;rowIndex<compositionCells.getRowCount();rowIndex++) {
//			for (int columnIndex=0;columnIndex<compositionCells.getColumnCount();columnIndex++) {
//				CompositionCell cell = compositionCells.getCell(columnIndex, rowIndex);
//				CompositionCellPersist compositionCellPersist = new CompositionCellPersist();
//				compositionCellPersist.setRow(rowIndex);
//				compositionCellPersist.setColumn(columnIndex);
//				compositionCellPersist.setCharacters(cell.getCharacters());
//				compositionCellsPersist.getCell().add(compositionCellPersist);
//			}
//		}
//
//		CompositionPersist.setCells(compositionCellsPersist);
//
//
//		CompositionPersist.setStartChange(composition.getStartChange().getDisplayString(false));
//		CompositionPersist.setStartRow(composition.getStartAtRow());
//		CompositionPersist.setStartStroke(StrokePersist.fromValue(composition.getStartStroke().toString()));
//		if (composition.getStartNotation().isPresent()) {
//			CompositionPersist.setStartNotation((composition.getStartNotation().get().getRawNotationDisplayString(0, true)));
//		}
//
//		CompositionPersist.setTerminationMaxRows(composition.getTerminationMaxRows());
//		if (composition.getTerminationMaxLeads().isPresent()) {
//			CompositionPersist.setTerminationMaxLeads(composition.getTerminationMaxLeads().get());
//		}
//		if (composition.getTerminationMaxParts().isPresent()) {
//			CompositionPersist.setTerminationMaxParts(composition.getTerminationMaxParts().get());
//		}
//		if (composition.getTerminationMaxCircularComposition().isPresent()) {
//			CompositionPersist.setTerminationMaxCircularity(composition.getTerminationMaxCircularComposition().get());
//		}
//		if (composition.getTerminationChange().isPresent()) {
//			CompositionPersist.setTerminationChange(composition.getTerminationChange().get().getDisplayString(false));
//		}
//
//		return CompositionPersist;
        return null;
    }

    private NotationKeyPersist getNotationKeyPersist(Notation nonSplicedActiveNotation) {
        NotationKeyPersist notationKeyPersist = new ObjectFactory().createNotationKeyPersist();
        notationKeyPersist.setNumberOfWorkingBells(nonSplicedActiveNotation.getNumberOfWorkingBells().toInt());
        notationKeyPersist.setName(nonSplicedActiveNotation.getName());
        return notationKeyPersist;
    }

    //TODO Reactive
//	 private DefinitionPersist buildDefinitionPersist(Definition definition) {
//		DefinitionPersist definitionPersist = new DefinitionPersist();
//		definitionPersist.setCharacters(definition.getCharacters());
//		definitionPersist.setShorthand(definition.getShorthand());
//		return definitionPersist;
//	}

    private CompositionNotationPersist buildCompositionNotationPersist(Notation notation) {
        CompositionNotationPersist notationPersist = new CompositionNotationPersist();
        notationPersist.setNumberOfWorkingBells(notation.getNumberOfWorkingBells().toInt());
        notationPersist.setName(notation.getName());
        notationPersist.setFoldedPalindrome(notation.isFoldedPalindrome());
        notationPersist.setNotation(notation.getRawNotationDisplayString(0, true));
        notationPersist.setNotation2(notation.getRawNotationDisplayString(1, true));

        if (notation.isCannedCalls()) {
            notationPersist.setUseCannedCalls(true);
        } else {
            Set<Call> calls = notation.getCalls();
            for (Call call : calls) {
                CallPersist callPersist = convertCall(call, (call == notation.getDefaultCall()));
                notationPersist.getCall().add(callPersist);
            }
        }
        notationPersist.setSplicedIdentifier(notation.getSpliceIdentifier());

        return notationPersist;
    }

    private CallPersist convertCall(Call call, boolean defaultCall) {
        CallPersist callPersist = new CallPersist();
        callPersist.setName(call.getName());
        callPersist.setShorthand(call.getNameShorthand());
        callPersist.setNotation(call.getNotationDisplayString(true));
        callPersist.setDefault(defaultCall);

        return callPersist;
    }

    @VisibleForTesting
    protected MutableComposition buildComposition(CompositionPersist CompositionPersist) {
//TODO Reactive
// 		int rowCount = CompositionPersist.getCells().getRows();
//		int columnCount = CompositionPersist.getCells().getColumns();
//		NumberOfBells numberOfBells = NumberOfBells.valueOf(CompositionPersist.getNumberOfBells());
//		Composition composition = CompositionBuilder.getInstance(numberOfBells, columnCount, rowCount);
//
//		composition.setTitle(CompositionPersist.getTitle());
//
//		composition.setAuthor(CompositionPersist.getAuthor());
//
//		composition.setCompositionCompositionType(CompositionType.valueOf(CompositionPersist.getCompositionChecking().toString()));
//
//		composition.setCallFromBell(Bell.valueOf(CompositionPersist.getCallFrom() - 1));
//
//		for (CompositionNotationPersist CompositionPersistNotation : CompositionPersist.getNotation()) {
//			Notation notation = buildNotation(CompositionPersistNotation);
//			composition.addNotation(notation);
//		}
//
//		if (CompositionPersist.getNonSplicedActiveNotation() != null) {
//
//			NotationKeyPersist nonSplicedActiveNotation = CompositionPersist.getNonSplicedActiveNotation();
//			List<Notation> allNotations = composition.getAllNotations();
//			for (Notation notation : allNotations) {
//				if (notation.getName().equals(nonSplicedActiveNotation.getName()) &&
//					(notation.getNumberOfWorkingBells().toInt() == nonSplicedActiveNotation.getNumberOfWorkingBells())) {
//					composition.setNonSplicedActiveNotation(notation);
//					break;
//				}
//			}
//		}
//
//		composition.setPlainLeadToken(CompositionPersist.getPlainLeadToken());
//
//		for (DefinitionPersist definitionPersist : CompositionPersist.getDefinition()) {
//			composition.addDefinition(definitionPersist.getShorthand(), definitionPersist.getCharacters());
//		}
//
//		for (CompositionCellPersist compositionCellPersist : CompositionPersist.getCells().getCell()) {
//			int row = compositionCellPersist.getRow();
//			int column = compositionCellPersist.getColumn();
//			String characters = compositionCellPersist.getCharacters();
//			composition.addCharacters(column, row, characters);
//		}
//
//		composition.setStartChange(MethodBuilder.parse(composition.getNumberOfBells(), CompositionPersist.getStartChange()));
//		composition.setStartAtRow(CompositionPersist.getStartRow());
//		composition.setStartStroke(Stroke.valueOf(CompositionPersist.getStartStroke().name()));
//		composition.setStartNotation(NotationBuilder.getInstance()
//				.setNumberOfWorkingBells(composition.getNumberOfBells())
//				.setUnfoldedNotationShorthand(Strings.nullToEmpty(CompositionPersist.getStartNotation()))
//				.build());
//
//		composition.setTerminationMaxRows(CompositionPersist.getTerminationMaxRows());
//		if(CompositionPersist.getTerminationMaxLeads() != null) {
//			composition.setTerminationMaxLeads(CompositionPersist.getTerminationMaxLeads());
//		}
//		if (CompositionPersist.getTerminationMaxParts() != null) {
//			composition.setTerminationMaxParts(CompositionPersist.getTerminationMaxParts());
//		}
//		if (CompositionPersist.getTerminationMaxCircularity() != null) {
//			composition.setTerminationMaxCircularity(CompositionPersist.getTerminationMaxCircularity());
//		}
//		if (CompositionPersist.getTerminationChange() != null) {
//			composition.setTerminationChange(MethodBuilder.parse(composition.getNumberOfBells(), CompositionPersist.getTerminationChange()));
//		}
//
//		return composition;
        return null;
    }

    private Notation buildNotation(CompositionNotationPersist CompositionPersistNotation) {
        NotationBuilder notationBuilder = NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.valueOf(CompositionPersistNotation.getNumberOfWorkingBells()))
                .setName(CompositionPersistNotation.getName());
        if (CompositionPersistNotation.isFoldedPalindrome()) {
            notationBuilder.setFoldedPalindromeNotationShorthand(CompositionPersistNotation.getNotation(), CompositionPersistNotation.getNotation2());
        } else {
            notationBuilder.setUnfoldedNotationShorthand(CompositionPersistNotation.getNotation());
        }

        if (CompositionPersistNotation.isUseCannedCalls() != null && CompositionPersistNotation.isUseCannedCalls()) {
            notationBuilder.setCannedCalls();
        } else {
            for (CallPersist callPersist : CompositionPersistNotation.getCall()) {
                notationBuilder.addCall(callPersist.getName(), callPersist.getShorthand(), callPersist.getNotation(), callPersist.isDefault());
            }
        }
        notationBuilder.setSpliceIdentifier(CompositionPersistNotation.getSplicedIdentifier());

        return notationBuilder.build();
    }
}