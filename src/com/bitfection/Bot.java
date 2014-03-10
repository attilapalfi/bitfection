package com.bitfection;


import java.util.ArrayList;
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


public class Bot implements IBot, Comparable<Bot> {
	private GamePosition myPosition = new GamePosition();
	private GamePosition myTask = null;
	private Bot myFriend;
	
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
		System.out.println("Width: " + iAreaWidth + ", Height: " + iAreaHeight);
		HigherIntelligence.addToAllRobots(this);
	}
	
	@Override
	public GameEvent onEvent (GameEvent gameEvent, int iCount) {
		
		if (myID == 0) {
			try {
				myFriend = HigherIntelligence.findByID(1);
			} catch (RobotNotFoundException R) {
				myFriend = HigherIntelligence.findNearest(this);
			}
		}
		
		
		
		
		
		// If the robot dies
		if (gameEvent != null && gameEvent == GameEvent.DEAD) {
			if (myTask == null)
				cloud_islandNeighbors.add(myTask);
			return GameEvent.MARK;
		}
		
		else if (myTask == null) {
			myTask = cloud_islandNeighbors.poll();
			
			if ((int) (Math.random() * 20) == 0)
				return GameEvent.MARK;
			else
				return GameEvent.MOVE(directions[(int) (Math.random() * 8)]);
			
		}
				
		else {
			if (myPosition.getX() != myTask.getX() || myPosition.getY() != myTask.getY())
				return GameEvent.MOVE(myTask.getX() - myPosition.getX(), myTask.getY() - myPosition.getY());
			else
				return GameEvent.MARK;
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
		HigherIntelligence.init();
	}
	
	
	public GamePosition getPosition () {
		return myPosition;
	}
	
	public int getMyID () { return myID; }
	
	
	public void setTask (GamePosition GP) {
		myTask = GP;
	}
	

	@Override
	public int compareTo(Bot b0) {
		return this.getMyID() - b0.getMyID();
	}
}

































