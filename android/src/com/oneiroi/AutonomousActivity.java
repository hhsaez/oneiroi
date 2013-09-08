package com.oneiroi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

import com.oneiroi.ai.AIDirector;

public class AutonomousActivity extends Activity implements AIDirector.Listener {
	
	private AIDirector director;
	private Handler uiThreadHandler = new Handler();
	private TextView txtOutput;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_autonomous);
		
		this.txtOutput = (TextView) findViewById(R.id.txtOutput);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (this.director != null) {
			this.director.stop();
			this.director = null;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (this.director == null) {
			this.director = new AIDirector(this);
		}
		this.director.start(this);
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
	
}
