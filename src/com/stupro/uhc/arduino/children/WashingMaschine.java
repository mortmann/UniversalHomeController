package com.stupro.uhc.arduino.children;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.stupro.uhc.arduino.modifier.Modifier;
//import com.stupro.uhc.arduino.modifier.Timer;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;

public class WashingMaschine extends Child {
	List<String> materials;
	boolean time;
	List<String> temperature;
	List<String> frequency;

	double freqValue;
	int minuteValue;
	int red[] = new int[4];
	int green[] = new int[4];
	int blue[] = new int[4];
	int maxBrightness[] = new int[] { 255, 255, 255, 255 };
	int child[] = new int[4];
	Timer t = new Timer();
	
	public WashingMaschine(String metaData) {
		ArrayList<Modifier> mods = new ArrayList<>();
		name = "WaschingMaschine";
		String materialMeta = metaData.substring(0, 2);
		switch (materialMeta) {
		case "01":
			materials = Arrays.asList("Polyester", "Wool");
			break;
		case "00":
			materials = Arrays.asList("Cottons",  "Polyester", "Wool");
			break;
		default:
			break;
		}

		String timeMeta = metaData.substring(4, 5);
		switch (timeMeta) {
		case "0":
			time = false;
			break;
		case "1":
			time = true;
			break;
		default:
			break;
		}

		String frequencyMeta = metaData.substring(2, 3);
		switch (frequencyMeta) {
		case "1":
			frequency = Arrays.asList("400 rpm", "800 rpm");
			break;
		case "0":
			frequency = Arrays.asList("400 rpm", "800 rpm", "1000 rpm");
			break;
		default:
			break;
		}

		String temperatureMeta = metaData.substring(2, 3);
		switch (temperatureMeta) {
		case "0":
			temperature = Arrays.asList("60C°", "40C°", "30C°");
			break;
		case "1":
			temperature = Arrays.asList("95C°", "60C°", "40C°", "30C°");
			break;
		default:
			break;
		}


	}

	@Override
	public Node GetInnerPane() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		// Buttons
		Button startButton = new Button("Start");
		GridPane.setConstraints(startButton, 1, 8);
		grid.setStyle("-fx-background-color: #98CCFD;");
		Button stopButton = new Button("Stop");
		GridPane.setConstraints(stopButton, 2, 8);

		// Wash Settings
		ChoiceBox<String> choiceBox = new ChoiceBox<>();
		GridPane.setConstraints(choiceBox, 0, 4);
		choiceBox.getItems().addAll(materials);
		choiceBox.setValue("Cottons");

		ChoiceBox<String> choiceBox2 = new ChoiceBox<>();
		GridPane.setConstraints(choiceBox2, 1, 4);
		choiceBox2.getItems().addAll(temperature);
		choiceBox2.setValue("60C°");

		ChoiceBox<String> choiceBox3 = new ChoiceBox<>();
		GridPane.setConstraints(choiceBox3, 2, 4);
		choiceBox3.getItems().addAll("60 Minutes", "120 Minutes", "30 Minutes");
		choiceBox3.setValue("120 Minutes");

		ChoiceBox<String> choiceBox4 = new ChoiceBox<>();
		GridPane.setConstraints(choiceBox4, 3, 4);
		choiceBox4.getItems().addAll(frequency);
		choiceBox4.setValue("800 rpm");
		// Labels
		Label label = new Label("Material");
		GridPane.setConstraints(label, 0, 3);

		Label label1 = new Label("Temperature");
		GridPane.setConstraints(label1, 1, 3);

		Label label2 = new Label("Time");
		GridPane.setConstraints(label2, 2, 3);

		Label label3 = new Label("frequency");
		GridPane.setConstraints(label3, 3, 3);

		/*
		 * if(time){
		 * 
		 * Timer timer = new Timer();
		 * 
		 * TitledPane titled = new TitledPane(); titled.setText("Timer");
		 * GridPane.setConstraints(titled, 0, 8); }
		 */

