package com.concurrentperformance.ringingmaster.fxui.desktop.analysis;

import com.concurrentperformance.fxutils.namevaluepair.NameValuePairModel;
import com.concurrentperformance.fxutils.namevaluepair.NameValuePairTable;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.fxui.desktop.proof.ProofManager;
import com.concurrentperformance.ringingmaster.fxui.desktop.util.ColorManager;
import javafx.application.Platform;

import java.util.Optional;

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
		proofManager.addListener(proof -> Platform.runLater(() -> {
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

		}));


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

	private void updateTouchTrue(Optional<Proof> proof) {
		if (proof.isPresent()) {
			if (proof.get().getAnalysis().isPresent()) {
				if (proof.get().getAnalysis().get().isTrueTouch()) {
					updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "TRUE", ColorManager.getPassHighlight());
				} else {
					updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "FALSE", ColorManager.getErrorHighlight());
				}
			} else {
				updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "INVALID", ColorManager.getErrorHighlight());
			}
		}
		else {
			updateDisplayProperty(TOUCH_TRUE_PROPERTY_NAME, "", null);
		}
	}


	private void updateTermination(Optional<Proof> proof) {
		if (proof.isPresent()) {
			String terminateReasonDisplayString = proof.get().getTerminateReasonDisplayString();

			switch (proof.get().getTerminationReason()) {
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
					throw new RuntimeException("Please code for termination reason [" + proof.get().getTerminationReason() + "]");
			}
		}
		else {
			updateDisplayProperty(TERMINATION_PROPERTY_NAME, "", null);
		}
	}

	private void updatePartCount(Optional<Proof> proof) {
		if (proof.isPresent()) {
			if (proof.get().getCreatedMethod().isPresent()) {
				updateDisplayProperty(PART_COUNT_PROPERTY_NAME, "TODO");
			} else {
				updateDisplayProperty(PART_COUNT_PROPERTY_NAME, "");
			}
		}
		else {
			updateDisplayProperty(PART_COUNT_PROPERTY_NAME, "");
		}
	}

	private void updateLeadCount(Optional<Proof> proof) {
		if (proof.isPresent()) {
			if (proof.get().getCreatedMethod().isPresent()) {
				updateDisplayProperty(LEAD_COUNT_PROPERTY_NAME, Integer.toString(proof.get().getCreatedMethod().get().getLeadCount()));
			} else {
				updateDisplayProperty(LEAD_COUNT_PROPERTY_NAME, "");
			}
		}
		else {
			updateDisplayProperty(LEAD_COUNT_PROPERTY_NAME, "");
		}
	}

	private void updateRowCount(Optional<Proof> proof) {
		if (proof.isPresent()) {
			if (proof.get().getCreatedMethod().isPresent()) {
				updateDisplayProperty(ROW_COUNT_PROPERTY_NAME, Integer.toString(proof.get().getCreatedMethod().get().getRowCount()));
			} else {
				updateDisplayProperty(ROW_COUNT_PROPERTY_NAME, "");
			}
		}
		else {
			updateDisplayProperty(ROW_COUNT_PROPERTY_NAME, "");
		}
	}

	private void updateCallCount(Optional<Proof> proof) {
		if (proof.isPresent()) {
			if (proof.get().getCreatedMethod().isPresent()) {
				updateDisplayProperty(CALL_COUNT_PROPERTY_NAME, "TODO");

				//TODO - From C++ - note Pain Course message
				//	//number of calls.
				//	str.Format("%d %s", method->getCallCount(),
				//	((method->getCallCount() == 0)&&(!method->isSpliced()))? "- Plain Course":"");
				//	addLine("Number of calls:" , str);
			} else {
				updateDisplayProperty(CALL_COUNT_PROPERTY_NAME, "");
			}
		}
		else {
			updateDisplayProperty(CALL_COUNT_PROPERTY_NAME, "");
		}
	}

	private void updateStartRow(Optional<Proof> proof) {
		if (proof.isPresent()) {
			if (proof.get().getCreatedMethod().isPresent()) {
				updateDisplayProperty(START_ROW_PROPERTY_NAME, proof.get().getCreatedMethod().get().getFirstRow().getDisplayString(true));
			} else {
				updateDisplayProperty(START_ROW_PROPERTY_NAME, "");
			}
		}
		else {
			updateDisplayProperty(START_ROW_PROPERTY_NAME, "");
		}
	}

	private void updateEndRow(Optional<Proof> proof) {
		if (proof.isPresent()) {
			if (proof.get().getCreatedMethod().isPresent()) {
				updateDisplayProperty(END_ROW_PROPERTY_NAME, proof.get().getCreatedMethod().get().getLastRow().getDisplayString(true));
			} else {
				updateDisplayProperty(END_ROW_PROPERTY_NAME, "");
			}
		}
		else {
			updateDisplayProperty(END_ROW_PROPERTY_NAME, "");
		}
	}

	private void updateEndStroke(Optional<Proof> proof) {
		if (proof.isPresent()) {
			if (proof.get().getCreatedMethod().isPresent()) {
				updateDisplayProperty(END_STROKE_PROPERTY_NAME, proof.get().getCreatedMethod().get().getLastRow().getStroke().getDisplayString());
			} else {
				updateDisplayProperty(END_STROKE_PROPERTY_NAME, "");
			}
		}
		else {
			updateDisplayProperty(END_STROKE_PROPERTY_NAME, "");
		}
	}

	private void updateProofTime(Optional<Proof> proof) {
		if (proof.isPresent()) {
			updateDisplayProperty(PROOF_TIME_PROPERTY_NAME, String.format("%.3f", (proof.get().getProofTimeMs()) / 1000.0) + " seconds");
		}
		else {
			updateDisplayProperty(PROOF_TIME_PROPERTY_NAME, "");
		}
	}

}