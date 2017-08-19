package com.stupro.uhc;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.net.URL;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import com.stupro.uhc.arduino.Arduino;
import com.stupro.uhc.arduino.ui.ArduinoBigUI;
import com.stupro.uhc.network.Network;

import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUI {
	public static GUI Instance;
    
	private boolean firstTime = true;
    private TrayIcon trayIcon;
    
	private Stage mainWindow;
	public StackPane mainStackPane;
	private BorderPane mainLayout;
	public StackPane center;
	private Scene scene;
	private Label returnText;
	private Network network;

	private Arduino currSelectedArduino;
	
	private AudioClip notifySound;
	private House myHouse;
	private ArduinoBigUI arduinoBigUI;
	Button b;
	public GUI() {
	}
	
	public void start(Stage s) throws Exception {
		Instance = this;
		createTrayIcon(s);
		
		center = new StackPane();
		center.setStyle("-fx-background-color:transparent;");
		center.toBack();
		this.mainWindow = s;
		mainStackPane = new StackPane();
		myHouse = new House();
		mainLayout= new BorderPane();
		
		network = new Network(this);

		Platform.setImplicitExit(false);
		
		mainLayout.setCenter(center);
		BorderPane.setAlignment(center, Pos.CENTER);
		//TODO: add menu on the top
		//TODO: add a what happend at the bottom?
		mainStackPane.getChildren().add(mainLayout);
		scene = new Scene(mainStackPane,1024,720);
		mainWindow.setScene(scene);
		mainWindow.setOnCloseRequest(x-> END());
		mainWindow.show();
		
	    URL resource = getClass().getResource("/Sounds/notify.mp3");
		notifySound = new AudioClip(resource.toString());// Applet.newAudioClip(url);
		
		changeToHouse();
		SetupRight();
		
		myHouse.AddAruinoToFloor(new Arduino(0, "test", null), 0);
	}
	
	
	private void SetupRight(){
		HBox h = new HBox();
		Glyph g = new FontAwesome().create(FontAwesome.Glyph.COG);
		g.setFontSize(20);
		b = new Button("Settings",g);
		b.setOnAction(x->showOptions());
		h.getChildren().add(b);
		mainLayout.setRight(h);
	}
	
	
	
	
	private void showOptions() {
		PopOver pop = new PopOver();
		pop.setOnCloseRequest(x->{SaveOptions();});
		pop.setTitle("Options"); 
		
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(5));
		vbox.setSpacing(5);
		vbox.getChildren().add(CreateHBox(new Label("BlaBla"),new ComboBox<String>()));
		vbox.getChildren().add(CreateHBox(new Label("Minimize to tray"),new CheckBox(" s ")));
		pop.setContentNode(vbox);
		pop.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		pop.setArrowLocation(ArrowLocation.TOP_CENTER);
		pop.show(b);
	}

	private HBox CreateHBox(Node n1, Node n2){
		HBox hbox = new HBox();
		hbox.getChildren().addAll(n1,n2);
		hbox.setSpacing(10);
		return hbox;
	}
	
	private void SaveOptions() {
		
	}

	public void addReturnText(String s){
		returnText.setText(returnText.getText() + "\n" + s);
	}
	public void END(){
		if(network!=null)
			network.CloseAllThreads();
		SystemTray.getSystemTray().remove(trayIcon);
		Platform.exit();
        System.exit(0);
	}
	
	public void AddArduino(Arduino adr){
		notifier("New Device found!", "It is a "+ adr  +"!\n"+"IP-Addess: " + adr.getIPAddress().toString());
		if(notifySound.isPlaying()==false)
			notifySound.play();
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

//Under here is code for Tray Icon stuff
    public void createTrayIcon(final Stage stage) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            // instead of minimizing goto the tray
            stage.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> obs, Boolean oldB, Boolean newB) {
					if(newB.booleanValue())
						hide(stage);	
					stage.setIconified(false);
				}
            });
            // create a action listener to listen for default action executed on the tray icon
            final ActionListener closeListener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    END();
                }
            };

            ActionListener showListener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                            stage.toFront();
                        }
                    });
                }
            };
            // create popup menu
            PopupMenu popup = new PopupMenu();

            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(showListener);
            popup.add(showItem);

            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(closeListener);
            popup.add(closeItem);
            
            // create a tray icon with a image
            Image image = Toolkit.getDefaultToolkit().getImage("Source/images/tray.png");
            int trayIconWidth = new TrayIcon(image).getSize().width;
            trayIcon = new TrayIcon(image.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "Home Device Manager", popup);
            //add a menu to icon
            trayIcon.addActionListener(showListener);
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

    }

    public void showProgramIsMinimizedMsg() {
    	if(firstTime){
    		notifier("The Programm is in the tray.",
    				"It will search for new devices.");
    		firstTime = false;
    	}
    }

    private void hide(final Stage stage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (SystemTray.isSupported()) {
                	stage.hide();
                    showProgramIsMinimizedMsg();
                } else {
                    END();
                }
            }
        });
        
    }
	
	private static void notifier(String pTitle, String pMessage) {
        Platform.runLater(() -> {
                    Stage owner = new Stage(StageStyle.UTILITY);
                    owner.setOpacity(0);
                    owner.setIconified(false);
                    StackPane root = new StackPane();
                    root.setStyle("-fx-background-color: TRANSPARENT");
                    Scene scene = new Scene(root, 1, 1);
                    scene.setFill(Color.TRANSPARENT);
                    owner.setScene(scene);
                    owner.setWidth(1);
                    owner.setHeight(1);
                    owner.toBack();
                    owner.show();
                    Notifications.create().title(pTitle).text(pMessage).showInformation();
                }
        );
    }

	public House getMyHouse() {
		return myHouse;
	}
	
}
