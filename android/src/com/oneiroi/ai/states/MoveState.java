package com.oneiroi.ai.states;

import com.oneiroi.ai.AIDirector;
import com.oneiroi.ai.AIState;
import com.oneiroi.serial.CommunicationManager;

public class MoveState implements AIState {
	
	public enum Direction {
		FORWARD,
		BACKWARD
	}
	
	private Direction direction;
	
	public MoveState(Direction direction) {
		this.direction = direction;
	}

	@Override
	public void perform(AIDirector director) {
		if (this.direction.equals(Direction.FORWARD)) {
			director.sendCommand(CommunicationManager.COMMAND_FORWARD);
		}
		else if (this.direction.equals(Direction.BACKWARD)) {
			director.sendCommand(CommunicationManager.COMMAND_BACKWARD);
		}
		else {
			director.setCurrentState(new IdleState());
		}
	}

	@Override
	public void onComplete(AIDirector director, String result) {
		director.setCurrentState(new IdleState());
	}
}
