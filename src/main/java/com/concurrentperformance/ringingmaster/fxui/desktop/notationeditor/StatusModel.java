package com.concurrentperformance.ringingmaster.fxui.desktop.notationeditor;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class StatusModel {

	private final String name;
	private final String value;


	public StatusModel(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
