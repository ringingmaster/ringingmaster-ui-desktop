package com.concurrentperformance.ringingmaster.fxui.desktop.proof;

import com.concurrentperformance.ringingmaster.engine.compiler.impl.LeadBasedCompiler;
import com.concurrentperformance.ringingmaster.engine.parser.Parser;
import com.concurrentperformance.ringingmaster.engine.parser.impl.DefaultParser;
import com.concurrentperformance.ringingmaster.engine.proof.Proof;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class ProofManager {

	private final static ProofManager INSTANCE = new ProofManager();

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Parser parser = new DefaultParser();

	private final Executor proofExecutor;
	private final Executor updateExecutor;
	private final AtomicLong nextProofId = new AtomicLong(0);

	private final Set<ProofManagerListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

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
					log.info("Ignoring finished proof [{}] as not current [{}]", proofId, currentProofId);
				}
			}
		});
	}

	private void updateProofState(final Proof proof) {
		updateExecutor.execute(new Runnable() {
			@Override
			public void run() {
				for (ProofManagerListener listener : listeners) {
					listener.proofManagerListener_proofFinished(proof);
				}
			}
		});
	}

	public void registerListener(ProofManagerListener listener) {
		listeners.add(listener);
	}

	public void deRegisterListener(ProofManagerListener listener) {
		checkState(listeners.remove(listener) == false);
	}
}
