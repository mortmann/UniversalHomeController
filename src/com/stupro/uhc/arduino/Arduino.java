package com.stupro.uhc.arduino;

import java.net.InetAddress;
import java.util.ArrayList;
import org.simpleframework.xml.Element;

import com.stupro.uhc.arduino.children.Child;
import com.stupro.uhc.arduino.children.LED;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

public class Arduino {
	@Element
	private String macAddress; 
	@Element
	private String name;
	@Element 
	private double x = 0;
	@Element 
	private double y = 0;
	@Element 
	private boolean isNew = true;
	@Element
	private boolean isActive = true; 
	
	private boolean timedOut = false; 
	private ObservableValue<Boolean> timeout;
	private ObservableValue<Boolean> active;
	private ArrayList<Child> myChildren;
	private InetAddress IPAddress;
	
	public Arduino(String id, String name, InetAddress IPAddress) {
		this.macAddress = id;
		this.name = name;
		active = new SimpleBooleanProperty(isActive).asObject();
		timeout = new SimpleBooleanProperty(timedOut).asObject();

		myChildren = new ArrayList<>();
		this.setIPAddress(IPAddress);
	}
	
	//For loading
	public Arduino(){
		myChildren = new ArrayList<>();
	}

	public ScrollPane GetBigLayout(){
		int perColumn = 2;
		int maxLED = myChildren.size(); // list.size() or smth gives 1-10 not 0-9
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setStyle("-fx-background-color:transparent;");
		GridPane ledGrid = new GridPane();
		for (int i = 0; i < maxLED; i++) {
//			Button b1 = new Button("Save Color");
//			b1.setOnAction(x-> Network.Instance.SendColorLED(num,(Color)myChildren.get(num).GetValue(),this));
			ledGrid.add(myChildren.get(i).GetPane(i), (i%perColumn),i/perColumn);
		}
		ledGrid.setPadding(new Insets(10, 10, 10, 10)); 
		
		scrollPane.setContent(ledGrid);
		return scrollPane;
	}
	public void HandleInfo(String info){
//		these are the cilds differentiated (most of the time it will have only 1)
//		v										v
//		<;;; (ID[;;][;;][;;]) ([;]) ([][]) <NEXT
		setTimedOut(false);
		String[] childs = info.split("<");
		if(myChildren.size()>0){
			return;
		}
		ArrayList<Child> leds = new ArrayList<Child>();
		for(int i=1;i<childs.length;i++){
			Child c = new LED(); // TODO: Make this semi automatic or full
			c.HandleInfo(childs[i]);
			leds.add(c); 
		}
		myChildren.addAll(leds);
		
	}
	public void UpdatePosition(double x, double y){
		this.x = x;
		this.y = y;
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

	@Override
	public String toString() {
		if(name==null || name.isEmpty())
			return IPAddress.toString(); // change this to some kind of default name for its id
		return name;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	public String getMacAddress(){
	    return macAddress;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public ObservableValue<Boolean> getActive() {
		return active;
	}

	public void setTimedOut(boolean b) {
		timedOut = b;
	}

	public ObservableValue<Boolean> getTimedOutProperty() {
		return timeout;
	}

	public boolean isTimedOut() {
		return timedOut;
	}
}
