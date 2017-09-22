package com.stupro.uhc.misc;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;

import com.stupro.uhc.GUI;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

public class TrayHandler {
	
	private boolean firstTime = true;
	private TrayIcon trayIcon;
	
	//Under here is code for Tray Icon stuff
    public TrayHandler(final Stage stage) {
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
                    GUI.Instance.END();
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
    		Notifier.inform("The Programm is in the tray.",
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
                	GUI.Instance.END();
                }
            }
        });
        
    }

}
