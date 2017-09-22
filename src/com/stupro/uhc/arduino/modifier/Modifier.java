package com.stupro.uhc.arduino.modifier;

import javafx.scene.layout.Pane;

public abstract class Modifier {
	protected int child;
	protected int ID;
	public abstract void SendPerNetwork();
	public abstract Pane GetPane();
	//data looks like this [;;][;;][;;]...
	public void HandleData(String data){
		data = data.replaceAll("]", ""); // remove all closing ]
		String[] specificData = data.split("[");
		// data is now only ;;; ;;; ;;; 
		for (String string : specificData) {
			HandleSpecificData(string.split(";"));
		}
	}
	
	protected abstract void HandleSpecificData(String[] data);
	
	public int getID() {
		return ID;
	}
}
