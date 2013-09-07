package com.oneiroi.ai;

public interface AIState {
	
	void perform(AIDirector director);
	void onComplete(AIDirector director, String result);

}
