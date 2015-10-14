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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages crating and switching of documents.
 *
 * @author Lake
 */
public class DocumentManager extends ConcurrentListenable<DocumentManagerListener> implements Listenable<DocumentManagerListener> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Optional<TouchDocHolder> currentDocument = Optional.empty();
	private TabPane documentWindow;
	private int docNumber = 0;

	private ProofManager proofManager;
	private BeanFactory beanFactory;
	private Stage globalStage;
	private TouchPersistence touchPersistence = new TouchPersistence();

	public void init() {
		documentWindow.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		documentWindow.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				currentDocument = Optional.empty();
				proofManager.parseAndProve(null);
				setApplicationTitle(null);
			}
			else {
				currentDocument = Optional.of(new TouchDocHolder(newValue));
				TouchDocument touchDocument = currentDocument.get().getTouchDocument();
				touchDocument.parseAndProve();
				if (touchDocument.getPath().isPresent()) {
					setApplicationTitle(touchDocument.getPath().toString());
				} else {
					setApplicationTitle(currentDocument.get().getTab().getText());
				}

			}
			fireUpdateDocument();
		});
	}

	public void buildNewDocument() {
		final TouchDocument touchDocument = beanFactory.build(TouchDocument.class);
		touchDocument.init(createDummyTouch());
		// this is needed to listen to document updates that are not changed with proof's.
		// example -> alter the start change to something that will fail, and this will
		// allow it to be put back to its original value.
		touchDocument.addListener(touchDoc -> fireUpdateDocument());
		Tab tab = createTab("Untitled " + ++docNumber, touchDocument);

		currentDocument = Optional.of(new TouchDocHolder(tab));
	}

	public void saveCurrentDocument() {
		if (currentDocument.isPresent()) {
			TouchDocument touchDocument = currentDocument.get().getTouchDocument();
			Tab tab = currentDocument.get().getTab();

			if (!touchDocument.getPath().isPresent()) {

				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save Touch File");
				fileChooser.setInitialFileName(tab.getText() +  ".touch");
				fileChooser.getExtensionFilters().addAll(
						new FileChooser.ExtensionFilter("Touch Files", "*.touch"));
				File selectedFile = fileChooser.showSaveDialog(globalStage);
				if (selectedFile == null) {
					return;
				}
				else {
					touchDocument.setPath(selectedFile.toPath());
				}
			}

			Touch touch = touchDocument.getTouch();
			Path path = touchDocument.getPath().get();
			touchPersistence.persist(path, touch);
			tab.setText(path.toString());
			setApplicationTitle(path.toString());
		}
	}

	private void setApplicationTitle(String fileText) {
		globalStage.setTitle("Ringingmaster Desktop" +((fileText== null)?"":" - [" + fileText + "]"));
	}

	private Tab createTab(String name, Node node) {
		Tab tab = new Tab();
		tab.setText(name);
		tab.setContent(node);
		documentWindow.getTabs().add(tab);
		return tab;
	}

	private void fireUpdateDocument() {
		Optional<TouchDocument> currentDocument = getCurrentDocument();

		for (DocumentManagerListener listener : getListeners()) {
			listener.documentManager_updateDocument(currentDocument);
		}
	}

	public Optional<TouchDocument> getCurrentDocument() {
		return Optional.ofNullable((currentDocument.isPresent() ? currentDocument.get().getTouchDocument():null));
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

	public void setGlobalStage(Stage globalStage) {
		this.globalStage = globalStage;
	}

	private class TouchDocHolder {
		private final Tab tab;
		private final TouchDocument touchDocument;


		private TouchDocHolder(Tab tab) {
			this.tab = checkNotNull(tab);
			TouchDocument touchDocument = (TouchDocument) tab.getContent();
			this.touchDocument = checkNotNull(touchDocument);
		}

		public Tab getTab() {
			return tab;
		}

		public TouchDocument getTouchDocument() {
			return touchDocument;
		}
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
