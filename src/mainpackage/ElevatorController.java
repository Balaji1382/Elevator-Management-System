package mainpackage;

import java.util.ArrayList;
import java.util.List;

public class ElevatorController {
	private static Integer totalLifts;
	private static Integer totalFloors;
	private static Integer basementFloors;
	
	List<Elevator> elevatorList;
	
	private static ElevatorController elevatorController = null;
	
	private ElevatorController() {
		elevatorList = createLifts(totalLifts);
	}
	
	public static void setTotalFloors(Integer totalFloors){	
		ElevatorController.totalFloors = totalFloors;
	}
	
	public static void setTotalLifts(Integer totalLifts) {
		ElevatorController.totalLifts = totalLifts;
	}
	
	public static void setBasementFloors(Integer basementFloors) {
		ElevatorController.basementFloors = basementFloors;
	}
	
	public static Integer getTotalFloors() {
		return totalFloors;
	}
	
	public static Integer getBasementFloors() {
		return basementFloors;
	}
	
	/**
	 * returns the singleton instance of the class
	 * @return ElevatorController
	 */
	public static ElevatorController getElevatorController() {
		if (elevatorController == null){  
			elevatorController = new ElevatorController();//instance will be created at request time                
		}
		return elevatorController;
	}
	
	/**
	 * creates lift threads for the system
	 * @param totalLifts
	 * @return List<Elevator>
	 */
	private List<Elevator> createLifts(Integer totalLifts){
		List<Elevator> newElevatorList = new ArrayList<>();
		for(Integer i = 0; i < totalLifts; i++) {
			Elevator newElevator = new Elevator(i+1);
			Thread newThread = new Thread(newElevator);
			newThread.start();
			newElevatorList.add(newElevator);
		}
		return newElevatorList;
	}
	
	/**
	 * finds the destination direction of the elevator
	 * @param sourceFloor
	 * @param destinationFloor
	 * @return LiftDirection
	 */
	protected LiftDirection findDestinationDirection(
			Integer sourceFloor, Integer destinationFloor) {
		if(sourceFloor > destinationFloor) {
			return LiftDirection.DOWN;
		}
		return LiftDirection.UP;
	}
	
	/**
	 * finds the most optimum elevator near a floor
	 * @param sourceFloor
	 * @param destinationFloor
	 * @return Elevator
	 */
	protected Elevator findElevator(Integer sourceFloor, Integer destinationFloor) {
		Elevator elevator = null;
		LiftDirection destinationDirection = 
				findDestinationDirection(sourceFloor, destinationFloor);
		for(Elevator elv : elevatorList) {
			// if any elevator is in the source Floor and is idle
			if(elv.getCurrentFloor() == sourceFloor 
					&& (elv.getCurrentState() == LiftState.IDLE)) {
				elevator = elv;
				elevator.addStops(destinationFloor, destinationDirection);
				return elevator;
			}
		}
		
		// to find the minimum floors between the elevator's current direction
		// and source floor
		Integer minFloors = totalFloors;
		
		if(destinationDirection.equals(LiftDirection.UP)) {
			for(Elevator elv: elevatorList) {
				// we only search for moving and stopped elevators here
				if(elv.getCurrentState() != LiftState.MOVING 
						&& elv.getCurrentState() != LiftState.STOPPED) {
					continue;
				}
				
				// find if any elevators are moving up
				if(elv.getCurrentDirection() == destinationDirection) {
					if(elv.getCurrentFloor() < sourceFloor) {
						// find the number of floors between this elevator's 
						// current direction and source Floor
						Integer floors = (sourceFloor - elv.getCurrentFloor());
						if(floors < minFloors) {
							elevator = elv;
							minFloors = floors;
						}
					}
				}
				// when the destination direction is up
				// but the elevator is moving down
				else {
					if(elv.getCurrentFloor() < sourceFloor) {
						// (2*elv.getCurrentFloor()) is because we assume the elevator
						// moves to the lowest floor and comes back
						Integer floors = (2*elv.getCurrentFloor()) + (sourceFloor - elv.getCurrentFloor());
						if(floors < minFloors) {
							elevator = elv;
							minFloors = floors;
						}
					}
					
					// when elevator's current Floor > source Floor
					// while moving down
					else {
						Integer floors = (sourceFloor + elv.getCurrentFloor());
						if(floors < minFloors) {
							elevator = elv;
							minFloors = floors;
						}
					}
				}
			}
		}
 
		else{
			for(Elevator elv: elevatorList) {
				// we only search moving and stopped elevators here
				if(elv.getCurrentState() != LiftState.MOVING 
						&& elv.getCurrentState() != LiftState.STOPPED) {
					continue;
				}
				
				// find if any elevators are moving down
				if(elv.getCurrentDirection() == destinationDirection) {
					if(elv.getCurrentFloor() > sourceFloor) {
						// find the number of floors between this elevator's 
						// current direction and source Floor
						Integer floors = (elv.getCurrentFloor() - sourceFloor);
						if(floors < minFloors) {
							elevator = elv;
							minFloors = floors;
						}
					}
				}
				
				// when the direction is down
				// but the elevator is moving up
				else {
					// when the elevator's current floor > source floor
					// while moving up
					if(elv.getCurrentFloor() > sourceFloor) {
						// (2*elv.getCurrentFloor()) because we assume that elevator is moves
						// all the way to the highest floor and then comes back
						Integer floors = (2*elv.getCurrentFloor()) + (elv.getCurrentFloor() - sourceFloor);
						if(floors < minFloors) {
							elevator = elv;
							minFloors = floors;
						}
					}
					else {
						Integer floors = ((totalFloors - sourceFloor) +
								(totalFloors - elv.getCurrentFloor()));
						if(floors < minFloors) {
							elevator = elv;
							minFloors = floors;
						}
					}
				}
			}
		}
		
		// if elevator is found, update its stops and directionStops
		if(elevator != null) {
			elevator.addStops(sourceFloor, destinationDirection);
			elevator.addStops(destinationFloor, destinationDirection);
			return elevator;
		}
		
		// if elevator is still not found
		// search through idle elevators near the source floor
		for(Elevator elv: elevatorList) {
			if(elv.getCurrentState() == LiftState.IDLE) {
				Integer floors = Math.abs(elv.getCurrentFloor() - sourceFloor);
				if(floors < minFloors) {
					elevator = elv;
					minFloors = floors;
				}
			}
		}
		
		// if elevator is found, update its stops and directionStops
		if(elevator != null) {
			elevator.addStops(sourceFloor, destinationDirection);
			elevator.addStops(destinationFloor, destinationDirection);
		}
		return elevator;
	}
	
	/**
	 * stops the whole system controller system
	 * @return Boolean
	 */
	protected Boolean stop() {
		Boolean flag = true;
		for(Elevator elv : elevatorList) {
			// checks if any elevator is moving
			if(elv.getCurrentState() == LiftState.MOVING) {
				System.out.println("Elevator " + elv.getId() + " is working");
				flag = false;
			}
		}
		// when flag is true no elevators are moving
		if(flag) {
			for(Elevator elv : elevatorList) {
				elv.stop();
			}
		}
		return flag;
	}
	
	/**
	 * prints the status of the passed elevator id
	 * @param id
	 * @return String
	 */
	protected String print(Integer id) {
		for(Elevator elv : elevatorList) {
			if(elv.getId() == id) {
				return elv.printStatus();
			}
		}
		return "-1";
	}
	
}
