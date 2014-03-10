package com.bitfection;


import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import com.bitfection.game.GameChallenge;
import com.bitfection.game.GameDirection;
import com.bitfection.game.GameEvent;
import com.bitfection.game.GamePosition;
import com.bitfection.game.GameScanDirectionResult;
import com.bitfection.game.GameScanDistanceResult;
import com.bitfection.game.IBot;


// commit test LOL
public class Bot implements IBot, Comparable<Bot> {
	private GamePosition myPosition = new GamePosition();
	private GamePosition myTask = null;
	private boolean firstTurn = true;
	private int RobotCount;
	private LinkedHashSet<Bot> MySlaves;
	private LinkedHashSet<Bot> arrivedSlaves;
	private Bot myMaster;
	
	private static LinkedHashSet<Bot> SlaveRobots;
	private static LinkedHashSet<Bot> ClosestRobots;
	private static LinkedHashSet<Bot> MasterRobots;
	private static Queue<GamePosition> cloud_islandNeighbors;
	private static int ID = 0;
	private int myID = ID++;
	
	private static final GameDirection[] directions = { 
		GameDirection.EAST,
		GameDirection.NORTH, GameDirection.NORTH_EAST,
		GameDirection.NORTH_WEST, GameDirection.SOUTH,
		GameDirection.SOUTH_EAST, GameDirection.SOUTH_WEST,
		GameDirection.WEST
	};
	

	@Override
	public void onInit(GameChallenge gameChallenge, int iRobotCount, UUID iId, int iX, int iY, int iAreaWidth, int iAreaHeight) {
		myPosition.setPosition(iX, iY);
		RobotCount = iRobotCount;			
		MySlaves = new LinkedHashSet<Bot>();
		arrivedSlaves = new LinkedHashSet<Bot>();
		
		if (myID % 10 == 0) {
			MasterRobots.add(this);	
			System.out.println(myID + " I am Master!");
		}
		
		else {
			SlaveRobots.add(this);
			ClosestRobots.add(this);
			System.out.println(myID + " I am Slave.");
		}		
	}
	
	@Override
	public GameEvent onEvent(GameEvent gameEvent, int iCount) {
		
		// First turn is different
		if (firstTurn == true) {
			firstTurn = false;
			if (myID % 10 == 0)
				findNineNearestBuddies();
			
			return GameEvent.IDLE;
		}
				
		
		// If the robot dies
		if (gameEvent != null && gameEvent == GameEvent.DEAD) {
			if (myTask == null)
				cloud_islandNeighbors.add(myTask);
			return GameEvent.MARK;
		}
		
		
		
		// Slave Robots go to their tasks
		if (myID % 10 != 0) {
			// If he has task
			if(myTask != null) {
				// But hasn't arrived, moves there
				if (myPosition.getX() != myTask.getX() || myPosition.getY() != myTask.getY())
					return GameEvent.MOVE(myTask.getX() - myPosition.getX(), myTask.getY() - myPosition.getY());
				// If he arrived, does nothing
				else {
					myMaster.slaveArrived(this);
					myTask = null;
					return GameEvent.IDLE;
				}
			}
			// if he doesn't have task, does nothing
			else
				return GameEvent.IDLE;
		}
		
		// Master Robots are niggers
		else {
			
			// If all his slaves have arrived
			if (allSlaveArrived()) {
				
				// If the Master Robot doesnt have a task, asks for one
				myTask = cloud_islandNeighbors.poll();
				
				// If he doesn't get one, goes to some random position, or marks.
				if (myTask == null) {				
					if ((int) (Math.random() * 20) == 0)
						return GameEvent.MARK;
					else
						return GameEvent.MOVE(directions[(int) (Math.random() * 8)]);
				}
				
				// If he gets one starts going there
				else {
					if (myPosition.getX() != myTask.getX() || myPosition.getY() != myTask.getY())
						return GameEvent.MOVE(myTask.getX() - myPosition.getX(), myTask.getY() - myPosition.getY());
					else
						return GameEvent.MARK;
				}
			}
			
			return GameEvent.SCAN_DIRECTION;
		}
		
	}
	
