package com.concurrentperformance.ringingmaster.fxui.desktop.analysis;

import com.concurrentperformance.fxutils.propertyeditor.LabelPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.PropertyEditor;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManagerListener;
import com.concurrentperformance.ringingmaster.fxui.desktop.util.ColorManager;
import javafx.application.Platform;
import javafx.scene.paint.Color;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AnalysisStatusWindow extends PropertyEditor implements ProofManagerListener {

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



	public AnalysisStatusWindow() {
		ProofManager.getInstance().addListener(this);

		add(new LabelPropertyValue(TOUCH_TRUE_PROPERTY_NAME));
		add(new LabelPropertyValue(TERMINATION_PROPERTY_NAME));
		add(new LabelPropertyValue(PART_COUNT_PROPERTY_NAME));
		add(new LabelPropertyValue(LEAD_COUNT_PROPERTY_NAME));
		add(new LabelPropertyValue(ROW_COUNT_PROPERTY_NAME));
		add(new LabelPropertyValue(CALL_COUNT_PROPERTY_NAME));
		add(new LabelPropertyValue(START_ROW_PROPERTY_NAME));
		add(new LabelPropertyValue(END_ROW_PROPERTY_NAME));
		add(new LabelPropertyValue(END_STROKE_PROPERTY_NAME));
		add(new LabelPropertyValue(PROOF_TIME_PROPERTY_NAME));
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
				updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "TRUE", ColorManager.getPassHighlight());
			}
			else {
				updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "FALSE", ColorManager.getErrorHighlight());
			}
		}
	}

	private void updateTermination(Proof proof) {
		if (proof != null) {
			String terminateReasonDisplayString = proof.getTerminateReasonDisplayString();

			switch(proof.getTerminationReason()) {

				case INVALID_TOUCH:
					break;
				case ROW_COUNT:
					updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString, ColorManager.getWarnHighlight());
					break;
				case LEAD_COUNT:
					updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString, ColorManager.getWarnHighlight());
					break;
				case SPECIFIED_ROW:
					updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString, ColorManager.getClearHighlight());
					break;
				case EMPTY_PARTS:
					updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString, ColorManager.getErrorHighlight());
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
			updateDisplayProperty(PART_COUNT_PROPERTY_NAME, "TODO", ColorManager.getClearHighlight());
		}
	}

	private void updateLeadCount(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(LEAD_COUNT_PROPERTY_NAME, Integer.toString(proof.getCreatedMethod().getLeadCount()), ColorManager.getClearHighlight());
		}
	}

	private void updateRowCount(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(ROW_COUNT_PROPERTY_NAME, Integer.toString(proof.getCreatedMethod().getRowCount()), ColorManager.getClearHighlight());
		}
	}

	private void updateCallCount(Proof proof) {
		if (proof != null && proof.getAnalysis() != null) {
			updateDisplayProperty(CALL_COUNT_PROPERTY_NAME, "TODO", ColorManager.getClearHighlight());

//TODO - From C++ - note Pain Course message
//	//number of calls.
//	str.Format("%d %s", method->getCallCount(),
//	((method->getCallCount() == 0)&&(!method->isSpliced()))? "- Plain Course":"");
//	addLine("Number of calls:" , str);
		}
	}

	private void updateStartRow(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(START_ROW_PROPERTY_NAME, proof.getCreatedMethod().getFirstRow().getDisplayString(true), ColorManager.getClearHighlight());
		}
	}

	private void updateEndRow(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(END_ROW_PROPERTY_NAME, proof.getCreatedMethod().getLastRow().getDisplayString(true), ColorManager.getClearHighlight());
		}
	}

	private void updateEndStroke(Proof proof) {
		if (proof != null && proof.getCreatedMethod() != null) {
			updateDisplayProperty(END_STROKE_PROPERTY_NAME, proof.getCreatedMethod().getLastRow().getStroke().getDisplayString(), ColorManager.getClearHighlight());
		}
	}

	private void updateProofTime(Proof proof) {
		if (proof != null) {
			updateDisplayProperty(PROOF_TIME_PROPERTY_NAME, Long.toString(proof.getProofTime()) + "ms", ColorManager.getClearHighlight());
		}
	}

	public void updateDisplayProperty(String propertyName, String value, Color color) {
		Platform.runLater(() -> {
			LabelPropertyValue propertyByName = (LabelPropertyValue) findPropertyByName(propertyName);
			propertyByName.setValue(value);
			propertyByName.setColor(color);
		});
	}
}