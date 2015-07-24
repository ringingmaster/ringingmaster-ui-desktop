package com.concurrentperformance.ringingmaster.fxui.desktop.statusbar;

import com.concurrentperformance.ringingmaster.engine.touch.proof.ProofTerminationReason;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class ProofState extends ImageView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Image proofTick = new Image(this.getClass().getResourceAsStream("/images/proof_tick.png"));
	private final Image proofWait = new Image(this.getClass().getResourceAsStream("/images/proof_wait.png"));
	private final Image proofCross = new Image(this.getClass().getResourceAsStream("/images/proof_cross.png"));

	public void setProofManager(ProofManager proofManager) {
		proofManager.addListener(proof -> {
			if (proof.getTerminationReason() == ProofTerminationReason.INVALID_TOUCH) {
				updateImage(proofWait);//TODO could make this a question mark.
			}
			else if (proof.getAnalysis().isPresent()) {
				if (proof.getAnalysis().get().isTrueTouch()) {
					updateImage(proofTick);
				}
				else {
					updateImage(proofCross);
				}
			}
			else {
				updateImage(proofWait);
			}
		});
	}

	private void updateImage(Image image) {
		Platform.runLater(() -> setImage(image));
	}
}
