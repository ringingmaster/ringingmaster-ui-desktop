package com.concurrentperformance.ringingmaster.ui.desktop.notationpanel;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
public interface PropertyNotationPanelListener {

	void propertyMethod_setSelectedNotation(Optional<NotationBody> notation);
}
