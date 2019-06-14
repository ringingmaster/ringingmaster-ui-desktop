package org.ringingmaster.ui.desktop.compositiondocument;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.composition.cell.Cell;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Call;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.persist.DocumentPersist;
import org.ringingmaster.persist.generated.v1.CallPersist;
import org.ringingmaster.persist.generated.v1.CallingPositionPersist;
import org.ringingmaster.persist.generated.v1.CellTablePersist;
import org.ringingmaster.persist.generated.v1.CellsTablePersist;
import org.ringingmaster.persist.generated.v1.CompositionNotationPersist;
import org.ringingmaster.persist.generated.v1.CompositionPersist;
import org.ringingmaster.persist.generated.v1.CompositionTypePersist;
import org.ringingmaster.persist.generated.v1.NotationKeyPersist;
import org.ringingmaster.persist.generated.v1.ObjectFactory;
import org.ringingmaster.persist.generated.v1.StrokePersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;
import static org.ringingmaster.engine.composition.TableType.DEFINITION_TABLE;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class CompositionPersistence {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private DocumentPersist documentPersist = new DocumentPersist();

    public void save(Path path, Composition composition) {

        CompositionPersist compositionPersist = buildCompositionPersist(composition);

        try {
            documentPersist.writeComposition(compositionPersist, path);
        } catch (IOException | JAXBException e) {
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
        CompositionPersist compositionPersist = new CompositionPersist();

        compositionPersist.setNumberOfBells(composition.getNumberOfBells().toInt());

        compositionPersist.setTitle(composition.getTitle());

        compositionPersist.setAuthor(composition.getAuthor());

        compositionPersist.setCompositionType(CompositionTypePersist.fromValue(composition.getCompositionType().toString()));

        compositionPersist.setCallFrom(composition.getCallFromBell().getZeroBasedBell() + 1);

        if (composition.getNonSplicedActiveNotation().isPresent()) {
            Notation nonSplicedActiveNotation = composition.getNonSplicedActiveNotation().get();
            NotationKeyPersist notationKeyPersist = buildNotationKeyPersist(nonSplicedActiveNotation);
            compositionPersist.setNonSplicedActiveNotation(notationKeyPersist);
        }

        compositionPersist.setSpliced(composition.isSpliced());

        compositionPersist.setPlainLeadToken(composition.getPlainLeadToken());

        for (Notation notation : composition.getAllNotations()) {
            CompositionNotationPersist notationType = buildCompositionNotationPersist(notation);
            compositionPersist.getNotation().add(notationType);
        }

        ImmutableArrayTable<Cell> compositionCells = composition.allCompositionCells();
        CellsTablePersist compositionCellsTablePersist = buildCellsPersist(compositionCells);
        compositionPersist.setCompositionTable(compositionCellsTablePersist);

        ImmutableArrayTable<Cell> definitionsCells = composition.allDefinitionCells();
        CellsTablePersist definitionCellsTablePersist = buildCellsPersist(definitionsCells);
        compositionPersist.setDefinitionTable(definitionCellsTablePersist);

        // Start
        compositionPersist.setStartChange(composition.getStartChange().getDisplayString(false));
        compositionPersist.setStartRow(composition.getStartAtRow());
        compositionPersist.setStartStroke(StrokePersist.fromValue(composition.getStartStroke().toString()));
        if (composition.getStartNotation().isPresent()) {
            compositionPersist.setStartNotation((composition.getStartNotation().get().getRawNotationDisplayString(0, true)));
        }

        //Termination
        compositionPersist.setTerminationMaxRows(composition.getTerminationMaxRows());

        if (composition.getTerminationMaxLeads().isPresent()) {
            compositionPersist.setTerminationMaxLeads(composition.getTerminationMaxLeads().get());
        }

        if (composition.getTerminationMaxParts().isPresent()) {
            compositionPersist.setTerminationMaxParts(composition.getTerminationMaxParts().get());
        }

        compositionPersist.setTerminationMaxCircularity(composition.getTerminationMaxCircularity());

        if (composition.getTerminationChange().isPresent()) {
            compositionPersist.setTerminationChange(composition.getTerminationChange().get().getDisplayString(false));
        }

        return compositionPersist;
    }

    private CellsTablePersist buildCellsPersist(ImmutableArrayTable<Cell> compositionCells) {
        CellsTablePersist compositionCellsPersist = new CellsTablePersist();

        for (BackingTableLocationAndValue<Cell> cell : compositionCells) {
            String characters = cell.getValue().getCharacters();
            if (characters.length() > 0) {
                CellTablePersist cellPersist = new CellTablePersist();
                cellPersist.setRow(cell.getRow());
                cellPersist.setColumn(cell.getCol());
                cellPersist.setCharacters(characters);
                compositionCellsPersist.getCells().add(cellPersist);
            }
        }
        return compositionCellsPersist;
    }

    private NotationKeyPersist buildNotationKeyPersist(Notation nonSplicedActiveNotation) {
        NotationKeyPersist notationKeyPersist = new ObjectFactory().createNotationKeyPersist();
        notationKeyPersist.setNumberOfWorkingBells(nonSplicedActiveNotation.getNumberOfWorkingBells().toInt());
        notationKeyPersist.setName(nonSplicedActiveNotation.getName());
        return notationKeyPersist;
    }

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
                CallPersist callPersist = buildCallPersist(call, (call == notation.getDefaultCall()));
                notationPersist.getCall().add(callPersist);
            }
        }
        notationPersist.getCallInitiationRows().addAll(notation.getCallInitiationRows());
        notationPersist.getCallingPositions().addAll(
                notation.getCallingPositions().stream().map(c -> {
                    CallingPositionPersist callingPositionPersist = new CallingPositionPersist();
                    callingPositionPersist.setName(c.getName());
                    callingPositionPersist.setCallInitiationRow(c.getCallInitiationRow());
                    callingPositionPersist.setLeadOfTenor(c.getLeadOfTenor());
                    return callingPositionPersist;
                }).collect(Collectors.toSet())
        );

        notationPersist.setSplicedIdentifier(notation.getSpliceIdentifier());

        return notationPersist;
    }

    private CallPersist buildCallPersist(Call call, boolean defaultCall) {
        CallPersist callPersist = new CallPersist();
        callPersist.setName(call.getName());
        callPersist.setShorthand(call.getNameShorthand());
        callPersist.setNotation(call.getNotationDisplayString(true));
        callPersist.setDefault(defaultCall);

        return callPersist;
    }

    @VisibleForTesting
    protected MutableComposition buildComposition(CompositionPersist compositionPersist) {
        MutableComposition composition = new MutableComposition();

        NumberOfBells numberOfBells = NumberOfBells.valueOf(compositionPersist.getNumberOfBells());

        composition.setTitle(compositionPersist.getTitle());

        composition.setAuthor(compositionPersist.getAuthor());

        composition.setNumberOfBells(numberOfBells);

        composition.setCompositionType(CompositionType.valueOf(compositionPersist.getCompositionType().toString()));

        composition.setCallFromBell(Bell.valueOf(compositionPersist.getCallFrom() - 1));

        for (CompositionNotationPersist CompositionPersistNotation : compositionPersist.getNotation()) {
            Notation notation = buildNotation(CompositionPersistNotation);
            composition.addNotation(notation);
        }

        if (compositionPersist.getNonSplicedActiveNotation() != null) {

            NotationKeyPersist nonSplicedActiveNotation = compositionPersist.getNonSplicedActiveNotation();
            NumberOfBells notationNumberOfBells = NumberOfBells.valueOf(nonSplicedActiveNotation.getNumberOfWorkingBells());

            Set<Notation> allNotations = composition.get().getAllNotations();
            for (Notation notation : allNotations) {
                if (notation.getName().equals(nonSplicedActiveNotation.getName()) &&
                        (notation.getNumberOfWorkingBells() == notationNumberOfBells)) {
                    composition.setNonSplicedActiveNotation(notation);
                    break;
                }
            }
        }

        composition.setPlainLeadToken(compositionPersist.getPlainLeadToken());

        buildCells(composition, COMPOSITION_TABLE, compositionPersist.getCompositionTable());
        buildCells(composition, DEFINITION_TABLE, compositionPersist.getDefinitionTable());

        composition.setStartChange(MethodBuilder.parse(numberOfBells, compositionPersist.getStartChange()));
        composition.setStartAtRow(compositionPersist.getStartRow());
        composition.setStartStroke(Stroke.valueOf(compositionPersist.getStartStroke().name()));
        composition.setStartNotation(NotationBuilder.getInstance()
                .setNumberOfWorkingBells(numberOfBells)
                .setUnfoldedNotationShorthand(Strings.nullToEmpty(compositionPersist.getStartNotation()))
                .build());

        composition.setTerminationMaxRows(compositionPersist.getTerminationMaxRows());
        if (compositionPersist.getTerminationMaxLeads() != null) {
            composition.setTerminationMaxLeads(compositionPersist.getTerminationMaxLeads());
        }
        if (compositionPersist.getTerminationMaxParts() != null) {
            composition.setTerminationMaxParts(compositionPersist.getTerminationMaxParts());
        }
        if (compositionPersist.getTerminationMaxCircularity() != null) {
            composition.setTerminationMaxCircularity(compositionPersist.getTerminationMaxCircularity());
        }
        if (compositionPersist.getTerminationChange() != null) {
            composition.setTerminationChange(MethodBuilder.parse(numberOfBells, compositionPersist.getTerminationChange()));
        }

        return composition;
    }

    private void buildCells(MutableComposition composition, TableType tableType, CellsTablePersist table) {

        Set<BackingTableLocationAndValue<String>> cells = table.getCells().stream()
                .map(cell -> {
                    int row = cell.getRow();
                    int column = cell.getColumn();
                    String characters = cell.getCharacters();
                    return new BackingTableLocationAndValue<>(characters, row, column);
                })
                .collect(Collectors.toSet());

        composition.bulkSetCharacters(tableType, cells);
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
        for (Integer callInitiationRow : CompositionPersistNotation.getCallInitiationRows()) {
            notationBuilder.addCallInitiationRow(callInitiationRow);
        }
        for (CallingPositionPersist callingPosition : CompositionPersistNotation.getCallingPositions()) {
            notationBuilder.addMethodCallingPosition(callingPosition.getName(), callingPosition.getCallInitiationRow(), callingPosition.getLeadOfTenor());
        }
        for (Integer callInitiationRow : CompositionPersistNotation.getCallInitiationRows()) {
            notationBuilder.addCallInitiationRow(callInitiationRow);
        }
        notationBuilder.setSpliceIdentifier(CompositionPersistNotation.getSplicedIdentifier());

        return notationBuilder.build();
    }
}