package com.oneiroi.serial;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

public class CommunicationManager {

	public static final String COMMAND_HANDSHAKE = "handshake";
	public static final String COMMAND_SCAN = "scan";
	public static final String COMMAND_FORWARD = "forward";
	public static final String COMMAND_BACKWARD = "backward";
	public static final String COMMAND_RIGHT = "right";
	public static final String COMMAND_LEFT = "left";
	
	private static final int BAUD_RATE = 9600;
	
	public interface Listener {
		void onSentSuccess(String command);
		void onDataReceived(String response);
		void onStatusChanged(String message);
	}
	
	private UsbSerialDriver serialDevice;
	private UsbManager usbManager;
	private SerialInputOutputManager serialIOManager;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private Listener listener;
	
	public CommunicationManager(Context context, Listener listener) {
		this.listener = listener;
		this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		
		this.listener.onStatusChanged("Communication manager created");
	}
	
	public void start() {
		this.listener.onStatusChanged("Acquiring device");
		serialDevice = UsbSerialProber.acquire(usbManager);
		if (serialDevice == null) {
			this.listener.onStatusChanged("Error acquiring device on start");
		}
		else {
			try {
				this.listener.onStatusChanged("Opening device");
				serialDevice.open();
				serialDevice.setBaudRate(BAUD_RATE);
				stopIOManager();
				startIOManager();
				this.listener.onStatusChanged("Device connected");
			}
			catch (Exception e) {
				this.listener.onStatusChanged("Error opening device on start");
				try {
					serialDevice.close();
				}
				catch (Exception ex) {
					// ignore it
				}
				serialDevice = null;
			}
		}
	}
	
	public void stop() {
		stopIOManager();
		if (serialDevice != null) {
			try {
				serialDevice.close();
			}
			catch (Exception e) {
				// ignore it
			}
			serialDevice = null;
		}
	}
	
	public void sendCommand(final String command) {
		if (serialDevice != null) {
			String str = command + "\n";
			try {
				byte[] bytes = str.getBytes();
				serialDevice.write(bytes, bytes.length);
				this.listener.onStatusChanged("Sending " + bytes.length + " bytes");
				this.listener.onSentSuccess(command);
			} catch (Exception e) {
				this.listener.onStatusChanged("Error sending data");
				e.printStackTrace();
			}
		}
	}
	
	private void startIOManager() {
		this.listener.onStatusChanged("Starting IO Manager");
		if (serialDevice != null) {
			serialIOManager = new SerialInputOutputManager(serialDevice, new SerialInputOutputManager.Listener() {
				
				private StringBuilder buffer = new StringBuilder();
				
				@Override
				public void onRunError(Exception arg0) {
					CommunicationManager.this.listener.onStatusChanged("Error on Serial IO: " + arg0.getMessage());
				}
				
				@Override
				public void onNewData(byte[] bytes) {
					try {
						CommunicationManager.this.listener.onStatusChanged("Received " + bytes.length + " bytes");
						
						String received = new String(bytes);
						for (int i = 0; i < received.length(); i++) {
							char c = received.charAt(i);
							if (c == '\n') {
								CommunicationManager.this.listener.onDataReceived(buffer.toString());
								buffer = new StringBuilder();
							}
							else {
								buffer.append(c);
							}
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						CommunicationManager.this.listener.onStatusChanged("Error parsing data");
					}
				}
			});
			executor.submit(serialIOManager);
		}
	}
	
	private void stopIOManager() {
		this.listener.onStatusChanged("Stopping IO Manager");
		if (serialIOManager != null) {
			serialIOManager.stop();
			serialIOManager = null;
		}
	}
	
}
