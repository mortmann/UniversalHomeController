package com.stupro.uhc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.stupro.uhc.arduino.Arduino;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

@Root
public class House {

	@ElementList
	private ObservableList<Floor> floors;
	
	private HashMap<Floor,ArrayList<Arduino>> floorToArduinos;
	
	private int currSelectedFloor = 0;
	
	public House(){
		floors = FXCollections.observableArrayList();
		floorToArduinos = new HashMap<>();
	}
	
	public Node getCenter() {
		if(floors.size()==0){
			createNewFloor("/images/testapartment.png","test");
		}
		return floors.get(currSelectedFloor).getCenter();
	}

	public void AddAruinoToFloor(Arduino ard, int number){
		if(floorToArduinos.get(floors.get(number))==null){
			floorToArduinos.put(floors.get(number), new ArrayList<>());
		}
		floorToArduinos.get(floors.get(number)).add(ard);
		floors.get(number).AddArduino(ard);
	}
	
	public void createNewFloor(String fileLocation, String name){
		Floor f = new Floor(fileLocation);
		floorToArduinos.put(f, new ArrayList<Arduino>());
		floors.add(f);
	}
	
	public void changeFloorPostion(Floor move,int newPos){
		Floor currselected = floors.get(currSelectedFloor);
		floors.remove(move);
		floors.add(newPos, move);
		currSelectedFloor = floors.indexOf(currselected);
	}

	public void RemoveArduino(Arduino adr) {
		for (int i = 0; i < floors.size();i++) {
			ArrayList<Arduino> temp = floorToArduinos.get(floors.get(i));
			if(temp.contains(adr)){
				floors.get(i).RemoveArduino(adr);
			}
		}
	}

	public ObservableList<Floor> getFloors() {
		return floors;
	}

	public void ChangeCurrFloor(Floor f){
		currSelectedFloor = floors.indexOf(f);
	}
	
	public void AddAruinoToFloor(Arduino ard, Floor floor) {
		AddAruinoToFloor(ard, floors.indexOf(floor));
	}

	public void load() {
		for (Floor floor : floors) {
			floor.load(); 
			floorToArduinos.put(floor,new ArrayList<>(floor.getArduinos()));
		}
	}

	public Collection<Arduino> getAllArduinos() {
		ArrayList<Arduino> ards = new ArrayList<>();
		for (Floor floor : floors) {
			ards.addAll(floor.getArduinos());
		}
		return ards;
	}

	public void ChangeArduinoFloor(Floor floor, Arduino arduino) {
		for(Floor f : floors){
			if(floorToArduinos.get(f).contains(arduino)){
				floorToArduinos.get(f).remove(arduino);
			}
		}
		AddAruinoToFloor(arduino,floor);
	}

	public Floor getCurrSelectedFloor() {
		return floors.get(currSelectedFloor);
	}

	public void setCurrSelectedFloor(int currSelectedFloor) {
		this.currSelectedFloor = currSelectedFloor;
	}
	public void deleteFloor(Floor f){
		if(floors.size()<=1){
			return;
		}
		floors.remove(f);
		GUI.Instance.addAllArduino(floorToArduinos.get(f));
		floorToArduinos.remove(f);
	}
}
