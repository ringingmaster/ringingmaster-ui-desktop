package com.concurrentperformance.ringingmaster.fxui.desktop.proof;

import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;

import java.util.Optional;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public interface ProofManagerListener {

	void proofManagerListener_proofFinished(Optional<Proof> proof);
}
