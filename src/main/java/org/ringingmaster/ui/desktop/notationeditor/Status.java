package org.ringingmaster.ui.desktop.notationeditor;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import org.ringingmaster.engine.analyser.Analyser;
import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.ui.desktop.util.ColorManager;
import org.ringingmaster.util.javafx.namevaluepair.NameValuePairModel;
import org.ringingmaster.util.javafx.namevaluepair.NameValuePairTable;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class Status {

    public static final String ERROR_PROPERTY_NAME = "Error";

    private NotationEditorDialog parent;
    private Analyser analyser = new Analyser();

    @FXML
    private NameValuePairTable status;

    public void init(NotationEditorDialog parent) {
        this.parent = parent;
    }

    void updateNotationStats(Notation notation) {
        ObservableList<NameValuePairModel> items = status.getItems();

        // Test build a plain course to see if it is possible.
        CompiledComposition compiledComposition = PlainCourseHelper.buildPlainCourse(notation, "Checking new notation");
        Proof plainCourseProof = analyser.apply(compiledComposition);
        Method plainCourse = plainCourseProof.getCompiledComposition().getMethod().get();

        items.clear();
        items.add(new NameValuePairModel("Name", notation.getNameIncludingNumberOfBells()));
        items.add(new NameValuePairModel("Notation", notation.getNotationDisplayString(true)));
        items.add(new NameValuePairModel("Notation Type", notation.isFoldedPalindrome() ? "symmetric" : "asymmetric"));
        items.add(new NameValuePairModel("Plain Course", plainCourseProof.isTrueComposition() ? "true" : "false"));
        items.add(new NameValuePairModel("Changes in Plain Lead", Integer.toString(plainCourse.getLead(0).getRowCount())));
        items.add(new NameValuePairModel("Changes in Plain Course", Integer.toString(plainCourse.getRowCount())));
        items.add(new NameValuePairModel("Leads in Plain Course", Integer.toString(plainCourse.getLeadCount())));
        items.add(new NameValuePairModel("Number of Hunt Bells", Integer.toString(plainCourse.getNumberOfBellsInHunt())));
        items.add(new NameValuePairModel("Number of Calls", Integer.toString(notation.getCalls().size())));
        status.setSize(200);

        status.setBackground(new Background(new BackgroundFill(ColorManager.getClearHighlight(), CornerRadii.EMPTY, Insets.EMPTY)));
        parent.getStage().setTitle(parent.getEditMode().getEditText() + ": " + notation.getNameIncludingNumberOfBells());
        parent.okButton.setDisable(false);
    }

    void updateNotationStats(Exception e) {
        ObservableList<NameValuePairModel> items = status.getItems();

        items.clear();
        items.add(new NameValuePairModel(ERROR_PROPERTY_NAME));
        status.updateDisplayProperty(ERROR_PROPERTY_NAME, e.getMessage(), ColorManager.getErrorHighlight());

        status.setBackground(new Background(new BackgroundFill(ColorManager.getErrorHighlight(), CornerRadii.EMPTY, Insets.EMPTY)));

        status.setSize(50);

        parent.getStage().setTitle(parent.getEditMode().getEditText() + ": " + parent.lastGoodNotation.getNameIncludingNumberOfBells());
        parent.okButton.setDisable(true);
    }
}

