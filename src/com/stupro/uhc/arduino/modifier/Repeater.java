package com.stupro.uhc.arduino.modifier;

import com.stupro.uhc.network.Network;
import com.stupro.uhc.util.NumberTextField;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Repeater extends Modifier {
	static int packetID = 5;
	
	int timeInSeconds = 0;

	private NumberTextField numberText;
	
	public Repeater(int timeInSeconds,int child){
		this.timeInSeconds = timeInSeconds;
	}

	@Override
	public void SendPerNetwork() {
		String data = packetID + "_" + child + "," + timeInSeconds;
		Network.Instance.SendPacketToArduino(data);
	}

	@Override
	public Pane GetPane() {
		GridPane gridPane = new GridPane();
		gridPane.add(new Label("Blink Repeat"), 0, 2);
		numberText = new NumberTextField();
		gridPane.add(numberText, 1, 2);
		return null;
	}

	@Override
	protected void HandleSpecificData(String[] data) {
		for (String string : data) {
			System.out.println(string);
		}		
	}
	
	
	
}
