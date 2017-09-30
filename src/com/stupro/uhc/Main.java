package com.stupro.uhc;

import java.sql.ResultSet;

import com.stupro.uhc.database.DatabaseIntegration;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String args[]) throws Exception {
		//Database call 
				DatabaseIntegration test = new DatabaseIntegration();
				ResultSet rs;
				rs = test.displaySHO();
				while (rs.next()) {
					System.out.println(rs.getString("id") + " " +rs.getString("nameSHO") + " " + rs.getString("metadataSHO") + " "
							+ rs.getString("company") + " " + rs.getString("type"));
				}
				
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
//		String test = "<10;0;0;255(5[])<0;0;128;0(5[1;2])<0;0;0;255(5[])< 0;0;128;0(5[]) < 0;0;0;255(5[]) < 0;0;128;0(5[]) < 0;0;0;255(5[]) < 0;0;128;0(5[]) < 0;0;0;255(5[]) < 0;0;128;0(5[])";
//		System.out.println(test.split("<")[1].split("\\[")[1] + "-> " + test.split("<")[2].split("\\[")[1]);
		new GUI().start(primaryStage);
	}


}
