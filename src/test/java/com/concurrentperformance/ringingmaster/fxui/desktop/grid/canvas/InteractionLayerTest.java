package com.concurrentperformance.ringingmaster.fxui.desktop.grid.canvas;

import com.concurrentperformance.ringingmaster.fxui.desktop.grid.GridPosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Lake
 */
public class InteractionLayerTest {

	@Test
	public void allCallsHandleEmptyModel() {
		StubGridEditorModel model = new StubGridEditorModel(new String[][]{}, new GridPosition(0,0,0));
		GridPane mockGridPane = mock(GridPane.class);
		when(mockGridPane.getModel()).thenReturn(model);

		InteractionLayer interactionLayer = new InteractionLayer(mockGridPane);
		interactionLayer.moveLeft();
		assertEquals(new GridPosition(0, 0, 0), model.getCaretPosition());
		interactionLayer.moveRight();
		assertEquals(new GridPosition(0,0,0), model.getCaretPosition());
		interactionLayer.moveUp();
		assertEquals(new GridPosition(0,0,0), model.getCaretPosition());
		interactionLayer.moveDown();
		assertEquals(new GridPosition(0,0,0), model.getCaretPosition());
		interactionLayer.moveToStartOfLastCellIfItHasContentsElseLastButOne();
		assertEquals(new GridPosition(0,0,0), model.getCaretPosition());
		interactionLayer.moveToStartOfRow();
		assertEquals(new GridPosition(0,0,0), model.getCaretPosition());
	}

	@Test
	public void rollsRightToNextPosition() {
		String[][] characters = {{"ab", "cd"},
				{"", "e"}};
		StubGridEditorModel model = new StubGridEditorModel(characters, new GridPosition(0,0,0));
		GridPane mockGridPane = mock(GridPane.class);
		when(mockGridPane.getModel()).thenReturn(model);
		InteractionLayer interactionLayer = new InteractionLayer(mockGridPane);

		interactionLayer.moveRight();
		assertEquals(new GridPosition(0, 0, 1), model.getCaretPosition());
		interactionLayer.moveRight();
		assertEquals(new GridPosition(0, 0, 2), model.getCaretPosition());
		interactionLayer.moveRight();
		assertEquals(new GridPosition(1, 0, 0), model.getCaretPosition());
		interactionLayer.moveRight();
		assertEquals(new GridPosition(1, 0, 1), model.getCaretPosition());
		interactionLayer.moveRight();
		assertEquals(new GridPosition(1, 0, 2), model.getCaretPosition());
		interactionLayer.moveRight();
		assertEquals(new GridPosition(0, 1, 0), model.getCaretPosition());
		interactionLayer.moveRight();
		assertEquals(new GridPosition(1, 1, 0), model.getCaretPosition());
		interactionLayer.moveRight();
		assertEquals(new GridPosition(1, 1, 1), model.getCaretPosition());
		interactionLayer.moveRight();
		assertEquals(new GridPosition(1,1,1), model.getCaretPosition());
	}

	@Test
	public void rollsLeftToPreviousPosition() {
		String[][] characters = {{"ab", "cd"},
				{"", "e"}};
		StubGridEditorModel model = new StubGridEditorModel(characters, new GridPosition(1,1,1));
		GridPane mockGridPane = mock(GridPane.class);
		when(mockGridPane.getModel()).thenReturn(model);
		InteractionLayer interactionLayer = new InteractionLayer(mockGridPane);

		interactionLayer.moveLeft();
		assertEquals(new GridPosition(1, 1, 0), model.getCaretPosition());
		interactionLayer.moveLeft();
		assertEquals(new GridPosition(0, 1, 0), model.getCaretPosition());
		interactionLayer.moveLeft();
		assertEquals(new GridPosition(1, 0, 2), model.getCaretPosition());
		interactionLayer.moveLeft();
		assertEquals(new GridPosition(1, 0, 1), model.getCaretPosition());
		interactionLayer.moveLeft();
		assertEquals(new GridPosition(1, 0, 0), model.getCaretPosition());
		interactionLayer.moveLeft();
		assertEquals(new GridPosition(0, 0, 2), model.getCaretPosition());
		interactionLayer.moveLeft();
		assertEquals(new GridPosition(0, 0, 1), model.getCaretPosition());
		interactionLayer.moveLeft();
		assertEquals(new GridPosition(0, 0, 0), model.getCaretPosition());
		interactionLayer.moveLeft();
		assertEquals(new GridPosition(0, 0, 0), model.getCaretPosition());
	}

