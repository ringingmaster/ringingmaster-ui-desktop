package com.concurrentperformance.ringingmaster.fxui.desktop;

import com.concurrentperformance.ringingmaster.fxui.desktop.document.TouchDocument;
import com.concurrentperformance.ringingmaster.fxui.desktop.panel.TouchPanel;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.fxutils.propertyeditor.PropertyEditor;
import com.concurrentperformance.fxutils.propertyeditor.SelectionPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.TextPropertyValue;
import com.concurrentperformance.ringingmaster.fxui.desktop.statusbar.StatusBar;
import com.concurrentperformance.ringingmaster.util.ThreadUncaughtExceptionHelper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class RingingMasterDesktopApp extends Application {

	private final static Logger log = LoggerFactory.getLogger(RingingMasterDesktopApp.class);
	public static final String SETUP_GROUP_NAME = "Setup";
	public static final String ADVANCED_SETUP_GROUP_NAME = "Advanced Setup";

	private final ProofManager proofManager = new ProofManager();

	public static void main(String[] args) {
		ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();
		log.info("Starting RingingMasterDesktopApp");
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		stage.setTitle("Ringingmaster Desktop");

		Parent parent = createMainWindow();

		Scene scene = new Scene(parent, 800,500);
		stage.setScene(scene);
		stage.initStyle(StageStyle.DECORATED);
		stage.getIcons().add(new Image(	RingingMasterDesktopApp.class.getResourceAsStream("/RingingMaster.png")));

		stage.show();
	}

	private Parent createMainWindow() {
		Node menuBar = createMenu();
		Node statusBar = new StatusBar(proofManager);
		TouchPanel touchPanel =  new TouchPanel();
		Node propertyWindow = buildPropertyWindow();
		Node analysis1 = buildAnalysis1();

		SplitPane leftRightSplit = new SplitPane();
		leftRightSplit.setOrientation(Orientation.HORIZONTAL);
		leftRightSplit.getItems().add(0, propertyWindow);
		leftRightSplit.getItems().add(1, touchPanel);
		leftRightSplit.setDividerPositions(0.3);
		SplitPane.setResizableWithParent(propertyWindow, false);

		SplitPane topBottomSplit = new SplitPane();
		topBottomSplit.setOrientation(Orientation.VERTICAL);
		topBottomSplit.getItems().add(0, leftRightSplit);
		topBottomSplit.getItems().add(1, analysis1);
		topBottomSplit.setDividerPositions(0.8);
		SplitPane.setResizableWithParent(analysis1, false);

		VBox mainVerticalLayout = new VBox(menuBar, topBottomSplit, statusBar);
		VBox.setVgrow(menuBar, Priority.NEVER);
		VBox.setVgrow(topBottomSplit, Priority.ALWAYS);
		VBox.setVgrow(statusBar, Priority.NEVER);

		TouchDocument document = new TouchDocument(proofManager);
		touchPanel.bind(document);

		return mainVerticalLayout;
	}

	private Node createMenu() {

		MenuBar menuBar = new MenuBar();

		// --- Menu File
		Menu menuFile = new Menu("File");
		menuFile.getItems().add(0,new MenuItem("Stephen"));

		menuBar.getMenus().addAll(menuFile);

		menuBar.setUseSystemMenuBar(true);
		return menuBar;
	}

	private Node buildPropertyWindow() {
		PropertyEditor propertyEditor = new PropertyEditor();
		propertyEditor.add(SETUP_GROUP_NAME, new TextPropertyValue("Title", "My Touch"));
		propertyEditor.add(SETUP_GROUP_NAME, new TextPropertyValue("Author", "Stephen"));

		propertyEditor.add(ADVANCED_SETUP_GROUP_NAME, new TextPropertyValue("Wrap Calls", "False"));
		FXCollections.observableArrayList(
				"Option 1",
				"Option 2",
				"Option 3"
		);
		propertyEditor.add(ADVANCED_SETUP_GROUP_NAME, new SelectionPropertyValue("Checking Type", "Single Block"));

		return propertyEditor;
	}

	private Node buildAnalysis1() {
		AnchorPane analysis1 = new AnchorPane();
		analysis1.setMinHeight(100.0);
		return analysis1;
	}
}