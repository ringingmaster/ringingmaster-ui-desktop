package org.ringingmaster.ui.desktop.documentpanel.grid.model;


import javafx.scene.paint.Color;
import org.ringingmaster.ui.desktop.documentpanel.grid.GridPosition;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public interface GridModel {

    /**
     * Get the number of columns (not lines)
     *
     * @return int number of columns.
     */
    int getColumnCount();

    /**
     * Get the number of rows (not lines)
     *
     * @return int number of rows.
     */
    int getRowCount();

    boolean isZeroSized();

    /**
     * Get the color of the lines that form the grid.
     *
     * @return Color
     */
    Color getGridColor();

    GridCellModel getCellModel(int column, int row);

    GridCharacterModel getCharacterModel(GridPosition gridPosition);

    GridCharacterGroup getRowHeader(int row);

    GridPosition getCaretPosition();

    void setCaretPosition(GridPosition newPosition);

    GridPosition getSelectionStartPosition();

    void setSelectionEndPosition(GridPosition gridPosition);

    boolean isSelection();


    void registerListener(GridModelListener listener);

    void deRegisterListener(GridModelListener listener);

}