	@Test
	public void rollsDownToNextRow() {
		String[][] characters = {{"ab", "cd"},
				{"r", "e"},
				{"", "x"},
				{"kwd", "z"}};
		StubGridEditorModel model = new StubGridEditorModel(characters, new GridPosition(0,0,1));
		GridPane mockGridPane = mock(GridPane.class);
		when(mockGridPane.getModel()).thenReturn(model);
		InteractionLayer interactionLayer = new InteractionLayer(mockGridPane);

		interactionLayer.moveRight();//to set the sticky position
		interactionLayer.moveDown();
		assertEquals(new GridPosition(0, 1, 1), model.getCaretPosition());
		interactionLayer.moveDown();
		assertEquals(new GridPosition(0, 2, 0), model.getCaretPosition());
		interactionLayer.moveDown();
		assertEquals(new GridPosition(0, 3, 2), model.getCaretPosition());
		interactionLayer.moveDown();
		assertEquals(new GridPosition(0, 3, 2), model.getCaretPosition());
	}

	@Test
	public void rollsUpToPreviousRow() {
		String[][] characters = {{"ab", "cd"},
				{"", "e"},
				{"w", "x"},
				{"wqs", "z"}};
		StubGridEditorModel model = new StubGridEditorModel(characters, new GridPosition(0,3,1));
		GridPane mockGridPane = mock(GridPane.class);
		when(mockGridPane.getModel()).thenReturn(model);
		InteractionLayer interactionLayer = new InteractionLayer(mockGridPane);

		interactionLayer.moveRight();//to set the sticky position
		interactionLayer.moveUp();
		assertEquals(new GridPosition(0, 2, 1), model.getCaretPosition());
		interactionLayer.moveUp();
		assertEquals(new GridPosition(0, 1, 0), model.getCaretPosition());
		interactionLayer.moveUp();
		assertEquals(new GridPosition(0, 0, 2), model.getCaretPosition());
		interactionLayer.moveUp();
		assertEquals(new GridPosition(0, 0, 2), model.getCaretPosition());
	}

	@Test
	public void movesToStartOfLastCellWhenPopulated() {
		String[][] characters = {{"ab", "cd", "test"},
				{"", "efgh", ""}};
		StubGridEditorModel model = new StubGridEditorModel(characters, new GridPosition(0,0,1));
		GridPane mockGridPane = mock(GridPane.class);
		when(mockGridPane.getModel()).thenReturn(model);
		InteractionLayer interactionLayer = new InteractionLayer(mockGridPane);

		interactionLayer.moveToStartOfLastCellIfItHasContentsElseLastButOne();
		assertEquals(new GridPosition(2, 0, 0), model.getCaretPosition());
		interactionLayer.moveDown();
		assertEquals(new GridPosition(2, 1, 0), model.getCaretPosition());
	}

	@Test
	public void movesToStartOfLastButOneCellWhenUnpopulated() {
		String[][] characters = {{"ab", "cd", "test"},
				{"", "efgh", ""}};
		StubGridEditorModel model = new StubGridEditorModel(characters, new GridPosition(1,1,0));
		GridPane mockGridPane = mock(GridPane.class);
		when(mockGridPane.getModel()).thenReturn(model);
		InteractionLayer interactionLayer = new InteractionLayer(mockGridPane);

		interactionLayer.moveToStartOfLastCellIfItHasContentsElseLastButOne();
		assertEquals(new GridPosition(1, 1, 0), model.getCaretPosition());
		interactionLayer.moveUp();
		assertEquals(new GridPosition(1, 0, 0), model.getCaretPosition());
	}

	@Test
	public void movesToStartOnluOneColumn() {
		String[][] characters = {{"ab"},
				{"",}};
		StubGridEditorModel model = new StubGridEditorModel(characters, new GridPosition(0, 0, 2));
		GridPane mockGridPane = mock(GridPane.class);
		when(mockGridPane.getModel()).thenReturn(model);
		InteractionLayer interactionLayer = new InteractionLayer(mockGridPane);

		interactionLayer.moveToStartOfLastCellIfItHasContentsElseLastButOne();
		assertEquals(new GridPosition(0, 0, 0), model.getCaretPosition());
		interactionLayer.moveUp();
		assertEquals(new GridPosition(0, 0, 0), model.getCaretPosition());
	}

	@Test
	public void movesToStartOfRow() {
		String[][] characters = {{"ab", "cd"},
				{"dl", "efgh"}};
		StubGridEditorModel model = new StubGridEditorModel(characters, new GridPosition(1,0,1));
		GridPane mockGridPane = mock(GridPane.class);
		when(mockGridPane.getModel()).thenReturn(model);
		InteractionLayer interactionLayer = new InteractionLayer(mockGridPane);

		interactionLayer.moveToStartOfRow();
		assertEquals(new GridPosition(0, 0, 0), model.getCaretPosition());
		interactionLayer.moveDown();
		assertEquals(new GridPosition(0, 1, 0), model.getCaretPosition());
	}
}
