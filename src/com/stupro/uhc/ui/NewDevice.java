package com.stupro.uhc.ui;

import org.controlsfx.glyphfont.FontAwesome;

import com.stupro.uhc.Floor;
import com.stupro.uhc.GUI;
import com.stupro.uhc.arduino.Arduino;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class NewDevice extends ButtonOver {
	
	Label numberOfDevices;
	
	public NewDevice() {
		super("New Device",FontAwesome.Glyph.PLUS_CIRCLE);
	}
	
	
	@Override
	protected void createPopOverContent() {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(5));
		vbox.setSpacing(5);
		ObservableList<Arduino> newArduinos = GUI.Instance.getNewArduinosObservableList();
		ComboBox<Arduino> arduinos = new ComboBox<Arduino>(newArduinos );
		popOver.setOnShowing(x->{
			if(newArduinos.size()>0)
				arduinos.getSelectionModel().select(0);
		});
		vbox.getChildren().add(CreateHBox(new Label("Arduino"),arduinos));
		
		
		numberOfDevices = new Label("    ");
		numberOfDevices.setTextFill(Color.WHITE);
		numberOfDevices.setStyle("-fx-background-image: url('images/marker_background.png');");
		StackPane.setAlignment(numberOfDevices, Pos.TOP_RIGHT);
		numberOfDevices.setMouseTransparent(true);
		getChildren().add(numberOfDevices);
		toggle.toBack();

		UpdateNumberLabel();
		GUI.Instance.getNewArduinosObservableList().addListener(new ListChangeListener<Arduino>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Arduino> c) {
				UpdateNumberLabel();
			}
		});
		ComboBox<Floor> floor = new ComboBox<Floor>(GUI.Instance.getMyHouse().getFloors());
		floor.getSelectionModel().select(0);
		vbox.getChildren().add(CreateHBox(new Label("Choose Floor:"),floor));
		Button add = new Button("ADD");
		add.setOnAction(x->{ 
			GUI.Instance.getMyHouse().AddAruinoToFloor(arduinos.getSelectionModel().getSelectedItem(), floor.getSelectionModel().getSelectedItem());
			newArduinos.remove(arduinos.getSelectionModel().getSelectedItem());
		});
		vbox.getChildren().add(CreateHBox(new Label(" "),add));
		popOver.setContentNode(vbox);
	}

	private void UpdateNumberLabel(){
		int numberOfNewArduinos = GUI.Instance.getNewArduinosObservableList().size();
		if(numberOfNewArduinos>0){
			if(reference.getChildren().contains(numberOfDevices)==false)
				reference.getChildren().add(numberOfDevices);
			numberOfDevices.setText(" " + numberOfNewArduinos+ " ");
		} else {
			if(reference.getChildren().contains(numberOfDevices))
				reference.getChildren().remove(numberOfDevices);
		}
		numberOfDevices.toFront();
	}
	
	@Override
	protected void onHiding() {
		
	}

	@Override
	protected void onClose() {
		
	}

}
