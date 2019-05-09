package org.ringingmaster.ui.desktop;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class MainParentWindow extends VBox {

    private Node menuBar;
    private Node toolBar;
    private Node statusBar;
    private Node documentWindow;
    private Node propertyWindow;
    private Node analysisLeftWindow;
    private Node analysisRightWindow;

    public void layoutWindows() {

        SplitPane leftRightSplit = new SplitPane();
        leftRightSplit.setOrientation(Orientation.HORIZONTAL);
        leftRightSplit.getItems().add(0, propertyWindow);
        leftRightSplit.getItems().add(1, documentWindow);
        leftRightSplit.setDividerPositions(0.3);
        SplitPane.setResizableWithParent(propertyWindow, false);

        SplitPane analysisSplit = new SplitPane();
        analysisSplit.setOrientation(Orientation.HORIZONTAL);
//        analysisSplit.getItems().add(0, analysisLeftWindow);
//        analysisSplit.getItems().add(1, analysisRightWindow);
        analysisSplit.setDividerPositions(0.5);

        SplitPane topBottomSplit = new SplitPane();
        topBottomSplit.setOrientation(Orientation.VERTICAL);
        topBottomSplit.getItems().add(0, leftRightSplit);
        topBottomSplit.getItems().add(1, analysisSplit);
        topBottomSplit.setDividerPositions(0.8);
        SplitPane.setResizableWithParent(analysisSplit, false);

        getChildren().add(menuBar);
        getChildren().add(toolBar);
        getChildren().add(topBottomSplit);
        getChildren().add(statusBar);

        VBox.setVgrow(menuBar, Priority.NEVER);
        VBox.setVgrow(toolBar, Priority.NEVER);
        VBox.setVgrow(topBottomSplit, Priority.ALWAYS);
        VBox.setVgrow(statusBar, Priority.NEVER);

    }


    public void setMenuBar(Node menuBar) {
        this.menuBar = menuBar;
    }

    public void setToolBar(Node toolBar) {
        this.toolBar = toolBar;
    }

    public void setStatusBar(Node statusBar) {
        this.statusBar = statusBar;
    }

    public void setDocumentWindow(Node documentWindow) {
        this.documentWindow = documentWindow;
    }

    public void setPropertyWindow(Node propertyWindow) {
        this.propertyWindow = propertyWindow;
    }

    public void setAnalysisLeftWindow(Node analysisLeftWindow) {
        this.analysisLeftWindow = analysisLeftWindow;
    }

    public void setAnalysisRightWindow(Node analysisRightWindow) {
        this.analysisRightWindow = analysisRightWindow;
    }
}
