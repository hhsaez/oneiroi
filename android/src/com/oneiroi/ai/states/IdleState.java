package com.oneiroi.ai.states;

import com.oneiroi.ai.AIDirector;
import com.oneiroi.ai.AIState;
import com.oneiroi.serial.CommunicationManager;

public class IdleState implements AIState {
	
	private final int PREF_DISTANCE_FRONT = 30;
	private final int MIN_DISTANCE_LEFT = 15;
	private final int PREF_DISTANCE_LEFT = 30;
	private final int MIN_DISTANCE_RIGHT = 15;
	private final int PREF_DISTANCE_RIGHT = 30;

	@Override
	public void perform(AIDirector director) {
		director.sendCommand(CommunicationManager.COMMAND_SCAN);
	}

	@Override
	public void onComplete(AIDirector director, String result) {
		final String[] input = result.replace("\\D+", " ").trim().split(" ");
		if (input.length == 3) {
			final int left = Integer.parseInt(input[0]);
			final int front = Integer.parseInt(input[1]);
			final int right = Integer.parseInt(input[2]);
			
			if (front > PREF_DISTANCE_FRONT && right > MIN_DISTANCE_RIGHT && left > MIN_DISTANCE_LEFT ) {
				director.setCurrentState(new MoveState(MoveState.Direction.FORWARD));
			} 
			else if (right > left && right > PREF_DISTANCE_RIGHT) {
				director.setCurrentState(new TurnState(TurnState.Direction.RIGHT));
			}
			else if (left > right && left > PREF_DISTANCE_LEFT) {
				director.setCurrentState(new TurnState(TurnState.Direction.LEFT));
			}
			else {
				director.setCurrentState(new MoveState(MoveState.Direction.BACKWARD));
			}
		}
		else {
			// attempt rescan
			director.setCurrentState(new IdleState());
		}
	}

}
