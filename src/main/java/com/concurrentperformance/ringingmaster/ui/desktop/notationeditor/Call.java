package com.concurrentperformance.ringingmaster.ui.desktop.notationeditor;


import com.concurrentperformance.fxutils.components.PressableButton;
import com.concurrentperformance.fxutils.dialog.EditMode;
import com.concurrentperformance.fxutils.table.EnhancedTextFieldTableCell;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationCall;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
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

		Callback<TableColumn<CallModel, String>, TableCell<CallModel, String>> cellFactory =
				EnhancedTextFieldTableCell.forTableColumn(
						(value) -> parent.roundTripDialogDataToModelToDialogData(),
						new DefaultStringConverter());

		callNameColumn.setCellFactory(cellFactory);
		callShorthandColumn.setCellFactory(cellFactory);
		notationColumn.setCellFactory(cellFactory);

		InputStream imageResourceAsStream = SkeletalNotationEditorTabController.class.getResourceAsStream(checkNotNull("/images/flag.png"));
		Image image = new Image(checkNotNull(imageResourceAsStream));


		defaultColumn.setCellFactory(new Callback<TableColumn<CallModel, Boolean>, TableCell<CallModel, Boolean>>() {
			@Override
			public TableCell<CallModel, Boolean> call(TableColumn<CallModel, Boolean> param) {
				return new TableCell<CallModel, Boolean>() {
					@Override
					protected void updateItem(Boolean item, boolean empty) {
//						log.info(item);
						super.setGraphic((item!=null && item)?new ImageView(image):null);
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
		}
		else {
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

		for (int index = 0; index< callsTable.getItems().size(); index++) {
			callsTable.getItems().get(index).setDefaultCall((index == selectedIndex));
		}
		parent.roundTripDialogDataToModelToDialogData();
	}
}