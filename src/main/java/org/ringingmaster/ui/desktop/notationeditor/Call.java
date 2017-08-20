package org.ringingmaster.ui.desktop.notationeditor;


import com.google.common.collect.Lists;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.NotationCall;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.fxutils.components.PressableButton;
import org.ringingmaster.fxutils.dialog.EditMode;
import org.ringingmaster.fxutils.dialog.SceneLauncher;
import org.ringingmaster.fxutils.table.EnhancedTextFieldTableCell;
import org.ringingmaster.ui.desktop.RingingMasterDesktopApp;
import org.ringingmaster.ui.desktop.leadheadtable.LeadHeadTablePane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class Call extends SkeletalNotationEditorTabController implements NotationEditorTabController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @FXML
    private TableView<CallModel> callsTable;

    @FXML
    private TableColumn<CallModel, String> callNameColumn;
    @FXML
    private TableColumn<CallModel, String> callShorthandColumn;
    @FXML
    private TableColumn<CallModel, String> notationColumn;
    @FXML
    private TableColumn<CallModel, Boolean> defaultColumn;

    @FXML
    private CheckBox cannedCalls;
    @FXML
    private Button addCallButton;
    @FXML
    private Button removeCallButton;
    @FXML
    private PressableButton defaultCallButton;

    @FXML
    private TextField leadHeadCode;

    @Override
    public String getTabName() {
        return "Calls";
    }

    @Override
    public void init(NotationEditorDialog parent, EditMode editMode) {
        super.init(parent, editMode);

        cannedCalls.selectedProperty().addListener(this::useCannedCallsUpdater);
        cannedCalls.selectedProperty().addListener(parent::roundTripDialogDataUpdater);

        callsTable.setPlaceholder(new Label("No Calls Defined"));
        callsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonState();
        });
        leadHeadCode.setDisable(true);

        Callback<TableColumn<CallModel, String>, TableCell<CallModel, String>> editableCellFactory =
                EnhancedTextFieldTableCell.forTableColumn(
                        (value) -> parent.roundTripDialogDataToModelToDialogData(),
                        new DefaultStringConverter());

        callNameColumn.setCellFactory(editableCellFactory);
        callShorthandColumn.setCellFactory(editableCellFactory);
        notationColumn.setCellFactory(editableCellFactory);

        Image flagImage = new Image(checkNotNull(SkeletalNotationEditorTabController.class.getResourceAsStream(checkNotNull("/images/flag.png"))));

        defaultColumn.setCellFactory(new Callback<TableColumn<CallModel, Boolean>, TableCell<CallModel, Boolean>>() {
            @Override
            public TableCell<CallModel, Boolean> call(TableColumn<CallModel, Boolean> param) {
                return new TableCell<CallModel, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.setGraphic((item != null && item) ? new StackPane(new ImageView(flagImage)) : null);
                    }
                };
            }
        });
    }

    public void useCannedCallsUpdater(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        callsTable.setDisable(newValue);
        addCallButton.setDisable(newValue);
        updateButtonState();
    }

    private void updateButtonState() {
        addCallButton.setDisable(cannedCalls.isSelected());

        removeCallButton.setDisable(cannedCalls.isSelected() ||
                callsTable.getSelectionModel().selectedItemProperty().get() == null);

        defaultCallButton.setDisable(cannedCalls.isSelected() ||
                callsTable.getSelectionModel().selectedItemProperty().get() == null);
        defaultCallButton.setPressedOverride(!cannedCalls.isSelected() &&
                callsTable.getSelectionModel().selectedItemProperty().get() != null &&
                callsTable.getSelectionModel().selectedItemProperty().get().getDefaultCall());

    }

    @Override
    public void buildDialogDataFromNotation(NotationBody notation) {
        cannedCalls.setSelected(notation.isCannedCalls());

        int selectedIndex = callsTable.getSelectionModel().selectedIndexProperty().get();
        ObservableList<CallModel> items = callsTable.getItems();
        items.clear();
        Set<NotationCall> calls = notation.getCalls();
        NotationCall defaultCall = notation.getDefaultCall();

        for (NotationCall call : calls) {
            items.add(new CallModel(
                    call.getName(),
                    call.getNameShorthand(),
                    call.getNotationDisplayString(false),
                    (call == defaultCall)));
        }
        callsTable.getSelectionModel().select(selectedIndex);

        leadHeadCode.setText(notation.getLeadHeadCode());
    }

    @Override
    public void buildNotationFromDialogData(NotationBuilder notationBuilder) {

        if (cannedCalls.isSelected()) {
            notationBuilder.setCannedCalls();
        } else {
            for (CallModel call : callsTable.getItems()) {
                notationBuilder.addCall(call.getCallName(),
                        call.getCallShorthand(),
                        call.getNotation(),
                        call.getDefaultCall());
            }
        }
    }

    @FXML
    private void onAddCall() {
        callsTable.getItems().add(0, new CallModel("<CALL>", "<SHORTHAND>", "<NOTATION>", Boolean.FALSE));
    }

    @FXML
    private void onRemoveCall() {
        int selectedIndex = callsTable.getSelectionModel().selectedIndexProperty().get();
        if (selectedIndex == -1) {
            return;
        }

        callsTable.getItems().remove(selectedIndex);
        parent.roundTripDialogDataToModelToDialogData();
    }


    @FXML
    private void onDefaultCall() {
        int selectedIndex = callsTable.getSelectionModel().selectedIndexProperty().get();
        if (selectedIndex == -1) {
            return;
        }

        for (int index = 0; index < callsTable.getItems().size(); index++) {
            callsTable.getItems().get(index).setDefaultCall((index == selectedIndex));
        }
        parent.roundTripDialogDataToModelToDialogData();
    }

    public void onShowLeadHeadTable(ActionEvent actionEvent) {
        new SceneLauncher(new LeadHeadTablePane(), Lists.<String>newArrayList(RingingMasterDesktopApp.STYLESHEET), parent.getStage(), "Lead Head Table");

    }
}