package mainpackage;

public class Elevator implements Runnable{
	
	// to know the id of the elevator
	private Integer id;
	
	// to know where this elevator is currently at
	private Integer currentFloor;
	
	// to know the destinationFloor
	// when this elevator is idle ,current Floor is same as destination Floor
	private Integer destinationFloor;
	
	// to know the direction of this elevator when it's moving or idle
	private LiftDirection currentDirection;
	
	// to know the state of this elevator
	private LiftState currentState;
	
	// to know whether this elevator is to stop at a floor
	protected Boolean[] stops;
	
	// to know whether this elevator is to stop at a floor at what direction
	protected Integer[] directionStops; // UP = 2, DOWN = 4;
	
	// to stop or run this thread
	private Boolean exit = false;
	
	// to store the total Floors
	private Integer totalFloors = ElevatorController.getTotalFloors();
	
	public Elevator(Integer id) {
		this.id = id;
		this.currentFloor = 0;
		this.destinationFloor = 0;
		this.currentDirection = LiftDirection.UP;
		this.currentState = LiftState.IDLE;
		stops = new Boolean[totalFloors];
		directionStops = new Integer[totalFloors];
		for(int index = 0; index < totalFloors; index++) {
			stops[index] = false;
			directionStops[index] = 0;
		}
	}
	
	
	public Integer getId() {
		return this.id;
	}
	
	
	public Integer getCurrentFloor() {
		return this.currentFloor;
	}
	
	
	public LiftDirection getCurrentDirection() {
		return this.currentDirection;
	}
	
	
	public LiftState getCurrentState() {
		return this.currentState;
	}
	
	/**
	 * changes the currentDirection & currentState of the Elevator
	 * Object
	 * @param currentFloor
	 * @param destinationFloor
	 */
	private void changeParameters(Integer currentFloor, Integer destinationFloor) {
		if(currentFloor > destinationFloor) {
			this.currentDirection = LiftDirection.DOWN;
			this.currentState = LiftState.MOVING;
		}
		else if(currentFloor < destinationFloor) {
			this.currentDirection = LiftDirection.UP;
			this.currentState = LiftState.MOVING;
		}
		// when there is no more further stops to be made 
		else {
			if(this.currentFloor == 0) {
				this.currentDirection = LiftDirection.UP;
			}
			else if(this.currentFloor == totalFloors) {
				this.currentDirection = LiftDirection.DOWN;
			}
			else {
				;
			}
			this.currentState = LiftState.IDLE;
		}
	}
	
	/**
	 * adds stops when a job is found
	 * @param floorNumber
	 * @param direction
	 */
	protected void addStops(Integer floorNumber, LiftDirection direction) {
		this.stops[floorNumber] = true;
		if(direction == LiftDirection.UP) {
			// if there is already a stop we need not add
			if(this.directionStops[floorNumber] != 2 
					&& this.directionStops[floorNumber] != 6) {
				this.directionStops[floorNumber] += 2;
			}
		}
		else if(direction == LiftDirection.DOWN) {
			if(this.directionStops[floorNumber] != 4 
					&& this.directionStops[floorNumber] != 6) {
				this.directionStops[floorNumber] += 4;
			}
		}
	}
	
