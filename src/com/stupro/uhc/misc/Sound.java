package com.stupro.uhc.misc;

import java.net.URL;

import com.stupro.uhc.GUI;

import javafx.scene.media.AudioClip;

public class Sound {
	static  String clipLocation = "/sounds/";

	static AudioClip sound;
	public static void playSound(Clip c){
	    URL resource = Sound.class.getResource(clipLocation + c.toString());
	    sound = new AudioClip(resource.toString());
		sound.setVolume(GUI.Instance.GetVolume() );
		if(sound.isPlaying()==false)
			sound.play();

	}
	
	public enum Clip {

	    notify("notify.mp3"),error("error.wav");
	    private String name;


	    Clip(String desc){
	        this.name=desc;
	    }
	    @Override
	    public String toString() {
	    	return name;
	    }
	};
}
