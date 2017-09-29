package com.stupro.uhc.arduino.modifier;

import com.stupro.uhc.network.Network;
import com.stupro.uhc.util.NumberTextField;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Timer extends Modifier {

	static int packetID = 6;
	
	private NumberTextField startHRText;
	private NumberTextField startMINText;
	private NumberTextField endHRText;
	private NumberTextField endMINText;

	private int startHR;
	private int startMIN;
	private int endHR;
	private int endMIN;

	
	public Timer(){
		ID = packetID;

	}
	
	@Override
	public String GetPerNetwork(int child) {
		String data = packetID+"_"+child+","+startHR+","+startMIN+","+endHR+","+endMIN;
		return data;
	}
	

	@Override
	protected Pane GetInnerPane() {
		GridPane gridPane = new GridPane();
		startHRText = new NumberTextField(startHR+"",2,24);
		gridPane.add(startHRText, 0, 1);
		startMINText = new NumberTextField(startMIN+"",2,59);
		gridPane.add(startMINText, 1, 1);
		endHRText = new NumberTextField(endHR+"",2,24);
		gridPane.add(endHRText, 0, 2);
		endMINText = new NumberTextField(endMIN+"",2,59);
		gridPane.add(endMINText, 1, 2);
		startHRText.textProperty().addListener((observable, oldValue, newValue) -> {
			setActive(true);
			startHR = startHRText.GetIntValue();
		});
		startMINText.textProperty().addListener((observable, oldValue, newValue) -> {
			setActive(true);
			startMIN = startMINText.GetIntValue();
		});
		endHRText.textProperty().addListener((observable, oldValue, newValue) -> {
			setActive(true);
			endHR = endHRText.GetIntValue();
		});
		endMINText.textProperty().addListener((observable, oldValue, newValue) -> {
			setActive(true);
			endMIN = endMINText.GetIntValue();
		});
		return gridPane;
	}

	@Override
	protected void HandleSpecificData(String[] data) {
		isActive = true;
		startHR = Integer.parseInt(data[0]);
		startMIN= Integer.parseInt(data[1]);
		endHR= Integer.parseInt(data[2]);
		endMIN= Integer.parseInt(data[3]);
	}

	@Override
	public String GetName() {
		return "Timer";
	}

	@Override
	public void Reset() {
		startHRText.setText("0");
		startMINText.setText("0");
		endHRText.setText("0");
		endMINText.setText("0");
		isActive=false;
	}

	@Override
	protected String GetParameterInfo() {
		return 	startHR+";"+startMIN+";"+endHR+";"+endMIN;
	}

}
