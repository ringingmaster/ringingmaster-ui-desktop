package org.ringingmaster.ui.desktop.notationeditor;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Window;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.ui.desktop.RingingMasterDesktopApp;
import org.ringingmaster.ui.desktop.notationsearch.NotationLibraryManager;
import org.ringingmaster.util.javafx.dialog.EditMode;
import org.ringingmaster.util.javafx.dialog.SkeletalDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class NotationEditorDialog extends SkeletalDialog<Notation> {

    public static final String NOTATION_EDITOR_FXML = "NotationEditorDialog.fxml";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private NotationLibraryManager notationLibraryManager;

    protected Notation lastGoodNotation;
    private List<NotationEditorTabController> tabControllers = new ArrayList<>();
    private Status statusController;

    @FXML
    private TabPane editorTabs;
    @FXML
    private TabPane statusTabs;
    @FXML
    protected Button okButton;

    public static void showDialog(EditMode editMode, Notation model, Window owner,
                                  Function<Notation, Boolean> onSuccessHandler,
                                  NotationLibraryManager notationLibraryManager) {

        NotationEditorDialog notationEditorDialog = new DialogBuilder<Notation, NotationEditorDialog>().buildDialog(editMode, model, owner, NotationEditorDialog.class.getResource(NOTATION_EDITOR_FXML),
                Lists.newArrayList(RingingMasterDesktopApp.STYLESHEET), onSuccessHandler);
        notationEditorDialog.setNotationLibraryManager(checkNotNull(notationLibraryManager));
        notationEditorDialog.showAndWait();
    }

    protected void initialiseDialog(EditMode editMode, Notation notation) {
        try {
            addEditorTabs();
            addStatusTabs();

            for (NotationEditorTabController tabController : tabControllers) {
                tabController.init(this, editMode);
            }
        } catch (IOException e) {
            log.error("Error building dialog structure for [" + notation + "]", e);
        }
    }

    private void addEditorTabs() throws IOException {
        addEditorTab("PlainCourse.fxml");
        addEditorTab("Call.fxml");
        addEditorTab("CallPointRow.fxml");
        addEditorTab("CallPointMethod.fxml");
        addEditorTab("CallPointAggregate.fxml");
        addEditorTab("SplicePoint.fxml");
        addEditorTab("LeadLinePoint.fxml");
        addEditorTab("CourseHeadPoint.fxml");
    }

    private void addEditorTab(String fxmlFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Node content = fxmlLoader.load();
        NotationEditorTabController controller = fxmlLoader.getController();
        tabControllers.add(controller);

        Tab tab = new Tab();
        tab.setText(controller.getTabName());
        tab.setContent(content);

        editorTabs.getTabs().add(tab);
    }

    private void addStatusTabs() throws IOException {
        //Status
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Status.fxml"));
        Node content = fxmlLoader.load();
        statusController = fxmlLoader.getController();
        statusController.init(this);

        Tab statusTab = new Tab();
        statusTab.setText("Status");
        statusTab.setContent(content);

        statusTabs.getTabs().add(statusTab);

        //Diagram
        Tab diagramTab = new Tab();
        diagramTab.setText("Diagram - TODO");
        //diagramTab.setContent(TODO);

        statusTabs.getTabs().add(diagramTab);
    }


    public void roundTripDialogDataOnFocusLossUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (Objects.equal(Boolean.FALSE, newValue)) {
            roundTripDialogDataToModelToDialogData();
        }
    }

    public void roundTripDialogDataUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        roundTripDialogDataToModelToDialogData();
    }

    protected void roundTripDialogDataToModelToDialogData() {
        Notation notation = buildModelFromDialogData();
        if (notation != null) {
            populateDialogFromModel(notation);
        }
    }

    protected Notation buildModelFromDialogData() {
        try {
            NotationBuilder notationBuilder = NotationBuilder.getInstance();
            for (NotationEditorTabController tabController : tabControllers) {
                tabController.buildNotationFromDialogData(notationBuilder);
            }

            Notation notationBody = notationBuilder.build();
            lastGoodNotation = notationBody;
            statusController.updateNotationStats(notationBody);
            return notationBody;
        } catch (Exception e) {
            log.info("Problem with checking notation [{}]", e.getMessage());
            log.debug("", e);
            statusController.updateNotationStats(e);
            return null;
        }
    }

    protected void populateDialogFromModel(Notation notation) {
        for (NotationEditorTabController tabController : tabControllers) {
            tabController.buildDialogDataFromNotation(notation);
        }
    }


    public NotationLibraryManager getNotationLibraryManager() {
        return checkNotNull(notationLibraryManager);
    }

    public void setNotationLibraryManager(NotationLibraryManager notationLibraryManager) {
        this.notationLibraryManager = notationLibraryManager;
    }
}
