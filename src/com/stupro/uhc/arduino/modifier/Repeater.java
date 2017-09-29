package com.stupro.uhc.arduino.modifier;

import com.stupro.uhc.util.NumberTextField;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Repeater extends Modifier {
	static int packetID = 5;
	
	int timeInSeconds = 0;

	private NumberTextField numberText;
	
	public Repeater(){
		ID = packetID;

	}

	@Override
	public String GetPerNetwork(int child) {
		String data = packetID + "_" + child + "," + timeInSeconds;
//		Network.Instance.SendPacketToArduino(data);
		return data;
	}

	@Override
	protected Pane GetInnerPane() {
		GridPane gridPane = new GridPane();
		numberText = new NumberTextField();
		numberText.textProperty().addListener(x->{
			isActive = true;
		});
		if(isActive)
			numberText.setText(timeInSeconds +" ");
		gridPane.add(new Label("Time: "), 0, 2);
		gridPane.add(numberText, 1, 2);
		return gridPane;
	}

	@Override
	protected void HandleSpecificData(String[] data) {
		isActive = true;
		timeInSeconds = Integer.parseInt(data[0]);		
	}

	@Override
	public String GetName() {
		return "Blink Repeat";
	}

	@Override
	public void Reset() {
		timeInSeconds = 0;
		isActive = false;
	}

	@Override
	public String GetParameterInfo() {
		return ""+timeInSeconds+"";
	}
	
	
	
}
