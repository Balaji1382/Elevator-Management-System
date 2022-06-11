package mainpackage;

import java.util.Scanner;

public class MainClass{
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Enter the number of Floors");
		Integer totalFloors = scanner.nextInt();
		ElevatorController.setTotalFloors(totalFloors);
		System.out.println("Enter the number of Lifts");
		Integer totalLifts = scanner.nextInt();
		ElevatorController.setTotalLifts(totalLifts);
		System.out.println("Enter the number of basement floors(included in total Floors)");
		Integer basementFloors = scanner.nextInt();
		ElevatorController.setBasementFloors(basementFloors);
		
		
		// getting the singleton instance of the class
		ElevatorController newController = ElevatorController.getElevatorController();
		
		while(true) {
			//Scanner scanner = new Scanner(System.in);
			Integer choice;
			System.out.println("Enter the choice :\n1. Find an elevator\n2. Print Status of an Elevator "
					+ "\n3. Stop the System");
			choice = scanner.nextInt();
			if(choice == 1) {
				System.out.println("Enter the source Floor");
				Integer sourceFloor = scanner.nextInt();
				if(sourceFloor < (0 - basementFloors) ||
						sourceFloor > (totalFloors - basementFloors - 1)) {
					System.out.println("The Total Floors available are " + 
						(0 - basementFloors) + " to " + (totalFloors - basementFloors - 1));
					continue;
				}
				System.out.println("Enter the destination Floor");
				Integer destinationFloor = scanner.nextInt();
				if(destinationFloor < (0 - basementFloors) ||
						destinationFloor > (totalFloors - basementFloors - 1)) {
					System.out.println("The Total Floors available are " + 
							(0 - basementFloors) + " to " + (totalFloors - basementFloors - 1));
					continue;
				}
				if(sourceFloor == destinationFloor) {
					System.out.println("Source and Destination Floor cannot be the same");
					continue;
				}
				Elevator foundElevator = newController.
						findElevator(sourceFloor + basementFloors, destinationFloor + basementFloors);
				if(foundElevator == null) {
					System.out.println("Cannot find any optimum elevator. Wait for some time");
				}
				else {
					System.out.println("The elevator allocated is " + foundElevator.getId());
				}
			}
			else if(choice == 2) {
				System.out.println("Enter the Elevator's id");
				Integer id = scanner.nextInt();
				String out = newController.print(id);
				if(out.equals("-1")) {
					System.out.println("Enter the correct id");
				}
				else {
					System.out.println(out);
				}
			}
			else if(choice == 3){
				Boolean flag = newController.stop();
				if(flag) {
					System.out.println("System stopped");
					break;
				}
				else {
					System.out.println("Cannot stop the system");
				}
			}
			else{
				System.out.println("Enter a correct choice");
			}
		}
	}
}
