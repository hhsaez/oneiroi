package com.oneiroi.ai.states;

import com.oneiroi.ai.AIDirector;
import com.oneiroi.ai.AIState;
import com.oneiroi.serial.CommunicationManager;

public class InitialState implements AIState {

	@Override
	public void perform(AIDirector director) {
		director.sendCommand(CommunicationManager.COMMAND_HANDSHAKE);
	}

	@Override
	public void onComplete(AIDirector director, String result) {
		director.setCurrentState(new IdleState());
	}

}
