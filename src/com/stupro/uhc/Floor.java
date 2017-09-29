package com.stupro.uhc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import com.stupro.uhc.arduino.Arduino;
import com.stupro.uhc.arduino.ui.ArduinoSmallUI;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class Floor {
	
	@Element(required=false)
	protected String name = "Test";
	@ElementList
	protected ArrayList<Arduino> arduinos;
	@Element
	protected String pictureLocation = "/images/testapartment.png";
	
	
	HashMap<Arduino,Pane> arduinoToPane;
	
	private StackPane center;
	
	protected double orgSceneX;
	protected double orgSceneY;
	protected double orgTranslateX;
	protected double orgTranslateY;
	static ImageView background;
	private static Pane arduinoRoot;
	static double maxX;
	static double maxY;
	protected double orgScreenX;
	protected double orgScreenY;
	protected boolean dragging;

	public Floor(String pictureLocation){
		this.pictureLocation = pictureLocation;
		initialize();
	}
	public Floor(){
		initialize();
	}
	
	public void AddArduino(Arduino ar){
		Pane p = new ArduinoSmallUI(ar);
		arduinoToPane.put(ar, p);
		System.out.println("arduinos " + ar);
		if(arduinos.contains(ar)==false){// this is for loading
			arduinos.add(ar);
		}
			
		arduinoRoot.getChildren().add(p);
		
	}
	
	private void initialize(){
		center = new StackPane();
		arduinoToPane = new HashMap<>();
		arduinos = new ArrayList<>();
		Image image = new Image(pictureLocation);
		background = new ImageView(image);
		maxX = image.getWidth();
		maxY = image.getHeight();
		background.setStyle("-fx-background-color: linear-gradient(to bottom, rgb(20,20,20), rgb(30,60,80));");
		arduinoRoot = new Pane();
		this.pictureLocation = pictureLocation;
		arduinoRoot.setMaxHeight(maxY);
		arduinoRoot.setMaxWidth(maxX);

		center.getChildren().addAll(background , arduinoRoot);
		
//		for (int i = 0; i < 1; i++) {
//			arduinoRoot.getChildren().add(new ArduinoSmallUI(new Arduino(0, "test", null)));
//		}
		center.setOnZoom(onZoomEventHandler);
		center.setOnScroll(onScrollEventHandler);
		center.toBack();
		center.setOnMousePressed(onMousePressedEventHandler);
		center.setOnMouseDragged(onMouseDraggedEventHandler);
		center.setOnMouseReleased(onMouseDraggedEndEventHandler);
	}
	
	public void RemoveArduino(Arduino adr) {
		arduinos.remove(adr);
		arduinoRoot.getChildren().remove(arduinoToPane.get(adr));
		arduinoToPane.remove(adr);
	}
	
	public Pane getCenter() {
		return center;
	}
	
	public Pane getCenterPane() {
		return arduinoRoot;
	}
	
	
	public static boolean outSideBounds( Point2D point) {
		Bounds bound = background.getBoundsInLocal();
		if(bound.contains(point)){
			return false; 
		}  else {
			return true;
		}
    }
	EventHandler<? super ZoomEvent> onZoomEventHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			 zoomCenter(event.getZoomFactor());
		}
	};
	EventHandler<? super ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0){
              zoomFactor = 2.0 - zoomFactor;
            }
            zoomCenter(zoomFactor);
		}
	};

	protected void zoomCenter(double zoomFactor){
		if(center.getScaleX()>3 && zoomFactor>1){
			return;
		}
		if(center.getScaleX()<0.5 && zoomFactor<1){
			return;
		}
        center.setScaleX(center.getScaleX() * zoomFactor);
        center.setScaleY(center.getScaleY() * zoomFactor);			

	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			orgSceneX = t.getSceneX();
			orgSceneY = t.getSceneY();
			orgTranslateX = center.getTranslateX();
			orgTranslateY = center.getTranslateY();
			orgScreenX = t.getScreenX();
			orgScreenY = t.getScreenY();
		}
	};

	EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			dragging=true;

			double offsetX = t.getSceneX() - orgSceneX;
			double offsetY = t.getSceneY() - orgSceneY;
			double newTranslateX = orgTranslateX + offsetX;
			double newTranslateY = orgTranslateY + offsetY;
			center.setTranslateX(newTranslateX);
			center.setTranslateY(newTranslateY);
			
		}
	};
	EventHandler<MouseEvent> onMouseDraggedEndEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			if(dragging==false){
				return;
			}
			dragging=false;
			Bounds bounds = center.getBoundsInParent();
			
			double width = bounds.getWidth();
			double height = bounds.getHeight();
			if(bounds.getMinX()>width/2){
				center.setTranslateX(width/2);
			}
			if(bounds.getMaxX()<width/2){
				center.setTranslateX(-width/2);
			}
			if(bounds.getMinY()>height/2){
				center.setTranslateY(height/2);
			}
			if(bounds.getMaxY()<height/2){
				center.setTranslateY(-height/2);
			}
		}
	};
	@Override
	public String toString(){
		return name;
	}

	public void load() {
		for (Arduino arduino : arduinos) {
			System.out.println(arduino.getName());
			AddArduino(arduino);
		}
	}

	public Collection<? extends Arduino> getArduinos() {
		return arduinos;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Floor == false){
			return false;
		}
		Floor f = (Floor) obj;
		return name.equals(f.name);
	}

	public void setPictureLocation(String fileLocation) {
		this.pictureLocation = fileLocation;
	}

	public String getPictureLocation() {
		return pictureLocation;
	}
	
}
