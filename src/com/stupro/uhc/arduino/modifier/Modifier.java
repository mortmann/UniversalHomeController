package com.stupro.uhc.arduino.modifier;

import javafx.scene.layout.Pane;

public abstract class Modifier {
	protected int child;
	public abstract void SendPerNetwork();
	public abstract Pane GetPane();

}