	@Override
	public void onEnergyChanged(int iEnergy) {
		
	}

	@Override
	public boolean onJoin() {
		return true;
	}

	@Override
	public void onMark(UUID uuIrregularityFoundId) {
		GamePosition gp;
		if (uuIrregularityFoundId != null)
			for (GameDirection dir : directions) {
				gp = new GamePosition(myPosition.getX() + dir.getDx(), myPosition.getY() + dir.getDy());
				cloud_islandNeighbors.add(gp);
			}
		
		myTask = null;
	}

	@Override
	public void onMove(GameDirection direction, int iX, int iY) {
		myPosition.setPosition(iX, iY);// Update bot's coordinate.
	}

	@Override
	public void onScanDirection(GameScanDirectionResult[] directions, int radius) {
	}

	@Override
	public void onScanDistance(GameScanDistanceResult[] distances, int radius) {
	}

	@Override
	public void onSetupStaticEnvironment() {
		System.out.println("onSetupStaticEnvironment");
		cloud_islandNeighbors = new LinkedList<GamePosition>();
		SlaveRobots = new LinkedHashSet<Bot>();
		ClosestRobots = new LinkedHashSet<Bot>();
		MasterRobots = new LinkedHashSet<Bot>();
	}
	
	
	
	
	// Master Robots call this method
	private void findNineNearestBuddies () {		
		// List for the closest robots.
		LinkedHashSet<Bot> MySlaves = new LinkedHashSet<Bot>();
		
		
		for (int j = 0; j < 9; j++) {
			Iterator<Bot> i = ClosestRobots.iterator();
			// Assume that closest robot is the first in the set.
			Bot minDistRobot = i.next();
			// int minDistance = minDistRobot.distance(myPosition.getX(), myPosition.getY());
			int minDistance = HigherIntelligence.DistanceBetween(this, minDistRobot);
			
			while (i.hasNext()) {
				Bot tempMinDistRobot = i.next();
				// int tempMinDistance = tempMinDistRobot.distance(myPosition.getX(), myPosition.getY());
				int tempMinDistance = HigherIntelligence.DistanceBetween(this, minDistRobot); 
				
				if (tempMinDistance < minDistance) {
					minDistance = tempMinDistance;
					minDistRobot = tempMinDistRobot;
				}
			}
			
			// Add the closest robot to the set of closest robots.
			MySlaves.add(minDistRobot);
			// Remove this robot from the set of all robots.
			ClosestRobots.remove(minDistRobot);
		}
		
		// Call the slaves to the master
		Iterator<Bot> i = MySlaves.iterator();
		while (i.hasNext()) {
			Bot tempBot = i.next();
			tempBot.setMyMaster(this);
			tempBot.setTask(myPosition);
		}
		
		
		Iterator<Bot> j = MySlaves.iterator();
		System.out.print("I am " + myID +". My slaves are: ");
		while (j.hasNext()) {
			System.out.print(j.next().getMyID() + " ");
		}
		System.out.println("");
			
	}
	
	
	
	public GamePosition getPosition () {
		return myPosition;
	}
	
	public int getMyID () { return myID; }
	
	public int distance (int x, int y) {
		int deltaX = Math.abs(x - myPosition.getX());
		int deltaY = Math.abs(y - myPosition.getY());
		return (int) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	public void setTask (GamePosition GP) {
		myTask = GP;
	}

	public Bot getMyMaster() {
		return myMaster;
	}

	public void setMyMaster(Bot myMaster) {
		this.myMaster = myMaster;
	}

	public LinkedHashSet<Bot> getMySlaves() {
		return MySlaves;
	}

	public void setMySlaves(LinkedHashSet<Bot> mySlaves) {
		MySlaves = mySlaves;
	}
	
	public void slaveArrived (Bot b) {
		arrivedSlaves.add(b);
	}
	
	public boolean allSlaveArrived () {
		System.out.println(MySlaves.equals(arrivedSlaves));
		return MySlaves.equals(arrivedSlaves);
	}
	

	@Override
	public int compareTo(Bot b0) {
		return this.getMyID() - b0.getMyID();
	}
}

