	@Override
	public void run() {
		while(!exit) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(this.currentFloor == this.destinationFloor) {
				findJobs();
			}
			else {
				this.move();
			}
		}
	}
	
	/**
	 * looks for jobs when the elevator is not moving
	 */
	private void findJobs() {
		Boolean oneDownFound = false;
		for(Integer index = 0; index < stops.length; index++) {
			if(stops[index]) {
				if(directionStops[index] == 2 || directionStops[index] == 6) {
					this.destinationFloor = index;
					changeParameters(this.currentFloor, this.destinationFloor);
					addStops(this.destinationFloor, this.currentDirection);
					changeDirectionStops();
					this.move();
					break;
				}
				// when the current floor is the lowest floor and
				// if the first found stop is down(stop), there had to be another floor
				// higher than this one, which has a down(stop)
				else if(directionStops[index] == 4 || directionStops[index] == 6) {
					if(!oneDownFound) {
						oneDownFound = true;
						if(this.currentFloor == 0) {
							continue;
						}
					}
					this.destinationFloor = index;
					changeParameters(this.currentFloor, this.destinationFloor);
					addStops(this.destinationFloor, this.currentDirection);
					changeDirectionStops();
					this.move();
					break;
				}
				
				// this condition never happens
				else {
					stops[index] = false;
				}
			}
		}
	}
	
	/**
	 * changes the values of the array directionStops
	 * when this elevator passes through a particular floor
	 */
	private void changeDirectionStops() {
		if(this.currentDirection == LiftDirection.UP) {
			if(this.directionStops[this.currentFloor] == 2 
					|| this.directionStops[this.currentFloor] == 6) { 
					this.directionStops[this.currentFloor] -= 2;
			}
		}
		else {
			if(this.directionStops[this.currentFloor] == 4 
					|| this.directionStops[this.currentFloor] == 6) { 
					this.directionStops[this.currentFloor] -= 4;
			}
		}
	}
	
	/**
	 * moves this elevator between floors
	 */
	private void move() {
		while(true){

			// to show the time difference between floors
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(this.currentFloor < this.destinationFloor) {
				this.currentFloor++;
				if(stops[this.currentFloor]) {
					// find if there is any stop in up direction
					if(directionStops[this.currentFloor] == 2 
							|| directionStops[this.currentFloor] == 6) {
						directionStops[this.currentFloor] -= 2;
						// to show the elevator has stopped at this floor
						this.currentState = LiftState.STOPPED;
						try {
							Thread.sleep(2000L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(this.currentFloor != this.destinationFloor) {
							changeParameters(this.currentFloor, this.destinationFloor);
						}
						// find if all the stops for this particular floor is completed
						if(directionStops[this.currentFloor] <= 0) {
							stops[this.currentFloor] = false;
							directionStops[this.currentFloor] = 0;
						}
					}
				}
			}
			else {
				this.currentFloor--;
				if(stops[this.currentFloor]) {
					// find if there is any stop in down direction
					if(directionStops[this.currentFloor] == 4 
							|| directionStops[this.currentFloor] == 6) {
						directionStops[this.currentFloor] -= 4;
						// to show the elevator has stopped at this floor
						this.currentState = LiftState.STOPPED;
						try {
							Thread.sleep(2000L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(this.currentFloor != this.destinationFloor) {
							changeParameters(this.currentFloor, this.destinationFloor);
						}
						// find if all the stops for the particular floor is completed
						if(directionStops[this.currentFloor] <= 0) {
							stops[this.currentFloor] = false;
							directionStops[this.currentFloor] = 0;
						}
					}
				}
			}
			
			// after moving through floors,
			// find if this elevator reached the destination floor
			if(this.currentFloor == this.destinationFloor) {
				break;
			}
		}
		
		// after reaching the destination floor
		// find if there are more stops allocated
		findStops(this.currentDirection);
		
	}
	
	/**
	 * finds the floors to stop when moving in a particular direction
	 * @param direction
	 */
	private void findStops(LiftDirection direction) {
		
		// if this elevator reached the destination floor while moving up
		if(direction == LiftDirection.UP) {
			// first search for stops above the current floor
			for(Integer index = this.currentFloor + 1; index < stops.length; index++) {
				if(stops[index] && 
						(directionStops[index] == 2 || directionStops[index] == 6)) {
					changeParameters(this.currentFloor, index);
					changeDirectionStops();
					return;					
				}
			}
			// then search for stops below the current floor
			for(Integer index = this.currentFloor - 1; index >= 0; index--) {
				if(stops[index] && 
						(directionStops[index] == 4 || directionStops[index] == 6)) {
					changeParameters(this.currentFloor, index);
					changeDirectionStops();
					return;
				}
			}
		}
		
		// if the elevator reached the destination floor while moving down
		else if(direction == LiftDirection.DOWN) {
			// first search for stops below the current floor
			for(Integer index = this.currentFloor - 1; index >= 0; index--) {
				if(stops[index] && 
					(directionStops[index] == 4 || directionStops[index] == 6)) {
					changeParameters(this.currentFloor, index);
					changeDirectionStops();
					return;
				}
			}
			// then search for stops above the current floor
			for(Integer index = this.currentFloor + 1; index < stops.length; index++) {
				if(stops[index] &&
					(directionStops[index] == 2 || directionStops[index] == 6)) {
					changeParameters(this.currentFloor, index);
					changeDirectionStops();
					return;
				}
			}
		}
		
		// if no stop is found, update states as idle
		changeParameters(this.currentFloor, this.currentFloor);
	}
	
	/**
	 * stops this particular elevator thread
	 */
	protected void stop() {
		exit = true;
	}
	
	/**
	 * prints the current status of the elevator
	 * @return String
	 */
	protected String printStatus() {
		return  "id : " + this.id + 
				"\ncurrent Floor : " + (this.currentFloor - ElevatorController.getBasementFloors())+ 
				"\ncurrent State : " + this.currentState + 
				"\nDirection : " + this.currentDirection;
	}
	
}
