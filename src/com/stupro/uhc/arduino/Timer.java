package com.stupro.uhc.arduino;

import com.stupro.uhc.arduino.modifier.Modifier;
import com.stupro.uhc.network.Network;
import com.stupro.uhc.util.NumberTextField;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Timer extends Modifier {

	static int packetID = 6;
	
	private NumberTextField startHRText;
	private NumberTextField startMINText;
	private NumberTextField endHRText;
	private NumberTextField endMINText;

	public Timer(int child){
		this.child = child;
	}
	
	@Override
	public void SendPerNetwork() {
		String data = packetID+"_"+startHRText.getText()+","+startMINText.getText()+","+endHRText.getText()+","+endMINText.getText();
		Network.Instance.SendPacketToArduino(data);
	}

	@Override
	public Pane GetPane() {
		GridPane gridPane = new GridPane();
		gridPane.add(new Label("Blink Repeat"), 0, 0);
		startHRText = new NumberTextField("12",2,24);
		gridPane.add(startHRText, 0, 1);
		startMINText = new NumberTextField("00",2,59);
		gridPane.add(startMINText, 1, 1);
		endHRText = new NumberTextField("12",2,24);
		gridPane.add(endHRText, 0, 2);
		endMINText = new NumberTextField("30",2,59);
		gridPane.add(endMINText, 1, 2);
		Button b2 = new Button("Save Timer");
		b2.setOnAction(x-> SendPerNetwork());
		gridPane.add(b2, 1, 3);
		return gridPane;
	}

}
