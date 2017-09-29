package com.stupro.uhc.arduino.children;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.stupro.uhc.arduino.modifier.Modifier;
import com.stupro.uhc.arduino.modifier.Repeater;
import com.stupro.uhc.arduino.modifier.Timer;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LED extends Child {
	int colorRange=255;
	int red = 0;
	int green = 0;
	int blue = 0;
	//dimmen 
	int dimmLevel = 255;
	int maxBrightness=255;
	ColorPicker cp;
	Slider dimmSlider;
	Label dimmProzentLabel;
	
	public LED(String metaData) {
		name = "LED";
		String colorRangeMeta = metaData.substring(0, 2);
		switch(colorRangeMeta){
			case "00":colorRange=110;
			break;
			case "01":colorRange=255;
			break;
			default:
			break;
		}
		String brightnessMeta = metaData.substring(2, 4);
		switch(brightnessMeta){
			case "00":maxBrightness=255;
			break;
			case "01":maxBrightness=155;
			break;
			default:
			break;
		}
		
		String lightmode = metaData.substring(4, 6);
		String timer = metaData.substring(6, 7);
		cp = new ColorPicker();
		cp.setValue(new Color((double)red/colorRange, (double)green/colorRange, (double)blue/colorRange,1));
		this.idToMyModifier = new HashMap<>();
		ArrayList<Modifier> mods = new ArrayList<>();
		mods.add(new Repeater());
		idToMyModifier.put(5, mods);
		mods = new ArrayList<>();
		mods.add(new Timer());
		idToMyModifier.put(6, mods);
		dimmSlider = new Slider(0,maxBrightness,maxBrightness);
		
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
	public Node GetInnerPane(){
		GridPane gridPane = new GridPane();
		gridPane.add(new Label("Color"), 0, 1);
		gridPane.add(cp, 1, 1);
		gridPane.add(new Label("Brightness"), 0, 2);
		VBox vbox = new VBox();
		vbox.getChildren().add(dimmSlider);
		dimmProzentLabel = new Label("100%");
		dimmSlider.valueProperty().addListener(x->{
			dimmLevel = (int) (Math.round(100*(dimmSlider.getValue())/255f));
			dimmProzentLabel.setText(dimmLevel +"%");
		});
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().add(dimmProzentLabel);
		gridPane.add(vbox, 1, 2);

		GridPane.setHalignment(cp, HPos.CENTER);
		TitledPane titled = new TitledPane();
		FlowPane flow = new FlowPane();
		for (int key : idToMyModifier.keySet()) {
			flow.getChildren().add(GetModifierNode(key));
		}
		titled.setContent(flow);
		titled.setText("Preferences");
		GridPane.setColumnSpan(titled, GridPane.REMAINING);
		GridPane.setRowSpan(titled, GridPane.REMAINING);

		titled.setExpanded(false);
		gridPane.add(titled, 3, 0);
		gridPane.setPadding(new Insets(10, 10, 10, 10)); 
		return gridPane;
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
		int dimmLevel = Integer.parseInt(data[3].trim());
		if(red !=this.red || green!=this.green || blue != this.blue){
			cp.setValue(new Color((double)red/255.0, (double)green/255.0, (double)blue/255.0,1));
		}
		if(dimmLevel!=this.dimmLevel){
			this.dimmLevel = dimmLevel;
			dimmSlider.setValue(dimmLevel);
		}
	}
	@Override
	protected String GetSpecificData() {
		String active = isActive()? "1" : "0";
		return active+";"+red+";"+green+";"+blue+";"+dimmLevel+";";
	}
	@Override
	public Collection<String> GetDatapackages() {
		return null;
	}

	
}
