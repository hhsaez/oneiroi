package com.oneiroi.ai.states;

import com.oneiroi.CommunicationManager;
import com.oneiroi.ai.AIDirector;
import com.oneiroi.ai.AIState;

public class TurnState implements AIState {
	
	public enum Direction {
		LEFT,
		RIGHT
	}
	
	private Direction direction;
	
	public TurnState(Direction direction) {
		this.direction = direction;
	}

	@Override
	public void perform(AIDirector director) {
		if (this.direction.equals(Direction.LEFT)) {
			director.sendCommand(CommunicationManager.COMMAND_LEFT);
		}
		else if (this.direction.equals(Direction.RIGHT)) {
			director.sendCommand(CommunicationManager.COMMAND_RIGHT);
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
