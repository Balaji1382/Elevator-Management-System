package mainpackage;

public enum LiftState {
	IDLE,   // when the elevator has no jobs (at the moment)
	MOVING,	// when the elevator is moving while in a job (either up or down)
	STOPPED; // when the elevator stops at a floor in the middle of a job
	//MAINTANENCE;
}
