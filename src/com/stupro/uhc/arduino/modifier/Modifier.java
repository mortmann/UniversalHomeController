package com.stupro.uhc.arduino.modifier;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public abstract class Modifier {
	protected int ID;
	public abstract String GetPerNetwork(int child);
	public abstract String GetName();
	public abstract void Reset();
	protected boolean isActive;
	protected abstract String GetParameterInfo();
	public String GetParameterString(){
		return "("+ID+"["+ GetParameterInfo() + "]"+")";
	}
	
	public Pane GetPane(){
		HBox hbox = new HBox();
		hbox.getChildren().add(GetInnerPane());
		Glyph ng  = new FontAwesome().create(FontAwesome.Glyph.TRASH);
		Button b = new Button("",ng);
		hbox.getChildren().add(b);
		return hbox;
	}
	
	protected abstract Pane GetInnerPane();
	
	//data looks like this ;;; ...
	public void HandleData(String data){
		if(data.trim().isEmpty()){
			return;
		}
		HandleSpecificData(data.split(";"));
	}
	
	protected abstract void HandleSpecificData(String[] data);
	
	public int getID() {
		return ID;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
