package org.ringingmaster.ui.desktop.documentmanager;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.persist.generated.v1.CompositionPersist;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.LEAD_BASED;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class CompositionPersistenceTest {

    @Test
    public void canRebuildComposition() {
        CompositionPersistence CompositionPersistence = new CompositionPersistence();
        Composition originalComposition = createDummyComposition();

        CompositionPersist CompositionPersist = CompositionPersistence.buildCompositionPersist(originalComposition);
        Composition recreatedComposition = CompositionPersistence.buildComposition(CompositionPersist).get();

        assertEquals(originalComposition.toString(), recreatedComposition.toString());
    }


    private static Composition createDummyComposition() {

        MutableComposition composition = new MutableComposition();

        composition.setTitle("My Composition");
        composition.setAuthor("by Stephen");

        composition.setCompositionType(LEAD_BASED);
        composition.addNotation(buildPlainBobMinor());
        composition.addNotation(buildLittleBobMinor());
        composition.addNotation(buildPlainBobMinimus());
        composition.addNotation(buildPlainBobMajor());

        composition.addCharacters(TableType.MAIN_TABLE, 0, 0, "-s");
        composition.addCharacters(TableType.MAIN_TABLE, 0, 1, "s-");
        composition.addCharacters(TableType.MAIN_TABLE, 1, 0, "p 3*");

        composition.addDefinition("3*", "-s-");
        composition.addDefinition("tr", "sps");

        composition.setStartChange(MethodBuilder.parse(composition.get().getNumberOfBells(), "654321"));
        composition.setStartAtRow(10);
        composition.setStartNotation(NotationBuilder.getInstance()
                .setNumberOfWorkingBells(composition.get().getNumberOfBells())
                .setUnfoldedNotationShorthand("x12x34")
                .build());
        composition.setStartStroke(Stroke.HANDSTROKE);

        composition.setTerminationChange(MethodBuilder.parse(composition.get().getNumberOfBells(), "132546"));
        composition.setTerminationMaxRows(2345);
        composition.setTerminationMaxParts(123);
        composition.setTerminationMaxLeads(999);
        composition.setTerminationMaxCircularity(12);

        return composition.get();
    }

    private static Notation buildPlainBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Plain Bob")
                .setFoldedPalindromeNotationShorthand("-16-16-16", "12")
                .setCannedCalls()
                .setSpliceIdentifier("P")
                .build();
    }

    private static Notation buildLittleBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("-16-14", "12")
                .setCannedCalls()
                .build();
    }

    private static Notation buildPlainBobMinimus() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_4)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("-14-14", "12")
                .addCall("Steve", "S", "14", false)
                .addCall("Judith", "O", "1234", false)
                .build();
    }

    public static Notation buildPlainBobMajor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_8)
                .setName("Plain Bob")
                .setFoldedPalindromeNotationShorthand("-18-18-18-18", "12")
                .addCall("MyCall", "C", "145678", true)
                .addCall("OtherCall", "O", "1234", false)
                .setSpliceIdentifier("X")
                .build();
    }
}