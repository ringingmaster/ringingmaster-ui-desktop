package com.concurrentperformance.ringingmaster.fxui.desktop.analysis;

import com.concurrentperformance.fxutils.namevaluepair.NameValueColumnDescriptor;
import com.concurrentperformance.fxutils.namevaluepair.NameValuePairModel;
import com.concurrentperformance.fxutils.namevaluepair.NameValuePairTable;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.util.ColorManager;
import javafx.scene.paint.Color;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AnalysisStatusWindow extends NameValuePairTable {

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



	public void setProofManager(ProofManager proofManager) {
		proofManager.addListener(proof -> {
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
		});

		getItems().add(new NameValuePairModel(TOUCH_TRUE_PROPERTY_NAME));
		getItems().add(new NameValuePairModel(TERMINATION_PROPERTY_NAME));
		getItems().add(new NameValuePairModel(PART_COUNT_PROPERTY_NAME));
		getItems().add(new NameValuePairModel(LEAD_COUNT_PROPERTY_NAME));
		getItems().add(new NameValuePairModel(ROW_COUNT_PROPERTY_NAME));
		getItems().add(new NameValuePairModel(CALL_COUNT_PROPERTY_NAME));
		getItems().add(new NameValuePairModel(START_ROW_PROPERTY_NAME));
		getItems().add(new NameValuePairModel(END_ROW_PROPERTY_NAME));
		getItems().add(new NameValuePairModel(END_STROKE_PROPERTY_NAME));
		getItems().add(new NameValuePairModel(PROOF_TIME_PROPERTY_NAME));
	}

	private void updateTouchTrue(Proof proof) {
		if (proof.getAnalysis().isPresent()) {
			if (proof.getAnalysis().get().isTrueTouch()) {
				updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "TRUE", ColorManager.getPassHighlight());
			}
			else {
				updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "FALSE", ColorManager.getErrorHighlight());
			}
		}
		else {
			updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "INVALID", ColorManager.getErrorHighlight());
		}
	}


	private void updateTermination(Proof proof) {
		String terminateReasonDisplayString = proof.getTerminateReasonDisplayString();

		switch(proof.getTerminationReason()) {
			case SPECIFIED_ROW:
				updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString);
				break;
			case ROW_COUNT:
			case LEAD_COUNT:
				updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString, ColorManager.getWarnHighlight());
				updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString, ColorManager.getWarnHighlight());
				break;
			case INVALID_TOUCH:
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

	private void updatePartCount(Proof proof) {
		if (proof.getCreatedMethod().isPresent()) {
			updateDisplayProperty(PART_COUNT_PROPERTY_NAME, "TODO");
		}
		else {
			updateDisplayProperty(PART_COUNT_PROPERTY_NAME, "");
		}
	}

	private void updateLeadCount(Proof proof) {
		if (proof.getCreatedMethod().isPresent()) {
			updateDisplayProperty(LEAD_COUNT_PROPERTY_NAME, Integer.toString(proof.getCreatedMethod().get().getLeadCount()));
		}
		else {
			updateDisplayProperty(LEAD_COUNT_PROPERTY_NAME, "");
		}
	}

	private void updateRowCount(Proof proof) {
		if (proof.getCreatedMethod().isPresent()) {
			updateDisplayProperty(ROW_COUNT_PROPERTY_NAME, Integer.toString(proof.getCreatedMethod().get().getRowCount()));
		}
		else {
			updateDisplayProperty(ROW_COUNT_PROPERTY_NAME, "");
		}
	}

	private void updateCallCount(Proof proof) {
		if (proof.getCreatedMethod().isPresent()) {
			updateDisplayProperty(CALL_COUNT_PROPERTY_NAME, "TODO");

//TODO - From C++ - note Pain Course message
//	//number of calls.
//	str.Format("%d %s", method->getCallCount(),
//	((method->getCallCount() == 0)&&(!method->isSpliced()))? "- Plain Course":"");
//	addLine("Number of calls:" , str);
		}
		else {
			updateDisplayProperty(CALL_COUNT_PROPERTY_NAME, "");
		}
	}

	private void updateStartRow(Proof proof) {
		if (proof.getCreatedMethod().isPresent()) {
			updateDisplayProperty(START_ROW_PROPERTY_NAME, proof.getCreatedMethod().get().getFirstRow().getDisplayString(true));
		}
		else {
			updateDisplayProperty(START_ROW_PROPERTY_NAME, "");
		}
	}

	private void updateEndRow(Proof proof) {
		if (proof.getCreatedMethod().isPresent()) {
			updateDisplayProperty(END_ROW_PROPERTY_NAME, proof.getCreatedMethod().get().getLastRow().getDisplayString(true));
		}
		else {
			updateDisplayProperty(END_ROW_PROPERTY_NAME, "");
		}
	}

	private void updateEndStroke(Proof proof) {
		if (proof.getCreatedMethod().isPresent()) {
			updateDisplayProperty(END_STROKE_PROPERTY_NAME, proof.getCreatedMethod().get().getLastRow().getStroke().getDisplayString());
		}
		else {
			updateDisplayProperty(END_STROKE_PROPERTY_NAME, "");
		}
	}

	private void updateProofTime(Proof proof) {
		updateDisplayProperty(PROOF_TIME_PROPERTY_NAME, String.format("%.3f", (proof.getProofTimeMs()) / 1000.0) + " seconds");
	}


	private void updateDisplayProperty(String propertyName, String value) {
		updateDisplayProperty(propertyName, value, null);
	}
	private void updateDisplayProperty(String propertyName, String value, Color valueColor) {
		getItems().stream()
				.filter(columnDescriptor -> columnDescriptor.getName().getText().equals(propertyName))
				.forEach(pair -> pair.setValue(new NameValueColumnDescriptor(value, valueColor, false)));
	}
}