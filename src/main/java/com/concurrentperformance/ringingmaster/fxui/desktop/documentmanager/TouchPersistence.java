package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchDefinition;
import com.concurrentperformance.ringingmaster.persist.DocumentPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.CallPersist;
import com.concurrentperformance.ringingmaster.persist.generated.v1.DefinitionType;
import com.concurrentperformance.ringingmaster.persist.generated.v1.NotationKeyType;
import com.concurrentperformance.ringingmaster.persist.generated.v1.ObjectFactory;
import com.concurrentperformance.ringingmaster.persist.generated.v1.TouchCheckingType;
import com.concurrentperformance.ringingmaster.persist.generated.v1.TouchNotationType;
import com.concurrentperformance.ringingmaster.persist.generated.v1.TouchType;
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
public class TouchPersistence {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private DocumentPersist documentPersist = new DocumentPersist();

	public void persist(Path path, Touch touch) {

		TouchType touchType = convertToTouchType(touch);

		try {
			documentPersist.writeTouch(touchType, path);
		}
		catch (IOException e) {
			log.error("TODO", e);
		}
		catch (JAXBException e) {
			log.error("TODO", e);
		}
	}

	private TouchType convertToTouchType(Touch touch) { //TODO move into engine.
		TouchType touchType = new TouchType();

		touchType.setTitle(touch.getTitle());

		touchType.setAuthor(touch.getAuthor());

		touchType.setNumberOfBells(touch.getNumberOfBells().getBellCount());

		touchType.setTouchChecking(TouchCheckingType.fromValue(touch.getTouchCheckingType().toString()));

		touchType.setCallFrom(touch.getCallFromBell().getZeroBasedBell());

		if (touch.getNonSplicedActiveNotation() != null) {
			NotationBody nonSplicedActiveNotation = touch.getNonSplicedActiveNotation();
			NotationKeyType notationKeyType = new ObjectFactory().createNotationKeyType();
			notationKeyType.setName(nonSplicedActiveNotation.getName());
			notationKeyType.setNumberOfBells(nonSplicedActiveNotation.getNumberOfWorkingBells().getBellCount());
			touchType.setNonSplicedActiveNotation(notationKeyType);
		}

		touchType.setSpliced(touch.isSpliced());

		touchType.setPlainLeadToken(touch.getPlainLeadToken());

		Set<TouchDefinition> definitions = touch.getDefinitions();
		for (TouchDefinition definition : definitions) {
			DefinitionType definitionType = convertDefinition(definition);
			touchType.getDefinition().add(definitionType);
		}

		for (NotationBody notation : touch.getAllNotations()) {
			TouchNotationType notationType = convertNotation(notation);
			touchType.getNotation().add(notationType);
		}

		return touchType;
	}

	private DefinitionType convertDefinition(TouchDefinition definition) {
		DefinitionType definitionType = new DefinitionType();
		definitionType.setNotation(definition.getAsString());
		definitionType.setShorthand(definition.getShorthand());
		return definitionType;
	}

	private TouchNotationType convertNotation(NotationBody notation) {
		TouchNotationType notationType = new TouchNotationType();
		notationType.setName(notation.getName());
		notationType.setNumberOfBells(notation.getNumberOfWorkingBells().getBellCount());
		notationType.setFoldedPalindrome(notation.isFoldedPalindrome());
		notationType.setNotation(notation.getRawNotationDisplayString(0, true));
		notationType.setNotation2(notation.getRawNotationDisplayString(1, true));

		Set<NotationCall> calls = notation.getCalls();
		for (NotationCall call : calls) {
			CallPersist callPersist = convertCall(call);
			notationType.getCall().add(callPersist);
		}
		return notationType;
	}

	private CallPersist convertCall(NotationCall call) {
		CallPersist callPersist = new CallPersist();
		callPersist.setName(call.getName());
		callPersist.setShorthand(call.getNameShorthand());
		callPersist.setNotation(call.getNotationDisplayString(true));
		return callPersist;
	}
}
