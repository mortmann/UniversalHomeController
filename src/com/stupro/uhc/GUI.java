package com.stupro.uhc;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.util.ArrayList;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.stupro.uhc.arduino.Arduino;
import com.stupro.uhc.arduino.ui.ArduinoBigUI;
import com.stupro.uhc.misc.Notifier;
import com.stupro.uhc.misc.Sound;
import com.stupro.uhc.misc.TrayHandler;
import com.stupro.uhc.misc.Sound.Clip;
import com.stupro.uhc.network.Network;
import com.stupro.uhc.ui.NewDevice;
import com.stupro.uhc.ui.NewFloor;
import com.stupro.uhc.ui.Options;

import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUI {
	public static GUI Instance;
    
    private TrayIcon trayIcon;
    
	private Stage mainWindow;
	public StackPane mainStackPane;
	private BorderPane mainLayout;
	public StackPane center;
	private Scene scene;
	private Network network;

	private Arduino currSelectedArduino;
	
	private House myHouse;
	private ArduinoBigUI arduinoBigUI;
	
	private NewDevice newDeviceButton;
	private ObservableList<Arduino> newArduinos;
	
	private Options options;
	
	//for windowdrag
	protected double xOffset;
	protected double yOffset;

	private NotificationPane notification;

	private ToggleGroup floorToggle;

	
	public GUI() {

	}
	
	public void start(Stage s) throws Exception {
		Instance = this;
		new TrayHandler(s);
		newArduinos = FXCollections.observableArrayList();
		center = new StackPane();
		center.setStyle("-fx-background-color:transparent;");
		center.toBack();
		this.mainWindow = s;
		mainWindow.setTitle("Universal Home Controller");
		mainWindow.getIcons().setAll(new Image("/images/tray.png"));
		mainWindow.initStyle(StageStyle.TRANSPARENT);
		
		mainStackPane = new StackPane();
		
		Serializer serializer = new Persister(new AnnotationStrategy());
		
		mainLayout= new BorderPane();
		

		Platform.setImplicitExit(false);
		mainLayout.setCenter(center);
		BorderPane.setAlignment(center, Pos.CENTER);
		
		//TODO: add menu on the top
		mainStackPane.getChildren().add(mainLayout);
		
		mainLayout.setMaxWidth(1024);
		mainLayout.setMaxHeight(720);
		mainLayout.setPrefWidth(1024);
		mainLayout.setPrefHeight(720);

		scene = new Scene(mainStackPane,1024,720);
        scene.getStylesheets().add("stylesheet/bootstrap3.css");

		mainWindow.setScene(scene);
		mainWindow.setOnCloseRequest(x-> END());
		mainWindow.show();
		
		
		
	
		
		Pane left = new Pane();
		left.setStyle("-fx-background-color: #1C8ADB;"); 
		left.setPrefWidth(10);
		mainLayout.setLeft(left);
		

		try {
			House os = serializer.read(House.class, new File("save.xml"));
			myHouse = os;
			myHouse.load();
			
		} catch (Exception e) {
			e.printStackTrace();
			//if the file doesn´t exist or can´t be correctly read
			//it´s gonna be catched here and thats fine
			myHouse = new House();
		}	
		network = new Network(this,myHouse.getAllArduinos());
		SetupRight();
		SetupTop();
		SetupBottom();
		changeToHouse();
		
		//SETUP
//		String macAddress = "AA:BB:CC:DD:EE:FF";
//		myHouse.AddAruinoToFloor(new Arduino(macAddress, "test", null), 0);
		
	}
	
	
	private void SetupBottom() {
		HBox buttonBox = new HBox();
		floorToggle = new ToggleGroup();
		floorToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov,
	            Toggle toggle, Toggle new_toggle) {
				if(toggle!=null&&new_toggle!=null)
					((ToggleButton)toggle).setStyle("-fx-background-color: White;");
				if(new_toggle!=null)
					((ToggleButton)new_toggle).setStyle("-fx-background-color: #98CCFD;");
			}
	                
	    });
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(25);
		for(Floor f : myHouse.getFloors()){
			buttonBox.getChildren().add(CreateFloorToggleButton(f));
		}
		myHouse.getFloors().addListener(new ListChangeListener<Floor>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Floor> c) {
				c.next();
					
				if(c.wasAdded())
					buttonBox.getChildren().add(CreateFloorToggleButton(c.getAddedSubList().get(0)));
				if(c.wasRemoved())
					buttonBox.getChildren().removeIf(x->x.getUserData()==c.getRemoved().get(0));
			}
		});
		GridPane bottom = new GridPane();
		bottom.add(buttonBox,0,0);
		bottom.setAlignment(Pos.CENTER);
		bottom.setStyle("-fx-background-color: #1C8ADB;"); 
		bottom.setPrefHeight(100);
		mainLayout.setBottom(bottom);
	}

	private ToggleButton CreateFloorToggleButton(Floor f){
		ToggleButton tgb = new ToggleButton();
		VBox box = new VBox();
		Image img = new Image(getCurrFloor().getPictureLocation());
		ImageView iv = new ImageView(img);
		tgb.setPrefSize(75, 75);
		tgb.setMaxSize(75, 75);
		if(img.getWidth()>img.getHeight()){
            double scale = img.getHeight()/img.getWidth();
            iv.setFitHeight(scale*75);
            iv.setFitWidth(75);
        } else {
            double scale = img.getWidth()/img.getHeight();
            iv.setFitWidth(scale*75);
            iv.setFitHeight(75);
        }
		tgb.setOnAction(x->{
			changeFloor(f);
		});
		if(f==getCurrFloor()){
			tgb.setSelected(true);
		}
		tgb.setUserData(f);
		tgb.setToggleGroup(floorToggle);
		tgb.setGraphic(box);
		box.setAlignment(Pos.CENTER);
		box.getChildren().addAll(iv,new Label(f.getName()));
		return tgb;
	}
	
	
	private void SetupTop() {
		VBox layer = new VBox();
		HBox buttonBox = new HBox();
		FontAwesome fontAwesome = new FontAwesome();
		Glyph cg = fontAwesome.create(FontAwesome.Glyph.CLOSE);
		Button close = new Button("",cg);
		
		close.setOnMouseEntered(new EventHandler<MouseEvent>
	    () {

	        @Override
	        public void handle(MouseEvent t) {
	        	close.getStyleClass().add("danger");
	        }
	    });

		close.setOnMouseExited(new EventHandler<MouseEvent>
	    () {

	        @Override
	        public void handle(MouseEvent t) {
	        	close.getStyleClass().remove("danger");
	        }
	    });
		
		close.setOnAction(x-> {END();});
		Glyph ming = fontAwesome.create(FontAwesome.Glyph.MINUS);
		Button minimize = new Button("",ming);
		minimize.setOnAction(x-> {mainWindow.setIconified(true);});
		Glyph maxgup = fontAwesome.create(FontAwesome.Glyph.CARET_SQUARE_ALT_UP);
		Glyph maxgdown = fontAwesome.create(FontAwesome.Glyph.CARET_SQUARE_ALT_DOWN);

		Button maximize = new Button("",maxgup);
		maximize.setOnAction(x-> {
			mainWindow.setMaximized(!mainWindow.isMaximized());
			maximize.setGraphic(mainWindow.isMaximized()? maxgdown : maxgup);
		});

		buttonBox.setAlignment(Pos.TOP_RIGHT);
//		buttonBox.setMinHeight(50);
		buttonBox.setMaxHeight(50);
		Label spacer = new Label();
		spacer.setMinWidth(5);
		spacer.setPrefWidth(10);
		buttonBox.getChildren().addAll(minimize,maximize,close,spacer);
		buttonBox.setOnMouseDragged(onMouseDraggedEventHandler);
		buttonBox.setOnMousePressed(onMousePressed);
		layer.getChildren().add(buttonBox);
		HBox temp = new HBox();
		notification = new NotificationPane();
		notification.setMinHeight(50);
		notification.setMaxWidth(Double.MAX_VALUE);
		notification.setMinWidth(500);
		notification.setCloseButtonVisible(true);
		notification.setText("NEW Device found! Add it to any Floor to be able to configure it!");
		notification.setGraphic(new ImageView(Notifications.class.getResource("/org/controlsfx/dialog/dialog-information.png").toExternalForm()));
		notification.setManaged(false);
		notification.setOnShowing(x->{
			notification.setMinHeight(50);
			notification.setManaged(true);
			notification.setMouseTransparent(false);
		});
		notification.setOnHidden(x->{
			notification.setMinHeight(0);
			notification.setManaged(false);
			notification.setMouseTransparent(true);
		});
		temp.getChildren().add(notification);
		HBox.setHgrow(notification, Priority.ALWAYS);
		layer.getChildren().add(temp);
		layer.setStyle("-fx-background-color: #1C8ADB;"); 
		mainLayout.setTop(layer);
//		notification.show();
	}

	private void SetupRight(){
		VBox h = new VBox();
		h.setAlignment(Pos.CENTER);
		Glyph ng  = new FontAwesome().create(FontAwesome.Glyph.PLUS_CIRCLE);
		ng.setFontSize(20);
		newDeviceButton = new NewDevice();
		
		options = new Options();
		NewFloor nf = new NewFloor();
		h.getChildren().addAll(options,newDeviceButton,nf);
		h.setStyle("-fx-background-color: #98CCFD;"
				+ "-fx-border-color: #1C8ADB;"
              + "-fx-border-width: 0 10 0 1; "); 

		mainLayout.setRight(h);
	}
	
	public void changeFloor(Floor floor) {
		if(myHouse.getCurrSelectedFloor().equals(floor)){
			return;
		}
		myHouse.ChangeCurrFloor(floor);
		changeToHouse();
	}

	public void HideNotificationBar(){
		notification.hide();
		options.toFront();
	}

	public void END(){
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			serializer.write(myHouse, new File("save.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(network!=null)
			network.CloseAllThreads();
		SystemTray.getSystemTray().remove(trayIcon);
		Platform.exit();
        System.exit(0);
	}
	
	public void AddArduino(Arduino adr){
		notification.show();
		newArduinos.add(adr);
		Sound.playSound(Clip.notify);
		if(adr.getIPAddress()!=null)
			Notifier.inform("New Device found!", "It is a "+ adr  +"!\n"+"IP-Addess: " + adr.getIPAddress().toString());
	}
	public void RemoveArduino(Arduino adr){
		if(adr == currSelectedArduino){
			mainLayout.setCenter(myHouse.getCenter());
		}
		myHouse.RemoveArduino(adr);
	}
	public void changeCenterToArduino(Arduino arduino) {
		currSelectedArduino = arduino;
		arduinoBigUI = new ArduinoBigUI(currSelectedArduino);
		center.getChildren().clear();
		center.getChildren().add(arduinoBigUI.getCenter());
	}

	public Arduino getCurrSelectedArduino() {
		return currSelectedArduino;
	}

	public void changeToHouse() {
		center.getChildren().clear();
		center.getChildren().add(myHouse.getCenter());
		center.toBack();
		center.maxWidth(1024-10-150);
		center.prefWidth(1024-10-150);

	} 
	
	public House getMyHouse() {
		return myHouse;
	}

	public double GetVolume() {
		if(options==null){
			return 0.5;
		}
		return options.GetVolume();
	}
	
	EventHandler<MouseEvent> onMousePressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            xOffset = mainWindow.getX() - event.getScreenX();
            yOffset = mainWindow.getY() - event.getScreenY();
        }
    };

	EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			mainWindow.setX(t.getScreenX() + xOffset);
			mainWindow.setY(t.getScreenY() + yOffset);
		}
	};
	public Floor getCurrFloor(){
		return myHouse.getCurrSelectedFloor();
	}
	public Stage getStage() {
		return mainWindow;
	}

	public void ArduinoTimedOut(Arduino arduino) {
		if(arduino==null){
			return;
		}
		arduino.setTimedOut(true);
	}

	public ObservableList<Arduino> getNewArduinosObservableList() {
		return newArduinos;
	}

	public void addAllArduino(ArrayList<Arduino> arrayList) {
		for (Arduino arduino : arrayList) {
			arduino.ResetPosition();
		}
		newArduinos.addAll(arrayList);
	}

}
