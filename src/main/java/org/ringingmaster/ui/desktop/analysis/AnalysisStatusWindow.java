package org.ringingmaster.ui.desktop.analysis;

import javafx.application.Platform;
import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.ui.desktop.proof.ProofManager;
import org.ringingmaster.util.javafx.namevaluepair.NameValuePairModel;
import org.ringingmaster.util.javafx.namevaluepair.NameValuePairTable;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AnalysisStatusWindow extends NameValuePairTable {

    public static final String COMPOSITION_TRUE_PROPERTY_NAME = "The composition is";
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
        proofManager.addListener(proofOptional -> Platform.runLater(() -> {
            if (proofOptional.isPresent()) {
                if (getItems().size() == 0) {
                    buildProperties();
                }

                Proof proof = proofOptional.get();
                updateCompositionTrue(proof);
                updateTermination(proof);
                updatePartCount(proof);
                updateLeadCount(proof);
                updateRowCount(proof);
                updateCallCount(proof);
                updateStartRow(proof);
                updateEndRow(proof);
                updateEndStroke(proof);
                updateProofTime(proof);
            } else {
                getItems().clear();
            }
        }));
    }

    private void buildProperties() {
        getItems().add(new NameValuePairModel(COMPOSITION_TRUE_PROPERTY_NAME));
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

    private void updateCompositionTrue(Proof proof) {
        //TODO Reactive
//        if (proof.getAnalysis().isPresent()) {
//            if (proof.isTrueComposition().getAnalysis().get().isTrueComposition()) {
//                updateDisplayProperty(COMPOSITION_TRUE_PROPERTY_NAME, "TRUE", ColorManager.getPassHighlight());
//            } else {
//                updateDisplayProperty(COMPOSITION_TRUE_PROPERTY_NAME, "FALSE", ColorManager.getErrorHighlight());
//            }
//        } else {
//            updateDisplayProperty(COMPOSITION_TRUE_PROPERTY_NAME, "INVALID", ColorManager.getErrorHighlight());
//        }
    }


    private void updateTermination(Proof proof) {
        //TODO Reactive
//        String terminateReasonDisplayString = proof.getTerminateReasonDisplayString();
//
//        switch (proof.getTerminationReason()) {
//            case SPECIFIED_ROW:
//                updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString);
//                break;
//            case ROW_COUNT:
//            case LEAD_COUNT:
//                updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString, ColorManager.getWarnHighlight());
//                updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString, ColorManager.getWarnHighlight());
//                break;
//            case INVALID_COMPOSITION:
//            case EMPTY_PARTS:
//                updateDisplayProperty(TERMINATION_PROPERTY_NAME, terminateReasonDisplayString, ColorManager.getErrorHighlight());
//                break;
//            // TODO this is from C++
//            //			case TR_PARTS:
//            //				str.Format("Part limit (%d)", method->getPartCount());
//            //				addLine("Termination:", str, RGB(255, 120, 255));
//            //				break;
//            //			case TR_CIRCLE:
//            //				addLine("Termination:", "Aborted - Circular composition", RGB(255, 120, 120));
//            //				break;
//
//            default:
//                throw new RuntimeException("Please code for termination reason [" + proof.getTerminationReason() + "]");
//        }
    }

    private void updatePartCount(Proof proof) {
        if (proof.getCompiledComposition().getMethod().isPresent()) {
            updateDisplayProperty(PART_COUNT_PROPERTY_NAME, "TODO");
        } else {
            updateDisplayProperty(PART_COUNT_PROPERTY_NAME, "");
        }
    }

    private void updateLeadCount(Proof proof) {
        if (proof.getCompiledComposition().getMethod().isPresent()) {
            updateDisplayProperty(LEAD_COUNT_PROPERTY_NAME, Integer.toString(proof.getCompiledComposition().getMethod().get().getLeadCount()));
        } else {
            updateDisplayProperty(LEAD_COUNT_PROPERTY_NAME, "");
        }
    }

    private void updateRowCount(Proof proof) {
        if (proof.getCompiledComposition().getMethod().isPresent()) {
            updateDisplayProperty(ROW_COUNT_PROPERTY_NAME, Integer.toString(proof.getCompiledComposition().getMethod().get().getRowCount()));
        } else {
            updateDisplayProperty(ROW_COUNT_PROPERTY_NAME, "");
        }
    }

    private void updateCallCount(Proof proof) {
        if (proof.getCompiledComposition().getMethod().isPresent()) {
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

    private void updateStartRow(Proof proof) {
        if (proof.getCompiledComposition().getMethod().isPresent()) {
            updateDisplayProperty(START_ROW_PROPERTY_NAME, proof.getCompiledComposition().getMethod().get().getFirstRow().get().getDisplayString(true));
        } else {
            updateDisplayProperty(START_ROW_PROPERTY_NAME, "");
        }
    }

    private void updateEndRow(Proof proof) {
        if (proof.getCompiledComposition().getMethod().isPresent()) {
            updateDisplayProperty(END_ROW_PROPERTY_NAME, proof.getCompiledComposition().getMethod().get().getLastRow().get().getDisplayString(true));
        } else {
            updateDisplayProperty(END_ROW_PROPERTY_NAME, "");
        }
    }

    private void updateEndStroke(Proof proof) {
        if (proof.getCompiledComposition().getMethod().isPresent()) {
            updateDisplayProperty(END_STROKE_PROPERTY_NAME, proof.getCompiledComposition().getMethod().get().getLastRow().get().getStroke().getDisplayString());
        } else {
            updateDisplayProperty(END_STROKE_PROPERTY_NAME, "");
        }
    }

    private void updateProofTime(Proof proof) {
        // TODO Reactive updateDisplayProperty(PROOF_TIME_PROPERTY_NAME, String.format("%.3f", (proof.getProofTimeMs()) / 1000.0) + " seconds");
    }

}