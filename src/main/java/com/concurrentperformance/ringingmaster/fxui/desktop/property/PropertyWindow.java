package com.concurrentperformance.ringingmaster.fxui.desktop.property;

import com.concurrentperformance.fxutils.propertyeditor.PropertyEditor;
import com.concurrentperformance.fxutils.propertyeditor.SelectionPropertyValue;
import com.concurrentperformance.fxutils.propertyeditor.TextPropertyValue;
import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comments ???
 *
 * @author Lake
 */
public class PropertyWindow extends PropertyEditor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String SETUP_GROUP_NAME = "Setup";
	public static final String ADVANCED_SETUP_GROUP_NAME = "Advanced Setup";

	public PropertyWindow() {

		buildSetupSection();
		buildAdvancedSection();
	}

	private final ChangeListener<String> cl = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue <? extends String> observable, String oldValue, String newValue) {
			log.info(newValue);
		}};


	private void buildSetupSection() {
		final TextPropertyValue title = new TextPropertyValue("Title", cl);
		add(SETUP_GROUP_NAME, title);
		title.setValue("My Touch");

		add(SETUP_GROUP_NAME, new TextPropertyValue("Author", cl));
		final ObservableList<String> numberOfBells = FXCollections.observableArrayList();

		for (NumberOfBells bells : NumberOfBells.values()) {
			numberOfBells.add(bells.getName() + " (" + bells.getBellCount() + ")");
		}
		add(SETUP_GROUP_NAME, new SelectionPropertyValue("Number Of Bells", numberOfBells));

		add(SETUP_GROUP_NAME, new SelectionPropertyValue("Number Of Bells", numberOfBells));

	}

	private void buildAdvancedSection() {
		add(ADVANCED_SETUP_GROUP_NAME, new TextPropertyValue("Wrap Calls", cl));
		final ObservableList<String> strings = FXCollections.observableArrayList(
				"Option 1",
				"Option 2",
				"Option 3"
		);

		strings.addListener(new ListChangeListener<String>() {
			@Override
			public void onChanged(Change<? extends String> c) {
				log.info(c.toString());
			}
		});
		add(ADVANCED_SETUP_GROUP_NAME, new SelectionPropertyValue("Checking Type", strings));
	}
}
