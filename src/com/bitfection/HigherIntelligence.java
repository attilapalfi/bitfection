package com.bitfection;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class HigherIntelligence {
	private static LinkedHashSet<Bot> allRobots;
	
	public static int DistanceBetween (Bot b1, Bot b2) {
		int deltaX = Math.abs(b2.getPosition().getX() - b1.getPosition().getX());
		int deltaY = Math.abs(b2.getPosition().getY() - b1.getPosition().getY());
		return (int) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	public static void init () {
		allRobots = new LinkedHashSet<Bot>(300);
	}
	
	public static void addToAllRobots (Bot b) {
		allRobots.add(b);
	}
	
	public static Bot findByID (int ID) throws RobotNotFoundException {
		for (Bot b : allRobots) {
			if (b.getMyID() == ID)
				return b;
		}
		
		throw new RobotNotFoundException("Can't find robot with ID: " + ID);
	}
	
	public static Bot findNearest (Bot bot) {
		Iterator<Bot> i = allRobots.iterator();
		// Assume that closest robot is the first in the set.
		Bot minDistRobot = i.next();
		
		if (minDistRobot == bot) {
			minDistRobot = i.next();
		}
		
		int minDistance = HigherIntelligence.DistanceBetween(bot, minDistRobot);
		
		
		// Bot minDistRobot = i.next();
		// int minDistance = HigherIntelligence.DistanceBetween(bot, minDistRobot);
		
		while (i.hasNext()) {
			Bot tempMinDistRobot = i.next();
			
			if (tempMinDistRobot != bot) {
				int tempMinDistance = HigherIntelligence.DistanceBetween(bot, minDistRobot); 
				
				if (tempMinDistance < minDistance) {
					minDistance = tempMinDistance;
					minDistRobot = tempMinDistRobot;
				}
			}
			
			else {
				i.next();
			}
		}
		
		// Add the closest robot to the set of closest robots.
		return minDistRobot;
	}
	
}
