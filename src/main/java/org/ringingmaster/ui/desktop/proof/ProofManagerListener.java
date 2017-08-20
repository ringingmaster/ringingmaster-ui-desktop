package org.ringingmaster.ui.desktop.proof;

import org.ringingmaster.engine.touch.proof.Proof;

import java.util.Optional;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public interface ProofManagerListener {

	void proofManagerListener_proofFinished(Optional<Proof> proof);
}
