package com.stupro.uhc;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String args[]) throws Exception {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
//		String test = "<10;0;0;255(5[])<0;0;128;0(5[1;2])<0;0;0;255(5[])< 0;0;128;0(5[]) < 0;0;0;255(5[]) < 0;0;128;0(5[]) < 0;0;0;255(5[]) < 0;0;128;0(5[]) < 0;0;0;255(5[]) < 0;0;128;0(5[])";
//		System.out.println(test.split("<").length + "-> " + test.split("<")[1]);
		new GUI().start(primaryStage);
	}


}