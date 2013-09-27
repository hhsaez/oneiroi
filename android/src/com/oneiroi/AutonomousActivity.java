package com.oneiroi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

import com.oneiroi.ai.AIDirector;
import com.oneiroi.camera.PhotoHandler;
import com.oneiroi.networking.NanoHTTPD.IHTTPSession;
import com.oneiroi.networking.NanoHTTPD.Response;
import com.oneiroi.networking.OneiroiHTTPD;

public class AutonomousActivity extends Activity implements AIDirector.Listener, OneiroiHTTPD.Provider {
	
	private AIDirector director;
	private Handler uiThreadHandler = new Handler();
	private TextView txtOutput;
	private OneiroiHTTPD httpd;
	private Camera camera;
	private int cameraId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_autonomous);
		
		this.txtOutput = (TextView) findViewById(R.id.txtOutput);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		cameraId = findFrontFacingCamera();
		if (cameraId < 0) {
			Log.e("LLEGO", "Cannot find front facing camera");
		}
		else {
			camera = Camera.open(cameraId);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		if (this.director != null) {
			this.director.stop();
			this.director = null;
		}
		
		if (this.httpd != null) {
			this.httpd.stop();
		}
		
		if (this.camera != null) {
			this.camera.release();
			this.camera = null;
		}
		
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (this.director == null) {
			this.director = new AIDirector(this);
		}
		this.director.start(this);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (httpd == null) {
						httpd = new OneiroiHTTPD(AutonomousActivity.this);
					}
					
					httpd.start();
					
					WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
					WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
					int ip = wifiInfo.getIpAddress();
					@SuppressWarnings("deprecation")
					String ipAddress = Formatter.formatIpAddress(ip);
					onStateChanged("HTTPD started at http://" + ipAddress + ":" + httpd.getListeningPort() + "/status");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onStateChanged(final String message) {
		this.uiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				txtOutput.append(message + "\n");
			}
		});
	}

	@Override
	public Response onStatusRequest(IHTTPSession session) {
		return new Response(decorateResponseText(encodeToHTML(txtOutput.getText().toString())));
	}
	
	@Override
	public Response onCameraRequest(IHTTPSession session) {
		if (this.camera == null) {
			return new Response("Cannot take picture");
		}
		
		Log.d("LLEGO", "taking picture");
		PhotoHandler handler = new PhotoHandler();
		this.camera.takePicture(null, null, handler);
		
		File file = new File(handler.getFileName());
		Response response = null;
		if (file.exists()) {
			InputStream is;
			try {
				is = new BufferedInputStream(new FileInputStream(file));
				response = new Response(Response.Status.OK, OneiroiHTTPD.MIME_JPG, is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else {
			response = new Response("Cannot find image: " + handler.getFileName());
		}
		
		return response;
	}
	
	private String decorateResponseText(String body) {
		StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head><title>Oneiroi Server</title></head>");
        sb.append("<body>");
        sb.append(body);
        sb.append("</body>");
        sb.append("</html>");
		return sb.toString();
	}
	
	private String encodeToHTML(String input) {
		return input.replaceAll("(\r\n|\n)", "<br />");
	}
	
	private int findFrontFacingCamera() {
		int cameraId = -1;
		int cameraCount = Camera.getNumberOfCameras();
		for (int i = 0; i < cameraCount; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				break;
			}
		}
		
		return cameraId;
	}
}

