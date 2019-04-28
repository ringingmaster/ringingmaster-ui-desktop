package org.ringingmaster.ui.desktop.documentpanel.grid;


import javax.annotation.concurrent.Immutable;

/**
 * TODO
 *
 * @author Stephen
 */
@Immutable
public class GridPosition implements Comparable<GridPosition> {

    private final int row;
    private final int column;
    private final int characterIndex;

    public GridPosition(int row, int column, int characterIndex) { //TODO Reactive switch row and col
        this.row = row;
        this.column = column;
        this.characterIndex = characterIndex;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public int getCharacterIndex() {
        return characterIndex;
    }

    @Override
    public int compareTo(GridPosition other) {
        int compare = row - other.row;
        if (compare != 0) {
            return compare;
        }
        compare = column - other.column;
        if (compare != 0) {
            return compare;
        }
        return characterIndex - other.characterIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GridPosition that = (GridPosition) o;

        if (characterIndex != that.characterIndex) return false;
        if (column != that.column) return false;
        return row == that.row;

    }

    @Override
    public int hashCode() {
        int result = column;
        result = 31 * result + row;
        result = 31 * result + characterIndex;
        return result;
    }

    @Override
    public String toString() {
        return "[c" + column +
                ", r" + row +
                ", " + characterIndex +
                "]";
    }
}
