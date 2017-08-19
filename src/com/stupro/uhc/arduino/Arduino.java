package com.stupro.uhc.arduino;

import java.net.InetAddress;
import java.util.ArrayList;

import com.stupro.uhc.GUI;
import com.stupro.uhc.arduino.children.Children;
import com.stupro.uhc.network.Network;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Arduino {

	private long id;
	private String name;
	private boolean isActive = true; 
	private ArrayList<Children> myChildren;
	private InetAddress IPAddress;
	private Button select;
	public Arduino(long id, String name, InetAddress IPAddress) {
		this.id = id;
		this.name = name;
		myChildren = new ArrayList<>();
		this.setIPAddress(IPAddress);
	}
	
	public Pane GetSmallLayout(){
		GridPane gridPane = new GridPane();
		Button d = new Button("X");
		
		// if it is active send 0 if not then 1
		d.setOnAction(x->{ 
			Network.Instance.SendPacketToThisArduino("2_"+ (isActive? 0 : 1),this);
			System.out.println("2_"+ (isActive? 0 : 1));
			this.isActive = !isActive;
			select.setDisable(!isActive);
		});
		gridPane.add(d, 0, 0);
		gridPane.add(new Label("Arduino"), 0, 1);
		gridPane.add(new Label(name), 1, 1);
		gridPane.add(new Label("Type " + id), 0, 2);
		gridPane.add(new Label("LED "), 1, 2);
		
		select = new Button("Select");
		select.setOnAction(x-> GUI.Instance.changeCenterToArduino(this));
		
		gridPane.add(select, 1, 3);
		return gridPane;
	}
	public Pane GetBigLayout(){
		int perColumn = 2;
		int maxLED = myChildren.size(); // list.size() or smth gives 1-10 not 0-9
		System.out.println(maxLED);
		GridPane ledGrid = new GridPane();
		for (int i = 0; i < maxLED; i++) {
			GridPane childPane = new GridPane();
			GridPane outerPane = new GridPane();

			Button b1 = new Button("Save Color");
			Button b2 = new Button("Save Timer");
			Button deactivate = new Button("X");
			int num = i;
			int numberBool = myChildren.get(num).isActive()? 0:1;
			deactivate.setOnAction(x->{
				Network.Instance.SendPacketToArduino("3_"+ num+";"+numberBool);
				myChildren.get(num).setActive(!myChildren.get(num).isActive());
				childPane.setDisable(myChildren.get(num).isActive());
				deactivate.setDisable(false);
			});
			childPane.add(myChildren.get(i).GetPane(i), 1, 0);
			
			b1.setOnAction(x-> Network.Instance.SendColorLED(num,(Color)myChildren.get(num).GetValue(),this));
			childPane.add(b1, 2, 0);
			childPane.add(b2, 2, 1);
			childPane.setPadding(new Insets(10, 10, 10, 10)); 

			outerPane.add(deactivate, 0, 0);
			outerPane.add(childPane, 1, 0);

			ledGrid.add(outerPane, (i%perColumn),i/perColumn);
		}
		ledGrid.setPadding(new Insets(10, 10, 10, 10)); 
		return ledGrid;
	}
	public void HandleInfo(String[] info){
		if(myChildren.size()>0){
			return;
		}
		ArrayList<LED> leds = new ArrayList<LED>();
		for(int i=0;i<=info.length-4;i+=4){
			leds.add(new LED(Integer.parseInt(info[i+1]), Integer.parseInt(info[i+2]), Integer.parseInt(info[i+3])));
		}
		myChildren.addAll(leds);
		
	}

	public InetAddress getIPAddress() {
		return IPAddress;
	}

	public void setIPAddress(InetAddress iPAddress) {
		IPAddress = iPAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}