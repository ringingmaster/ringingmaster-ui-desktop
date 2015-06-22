package com.concurrentperformance.ringingmaster.fxui.desktop;

import com.concurrentperformance.ringingmaster.fxui.desktop.analysis.AnalysisWindow;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.menu.ConfiguredMenuBar;
import com.concurrentperformance.ringingmaster.fxui.desktop.property.PropertyWindow;
import com.concurrentperformance.ringingmaster.fxui.desktop.statusbar.ConfiguredStatusBar;
import com.concurrentperformance.ringingmaster.fxui.desktop.toolbar.ConfiguredToolBar;
import com.concurrentperformance.util.thread.ThreadUncaughtExceptionHelper;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
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

	public static void main(String[] args) {
		ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();
		log.info("Starting RingingMasterDesktopApp");
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		stage.setTitle("Ringingmaster Desktop");

		Parent parent = createMainWindow();

		Scene scene = new Scene(parent, 900,650);
		scene.getStylesheets().add("com/concurrentperformance/ringingmaster/fxui/desktop/stylesheet.css");


		stage.setScene(scene);
		stage.initStyle(StageStyle.DECORATED);
		stage.getIcons().add(new Image(	RingingMasterDesktopApp.class.getResourceAsStream("/images/RingingMaster.png")));

		stage.show();
	}

	private Parent createMainWindow() {
		Node menuBar = new ConfiguredMenuBar();
		Node buttonBar = new ConfiguredToolBar();
		Node statusBar = new ConfiguredStatusBar();
		Node touchDocumentTabs =  DocumentManager.getInstance().getDocPane();
		Node propertyWindow = new PropertyWindow();
		Node analysisLeft = new AnalysisWindow(AnalysisWindow.Type.LEFT);
		Node analysisRight = new AnalysisWindow(AnalysisWindow.Type.RIGHT);

		SplitPane leftRightSplit = new SplitPane();
		leftRightSplit.setOrientation(Orientation.HORIZONTAL);
		leftRightSplit.getItems().add(0, propertyWindow);
		leftRightSplit.getItems().add(1, touchDocumentTabs);
		leftRightSplit.setDividerPositions(0.3);
		SplitPane.setResizableWithParent(propertyWindow, false);

		SplitPane analysisSplit = new SplitPane();
		analysisSplit.setOrientation(Orientation.HORIZONTAL);
		analysisSplit.getItems().add(0, analysisLeft);
		analysisSplit.getItems().add(1, analysisRight);
		analysisSplit.setDividerPositions(0.5);

		SplitPane topBottomSplit = new SplitPane();
		topBottomSplit.setOrientation(Orientation.VERTICAL);
		topBottomSplit.getItems().add(0, leftRightSplit);
		topBottomSplit.getItems().add(1, analysisSplit);
		topBottomSplit.setDividerPositions(0.6);
		SplitPane.setResizableWithParent(analysisSplit, false);

		VBox mainVerticalLayout = new VBox(menuBar, buttonBar, topBottomSplit, statusBar);
		VBox.setVgrow(menuBar, Priority.NEVER);
		VBox.setVgrow(buttonBar, Priority.NEVER);
		VBox.setVgrow(topBottomSplit, Priority.ALWAYS);
		VBox.setVgrow(statusBar, Priority.NEVER);

		DocumentManager.buildNewDocument();
		DocumentManager.buildNewDocument();

		return mainVerticalLayout;
	}

}