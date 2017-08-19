package com.stupro.uhc.arduino;

import java.util.ArrayList;

import com.stupro.uhc.arduino.children.Children;
import com.stupro.uhc.arduino.modifier.Modifier;

import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class LED extends Children {

	int red = 0;
	int green = 0;
	int blue = 0;
	//dimmen 
	
	public int getRepeatTimer() {
		return 0;
	}

	ColorPicker cp;
	public LED(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		cp = new ColorPicker();
		cp.setValue(new Color((double)red/255.0, (double)green/255.0, (double)blue/255.0,1));
		this.myModifier = new ArrayList<>();
	}
	public void AddModifier(Modifier m){
		myModifier.add(m);
	}
	
	public double GetRedDouble(){
		return cp.getValue().getRed();
	}
	public double GetGreenDouble(){
		return cp.getValue().getGreen();
	}
	public double GetBlueDouble(){
		return cp.getValue().getBlue();
	}
	@Override
	public Pane GetPane(int number){
		GridPane gridPane = new GridPane();
		gridPane.add(new Label("LED"), 0, 0);
		gridPane.add(new Label((number+1) +"."), 1, 0);
		gridPane.add(new Label("Current Color"), 0, 1);
		gridPane.add(cp, 1, 1);
		gridPane.add(new Label(""), 0, 2);
		gridPane.setPadding(new Insets(10, 10, 10, 10)); 
		return gridPane;
	}

	@Override
	public Object GetValue() {
		return cp.getValue();
	}
}
