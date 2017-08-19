package com.stupro.uhc;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String args[]) throws Exception {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new GUI().start(primaryStage);
	}
	
}
