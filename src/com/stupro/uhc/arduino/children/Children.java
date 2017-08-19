package com.stupro.uhc.arduino.children;

import java.util.List;

import com.stupro.uhc.arduino.modifier.Modifier;

import javafx.scene.layout.Pane;

public abstract class Children {
	protected boolean active;
	protected List<Modifier> myModifier;
	public abstract Pane GetPane(int number);
	public abstract Object GetValue();
	
	public List<Modifier> getMyModifier() {
		return myModifier;
	}
	public void setMyModifier(List<Modifier> myModifier) {
		this.myModifier = myModifier;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean isActive) {
		active = isActive;
	}
}
