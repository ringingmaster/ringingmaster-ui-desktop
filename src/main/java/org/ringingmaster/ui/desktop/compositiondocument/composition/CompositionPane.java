package org.ringingmaster.ui.desktop.compositiondocument.composition;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.ringingmaster.ui.common.CompositionStyle;
import org.ringingmaster.ui.desktop.compositiondocument.CompositionDocument;
import org.ringingmaster.ui.desktop.compositiondocument.composition.gridmodel.DefinitionGridModel;
import org.ringingmaster.ui.desktop.compositiondocument.composition.gridmodel.MainGridModel;
import org.ringingmaster.util.javafx.color.ColorUtil;
import org.ringingmaster.util.javafx.grid.canvas.GridPane;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class CompositionPane extends ScrollPane {

    public static final String STYLESHEET = "org/ringingmaster/ui/desktop/compositiondocument/composition/titlepane.css"; //TODO using wrong CSS


    private MainGridModel mainGridModel;
    private DefinitionGridModel definitionModel;


    // UI components
    private final TitlePane titlePane = new TitlePane();
    private final GridPane gridPane = new GridPane("Main");
    private final TextField definitionText = new TextField("Definitions"); //TODO separate out
    private final GridPane definitionPane = new GridPane("Definition");


    public CompositionPane() {
    }

    public void init(CompositionDocument compositionDocument) {

        layoutNodes();

        titlePane.setCompositionStyle(compositionDocument.getCompositionStyle());
        titlePane.init(compositionDocument.getMutableComposition());

        mainGridModel = new MainGridModel(compositionDocument);
        gridPane.setModel(mainGridModel);

        //TODO package in its own class
        definitionText.getStylesheets().add(STYLESHEET);
        definitionText.setFont(new Font(14));
        // TODO make style reactive
        Color titleColor = compositionDocument.getCompositionStyle().getColour(CompositionStyle.CompositionStyleColor.DEFINITION);
        definitionText.setStyle("-fx-text-inner-color: " + ColorUtil.toWeb(titleColor) + ";");

        definitionModel = new DefinitionGridModel(compositionDocument);
        definitionPane.setModel(definitionModel);

    }

    private void layoutNodes() {

        HBox definitionLayout = new HBox(10, new Pane(), definitionPane);

        VBox verticalLayout = new VBox(titlePane, gridPane, new Pane(), definitionText, definitionLayout);
        verticalLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        verticalLayout.setSpacing(20.0);

        BorderPane border = new BorderPane(verticalLayout);
        BorderPane.setMargin(verticalLayout, new Insets(20));
        border.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        setContent(border);
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        setFitToHeight(true);
        setFitToWidth(true);
        setFocusTraversable(false);
    }

}
