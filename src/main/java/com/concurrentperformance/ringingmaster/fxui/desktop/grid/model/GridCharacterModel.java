package com.concurrentperformance.ringingmaster.fxui.desktop.grid.model;


import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * TODO comments???
 * User: Stephen
 */
public interface GridCharacterModel {

	public char getCharacter();

	/**
	 * Get the font that will render this character.
	 * @return
	 */
	public Font getFont();

	/**
	 * Get the colour of the font that will render this cell.
	 * @return
	 */
	public Color getColor();
}
