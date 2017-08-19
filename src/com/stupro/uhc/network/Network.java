package com.stupro.uhc.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.stupro.uhc.GUI;
import com.stupro.uhc.arduino.Arduino;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

// 0 = broadcast
// 1 = heartbeat
// 2 = data
// 3+ = commands for arduino

// Number of type + _ + data here
public class Network {
	public static Network Instance;
	
	private static int timeoutTime = 5000;
	
	private DatagramSocket clientSocket;
	private TimerTask heartbeat;
	private Thread receive;
	private Timer timer = new Timer();
	private HashMap<InetAddress,Long> ipToTimer;
	private ArrayList<InetAddress> inetAdresses;
	
	private HashMap<InetAddress,Arduino> arduinos;

	
	private GUI gui;

	private InetAddress myAddress;
	
	public Network(GUI gui) {
		Instance = this;
		try {
			clientSocket = new DatagramSocket(8888);
			clientSocket.setReuseAddress(true);
		} catch (SocketException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Warning!");
			alert.setContentText("Required Port is already in use! Please freeup that Port!");
		    URL resource = getClass().getResource("/Sounds/error.wav");
			new AudioClip(resource.toString()).play();
			alert.showAndWait();

			System.exit(0);
		}
		arduinos = new HashMap<>();

		this.gui = gui;
		ipToTimer = new HashMap<>();
		inetAdresses = new ArrayList<InetAddress>();
		myAddress = null;
		try {
			myAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		ThreadPoolExecutor service = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
		service.setKeepAliveTime(5, TimeUnit.SECONDS);
		receive = new Thread("Receive") {
			public void run() {				
				while(!Thread.currentThread().isInterrupted()){
					byte[] receiveData = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					try {
						clientSocket.setSoTimeout(2000);
						clientSocket.receive(receivePacket);
					} catch (Exception e) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException ie) {
							//do nothing because it doesnt matter
						}
						continue;
					}
					
					
					InetAddress IPAddress = receivePacket.getAddress();
					//reject own broadcast
					if(IPAddress.equals(myAddress)){
						continue;
					}
					
					String data = new String(receivePacket.getData(), 0, receivePacket.getLength());
//					System.out.println(IPAddress + " Data " + data + " ");
					String[] splitData = data.split("_");
					int type = Integer.parseInt(splitData[0]);
					
					switch(type){
						case 0: 
							if(inetAdresses.contains(IPAddress) == false){
								inetAdresses.add(IPAddress);
								arduinos.put(IPAddress, new Arduino((long)Math.random(), "placeholder", IPAddress));

								ipToTimer.put(IPAddress, System.currentTimeMillis());
								
								//this is because fx can only change ui in fx-thread so it runs it later in it
								Platform.runLater(new Runnable(){
									@Override
									public void run() {
										gui.AddArduino(arduinos.get(IPAddress));
									}
								});
							}
						break;
						case 1:
							break;
						case 2: 
							ipToTimer.replace(IPAddress, System.currentTimeMillis());
							HandleInfo(splitData[1],IPAddress);
							
							break;
						case 3: //received command -> update?
							System.out.println(splitData[1]);
							break;
						default:
							break;
					}
					
				}
			}
		};
		heartbeat = new TimerTask() {
			public void run() {
				for (int i = inetAdresses.size()-1; i >= 0; i--) {
					if(ipToTimer.containsKey(inetAdresses.get(i)) && System.currentTimeMillis()-ipToTimer.get(inetAdresses.get(i)) > timeoutTime){
						System.out.println("Remove ARDUINO");
						int num = i;
						InetAddress inet = inetAdresses.get(num);
						Platform.runLater(new Runnable(){
							@Override
							public void run() {
								gui.RemoveArduino(arduinos.get(inet));
							}
						});
						ipToTimer.remove(inetAdresses.get(i));
						inetAdresses.remove(i);
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// SEND broadcast to find new Arduinos
				byte[] sendData = new byte[1024];
				LocalDateTime timePoint = LocalDateTime.now();				
				String time = timePoint.format(DateTimeFormatter.ofPattern("H,m,s,d,M,YYYY"));
				sendData = ("0_"+time).getBytes(); // broadcast
				DatagramPacket sendPacket;
				try {
					sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
					if(clientSocket.isClosed()){
						Thread.currentThread().interrupt();
					} else {
						clientSocket.send(sendPacket);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// Send to all found arduinos to get their data
				// and check if they are still alive
				for (InetAddress inetAddress : inetAdresses) {
					sendData = "1_heartbeatyo".getBytes();
					try {
						sendPacket = new DatagramPacket(sendData, sendData.length,inetAddress, 8888);
						clientSocket.send(sendPacket);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		timer.scheduleAtFixedRate(heartbeat, 0, 500);
		receive.start();
		
	}
	public void HandleInfo(String data, InetAddress iPAddress) {
		String[] info = data.split(";");
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				arduinos.get(iPAddress).HandleInfo(info);
			}
		});
	}
	
	
	public void SendColorLED(int led,Color c,Arduino ar){
		if(ar.getIPAddress() == null){
			return;
		}
//		InetAddress IPAddress = receivePacket.getAddress();//InetAddress.getByName("255.255.255.255");// 192.168.0.32
		byte[] sendData = new byte[1024];
		String s ="4_"+ led+","+(int)(c.getRed()*255)+","+(int)(c.getGreen()*255)+","+(int)(c.getBlue()*255);
		System.out.println(s);
		sendData = s.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ar.getIPAddress(), 8888);
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void SendTimeRepeatLED(int led, int secRepeatTime,Arduino ar){
		if(ar.getIPAddress() == null){
			return;
		}
		byte[] sendData = new byte[1024];
		String s ="5_"+ led+","+secRepeatTime;
		System.out.println(s);
		sendData = s.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ar.getIPAddress(), 8888);
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void SendPacketToArduino(String data){
		SendPacketToThisArduino(data, gui.getCurrSelectedArduino());
	}
	
	public void CloseAllThreads(){
		//CLOSE ALL THREADS SO IT CAN COMPLETLY CLOSE THE PROGRAM
		receive.interrupt();
		timer.cancel();
		heartbeat.cancel();
		timer.purge();
		
		clientSocket.close();
		
	}
	public void SendPacketToThisArduino(String data, Arduino arduino) {
		if(arduino.getIPAddress() == null){
			return;
		}
		byte[] sendData = new byte[1024];
		sendData = data.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, arduino.getIPAddress(), 8888);
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
}
