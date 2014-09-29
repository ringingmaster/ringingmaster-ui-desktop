package com.concurrentperformance.ringingmaster.fxui.desktop.document;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;
import com.concurrentperformance.ringingmaster.engine.touch.impl.TouchBuilder;
import com.concurrentperformance.ringingmaster.fxui.desktop.document.definitiongrid.DefinitionGridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.document.maingrid.MainGridModel;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.grid.model.GridModel;
import com.concurrentperformance.ringingmaster.ui.common.TouchStyle;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides the interface between the engine {@code Touch} and the various
 * UI components.
 *
 * @author Lake
 */
public class TouchDocument {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Touch touch;
	private final TouchStyle touchStyle = new TouchStyle();

	private MainGridModel mainGridModel = new MainGridModel(this);
	private final List<GridModel> definitionModels = new ArrayList<>();

	private final ProofManager proofManager;

	private final JavaBeanStringProperty titleProperty;


	public TouchDocument(ProofManager proofManager) {
		this.proofManager = checkNotNull(proofManager);

		//TODO all this must be persisted.
		touch = createDummyTouch();
		parseAndProve();

		try {
			titleProperty = new JavaBeanStringPropertyBuilder().bean(touch).name("name").build();
		} catch (NoSuchMethodException e) {
			log.error("TODO", e);
			throw new RuntimeException(e);
		}

		configureDefinitionModels();
	}

	public JavaBeanStringProperty getTitleProperty() {
		return titleProperty;
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

	public Touch getTouch() {
		return touch;
	}

	public TouchStyle getTouchStyle() {
		return touchStyle;
	}

	public void parseAndProve() {
		proofManager.parseAndProve(touch);
	}

	//TODO remove this
	private static Touch createDummyTouch() {

		Touch touch = TouchBuilder.getInstance(NumberOfBells.BELLS_6, 2, 2);
		touch.setTouchType(TouchType.LEAD_BASED);
		touch.addNotation(buildPlainBobMinor());
		touch.setName("TEST NAME");
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
				.build();
	}
}
