package org.ringingmaster.ui.desktop.proof;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.util.listener.ConcurrentListenable;
import org.ringingmaster.util.listener.Listenable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages all asynchronous requests for a proof
 *
 * @author Lake
 */
public class ProofManager extends ConcurrentListenable<ProofManagerListener> implements Listenable<ProofManagerListener> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Parser parser = new Parser();

    private final Executor proofExecutor;
    private final Executor updateExecutor;
    private final AtomicLong nextProofId = new AtomicLong(0);

    public ProofManager() { // TODO should we set a low thread priority?
        proofExecutor = new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("ProofManager-proof-%d")
                        .setDaemon(true)
                        .build());

        updateExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("ProofManager-update-%d")
                        .setDaemon(true)
                        .build());
    }

    public void parseAndProve(Composition composition) {
//        fireUpdateProofState(Optional.empty());

//        if (composition == null) {
//            return;
//        }
//TODO Reactive
//        parser.parseAndAnnotate(composition);
//
//        // Get the proofId before clearing the proof, so another thread does not preempt us.
//        final long proofId = this.nextProofId.incrementAndGet();
//        log.info("Submit Proof [{}]", proofId);
//
//        //Do this on the calling thread so the Composition has not changed by the time it is processed.
//        final Compiler compiler = CompilerFactory.getInstance(composition, "Proof-" + Long.toString(proofId));
//
//        proofExecutor.execute(() -> {
//            long start = System.currentTimeMillis();
//            log.info(">>>> Proof of [{}]", proofId);
//            Proof proof = null;
//            try {
//                proof = compiler.compile(true, () -> !isCurrentProof(proofId));
//                if (isCurrentProof(proofId)) {
//                    fireUpdateProofState(Optional.of(proof));
//                } else {
//                    log.info("Ignoring finished proof [{}] as not current [{}]", proofId, nextProofId.get());
//                }
//            } catch (TerminateEarlyException e) {
//                log.info("<<<< Terminate early request for proof [{}]", proofId);
//                return;
//            }
//
//            log.info("<<<< Proof of [{}], [{}ms], Calculation Time [{}ms]", proofId, System.currentTimeMillis() - start, proof.getProofTimeMs());
//        });
    }

    private boolean isCurrentProof(long proofId) {
        return nextProofId.get() == proofId;
    }

    private void fireUpdateProofState(Optional<Proof> proof) {

        checkNotNull(proof);
        updateExecutor.execute(() -> {
            for (ProofManagerListener listener : getListeners()) {
                listener.proofManagerListener_proofFinished(proof);
            }
        });
    }
}
