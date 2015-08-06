package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel;

import com.concurrentperformance.fxutils.color.ColorUtil;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentmodel.TouchDocument;
import com.concurrentperformance.ringingmaster.ui.common.TouchStyle;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class TitlePane extends VBox {

	public static final String STYLESHEET = "com/concurrentperformance/ringingmaster/fxui/desktop/documentpanel/titlepane.css";

	private final TextField titleText = new TextField();
	private final TextField authorText = new TextField();

	private TouchDocument touchDocument;

	public TitlePane(TouchDocument touchDocument) {
		this.touchDocument = touchDocument;

		getStylesheets().add(STYLESHEET);

		titleText.setFont(new Font(20));
		//TODO 	titleText.setFontSmoothingType(FontSmoothingType.LCD);
		getChildren().add(titleText);
		titleText.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == false) touchDocument.setTitle(titleText.getText());
		});


		authorText.setFont(new Font(14));
		//TODO authorText.setFontSmoothingType(FontSmoothingType.LCD);
		authorText.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == false) touchDocument.setAuthor(authorText.getText());
		});

		Text space = new Text();
		HBox authorOffset = new HBox(space,authorText);
		authorOffset.setSpacing(20);
		getChildren().add(authorOffset);


		setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		setSpacing(2.0);

		setFocusTraversable(false);
	}

	public void setText(String title, String author) {
		titleText.textProperty().set(title);
		authorText.textProperty().set(author);
	}

	public void setTouchStyle(TouchStyle touchStyle) {
		Color titleColor = touchStyle.getColour(TouchStyle.TouchStyleColor.TITLE);
		titleText.setStyle("-fx-text-inner-color: " + ColorUtil.toWeb(titleColor) + ";");

		Color authorColor = touchStyle.getColour(TouchStyle.TouchStyleColor.AUTHOR);
		authorText.setStyle("-fx-text-inner-color: " + ColorUtil.toWeb(authorColor) + ";");
	}
}
