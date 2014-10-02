package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.canvas;

import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterGroup;
import com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model.GridCharacterModel;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * TODO
 *
 * @author Stephen
 */
public class CharacterGroupMeasurer {

	public static final float MINIMUM_HEIGHT = 6;

	CharacterGroupMeasurement measureCell(final GridCharacterGroup characterGroup) {
		checkNotNull(characterGroup);
        double height = measureCellHeight(characterGroup);
		double bottomGap = measureBottomGap(characterGroup);
		double[] characterWidths = measureCellTextWidth(characterGroup);
	    int widthPadding = measureCellHorizontalPadding(characterGroup);

        CharacterGroupMeasurement dimension = new CharacterGroupMeasurement(height, bottomGap, characterWidths, widthPadding);
        return dimension;
    }


    private float measureCellHeight(final GridCharacterGroup characterGroup) {
	    float maxHeight = MINIMUM_HEIGHT;
	    for (GridCharacterModel gridCharacterModel : characterGroup) {
		    final Font font = gridCharacterModel.getFont();
		    final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
		    final float height = fm.getDescent() + fm.getAscent();
		    if (height > maxHeight) {
			    maxHeight = height;
		    }
	    }
        return maxHeight;
    }

    private double[] measureCellTextWidth(final GridCharacterGroup characterGroup) {
	    int characterCount = characterGroup.getLength();
	    double[] characterWidths = new double[characterCount];
	    for (int index=0;index<characterGroup.getLength();index++) {
		    GridCharacterModel GridCharacterModel = characterGroup.getGridCharacterModel(index);
		    final Font font = GridCharacterModel.getFont();
		    final char cellText = GridCharacterModel.getCharacter();
		    FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
		    characterWidths[index] = fm.computeStringWidth(String.valueOf(cellText));
	    }
        return characterWidths;
    }

	private int measureCellHorizontalPadding(final GridCharacterGroup characterGroup) {
		return 3; //TODO get from a percentage of the largest character - although it should be consistent in a column.????
	}

    private float measureBottomGap(final GridCharacterGroup characterGroup) {
	    float bottomGap = 0;
	    for (GridCharacterModel GridCharacterModel : characterGroup) {
		    final Font font = GridCharacterModel.getFont();
		    FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
		    final float descent = fm.getMaxDescent();
		    if (bottomGap < descent) {
			    bottomGap = descent;
		    }
	    }
	    return bottomGap;
    }
}