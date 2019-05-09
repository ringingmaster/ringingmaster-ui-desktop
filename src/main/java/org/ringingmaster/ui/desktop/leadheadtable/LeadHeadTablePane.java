package org.ringingmaster.ui.desktop.leadheadtable;


import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.LeadHeadCalculator;

import java.util.List;

import static org.ringingmaster.engine.NumberOfBells.BELLS_22;
import static org.ringingmaster.engine.NumberOfBells.BELLS_5;
import static org.ringingmaster.engine.notation.LeadHeadCalculator.LeadHeadValidity.INVALID_LEADHEAD;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class LeadHeadTablePane extends ScrollPane {


    private Font boldFont = Font.font(Font.getDefault().getFamily(), FontWeight.EXTRA_BOLD, Font.getDefault().getSize());

    public LeadHeadTablePane() {
        super();
        setContent(new GridPane() {
            {
                setHgap(5);
                setVgap(3);
                int maxColumns = BELLS_22.toInt() - BELLS_5.toInt();

                List<LeadHeadCalculator.LeadHeadCodes> orderedLeadHeadCodes = LeadHeadCalculator.getOrderedLeadHeadCodes();
                for (int orderedLeadHeadCodesIndex = 0; orderedLeadHeadCodesIndex < orderedLeadHeadCodes.size(); orderedLeadHeadCodesIndex++) {
                    int rowIndex = orderedLeadHeadCodesIndex + 1;
                    String nearLeadHeadCode = orderedLeadHeadCodes.get(orderedLeadHeadCodesIndex).getNear();
                    Label nearCode = new Label(nearLeadHeadCode);
                    nearCode.setFont(boldFont);
                    nearCode.setTextFill(isEven(rowIndex) ? Color.color(.4, .4, .4) : Color.BLACK);
                    getChildren().addAll(nearCode);
                    GridPane.setRowIndex(nearCode, rowIndex);
                    GridPane.setColumnIndex(nearCode, 0);
                    GridPane.setHalignment(nearCode, HPos.CENTER);

                    String extremeLeadHeadCode = orderedLeadHeadCodes.get(orderedLeadHeadCodesIndex).getExtreme();
                    Label extremeCode = new Label(extremeLeadHeadCode);
                    extremeCode.setFont(boldFont);
                    nearCode.setTextFill(isEven(rowIndex) ? Color.color(.4, .4, .4) : Color.BLACK);
                    getChildren().addAll(extremeCode);
                    GridPane.setRowIndex(extremeCode, rowIndex);
                    GridPane.setColumnIndex(extremeCode, maxColumns + 2);
                    GridPane.setHalignment(extremeCode, HPos.CENTER);
                }

                for (int columnIndex = 1; columnIndex <= maxColumns + 1; columnIndex++) {
                    NumberOfBells numberOfBells = NumberOfBells.valueOf(columnIndex + 4);

                    Label header = new Label(numberOfBells.getName());
                    header.setFont(boldFont);
                    getChildren().addAll(header);
                    GridPane.setRowIndex(header, 0);
                    GridPane.setColumnIndex(header, columnIndex);
                    GridPane.setHalignment(header, HPos.CENTER);

                    int rowIndex = 1;
                    for (LeadHeadCalculator.LeadHeadCodes leadHeadCode : orderedLeadHeadCodes) {
                        addRow(columnIndex, rowIndex++, leadHeadCode, numberOfBells);
                    }
                }

            }

            private void addRow(int columnIndex, int rowIndex, LeadHeadCalculator.LeadHeadCodes leadHeadCode, NumberOfBells numberOfBells) {

                String leadHead = "-";
                if (LeadHeadCalculator.getLeadHeadValidity(leadHeadCode.getNear(), numberOfBells) != INVALID_LEADHEAD) {
                    leadHead = LeadHeadCalculator.lookupRowFromCode(leadHeadCode.getNear(), numberOfBells);
                }
                Label label = new Label(leadHead);
                label.setTextFill(isEven(rowIndex) ? Color.color(.4, .4, .4) : Color.BLACK);
                getChildren().addAll(label);
                GridPane.setRowIndex(label, rowIndex);
                GridPane.setColumnIndex(label, columnIndex);
                GridPane.setHalignment(label, HPos.CENTER);
            }

            public boolean isEven(int value) {
                return (value % 2) == 0;
            }


        });


        setPrefHeight(610);
        setPrefWidth(600);

    }
}
