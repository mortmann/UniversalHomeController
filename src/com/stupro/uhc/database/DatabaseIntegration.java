package com.stupro.uhc.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseIntegration {
	private static Connection con;
	private static boolean hasData = false;

	public ResultSet displaySHO() throws ClassNotFoundException, SQLException {
		if (con == null) {
			getConnection();
		}
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT id,nameSHO, metadataSHO, company, type FROM smartHomeObject;");
		return res;
	}

	private void getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite:SQLiteTest1.db");
		initialise();
	}

	private void initialise() throws SQLException {
		if (!hasData) {
			hasData = true;

			Statement state = con.createStatement();
			ResultSet res = state
					.executeQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name='smartHomeObject';");
			if (!res.next()) {
				System.out.println("Building the SmartHomeObject table with prepopulated values.");
				// Create Database Tables
				Statement state2 = con.createStatement();
				state2.execute("CREATE TABLE smartHomeObject(id INTEGER PRIMARY KEY, " 
						+ "nameSHO varchar(60),"
						+ "metadataSHO varchar(60)," + "company varchar(60)," + "type varchar(60)"
						+ ")");

				// inserting some sample data
				PreparedStatement prep = con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				
				prep.setString(2, "LED Kette");
				prep.setString(3, "00010A0000001");
				prep.setString(4, "1");
				prep.setString(5, "Light");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "EnviroHome");
				prep.setString(3, "000211111");
				prep.setString(4, "2");
				prep.setString(5, "Environment Measure");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "SimpleWindow");
				prep.setString(3, "00031");
				prep.setString(4, "1");
				prep.setString(5, "WindowDoor Contact");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "MoveItSave");
				prep.setString(3, "00041");
				prep.setString(4, "4");
				prep.setString(5, "Motion Detector");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "SpyGuy");
				prep.setString(3, "0005011");
				prep.setString(4, "4");
				prep.setString(5, "Camera");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "Green Thumb");
				prep.setString(3, "00061111");
				prep.setString(4, "4");
				prep.setString(5, "Watering");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "Smoke King");
				prep.setString(3, "0007111");
				prep.setString(4, "4");
				prep.setString(5, "Smoke Detector");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "Squeaky");
				prep.setString(3, "000800011");
				prep.setString(4, "4");
				prep.setString(5, "Siren");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "RouteMi");
				prep.setString(3, "00090011");
				prep.setString(4, "3");
				prep.setString(5, "Router");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "Simple Heat");
				prep.setString(3, "000A111");
				prep.setString(4, "1");
				prep.setString(5, "Heater Thermostat");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "ButtonUp");
				prep.setString(3, "000B0101");
				prep.setString(4, "4");
				prep.setString(5, "Button");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "SimpleElectSave");
				prep.setString(3, "000C02000");
				prep.setString(4, "1");
				prep.setString(5, "Switch Adapter");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "KeepITCool");
				prep.setString(3, "000D111");
				prep.setString(4, "4");
				prep.setString(5, "Fridge");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "WashMi");
				prep.setString(3, "000E011111");
				prep.setString(4, "3");
				prep.setString(5, "Washing Maschine");
				prep.execute();

				con.prepareStatement("INSERT INTO smartHomeObject values(?,?,?,?,?);");
				prep.setString(2, "ShutYourHome");
				prep.setString(3, "000F0111");
				prep.setString(4, "2");
				prep.setString(5, "Shutters");
				prep.execute();

			}
		}
	}

	public String getMetaDataForID(int id) {
		if (con == null) {
			try {
				getConnection();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Statement stmt = null;
		String query = "select metadataSHO " + "from smartHomeObject " + "where id=" + id + " ";
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("metadataSHO");
		} catch (Exception e) {

		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	//use or remove
	public void addUser(String firstName, String lastName) throws ClassNotFoundException, SQLException {
		if (con == null) {
			getConnection();

		}
		PreparedStatement prep = con.prepareStatement("INSERT INTO user values (?,?,?);");
		prep.setString(2, firstName);
		prep.setString(3, lastName);
		prep.execute();
	}

	public String getNameForID(int typeID) {
		if (con == null) {
			try {
				getConnection();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Statement stmt = null;
	    String query = "select nameSHO " +
	                   "from smartHomeObject "+
	                   "where id="+typeID;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			System.out.println(rs.getString("nameSHO"));
			return rs.getString("nameSHO");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
