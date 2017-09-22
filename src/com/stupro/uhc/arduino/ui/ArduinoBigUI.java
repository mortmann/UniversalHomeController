package com.stupro.uhc.arduino.ui;

import com.stupro.uhc.Floor;
import com.stupro.uhc.GUI;
import com.stupro.uhc.arduino.Arduino;
import com.stupro.uhc.network.Network;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
		Button disable = new Button();
		disable.setText("Disable Device!");
		Button activate = new Button();
		activate.setText("Activate Device!");
		disable.setOnAction(x->{
			Alert alert = new Alert(AlertType.CONFIRMATION, " Do you really want to disable this device?\nIt will no longer do it´s job!", ButtonType.YES, ButtonType.NO);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
			    Network.Instance.SendPacketToThisArduino("2_disable", myArduino);
			    myArduino.setActive(false);
			}
			disable.setVisible(false);
			activate.setVisible(true);
		});
		activate.setOnAction(x->{
			Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to activate this device?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
			    Network.Instance.SendPacketToThisArduino("2_activate", myArduino);
			    myArduino.setActive(true);
			}
			disable.setVisible(true);
			activate.setVisible(false);
		});
		top.getChildren().addAll(back,label,floor,disable);
		mainLayout.setTop(top);
	}
	

	public Pane getCenter() {
		return mainLayout;
	}
	
}