		// Add everything to grid
		startButton.setOnAction(e -> convertForController(choiceBox4.getValue(), choiceBox.getValue(),
				choiceBox2.getValue(), choiceBox3.getValue()));
		stopButton.setOnAction(e -> convertForController("stop", "stop", "stop", "stop"));

		grid.getChildren().addAll(choiceBox, choiceBox2, choiceBox3, choiceBox4, startButton, stopButton, label, label1,
				label2, label3);
		grid.minWidth(500);
		grid.maxHeight(500);
		return grid;
	}

	private Object convertForController(String freq, String mat, String temp, String min) {
		System.out.println(freq +" "+ mat+"  "+ temp+ " " + min);
		if (freq == "stop") {
			for (int i = 0; i < red.length; i++) {
				red[i] = 0;
				green[i] = 0;
				blue[i] = 0;
			}
		}
		else{
			red[0]=0;
			green[0]=255;
			blue[0]=0;
		switch (freq) {
		case "400 rpm":
			red[3] = 255;
			green[3] = 255;
			blue[3] = 255;
			freqValue = 1;
			break;
		case "800 rpm":
			red[3] = 255;
			green[3] = 255;
			blue[3] = 255;
			freqValue = 0.8;
			break;
		case "1000 rpm":
			red[3] = 255;
			green[3] = 255;
			blue[3] = 255;
			freqValue = 0.4;
			break;
		}
		switch (mat) {
		case "Cottons":
			red[1] = 0;
			green[1] = 255;
			blue[1] = 255;
			break;
		case "Wool":
			red[1] = 204;
			green[1] = 255;
			blue[1] = 229;
			break;
		case "Polyester":
			red[1] = 0;
			green[1] = 255;
			blue[1] = 255;
			break;

		}
		switch (temp) {
		case "95C°":
			red[2] = 255;
			green[2] = 0;
			blue[2] = 0;
			break;
		case "60C°":
			red[2] = 255;
			green[2] = 128;
			blue[2] = 0;
			break;
		case "40C°":
			red[2] = 255;
			green[2] = 255;
			blue[2] = 0;
			break;
		case "30C°":
			red[2] = 125;
			green[2] = 255;
			blue[2] = 0;
			break;
		}
		switch (min) {
		case "60 Minutes":
			minuteValue = 60;
			t.scheduleAtFixedRate(task,1000,1000);
			break;
		case "120 Minutes":
			minuteValue = 120;
			break;
		case "30 Minutes":
			minuteValue = 30;
			break;
		}
		}

		return null;
	}
	
	
		TimerTask task = new TimerTask(){
			public void run(){
				
				if(minuteValue == 0){
				for (int i = 0; i < red.length; i++) {
					red[i] = 0;
					green[i] = 0;
					blue[i] = 0;
				}
				}
				minuteValue --;
			}
		};
	

	@Override
	public Object GetValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void HandleSpecificData(String[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String GetSpecificData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> GetDatapackages(int child) {
		ArrayList<String> myStrings = new ArrayList<String>();
		String myData = "4_" + 1 + "," + red[0] + "," + green[0] + "," + blue[0] + "," + maxBrightness[0];
		String myData1 = "4_" + 1 + "," + red[1] + "," + green[1] + "," + blue[1] + "," + maxBrightness[1];
		String myData2 = "4_" + 1 + "," + red[2] + "," + green[2] + "," + blue[2] + "," + maxBrightness[2];
		String myData3 = "4_" + 1 + "," + red[3] + "," + green[3] + "," + blue[3] + "," + maxBrightness[3];

//		for (int id : idToMyModifier.keySet()) {
//			ArrayList<Modifier> mods = idToMyModifier.get(id);
//			String temp = "";
//			for (Modifier m : mods) {
//				if (m.isActive() == false) {
//					continue;
//				}
//				temp += m.GetPerNetwork(child);
//			}
//			
//		}
		String temp;
		temp = 5 + "_" + 3 + "," + freqValue;
		myStrings.add(temp);
		myStrings.add(myData);
		myStrings.add(myData1);
		myStrings.add(myData2);
		myStrings.add(myData3);
		return myStrings;
	}
}
