package com.concurrentperformance.ringingmaster.fxui.desktop;

import com.concurrentperformance.ringingmaster.fxui.desktop.analysis.AnalysisWindow;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmanager.DocumentManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.property.PropertyWindow;
import com.concurrentperformance.ringingmaster.fxui.desktop.property.methods.AddNotationButton;
import com.concurrentperformance.ringingmaster.fxui.desktop.statusbar.StatusBar;
import com.concurrentperformance.util.thread.ThreadUncaughtExceptionHelper;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static javafx.scene.text.FontSmoothingType.*;

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
		Node menuBar = createMenu();
		Node buttonBar = createButtonBar();
		Node statusBar = new StatusBar();
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

	private Node createButtonBar() {
		ToolBar toolBar = new ToolBar();

		toolBar.getItems().addAll(new AddNotationButton(),
				new Separator(Orientation.VERTICAL));

		return toolBar;

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

}