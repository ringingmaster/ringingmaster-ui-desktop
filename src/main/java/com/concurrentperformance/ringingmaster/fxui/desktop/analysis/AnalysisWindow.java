package com.concurrentperformance.ringingmaster.fxui.desktop.analysis;

import com.concurrentperformance.fxutils.propertyeditor.DisplayPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.PropertyEditor;
import com.concurrentperformance.ringingmaster.engine.proof.Proof;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManagerListener;
import javafx.application.Platform;
import javafx.scene.paint.Color;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AnalysisWindow extends PropertyEditor implements ProofManagerListener {

	public static final String TOUCH_TRUE_PROPERTY_NAME = "The touch is";
	public static final String TERMINATION_PROPERTY_NAME = "Termination";
	public static final String PART_COUNT_PROPERTY_NAME = "Part Count";
	public static final String LEAD_COUNT_PROPERTY_NAME = "Lead Count";
	public static final String ROW_COUNT_PROPERTY_NAME = "Row Count";
	public static final String CALL_COUNT_PROPERTY_NAME = "Call Count";
	public static final String START_ROW_PROPERTY_NAME = "Start Row";
	public static final String END_ROW_PROPERTY_NAME = "End Row";
	public static final String END_STROKE_PROPERTY_NAME = "End Stroke";
	public static final String PROOF_TIME_PROPERTY_NAME = "Proof Time";



	public AnalysisWindow() {
		ProofManager.getInstance().addListener(this);

		add(new DisplayPropertyValue(TOUCH_TRUE_PROPERTY_NAME));
		add(new DisplayPropertyValue(TERMINATION_PROPERTY_NAME));
		add(new DisplayPropertyValue(PART_COUNT_PROPERTY_NAME));
		add(new DisplayPropertyValue(LEAD_COUNT_PROPERTY_NAME));
		add(new DisplayPropertyValue(ROW_COUNT_PROPERTY_NAME));
		add(new DisplayPropertyValue(CALL_COUNT_PROPERTY_NAME));
		add(new DisplayPropertyValue(START_ROW_PROPERTY_NAME));
		add(new DisplayPropertyValue(END_ROW_PROPERTY_NAME));
		add(new DisplayPropertyValue(END_STROKE_PROPERTY_NAME));
		add(new DisplayPropertyValue(PROOF_TIME_PROPERTY_NAME));
	}

	@Override
	public void proofManagerListener_proofFinished(Proof proof) {
		updateTouchTrue(proof);
		updateTermination(proof);
		updatePartCount(proof);
		updateLeadCount(proof);
		updateRowCount(proof);
		updateCallCount(proof);
		updateStartRow(proof);
		updateEndRow(proof);
		updateEndStroke(proof);
		updateProofTime(proof);
	}

	private void updateTouchTrue(Proof proof) {
		if (proof != null && proof.getAnalysis() != null) {
			if (proof.getAnalysis().isTrueTouch()) {
				updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "TRUE", Color.rgb(120, 255, 120));
			}
			else {
				updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "FALSE", Color.rgb(255, 120, 120));
			}
		}
	}

	private void updateTermination(Proof proof) {
		if (proof != null) {
			switch(proof.getTerminationReason()) {

				case INVALID_TOUCH:
					break;
				case ROW_COUNT:
					updateDisplayProperty(TERMINATION_PROPERTY_NAME, "Row limit (" + proof.getCreatedMethod().getRowCount() + ")", Color.rgb(255, 120, 255));
					break;
				case LEAD_COUNT:
					updateDisplayProperty(TERMINATION_PROPERTY_NAME, "Lead limit (" + proof.getCreatedMethod().getLeadCount() + ")", Color.rgb(255, 120, 255));
					break;
				case SPECIFIED_ROW:
					updateDisplayProperty(TERMINATION_PROPERTY_NAME, "Change (" + proof.getCreatedMethod().getLastRow().getDisplayString(true) + ")", Color.WHITE);
					break;
				case EMPTY_PARTS:
					updateDisplayProperty(TERMINATION_PROPERTY_NAME, "Aborted - Empty parts found", Color.rgb(255, 120, 120));
					break;
				// TODO this is from C++
//			case TR_PARTS:
//				str.Format("Part limit (%d)", method->getPartCount());
//				addLine("Termination:", str, RGB(255, 120, 255));
//				break;
//
//			case TR_CIRCLE:
//				addLine("Termination:", "Aborted - Circular touch", RGB(255, 120, 120));
//				break;

				default:
					throw new RuntimeException("Please code for termination reason [" + proof.getTerminationReason() + "]");
			}

		}
	}

	private void updatePartCount(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(PART_COUNT_PROPERTY_NAME, "TODO", Color.WHITE);
		}
	}

	private void updateLeadCount(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(LEAD_COUNT_PROPERTY_NAME, Integer.toString(proof.getCreatedMethod().getLeadCount()), Color.WHITE);
		}
	}

	private void updateRowCount(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(ROW_COUNT_PROPERTY_NAME, Integer.toString(proof.getCreatedMethod().getRowCount()), Color.WHITE);
		}
	}

	private void updateCallCount(Proof proof) {
		if (proof != null && proof.getAnalysis() != null) {
			updateDisplayProperty(CALL_COUNT_PROPERTY_NAME, "TODO", Color.WHITE);

//TODO - From C++ - note Pain Course message
//	//number of calls.
//	str.Format("%d %s", method->getCallCount(),
//	((method->getCallCount() == 0)&&(!method->isSpliced()))? "- Plain Course":"");
//	addLine("Number of calls:" , str);
		}
	}

	private void updateStartRow(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(START_ROW_PROPERTY_NAME, proof.getCreatedMethod().getFirstRow().getDisplayString(true), Color.WHITE);
		}
	}

	private void updateEndRow(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(END_ROW_PROPERTY_NAME, proof.getCreatedMethod().getLastRow().getDisplayString(true), Color.WHITE);
		}
	}

	private void updateEndStroke(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(END_STROKE_PROPERTY_NAME, proof.getCreatedMethod().getLastRow().getStroke().getDisplayString(), Color.WHITE);
		}
	}

	private void updateProofTime(Proof proof) {
		if (proof != null) {
			updateDisplayProperty(PROOF_TIME_PROPERTY_NAME, Long.toString(proof.getProofTime()) + "ms", Color.WHITE);
		}
	}

	public void updateDisplayProperty(String propertyName, String touchTrue, Color color) {
		Platform.runLater(() -> {
			DisplayPropertyValue propertyByName = (DisplayPropertyValue) findPropertyByName(propertyName);
			propertyByName.setValue(touchTrue);
			propertyByName.setColor(color);
		});
	}
}
