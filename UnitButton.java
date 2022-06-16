/*
 * UnitButton.java
 *
 * Unit Button keeps track of unit cooldowns and spawn times
 * after the in-game unit button is pressed.
 *	It also is what is used when a unit must be spawned into the battle field
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
	private int ubx;
	private int uby;
	private Unit unit;
	
	private javax.swing.Timer spawnTimer;
	private boolean unitReady;
	
	public UnitButton(int x,int y, Unit unit){
		ubx=x;
		uby=y;
		this.unit=unit;
		unitReady = false;
		
		spawnTimer = new javax.swing.Timer(unit.getSpawnCD(), this);
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
		//allows unit to be spawnd again after cooldown is over
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