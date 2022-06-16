/*
 * Lane.java
 * The Lane class holds the units inside each lane. It allows then to be updated
 * and also contains information that the AI for the game will use.
 * The main game by default has  lanes in it's battlefield.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;
import java.util.*;
import java.io.*;
import java.awt.Graphics2D.*;
import java.applet.*;

public class Lane{
	//Direction indicators
	private static final int RIGHT = -1;
	private static final int LEFT = 1;
	private int LaneY;
	private ArrayList<Unit> leftUnits;	//The units are sorted according to which side they are on
	private ArrayList<Unit> rightUnits;
	private int advan;	//An indicator of who has the advantage in this lane;
	
	public Lane(int LaneY){
		this.LaneY=LaneY;
		leftUnits = new ArrayList<Unit>();
		rightUnits = new ArrayList<Unit>();
		advan=0;
	}
	
	public int getLaneY(){
		return LaneY;
	}
	
	public int getAdvan(){
		return advan;
	}
	
	public ArrayList<Unit> getUnitsL(){
		return leftUnits;
	}
	
	public ArrayList<Unit> getUnitsR(){
		return rightUnits;
	}
	
	public void addU(Unit u){	//adding a unit to this lane
		if(u.getDir()==RIGHT){	//determining which side to add the unit to
			u.setX(720);	//setting the correct starting location of the unit
			u.setY(LaneY);
			rightUnits.add(u);
		}
		else{
			u.setX(0);
			u.setY(LaneY);
			leftUnits.add(u);
		}
	}
	
	public void removeU(Unit u){	//Removing a unit from this Lane and the associated ArrayLists
		if(u.getDir()==RIGHT){
			rightUnits.remove(u);
		}
		else{
			leftUnits.remove(u);
		}
	}
	
	public void update(ArrayList<Unit> ucleared, ArrayList<Unit> newDead){	//udadteing the lane and every unit inside of it
		advan = rightUnits.size()-leftUnits.size();	//advan tracks the advantage of the leftplayer in the Lane
		for(Unit u: leftUnits){						//a lower number means the right player is outnumbered
			u.update();								//ai uses this to select which lane to deploy to
			
			if(u.getClear()){	//if unit has crossed the battlefield to score
				ucleared.add(u);
			}
			if(u.getDead()){	//if unit has died
				newDead.add(u);
			}
		}
		for(Unit u: rightUnits){
			u.update();
			if(u.getClear()){
				ucleared.add(u);
			}
			if(u.getDead()){
				newDead.add(u);
			}
		}
	}
	
}