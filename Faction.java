/* 
 * Faction.java
 *
 * This class contains the attributes of the units in each faction.
 * This Includes: 
 				- Unit Type
 				- Their list of move pictures
 				- List of attacking pictures
 				- Dead image
 				- Profile Pic (for unit button icon)
 				- Move speed
 				- Range of attack
 				- Spawning cooldown
 				- Accuracy
 				- Sounds
  */
  
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.applet.*;

public class Faction{
	private String name;
	private String type;
	private int dir;
	private Unit[] units;
	
	public Faction(String name,String type,int dir){
		this.name = name;
		this.type = type;
		this.dir=dir;
		units = new Unit[6];
	}
	//Adding attributes to units in the factions (Found in text file)
	
	public void addUnit(int index, String unit_type, Image[] movList, Image[] atkList, Image[] meleeList,
						 Image deadPic, Image profPic, int h, int rdmg, int mdmg, int mvspd, int range,
						  int spwncd, int rof, int acmod, AudioClip attack, AudioClip punch){
		units[index]= new Unit(type,unit_type,dir,movList,atkList,meleeList,deadPic,profPic,h,rdmg,mdmg,
								mvspd,range,spwncd,rof,acmod,attack,punch);
		
	}
	
	public Unit getUnit(int index){
		return units[index];
	}
	
	public String getName(){
		return name;
	}
	public String getType(){
		return type;
	}
}