package com.concurrentperformance.ringingmaster.ui.desktop.documentpanel.grid.canvas;

import com.concurrentperformance.util.listener.ConcurrentListenable;
import com.concurrentperformance.util.listener.Listenable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A wrapper to prevent each grid having its own thread for a blink timer.
 *
 * @author Lake
 */
public class BlinkTimerManager extends ConcurrentListenable<BlinkTimerListener> implements Listenable<BlinkTimerListener> {

	private static final int BLINK_RATE_MS = 500;

	private final Timer timer = new Timer("Caret", true);
	private volatile boolean blinkOn = false;


	private final static BlinkTimerManager INSTANCE = new BlinkTimerManager();

	private BlinkTimerManager() {
		startTimer();
	}

	public static BlinkTimerManager getInstance() {
		return INSTANCE;
	}

	private void startTimer() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				blinkOn = !blinkOn;
				for (BlinkTimerListener listener : getListeners()) {
					listener.blinkTimerManager_triggerBlink(blinkOn);
				}

			}
		}, BLINK_RATE_MS, BLINK_RATE_MS);
	}



}
