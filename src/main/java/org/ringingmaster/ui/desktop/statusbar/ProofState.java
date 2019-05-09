package org.ringingmaster.ui.desktop.statusbar;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments ???
 *
 * @author Steve Lake
 */
public class ProofState extends ImageView {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Image proofTick = new Image(this.getClass().getResourceAsStream("/images/proof_tick.png"));
    private final Image proofWait = new Image(this.getClass().getResourceAsStream("/images/proof_wait.png"));//TODO could make this a question mark.
    private final Image proofCross = new Image(this.getClass().getResourceAsStream("/images/proof_cross.png"));

//TODO Reactive    public void setProofManager(ProofManager proofManager) {
//        proofManager.addListener(proof -> {
//            if (!proof.isPresent()) {
//                updateImage(null);
//            } else {
//                if (proof.get().getCompiledComposition().getTerminationReason() == INVALID_COMPOSITION) {
//                    updateImage(proofWait);
//                } else {
//                    if (proof.get().isTrueComposition()) {
//                        updateImage(proofTick);
//                    } else {
//                        updateImage(proofCross);
//                    }
//                }
//            }
//        });
//    }

    private void updateImage(Image image) {
        Platform.runLater(() -> setImage(image));
    }
}
