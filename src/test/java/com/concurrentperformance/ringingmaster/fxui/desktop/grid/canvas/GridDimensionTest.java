package com.concurrentperformance.ringingmaster.fxui.desktop.grid.canvas;

import com.concurrentperformance.ringingmaster.fxui.desktop.grid.GridPosition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class GridDimensionTest {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void rowHeaderOffsetIsCorrect() {

		String[][] characters = {{"abc"},
								 {"d"}};
		String[] rowHeaders = {"12", "34"};


		StubGridEditorModel model = new StubGridEditorModel(characters, rowHeaders, new GridPosition(0,0,0));

		GridDimension d = new GridDimensionBuilder().setModel(model).build();

		assertEquals(model.getRowCount(), d.getRowCount());
		assertEquals(model.getColumnCount(), d.getColumnCount());

		assertEquals(d.getTableLeft(), d.getTableVerticalLinePosition(0), 0.0);
		assertEquals(d.getTableTop(), d.getTableHorizontalLinePosition(0), 0.0);

		assertEquals(d.getRowHeaderLeft(), 0.0, 0.0);

		double lastVerticalLinePosition = d.getRowHeaderLeft();
		for (int i=0;i<d.getColumnCount()+1;i++) {
			final double verticalLinePosition = d.getTableVerticalLinePosition(i);
			log.info("column index[{}], pos[{}]", i, verticalLinePosition);
			assertTrue(verticalLinePosition > (lastVerticalLinePosition + 2));// The +2 is to enforce the minimum padding between columns
			lastVerticalLinePosition = verticalLinePosition;
		}

		double lastHorizontalLinePosition = 0.0;
		for (int i=1;i<d.getRowCount()+1;i++) {
			final double horizontalLinePosition = d.getTableHorizontalLinePosition(i);
			log.info("row index[{}], pos[{}]", i, horizontalLinePosition);
			assertTrue(horizontalLinePosition > (lastHorizontalLinePosition + 2));// The +2 is to enforce the minimum padding between columns
			lastHorizontalLinePosition = horizontalLinePosition;
		}

	}
}
