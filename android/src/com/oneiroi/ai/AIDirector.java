package com.oneiroi.ai;

import android.content.Context;

import com.oneiroi.CommunicationManager;
import com.oneiroi.ai.states.IdleState;

public class AIDirector implements CommunicationManager.Listener {
	
	public interface Listener {
		void onStateChanged(String message);
	}
	
	private AIState currentState;
	private CommunicationManager commManager;
	private Listener listener;
	
	public AIDirector(Listener listener) {
		this.listener = listener;
	}
	
	public AIState getCurrentState() {
		return this.currentState;
	}
	
	public void setCurrentState(AIState state) {
		this.listener.onStateChanged("State changed");
		this.currentState = state;
		if (this.currentState != null) {
			this.currentState.perform(this);
		}
	}

	public void stop() {
		if (this.commManager != null) {
			this.commManager.stop();
			this.commManager = null;
		}
		this.listener.onStateChanged("AI director stopped");
	}
	
	public void start(Context context) {
		this.listener.onStateChanged("AI director started");
		if (this.commManager == null) {
			this.commManager = new CommunicationManager(context, this);
		}
		this.commManager.start();
		this.setCurrentState(new IdleState());
	}
	
	public void sendCommand(String command) {
		if (this.commManager != null) {
			this.commManager.sendCommand(command);
		}
	}

	@Override
	public void onSentSuccess(String command) {
		this.listener.onStateChanged("Sent: " + command);
	}

	@Override
	public void onDataReceived(String response) {
		this.listener.onStateChanged("Received: " + response);
		if (this.currentState != null) {
			this.currentState.onComplete(this, response);
		}
	}

	@Override
	public void onStatusChanged(String message) {
		this.listener.onStateChanged(message);
	}
}

