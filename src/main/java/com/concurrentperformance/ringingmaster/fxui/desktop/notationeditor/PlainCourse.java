package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;


import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO Comments
 *
 * @author Lake
 */


public class PlainCourse {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@FXML
	private TextField name;
	@FXML
	private TextField shorthand;
	@FXML
	private TextField notation;
	@FXML
	private TextField leadend;

	private NotationEditorDialog parent;

	public void init(NotationBody notation, NotationEditorDialog parent) {
		checkNotNull(notation);
		checkState(this.parent == null, "Don't init more than once");
		this.parent = checkNotNull(parent);
		name.setText(notation.getName());
		shorthand.setText(notation.getSpliceIdentifier());
		this.notation.setText(notation.getNotationDisplayString(false));
//		this.leadend.setText( notation.get(false));
	}

	@FXML
	protected void update() {
		parent.update();
	}

	public void build(NotationBuilder notationBuilder) {
		notationBuilder.setName(name.getText());
	}
}