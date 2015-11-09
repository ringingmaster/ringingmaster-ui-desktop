package com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCheckingType;
import com.concurrentperformance.ringingmaster.engine.touch.container.impl.TouchBuilder;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.Document;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentTypeManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.TouchPersistence;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.util.beanfactory.BeanFactory;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import com.google.common.collect.Lists;
import javafx.stage.FileChooser;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TouchDocumentTypeManager extends ConcurrentListenable<TouchDocumentTypeListener> implements DocumentTypeManager, Listenable<TouchDocumentTypeListener> {

	public static final String DOCUMENT_TYPE_NAME = "Touch";
	private BeanFactory beanFactory;
	private ProofManager proofManager;
	private TouchPersistence touchPersistence = new TouchPersistence();
	private Optional<TouchDocument> currentDocument = Optional.empty();

	private int docNumber = 0;

	@Override
	public Document createNewDocument() {
		Touch touch = createEmptyTouch();
		final TouchDocument touchDocument = buildTouchDocumentForTouch(touch);
		touchDocument.setDocumentName("Untitled "+ DOCUMENT_TYPE_NAME + " " + ++docNumber);
		return touchDocument;

	}

	@Override
	public Document openDocument(Path path) {
		Touch touch = touchPersistence.load(path);
		TouchDocument touchDocument = buildTouchDocumentForTouch(touch);
		return touchDocument;
	}

	@Override
	public void saveDocument(Document document) {
		TouchDocument touchDocument = (TouchDocument)document;
		Path path = document.getPath();
		Touch touch = touchDocument.getTouch();
		touchPersistence.save(path, touch);
	}

	public Collection<FileChooser.ExtensionFilter> getFileChooserExtensionFilters() {
		return Lists.newArrayList(new FileChooser.ExtensionFilter("Touch Files", "*.touch"));
	}

	@Override
	public String getDocumentTypeName() {
		return DOCUMENT_TYPE_NAME;
	}


	private TouchDocument buildTouchDocumentForTouch(Touch touch) {
		final TouchDocument touchDocument = beanFactory.build(TouchDocument.class);
		touchDocument.init(touch);
		// this is needed to listen to document updates that are not changed with proof's.
		// example -> alter the start change to something that will fail, and this will
		// allow it to be put back to its original value.
		touchDocument.addListener(touchDoc -> fireUpdateDocument());
		return touchDocument;
	}

	private void fireUpdateDocument() {
		for (TouchDocumentTypeListener touchDocumentTypeListener : getListeners()) {
			touchDocumentTypeListener.touchDocumentType_updateDocument(currentDocument);
		}
	}

	public Optional<TouchDocument> getCurrentDocument() {
		return currentDocument;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		documentManager.addListener(document -> {
			if (document instanceof TouchDocument) {
				TouchDocument touchDocument = (TouchDocument) document;
				currentDocument = Optional.of(touchDocument);
				touchDocument.parseAndProve();
			}
			else {
				currentDocument = Optional.empty();
				proofManager.parseAndProve(null);
			}
			fireUpdateDocument();
		});
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setProofManager(ProofManager proofManager) {
		this.proofManager = proofManager;
	}

	//TODO Make this return a new touch.
	private Touch createEmptyTouch() {
		Touch touch = TouchBuilder.getInstance(NumberOfBells.BELLS_6, 2, 2);

		touch.setTitle("My Touch");
		touch.setAuthor("by Stephen");

		touch.setTouchCheckingType(TouchCheckingType.LEAD_BASED);
		touch.addNotation(buildPlainBobMinor());
		touch.addNotation(buildLittleBobMinor());
		touch.addNotation(buildPlainBobMinimus());
		touch.addNotation(buildPlainBobMajor());

		touch.addCharacters(0,0,"-s");
		touch.addCharacters(1,0,"p 3*");
		touch.addCharacters(0,1,"s-");

		touch.addDefinition("3*", "-s-");
		touch.addDefinition("tr", "sps");

		touch.setTerminationChange(MethodBuilder.buildRoundsRow(touch.getNumberOfBells()));

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
