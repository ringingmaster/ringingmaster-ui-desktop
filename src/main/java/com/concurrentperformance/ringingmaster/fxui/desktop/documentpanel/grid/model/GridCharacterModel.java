package com.concurrentperformance.ringingmaster.fxui.desktop.documentpanel.grid.model;


import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Optional;
import java.util.Set;

/**
 * TODO comments???
 * User: Stephen
 */
public interface GridCharacterModel {

	char getCharacter();

	/**
	 * Get the font that will render this character.
	 */
	Font getFont();

	/**
	 * Get the colour of the font that will render this cell.
	 */
	Color getColor();

	/**
	 * Additional Styles that can't be represented in the Font
	 */
	Set<AdditionalStyleType> getAdditionalStyle();

	/**
	 * Tooltip Text
	 */
	Optional<String> getTooltipText();
}
