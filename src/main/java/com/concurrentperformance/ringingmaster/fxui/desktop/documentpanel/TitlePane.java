package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class TitlePane extends VBox {
	private final Text titleText = new Text();
	private final Text authorText = new Text();


	public TitlePane() {
		titleText.setFont(new Font(20));
		titleText.setFocusTraversable(false);
		titleText.setFill(Color.BLACK);
		titleText.setFontSmoothingType(FontSmoothingType.LCD);
		getChildren().add(titleText);

		authorText.setFont(new Font(14));
		authorText.setFocusTraversable(false);
		authorText.setFill(Color.DARKBLUE);
		authorText.setFontSmoothingType(FontSmoothingType.LCD);

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
}
