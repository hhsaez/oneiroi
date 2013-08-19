package com.oneiroi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements CommunicationManager.Listener {
	
	private Handler handler = new Handler();
	private CommunicationManager commMananger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setCommandForButton(R.id.btnScan, CommunicationManager.COMMAND_SCAN);
		setCommandForButton(R.id.btnForward, CommunicationManager.COMMAND_FORWARD);
		setCommandForButton(R.id.btnBackward, CommunicationManager.COMMAND_BACKWARD);
		setCommandForButton(R.id.btnRight, CommunicationManager.COMMAND_RIGHT);
		setCommandForButton(R.id.btnLeft, CommunicationManager.COMMAND_LEFT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void setCommandForButton(final int buttonId, final String command) {
		((Button)(findViewById(buttonId))).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommunicationManager commMananger = MainActivity.this.commMananger;
				if (commMananger != null) {
					commMananger.sendCommand(command);
				}
				else {
					Log.e("LLEGO", "Communication manager not initialized");
				}
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (this.commMananger != null) {
			this.commMananger.stop();
			this.commMananger = null;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (this.commMananger == null) {
			this.commMananger = new CommunicationManager(this, this);
		}
		this.commMananger.start();
	}
	
	@Override
	public void onSentSuccess(String command) {
		updateText(R.id.txtSent, command);
	}

	@Override
	public void onDataReceived(String response) {
		updateText(R.id.txtReceived, response);
	}

	@Override
	public void onStatusChanged(String message) {
		updateText(R.id.txtStatus, message);
	}

	private void updateText(final int textViewId, final String text) {
		this.handler.post(new Runnable() {
			@Override
			public void run() {
				((TextView)(findViewById(textViewId))).setText(text);
			}
		});
	}

}
