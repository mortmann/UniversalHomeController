package com.stupro.uhc.arduino.ui;

import com.stupro.uhc.Floor;
import com.stupro.uhc.GUI;
import com.stupro.uhc.arduino.Arduino;
import com.stupro.uhc.network.Network;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class ArduinoBigUI {
	
	Arduino myArduino;
	private BorderPane mainLayout;
	
	public ArduinoBigUI(Arduino arduino){
		myArduino = arduino;
		mainLayout = new BorderPane();
		mainLayout.setCenter(GetCenterLayout());
		
		HBox top = new HBox();
		Button back = new Button();
		back.setText("<-");
		Label label = new Label("	Floor: ");
		label.setMaxHeight(50);
		ComboBox<Floor> floor = new ComboBox<>(GUI.Instance.getMyHouse().getFloors());
		floor.getSelectionModel().select(GUI.Instance.getCurrFloor());
		Button floorButton = new Button();
		floorButton.setDisable(true);
		floor.valueProperty().addListener(new ChangeListener<Floor>() {
			@Override
			public void changed(ObservableValue<? extends Floor> observable, Floor oldF, Floor newF) {
				if(newF!=GUI.Instance.getCurrFloor()){
					floorButton.setDisable(false);
				} else {
					floorButton.setDisable(true);
				}
			}    
	      });
		floorButton.setText("Change");
		floorButton.setOnAction(x->{
			GUI.Instance.getMyHouse().ChangeArduinoFloor(floor.getSelectionModel().getSelectedItem(),myArduino);
			GUI.Instance.changeFloor(floor.getSelectionModel().getSelectedItem());
			myArduino.ResetPosition();
		});

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
		
		Button saveChanges = new Button();
		saveChanges.setText("Save Changes!");
		saveChanges.setOnAction(x->{
			Network.Instance.SendPacketToArduino(myArduino.GetNetworkDataCollection());
		});
		Label spacer = new Label("	    ");
		top.getChildren().addAll(back,saveChanges,label,floor,floorButton,spacer,disable);
		mainLayout.setTop(top);
	}
	

	public ScrollPane GetCenterLayout(){
		int maxLED = myArduino.getChildren().size(); // list.size() or smth gives 1-10 not 0-9
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setStyle("-fx-background-color:transparent;");
		FlowPane ledGrid = new FlowPane();
//		ledGrid.setPrefWrapLength(890);
		for (int i = 0; i < maxLED; i++) {
			Node n = myArduino.getChildren().get(i).GetPane(i);
			n.maxHeight(Double.MAX_VALUE);
			n.maxWidth(Double.MAX_VALUE);
			ledGrid.getChildren().add(n); 
		}
//		ledGrid.setPadding(new Insets(5, 5, 5, 5)); 
		
		scrollPane.setContent(ledGrid);
		return scrollPane;
	}
	
	public Pane getCenter() {
		return mainLayout;
	}
	
}
