package com.bitfection;

public class HigherIntelligence {
	public static int DistanceBetween (Bot b1, Bot b2) {
		int deltaX = Math.abs(b2.getPosition().getX() - b1.getPosition().getX());
		int deltaY = Math.abs(b2.getPosition().getY() - b1.getPosition().getY());
		return (int) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	
}
