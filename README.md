# Elevator Management System
  This project is used to find the most optimum elevator (when multiple elevators are present in the same place) for people while moving through floors in a multi storey building. There are 3 classes present: MainClass, ElevatorController & Elevator. There are 2 enumerations present: LiftDirection & LiftState. The class MainClass gets the inputs -> total Floors, basement Floors, total Lifts (these 3 inputs are asked only only time) & choice. According to the choice, we can find an elevator (when choice is 1), find the status of a particular elevator (when choice is 2), & to stop the whole system (when choice is 3). The choice will be got endlessly until the whole system is stopped. The ElevatorController class is responsible for allocating an optimum elevator, it assigns jobs for elevators etc., The Elevator class contains some basic data memebers and member functions.

  Basically the flow looks something like this:
    MainClass -> ElevatorController -> Elevator (i.e) the user enters the source Floor and destination Floor (when he/she wants to find an elevator), the request is then passed to the ElevatorController. The ElevatorController assigns job to a particular elevator and that particular elevator starts moving towards the source Floor.
    
  This project uses mainly focuses on the concept of multi-threading.

## Problem statement
When in a building with multiple floors with multiple elevators, if someone wants to go from one floor to another floor, the central system should allocate the most optimum elevator. 

## Run 
In the command prompt,
cd "Elevator Management System\bin\mainpackage"
java MainClass

