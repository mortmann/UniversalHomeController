package com.stupro.uhc.arduino.children;

import java.util.HashMap;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import com.stupro.uhc.arduino.modifier.Modifier;
import com.stupro.uhc.network.Network;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class LED extends Child {

	int red = 0;
	int green = 0;
	int blue = 0;
	//dimmen 
	
	ColorPicker cp;
	public LED() {
		cp = new ColorPicker();
		cp.setValue(new Color((double)red/255.0, (double)green/255.0, (double)blue/255.0,1));
		this.idToMyModifier = new HashMap<>();
	}
	public void AddModifier(Modifier m){
		idToMyModifier.put(m.getID(),m);
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
	public Node GetPane(int number){
		TitledPane t = new TitledPane();
		Glyph fontG = new FontAwesome().create(org.controlsfx.glyphfont.FontAwesome.Glyph.POWER_OFF);
		fontG.setFontSize(15);
		Button deactivate = new Button("",fontG);
		int numberBool = isActive()? 0:1;
		deactivate.setOnAction(x->{
			Network.Instance.SendPacketToArduino("3_"+ number +";"+numberBool);
			setActive(!isActive());
			t.setDisable(isActive());
			deactivate.setDisable(false);
		});
		deactivate.setMaxSize(10, 20);
		deactivate.setPrefSize(10, 20);
		deactivate.setMinSize(10, 20);
		HBox title = new HBox();
		title.getChildren().add(new Label("LED " + (number+1) +"."));
		Region  emptySpacer = new Region ();
		HBox.setHgrow(emptySpacer, Priority.ALWAYS);
		title.getChildren().add(emptySpacer);
		title.getChildren().add(deactivate);
		t.setGraphic(title);
		GridPane gridPane = new GridPane();
//		gridPane.add(new Label("LED"), 0, 0);
//		gridPane.add(new Label((number+1) +"."), 1, 0);
		gridPane.add(new Label("Current Color"), 0, 1);
		gridPane.add(cp, 1, 1);
		gridPane.add(new Label(""), 0, 2);
		gridPane.setPadding(new Insets(10, 10, 10, 10)); 
		t.setContent(gridPane);
		return t;
	}

	@Override
	public Object GetValue() {
		return cp.getValue();
	}
	@Override
	public void HandleSpecificData(String[] data) {
		int red = Integer.parseInt(data[0].trim());
		int green = Integer.parseInt(data[1].trim());
		int blue = Integer.parseInt(data[2].trim());
		if(red !=this.red || green!=this.green || blue != this.blue){
			cp.setValue(new Color((double)red/255.0, (double)green/255.0, (double)blue/255.0,1));
		}
	}

	
}
