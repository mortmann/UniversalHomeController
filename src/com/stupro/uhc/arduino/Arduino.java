package com.stupro.uhc.arduino;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.Element;

import com.stupro.uhc.arduino.children.Child;
import com.stupro.uhc.arduino.children.LED;
import com.stupro.uhc.arduino.children.WashingMaschine;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

public class Arduino {
	@Element
	protected String macAddress;
	@Element
	protected String name;
	@Element
	protected double x = 0;
	@Element
	protected double y = 0;
	@Element
	protected boolean isNew = true;
	@Element
	protected boolean isActive = true;
	@Element
	protected String metaData;

	private boolean timedOut = false;
	private ObservableValue<Boolean> timeout;
	private ObservableValue<Boolean> active;
	private ArrayList<Child> myChildren;
	private InetAddress IPAddress;

	public Arduino(String id, String name, InetAddress IPAddress, String metaData) {
		this.macAddress = id;
		this.name = name;
		this.metaData = metaData;
		this.IPAddress = IPAddress;
		initialize();
	}

	// For loading
	public Arduino() {
		initialize();
	}

	private void initialize() {
		myChildren = new ArrayList<>();
		active = new SimpleBooleanProperty(isActive).asObject();
		timeout = new SimpleBooleanProperty(timedOut).asObject();
		String type = metaData.substring(0, 4);
		System.out.println(metaData);
		switch (type) {
		case "0001":
			int childCount = Integer.parseInt(metaData.substring(4, 6), 16);
			for (int i = 0; i <= childCount; i++) {
				myChildren.add(new LED(metaData.substring(6, metaData.length())));
			}
			break;

		case "000E":
			myChildren.add(new WashingMaschine(metaData));
			break;
		case "0201":
			break;
		default:
			break;
		}
	}

	public void HandleInfo(String info) {
		// these are the cilds differentiated (most of the time it will have
		// only 1)
		// v v
		// <;;; (ID[;;][;;][;;]) ([;]) ([][]) <NEXT
		setTimedOut(false);
		String[] childs = info.split("<");
		for (int i = 1; i < childs.length; i++) {
			myChildren.get(i - 1).HandleInfo(childs[i]);
		}
	}

	public String GetNetworkData() {
		String data = "";
		for (Child c : myChildren) {
			data += c.GetDataString();
			data += "<";
		}
		return data;
	}

	public Collection<String> GetNetworkDataCollection() {
		ArrayList<String> datas = new ArrayList<>();
		for (int i = 0; i < myChildren.size(); i++) {
			datas.addAll(myChildren.get(i).GetDatapackages(i));
		}
		datas.removeIf(x -> x == null || x.trim().isEmpty());
		return datas;
	}

	public void UpdatePosition(double x, double y) {
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
		if (name == null || name.isEmpty())
			return IPAddress.toString(); // change this to some kind of default
											// name for its id
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

	public String getMacAddress() {
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

	public void ResetPosition() {
		x = 0;
		y = 0;
	}

	public ArrayList<Child> getChildren() {
		return myChildren;
	}
}
