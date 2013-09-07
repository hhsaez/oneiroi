package com.oneiroi.ai.states;

import com.oneiroi.CommunicationManager;
import com.oneiroi.ai.AIDirector;
import com.oneiroi.ai.AIState;

public class IdleState implements AIState {

	@Override
	public void perform(AIDirector director) {
		director.sendCommand(CommunicationManager.COMMAND_SCAN);
	}

	@Override
	public void onComplete(AIDirector director, String result) {
		final String[] input = result.replace("\\D+", " ").trim().split(" ");
		if (input.length == 3) {
			final int right = Integer.parseInt(input[0]);
			final int front = Integer.parseInt(input[1]);
			final int left = Integer.parseInt(input[2]);
			
			if (front > 30) {
				director.setCurrentState(new MoveState(MoveState.Direction.FORWARD));
			} 
			else if (right > left && right > 30) {
				director.setCurrentState(new TurnState(TurnState.Direction.RIGHT));
			}
			else if (left > right && left > 30) {
				director.setCurrentState(new TurnState(TurnState.Direction.LEFT));
			}
			else {
				director.setCurrentState(new MoveState(MoveState.Direction.BACKWARD));
			}
		}
		
		director.setCurrentState(new IdleState());
	}

}
