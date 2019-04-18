package org.ringingmaster.ui.desktop.documentmanager;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.ringingmaster.engine.touch.container.impl.TouchBuilder;
import org.ringingmaster.persist.generated.v1.CompositionPersist;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CompositionPersistenceTest {

	@Test
	public void canRebuildTouch() {
		CompositionPersistence CompositionPersistence = new CompositionPersistence();
		Touch originalTouch = createDummyTouch();

		CompositionPersist CompositionPersist = CompositionPersistence.buildCompositionPersist(originalTouch);
		Touch recreatedTouch = CompositionPersistence.buildTouch(CompositionPersist);

		assertEquals(originalTouch.toString(), recreatedTouch.toString());
	}


	private static Touch createDummyTouch() {

		Touch touch = TouchBuilder.getInstance(NumberOfBells.BELLS_6, 2, 2);

		touch.setTitle("My Touch");
		touch.setAuthor("by Stephen");

		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
		touch.addNotation(buildPlainBobMinor());
		touch.addNotation(buildLittleBobMinor());
		touch.addNotation(buildPlainBobMinimus());
		touch.addNotation(buildPlainBobMajor());

		touch.addCharacters(0, 0, "-s");
		touch.addCharacters(0, 1, "s-");
		touch.addCharacters(1, 0 ,"p 3*");

		touch.addDefinition("3*", "-s-");
		touch.addDefinition("tr", "sps");

		touch.setStartChange(MethodBuilder.parse(touch.getNumberOfBells(),"654321"));
		touch.setStartAtRow(10);
		touch.setStartNotation(NotationBuilder.getInstance()
				.setNumberOfWorkingBells(touch.getNumberOfBells())
				.setUnfoldedNotationShorthand("x12x34")
				.build());
		touch.setStartStroke(Stroke.HANDSTROKE);

		touch.setTerminationChange(MethodBuilder.parse(touch.getNumberOfBells(), "132546"));
		touch.setTerminationMaxRows(2345);
		touch.setTerminationMaxParts(123);
		touch.setTerminationMaxLeads(999);
		touch.setTerminationMaxCircularTouch(12);

		return touch;
	}

	private static NotationBody buildPlainBobMinor() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Plain Bob")
				.setFoldedPalindromeNotationShorthand("-16-16-16", "12")
				.setCannedCalls()
				.setSpliceIdentifier("P")
				.build();
	}

	private static NotationBody buildLittleBobMinor() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Little Bob")
				.setFoldedPalindromeNotationShorthand("-16-14", "12")
				.setCannedCalls()
				.build();
	}

	private static NotationBody buildPlainBobMinimus() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_4)
				.setName("Little Bob")
				.setFoldedPalindromeNotationShorthand("-14-14", "12")
				.addCall("Steve", "S", "14", false)
				.addCall("Judith", "O", "1234", false)
				.build();
	}

	public static NotationBody buildPlainBobMajor() {
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