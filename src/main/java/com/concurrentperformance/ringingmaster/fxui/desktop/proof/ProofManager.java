package com.concurrentperformance.ringingmaster.fxui.desktop.proof;

import com.concurrentperformance.ringingmaster.engine.compiler.impl.LeadBasedCompiler;
import com.concurrentperformance.ringingmaster.engine.parser.Parser;
import com.concurrentperformance.ringingmaster.engine.parser.impl.DefaultParser;
import com.concurrentperformance.ringingmaster.engine.proof.Proof;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class ProofManager extends ConcurrentListenable<ProofManagerListener> implements Listenable<ProofManagerListener> {

	private final static ProofManager INSTANCE = new ProofManager();

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Parser parser = new DefaultParser();

	private final Executor proofExecutor;
	private final Executor updateExecutor;
	private final AtomicLong nextProofId = new AtomicLong(0);


	public static ProofManager getInstance() {
		return INSTANCE;
	}

	private ProofManager() {
		proofExecutor =  new ThreadPoolExecutor(1, 1,
						0L, TimeUnit.MILLISECONDS,
						new LinkedBlockingQueue<>(),
						new ThreadFactoryBuilder()
								.setNameFormat("proof-pool-%d")
								.setDaemon(true)
								.build());
		updateExecutor =  new ThreadPoolExecutor(1, 1,
						0L, TimeUnit.MILLISECONDS,
						new LinkedBlockingQueue<>(),
						new ThreadFactoryBuilder()
								.setNameFormat("update-pool-%d")
								.setDaemon(true)
								.build());
	}

	public void parseAndProve(Touch touch) {
		parser.parseAndAnnotate(touch);

		// Get the proofId before clearing the proof, so another thread does not preempt us.
		final long proofId = this.nextProofId.incrementAndGet();

		updateProofState(null);

		proofExecutor.execute(new Runnable() {
			@Override
			public void run() {
				final LeadBasedCompiler compiler = new LeadBasedCompiler(touch, "Proof-" + Long.toString(proofId));
				Proof proof = compiler.compile(true);
				final long currentProofId = nextProofId.get();
				if (proofId == currentProofId) {
					updateProofState(proof);
				}
				else {
					log.info("Ignoring finished proof [{}] as not current [{}]", proofId, currentProofId); //TODO ned a mech of cancelling a proff nid term.
				}
			}
		});
	}

	private void updateProofState(final Proof proof) {
		updateExecutor.execute(new Runnable() {
			@Override
			public void run() {
				for (ProofManagerListener listener : getListeners()) {
					listener.proofManagerListener_proofFinished(proof);
				}
			}
		});
	}
}
