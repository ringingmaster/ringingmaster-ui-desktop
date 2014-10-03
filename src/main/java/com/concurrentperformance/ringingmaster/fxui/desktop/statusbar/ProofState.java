package com.concurrentperformance.ringingmaster.fxui.desktop.statusbar;

import com.concurrentperformance.ringingmaster.engine.proof.Proof;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManagerListener;
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
public class ProofState extends ImageView implements ProofManagerListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Image proofTick = new Image(this.getClass().getResourceAsStream("/proof_tick.png"));
	private final Image proofWait = new Image(this.getClass().getResourceAsStream("/proof_wait.png"));
	private final Image proofCross = new Image(this.getClass().getResourceAsStream("/proof_cross.png"));

	public ProofState() {
		ProofManager.getInstance().registerListener(this);
	}

	@Override
	public void proofManagerListener_proofFinished(Proof proof) {
		if (proof == null) {
			updateImage(proofWait);
		}
		else {
			if (proof.getAnalysis().isTrueTouch()) {
				updateImage(proofTick);
			}
			else {
				updateImage(proofCross);
			}
		}
	}

	private void updateImage(Image image) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setImage(image);
			}
		});
	}
}
