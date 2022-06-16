/*
 * UnitButton.java
 *
 * Unit Button keeps track of unit cooldowns and spawn times
 * after the in-game unit button is pressed.
 *
 * We created a spawnTimer accompanied with a 
 * boolean which would set it off after the unit was ready
 * to spawn
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;
import java.util.*;
import java.io.*;
import java.awt.Graphics2D.*;
import java.applet.*;

public class UnitButton implements ActionListener{
	private int ubx;   //Unit button x/y
	private int uby;
	private Unit unit; //Calls Unit class
	
	private javax.swing.Timer spawnTimer;
	private boolean unitReady;
	
	public UnitButton(int x,int y, Unit unit){
		ubx=x;
		uby=y;
		this.unit=unit;
		unitReady = false;
		
		spawnTimer = new javax.swing.Timer(unit.getSpawnCD(), this); //Timer is based on set cooldowns of each unit
		spawnTimer.start();
	}
	
	public int getX(){
		return ubx;
	}
	
	public int getY(){
		return uby;
	}
	
	public boolean getReady(){
		return unitReady;
	}
	
	public Unit getUnit(){
		Unit newunit = unit.copy(); 
		return newunit;
	}
	
	public void startTimer(){
		if (unitReady == true){
			spawnTimer.start();
		}
	}
	
	public void actionPerformed(ActionEvent e){
    	Object source = e.getSource();
		if(source==spawnTimer){
    		unitReady = true;
    		spawnTimer.stop();
		}
    }
    public void unitSpawned(){
    	unitReady = false;
    	spawnTimer = new javax.swing.Timer(unit.getSpawnCD(), this); 
    	spawnTimer.start();
    }
}