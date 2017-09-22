package com.stupro.uhc.arduino.children;

import java.util.HashMap;
import java.util.regex.Pattern;

import com.stupro.uhc.arduino.modifier.Modifier;

import javafx.scene.Node;

public abstract class Child {
	protected boolean active;
	protected HashMap<Integer,Modifier> idToMyModifier;
	public abstract Node GetPane(int number);
	public abstract Object GetValue();
	
	public void HandleInfo(String info) {
//		here is the specific data for the child
//		v      v      v
//		first;second;third (ID[;;][;;][;;]) ([;])
//							^^					^^
//							These are the modifier
		String[] splitData = info.split(Pattern.quote("("));

		String[] specific = splitData[0].split(";");
		int numOfCommonData = 1;
		//if this child is active -> arduino works with 0/1
		active = Integer.parseInt(specific[0].trim())==1;
		
		HandleSpecificData(specific);//(String[]) Array.get(specific, numOfCommonData)
		for (int i = numOfCommonData; i < splitData.length; i++) {
			HandleModifierData(splitData[i]);
		}
		
	}
	//	here is the specific data for the child
	//	v      v      v
	//	first;second;third
	public abstract void HandleSpecificData(String[] data);
	/**		first|second|third data for modifier
	*	   v 	v     v 
	*	ID[;;][;;][;;])
	*	   ^			  ^
	*	These is the modifier
	*/
	public void HandleModifierData(String data){
		//get the id before the first datapackage []
		int id = Integer.parseInt(data.substring(0, data.indexOf("[")));
		String modifierData = data.substring(data.indexOf("["),data.length());
		if(modifierData.replace("]", "").isEmpty()){
			return;
		}
		if(idToMyModifier.containsKey(id))
			idToMyModifier.get(id).HandleData(modifierData);
	}

	public HashMap<Integer,Modifier> getMyModifier() {
		return idToMyModifier;
	}
	public void setMyModifier(HashMap<Integer,Modifier> myModifier) {
		this.idToMyModifier = myModifier;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean isActive) {
		active = isActive;
	}
}
