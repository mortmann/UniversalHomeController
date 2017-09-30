package com.stupro.uhc.arduino.children;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import com.stupro.uhc.arduino.modifier.Modifier;
import com.stupro.uhc.network.Network;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public abstract class Child {
	protected boolean active;
	protected HashMap<Integer,ArrayList<Modifier>> idToMyModifier;
	
	protected String name;
	public abstract Collection<String> GetDatapackages(int child);
	
	public abstract Node GetInnerPane();
	public abstract Object GetValue();
	private Node innerPane;
	public Node GetPane(int number){
		TitledPane t = new TitledPane();
		Glyph fontG = new FontAwesome().create(org.controlsfx.glyphfont.FontAwesome.Glyph.POWER_OFF);
		fontG.setFontSize(15);
		Button deactivate = new Button("",fontG);
		int numberBool = isActive()? 0:1;
		deactivate.setOnAction(x->{
			Network.Instance.SendPacketToArduino("3_"+ number +";"+numberBool);
			setActive(!isActive());
			innerPane.setDisable(isActive());
			t.setExpanded(false);
			t.setCollapsible(isActive());
		});
		deactivate.setMaxSize(10, 20);
		deactivate.setPrefSize(10, 20);
		deactivate.setMinSize(10, 20);
		
		GridPane title = new GridPane();
		title.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		Label label = new Label(name + (number+1) +".");
		label.setMinWidth(200);
		title.add(label,0,0);
		Label spacer = new Label("    						");
		spacer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		title.add(spacer,1,0);
		title.add(deactivate,2,0);
		GridPane.setHgrow(spacer, Priority.ALWAYS);
		GridPane.setHalignment(deactivate, HPos.RIGHT);
		t.setGraphic(title);
		innerPane = GetInnerPane();
		t.setContent(innerPane);
		t.setMinWidth(835);
		t.setMaxWidth(Double.MAX_VALUE);
//		t.setPrefWidth(400);
		return t;
	}

	public Node GetModifierNode(int modifierID){
		TitledPane titl = new TitledPane();
		ArrayList<Modifier> mod = idToMyModifier.get(modifierID);
		HBox hbox = new HBox();
		for(Modifier m : mod){
			hbox.getChildren().add(m.GetPane());
			titl.setText(m.GetName());
		}
		titl.setContent(hbox);
		titl.setExpanded(false);
		return titl;
	}
	
	public void AddModifier(Modifier m){
		if(idToMyModifier.containsKey(m.getID())==false){
			idToMyModifier.put(m.getID(),new ArrayList<Modifier>());
		} 
		idToMyModifier.get(m.getID()).add(m);
	}
	
	public void HandleInfo(String info) {
//		here is the specific data for the child
//		v      v      v
//		first;second;third (ID[;;])(ID[;;])(ID[;;])
//							^^					^^
//							These are the modifier
		String preModifierData ="";
		String modifierData ="";
		if(info.contains("(")==false){
			preModifierData = info;
		} else {
			preModifierData = info.substring(0,info.indexOf("("));
			modifierData = info.substring(info.indexOf("("),info.length());
		}
		//we need to keep track 
		String[] specificArray = preModifierData.split(";");
		//if this child is active -> arduino works with 0/1
		active = Integer.parseInt(specificArray[0].trim())==1;
		HandleSpecificData(specificArray);

		if(modifierData==null||modifierData.isEmpty()){
			return;
		}

		String[] modifierDataArray = modifierData.split(Pattern.quote("("));
		HashMap<Integer,Integer> modIDToNumberInArray = new HashMap<>();
		for (int i = 1; i < modifierDataArray.length; i++) {
			String mod = modifierDataArray[i];
			mod = mod.replace(")", "");

			
			int id = Integer.parseInt(mod.substring(0, mod.indexOf("[")));
			mod = mod.substring(mod.indexOf("["),mod.length());
			if(modIDToNumberInArray.containsKey(id)==false){
				modIDToNumberInArray.put(id, 0);
			} else{
				int temp = modIDToNumberInArray.get(id);
				modIDToNumberInArray.replace(id, temp++);
			}
			HandleModifierData(mod,id,modIDToNumberInArray.get(id));
		}
		
	}
	//	here is the specific data for the child
	//	v      v      v
	//	first;second;third
	public abstract void HandleSpecificData(String[] data);
	/**		first|second|third data for modifier
	*	   v 	v     v 
	*	(ID[;;])(ID[;;])(ID[;;])
	*	   ^			  ^
	*	These are the modifier
	*/
	public void HandleModifierData(String data, int id, int number){
		//get the id before the first datapackage []
		if(idToMyModifier.containsKey(id) == false){
			return;
		}
		data = data.replace("[","");
		data = data.replace("]","");
		if(data.isEmpty()){
			return;
		}

		if(idToMyModifier.get(id).size()>number){
			idToMyModifier.get(id).get(number).HandleData(data);
		}

	}
	
	public String GetDataString(){
		String data= GetSpecificData();
		for(int id : idToMyModifier.keySet()){
			ArrayList<Modifier> mods = idToMyModifier.get(id);
			String temp ="";
			for(Modifier m : mods){
				if(m.isActive()==false){
					continue;
				}
				temp += m.GetParameterString();
			}	
			data+=temp;
		}
		return data;
	}
	
	protected abstract String GetSpecificData();
	
	public HashMap<Integer, ArrayList<Modifier>> getMyModifier() {
		return idToMyModifier;
	}
	public void setMyModifier(HashMap<Integer, ArrayList<Modifier>> myModifier) {
		this.idToMyModifier = myModifier;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean isActive) {
		active = isActive;
	}
}
