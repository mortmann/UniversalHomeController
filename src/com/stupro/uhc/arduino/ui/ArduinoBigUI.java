package com.stupro.uhc.arduino.ui;

import com.stupro.uhc.Floor;
import com.stupro.uhc.GUI;
import com.stupro.uhc.arduino.Arduino;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class ArduinoBigUI {
	
	Arduino myArduino;
	private BorderPane mainLayout;
	
	public ArduinoBigUI(Arduino arduino){
		myArduino = arduino;
		mainLayout = new BorderPane();
		mainLayout.setCenter(myArduino.GetBigLayout());
		
		HBox top = new HBox();
		Button back = new Button();
		back.setText("<-");
		Label label = new Label("Floor:");
		ComboBox<Floor> floor = new ComboBox<>(GUI.Instance.getMyHouse().getFloors());
		back.setOnAction(x->{GUI.Instance.changeToHouse();});
		top.getChildren().addAll(back,label,floor);
		mainLayout.setTop(top);
	}
	

	public Pane getCenter() {
		return mainLayout;
	}
	
}
