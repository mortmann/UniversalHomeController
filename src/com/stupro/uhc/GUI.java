package com.stupro.uhc;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
	private Label returnText;
	private Network network;

	private Arduino currSelectedArduino;
	
	private House myHouse;
	private ArduinoBigUI arduinoBigUI;
	
	private NewDevice newDeviceButton;
	private ObservableList<Arduino> newArduinos;
	
	ComboBox<Floor> currFloorSelect;
	
	private Options options;
	
	//for windowdrag
	protected double xOffset;
	protected double yOffset;

	private NotificationPane notification;

	
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
		mainWindow.initStyle(StageStyle.UNDECORATED);
		
		mainStackPane = new StackPane();
		myHouse = new House();
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			House os = serializer.read(House.class, new File("save.xml"));
			myHouse = os;
			myHouse.load();
		} catch (Exception e) {
			//if the file doesn´t exist or can´t be correctly read
			//it´s gonna be catched here and thats fine
		}	
		network = new Network(this,myHouse.getAllArduinos());

		mainLayout= new BorderPane();
		

		Platform.setImplicitExit(false);
		mainLayout.setCenter(center);
		BorderPane.setAlignment(center, Pos.CENTER);
		//TODO: add menu on the top
		mainStackPane.getChildren().add(mainLayout);
		scene = new Scene(mainStackPane,1024,720);
        scene.getStylesheets().add("stylesheet/bootstrap3.css");

		mainWindow.setScene(scene);
		mainWindow.setOnCloseRequest(x-> END());
		mainWindow.show();
		
		
		changeToHouse();
		SetupRight();
		SetupTop();
		
		
		//SETUP
//		String macAddress = "AA:BB:CC:DD:EE:FF";
//		myHouse.AddAruinoToFloor(new Arduino(macAddress, "test", null), 0);
		
	}
	
	
	private void SetupTop() {
		VBox layer = new VBox();
		HBox buttonBox = new HBox();
		FontAwesome fontAwesome = new FontAwesome();
		Glyph cg = fontAwesome.create(FontAwesome.Glyph.CLOSE);
		Button close = new Button("",cg);
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
		buttonBox.getChildren().addAll(minimize,maximize,close);
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
		notification.setOnShowing(x->{notification.setManaged(true);});
		notification.setOnHidden(x->{notification.setManaged(false);});
		temp.getChildren().add(notification);
		HBox.setHgrow(notification, Priority.ALWAYS);
		
		layer.getChildren().add(temp);

		mainLayout.setTop(layer);
//		notification.show();
	}

	private void SetupRight(){
		VBox h = new VBox();
		Glyph ng  = new FontAwesome().create(FontAwesome.Glyph.PLUS_CIRCLE);
		ng.setFontSize(20);
		newDeviceButton = new NewDevice();
//		newDeviceButton.setOnAction(x->showNewDevices());
		options = new Options();
		currFloorSelect = new ComboBox<>(myHouse.getFloors());
		currFloorSelect.getSelectionModel().select(0);
		currFloorSelect.setOnAction(x->ChangeFloor());
		h.getChildren().addAll(options,newDeviceButton,currFloorSelect,new NewFloor());
		mainLayout.setRight(h);
	}
	
	private void ChangeFloor() {
		Floor f = currFloorSelect.getSelectionModel().getSelectedItem();
		myHouse.ChangeCurrFloor(f);
		changeToHouse();
	}

	public void HideNotificationBar(){
		notification.hide();
	}

	public void addReturnText(String s){
		returnText.setText(returnText.getText() + "\n" + s);
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

	public Stage getStage() {
		return mainWindow;
	}

	public void ArduinoTimedOut(Arduino arduino) {
		arduino.setTimedOut(true);
	}

	public ObservableList<Arduino> getNewArduinosObservableList() {
		return newArduinos;
	}
}
