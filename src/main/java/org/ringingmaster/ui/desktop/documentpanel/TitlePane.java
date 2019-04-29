package org.ringingmaster.ui.desktop.documentpanel;

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
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.util.javafx.color.ColorUtil;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class TitlePane extends VBox {

    public static final String STYLESHEET = "org/ringingmaster/ui/desktop/documentpanel/titlepane.css";

    private final TextField titleText = new TextField();
    private final TextField authorText = new TextField();


    public TitlePane() {
        getStylesheets().add(STYLESHEET);

        titleText.setFont(new Font(20));
        //TODO 	titleText.setFontSmoothingType(FontSmoothingType.LCD);
        getChildren().add(titleText);

        authorText.setFont(new Font(14));
        //TODO authorText.setFontSmoothingType(FontSmoothingType.LCD);

        Text space = new Text();
        HBox authorOffset = new HBox(space, authorText);
        authorOffset.setSpacing(20);
        getChildren().add(authorOffset);


        setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        setSpacing(2.0);

        setFocusTraversable(false);
    }

    public void init(ObservableComposition observableComposition) {
        observableComposition.observable().subscribe(composition -> {
            titleText.textProperty().set(composition.getTitle());
            authorText.textProperty().set(composition.getAuthor());
        });

        titleText.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) observableComposition.setTitle(titleText.getText());
        });

        authorText.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false) observableComposition.setAuthor(authorText.getText());
        });

    }

    // TODO make style reactive
    public void setCompositionStyle(CompositionStyle compositionStyle) {
        Color titleColor = compositionStyle.getColour(CompositionStyle.CompositionStyleColor.TITLE);
        titleText.setStyle("-fx-text-inner-color: " + ColorUtil.toWeb(titleColor) + ";");

        Color authorColor = compositionStyle.getColour(CompositionStyle.CompositionStyleColor.AUTHOR);
        authorText.setStyle("-fx-text-inner-color: " + ColorUtil.toWeb(authorColor) + ";");
    }
}
