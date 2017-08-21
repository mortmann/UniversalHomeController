package com.stupro.uhc;

import java.io.File;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow.AnchorLocation;

public class Options extends Pane {

	ToggleButton optionButton;
	PopOver optionPopOver;
	Slider soundSlider;
	CheckBox iconifiedToTrayCheckBox; 
	
	public Options(){
		Glyph og = new FontAwesome().create(FontAwesome.Glyph.COG);
		og.setFontSize(20);
		optionButton = new ToggleButton("Settings",og);
		optionButton.setOnAction(x->show());
		this.getChildren().addAll(optionButton);
		createPopOver();
		loadSave();
	}

	private void show() {
		if(optionPopOver!=null&&optionPopOver.isShowing()){
			optionPopOver.hide();
			return;
		}
		optionPopOver.show(optionButton);
	}
	
	private void createPopOver(){
		optionPopOver = new PopOver();
		optionPopOver.setOnHiding(x->{SaveOptions();});
		optionPopOver.setTitle("Options"); 
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(5));
		vbox.setSpacing(5);
		vbox.getChildren().add(CreateHBox(new Label("BlaBla"),new ComboBox<String>()));
		iconifiedToTrayCheckBox = new CheckBox();
		iconifiedToTrayCheckBox.setSelected(true);
		vbox.getChildren().add(CreateHBox(new Label("Minimize to tray"),iconifiedToTrayCheckBox));
		soundSlider = SetUpSoundSlider();
		vbox.getChildren().add(CreateHBox(new Label("Sound-Volume"),soundSlider));
		optionPopOver.setContentNode(vbox);
		optionPopOver.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		optionPopOver.setArrowLocation(ArrowLocation.TOP_CENTER);
	}
	
	private Slider SetUpSoundSlider(){
		Slider slider = new Slider();
		slider.setMin(0);
		slider.setMax(100);
		slider.setValue(50);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(50);
		slider.setMinorTickCount(5);
		slider.setBlockIncrement(10);
		return slider;
	}
	private void SaveOptions() {
		optionButton.setSelected(false);
		OptionSave os = new OptionSave();
		os.iconifiedToTray = iconifiedToTrayCheckBox.isSelected();
		os.soundVolume = soundSlider.getValue();
		Serializer serializer = new Persister(new AnnotationStrategy());
		
		try {
			serializer.write(os, new File("options.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void loadSave() {
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			OptionSave os = serializer.read(OptionSave.class, new File("options.xml"));
			iconifiedToTrayCheckBox.setSelected(os.iconifiedToTray);
			soundSlider.setValue(os.soundVolume);
		} catch (Exception e) {
			//if the file doesn´t exist or can´t be correctly read
			//it´s gonna be catched here and thats fine
		}
	}
	private HBox CreateHBox(Node n1, Node n2){
		HBox hbox = new HBox();
		hbox.getChildren().addAll(n1,n2);
		hbox.setSpacing(10);
		return hbox;
	}
	@Root(strict=false)
	protected static class OptionSave {
		
		@Element(required=false)public double soundVolume;
		@Element(required=false)public boolean iconifiedToTray;
		
	}
	
}
