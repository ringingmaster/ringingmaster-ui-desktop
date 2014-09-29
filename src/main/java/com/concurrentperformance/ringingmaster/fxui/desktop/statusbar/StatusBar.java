package com.concurrentperformance.ringingmaster.fxui.desktop.statusbar;

import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class StatusBar extends HBox {



	public StatusBar(ProofManager proofManager) {

		ProofState proofState = new ProofState(proofManager);

		Label dummy = new Label("TEST");

		Pane expansionPane = new Pane();

		getChildren().addAll(dummy, expansionPane, proofState);
		setMinHeight(20.0);
		setPadding(new Insets(3.0,3.0,3.0,3.0));
		HBox.setHgrow(dummy, Priority.NEVER);
		HBox.setHgrow(expansionPane, Priority.ALWAYS);
		HBox.setHgrow(proofState, Priority.NEVER);
	}



}
