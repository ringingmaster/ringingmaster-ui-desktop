package com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCheckingType;
import com.concurrentperformance.ringingmaster.engine.touch.container.impl.TouchBuilder;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.util.beanfactory.BeanFactory;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Manages crating and switching of documents.
 *
 * @author Lake
 */
public class DocumentManager extends ConcurrentListenable<DocumentManagerListener> implements Listenable<DocumentManagerListener> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Optional<TouchDocument> currentDocument = Optional.empty();
	private TabPane documentWindow;
	private int docNumber = 0;

	private ProofManager proofManager;
	private BeanFactory beanFactory;
	private TouchPersistence touchPersistence = new TouchPersistence();

	public void init() {
		documentWindow.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		documentWindow.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			log.info("Tab " + newValue);
			if (newValue == null) {
				currentDocument = Optional.empty();
				proofManager.parseAndProve(null);
			}
			else {
				TouchDocument touchDocument = (TouchDocument) newValue.getContent();
				touchDocument.parseAndProve();
				currentDocument = Optional.of(touchDocument);
			}
			fireUpdateDocument();
		});
	}

	public void buildNewDocument() {
		final TouchDocument touchDocument = beanFactory.build(TouchDocument.class);
		touchDocument.init(createDummyTouch());
		// this is needed to listn to document updates that are not changed with proof's.
		// example -> alter the start change to something that will fail, and this will
		// allow it to be put back to its original value.
		touchDocument.addListener(touchDoc -> fireUpdateDocument());
		createTab("New Touch #" + ++docNumber, touchDocument);

		currentDocument = Optional.of(touchDocument);
	}

	public void saveCurrentDocument() {
		if (currentDocument.isPresent()) {
			Touch touch = currentDocument.get().getTouch();
			Path path = Paths.get("Temp.touch"); //TODO Need to start a file dialog.
			touchPersistence.persist(path, touch);
		}
	}

	private void createTab(String name, Node node) {
		Tab tab = new Tab();
		tab.setText(name);
		tab.setContent(node);
		documentWindow.getTabs().add(tab);
	}

	private void fireUpdateDocument() {
		for (DocumentManagerListener listener : getListeners()) {
			listener.documentManager_updateDocument(currentDocument);
		}
	}

	public Optional<TouchDocument> getCurrentDocument() {
		return currentDocument;
	}

	public void setDocumentWindow(TabPane documentWindow) {
		this.documentWindow = documentWindow;
	}

	public void setProofManager(ProofManager proofManager) {
		this.proofManager = proofManager;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	//TODO remove this
	private static Touch createDummyTouch() {

		Touch touch = TouchBuilder.getInstance(NumberOfBells.BELLS_6, 2, 2);

		touch.setTitle("My Touch");
		touch.setAuthor("by Stephen");

		touch.setTouchCheckingType(TouchCheckingType.LEAD_BASED);
		touch.addNotation(buildPlainBobMinor());
		touch.addNotation(buildLittleBobMinor());
		touch.addNotation(buildPlainBobMinimus());
		touch.addNotation(buildPlainBobMajor());


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

	// TODO remove this
	private static NotationBody buildPlainBobMinor() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Plain Bob")
				.setFoldedPalindromeNotationShorthand("-16-16-16", "12")
				.setCannedCalls()
				.setSpliceIdentifier("P")
				.build();
	}

	// TODO remove this
	private static NotationBody buildLittleBobMinor() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Little Bob")
				.setFoldedPalindromeNotationShorthand("-16-14", "12")
				.setCannedCalls()
				.build();
	}

	// TODO remove this
	private static NotationBody buildPlainBobMinimus() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_4)
				.setName("Little Bob")
				.setFoldedPalindromeNotationShorthand("-14-14", "12")
				.setCannedCalls()
				.build();
	}

	// TODO remove this
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
