package com.stupro.uhc.ui;

import java.io.File;

import org.controlsfx.glyphfont.FontAwesome;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class Options extends ButtonOver {

	Slider soundSlider;
	CheckBox iconifiedToTrayCheckBox; 
	
	public Options(){
		super("Settings",FontAwesome.Glyph.COG);
		loadSave();
	}
	protected void createPopOverContent(){
		popOver.setTitle("Options"); 
		iconifiedToTrayCheckBox = new CheckBox();
		iconifiedToTrayCheckBox.setSelected(true);
		vbox.getChildren().add(CreateHBox(new Label("Minimize to tray"),iconifiedToTrayCheckBox));
		soundSlider = SetUpSoundSlider();
		vbox.getChildren().add(CreateHBox(new Label("Sound-Volume"),soundSlider));
		popOver.setContentNode(vbox);
	}
	public double GetVolume(){
		return soundSlider.getValue();
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
	@Root(strict=false)
	protected static class OptionSave {
		
		@Element(required=false)public double soundVolume;
		@Element(required=false)public boolean iconifiedToTray;
		
	}
	@Override
	protected void onHiding() {
		SaveOptions();
	}
	@Override
	protected void onClose() {
		SaveOptions();
	}
	@Override
	protected void onShowing() {
		// TODO Auto-generated method stub
		
	}
	
}
