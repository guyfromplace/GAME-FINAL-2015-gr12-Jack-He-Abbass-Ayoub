/**
 * Unit.java
 *
 * Unit class keeps track of all stats and variables pertaining
 * to units and applies all changes and updates to Units 
 * 
 * This class takes in several parameters and contains many functions, which 
 * mainly are called during the main game loop of the game.
 */
 
import java.util.*; 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;

public class Unit implements ActionListener{
	private int x, y, dir;
	private int health, rdmg, mdmg, mvspd, cooldown, exp, range; //ranged damage, melee damage, movespeed
	private float movframe, atkframe, meleeframe;				 //Which pictures to use in the list, from moving, attacking, and melee-ing
	private int spwncd, rof, acmod;								 //Spawn cooldown, rate of fire, accuracy modifier						
	
	private String unit_type;
	private String faction_type;
	
	//Assigned ints depending on what side the unit is on
	private static final int RIGHT = -1;
	private static final int LEFT = 1;
	
	//ArrayList of pictures for sprites
	private Image[] moveList;
	private Image[] atkList;
	private Image[] meleeList;	//Imperial Guard cannot melee, so is kept null
	
	private Image deadPic;
	private Image profPic;
	private Image curPic;
	
	//Booleans for unit actions
	private boolean moving=true;
	private boolean firing=false;
	private boolean meleeing=false;
	private boolean rapidfiring=false;
	 
	private boolean fired=false;
	private boolean meleed=false;
	 
	private boolean clear=false; //if unit has cleared the field
	private boolean dead=false;
	
	//Timers for cooldowns of shooting and melee
	private javax.swing.Timer fireCD;
	private javax.swing.Timer fireDur;
	private javax.swing.Timer meleeDur;
	
	private AudioClip attack;	//attack sounds
	private AudioClip punch;
	
	
	private Unit target;	//the closest enemy Unit within this Unit's lane
							//is the focus of the majority of this Unit's attacks and actions
	private Lane curLane;	//Current Lane that unit was spawned in
	
	public Unit(String faction_type, String unit_type, int dir, Image[] moveList, Image[] atkList, Image[] meleeList,
				 Image deadPic, Image profPic, int h, int rd, int md, int m, int r,int spwncd, int rof, int acmod,
				 AudioClip attack, AudioClip punch){
		this.dir = dir;		//Direction
		health=h;
		rdmg=rd;			//Ranged damage
		mdmg=md;			//Melee damage
		mvspd=m;
		range=r;
		this.spwncd=spwncd;
		this.rof=rof;		//Rate of fire
		this.acmod=acmod;

		this.faction_type = faction_type; 
		this.unit_type = unit_type;
		movframe=0;			//Start at first picture
		atkframe=0;
		target=null;		//are not assigned until they are spawned
		curLane=null;
		
		this.moveList = moveList;
		this.atkList = atkList;
		this.meleeList = meleeList;
		
		this.attack=attack;
		if(this.unit_type.equals("Chainsword")){   //Chainsword unit is melee only
			this.punch=attack;
		}
		else{
			this.punch=punch;
		}
		
		this.deadPic=deadPic;
		this.profPic=profPic; 
		curPic=moveList[(int)movframe];
	}
	
	public void addMovPic(int index,Image p){
		moveList[index] = p;
	}
	
	public String getName(){
		return unit_type;
	}
	
	//Getters and Setters
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getHealth(){
		return health;
	}
	public int getDir(){
		return dir;
	}
	public int getSpawnCD(){
		return spwncd;
	}
	public Unit getTarget(){
		return target;
	}
	public Image getDeadPic(){
		return deadPic;
	}
	public Image getProf(){
		return profPic;
	}
	public Image getCurPic(){
		return curPic;
	}
	public boolean getClear(){
		return clear;
	}
	public boolean getDead(){
		return dead;
	}
	public boolean getFiring(){
		return firing;
	}
	public boolean getRapidFiring(){
		return rapidfiring;
	}
	public Lane getLane(){
		return curLane;
	}
	
	public Unit copy(){
		Unit newunit= new Unit(faction_type,unit_type,dir,moveList,atkList,meleeList,deadPic,profPic,health,rdmg,mdmg,mvspd,range,spwncd,rof,acmod,attack,punch);
		return newunit;
	}
	
	public void setX(int x){
		this.x= x;
	}
	public void setY(int y){
		this.y= y;
	}
	public void setHealth(int h){
		health=h;
	}
	public void setDir(int dir){
		this.dir= dir;
	}
	public void setTarget(Unit target){
		this.target= target;
	}
	public void setLane(Lane l){
		curLane=l;
	}
	
	//Deal damage method - unit loses health depending on damage d inflicted
	public void dealDam(Unit u, int d){
		u.setHealth(u.getHealth()-d);
	}
	
	public void findTarget(){	//Searches the Lane that this unit is in for closest enemy, sets null if none are found
		setTarget(null);
		if(dir==LEFT && curLane.getUnitsR().size()>0){//selecting which ArrayList to search
			for(Unit u : curLane.getUnitsR()){
				if(target==null){			//if unit is not already assigned as a target
					setTarget(u);
				}
				if(u.getX()<target.getX()){	//if new unit is closer than old unit, reassign
					setTarget(u);
				}
			}
		}
		else if(dir==RIGHT && curLane.getUnitsL().size()>0){
			for(Unit u : curLane.getUnitsL()){
				if(target==null){
					setTarget(u);
				}
				if(u.getX()>target.getX()){
					setTarget(u);
				}
			}
		}
	}
	
	public int dist(Unit u1, Unit u2){	//finds distance between two Units
		int x=Math.abs((u1.getX()-u2.getX()));
		return x;
	}
	
	public void update(){
		if(health<=0){
			dead=true;//disable units from doing other actions
		}
		findTarget();
		if(firing){			//setting sprits for Units that are firing
			atkframe+=.2;
			if(unit_type.equals("Flamer") && atkframe>3){//flamer's fire must stay on at max
				atkframe = 4;
			}
			if(unit_type.equals("Rocket")||unit_type.equals("RPG")){
				atkframe = atkframe%4;
			}
			if(!unit_type.equals("Rocket")&&!unit_type.equals("Flamer")&&!unit_type.equals("RPG")){
				atkframe = atkframe%2;
			}
			curPic = atkList[(int)atkframe];
			if(unit_type.equals("Flamer")){	//flamer deals damage throughout out its duration
				if(dir==LEFT){//finding units in range of its fire;
					for(Unit u : curLane.getUnitsR()){
						if(dist(u,this)<150){
							dealDam(u,rdmg);//dealing the damage over time
						}
					}
				}
				else{
					for(Unit u : curLane.getUnitsL()){
						if(dist(u,this)<150){
							dealDam(u,rdmg);
						}
					}
				}
			}
		}
		//Setting sprites for Units that are meleeing
		if(meleeing){
			meleeframe+=.25;
			if(unit_type.equals("Chainsword")){
				meleeframe = meleeframe%4;
			}
			else{
				meleeframe = meleeframe%3;
			}
			curPic = meleeList[(int)meleeframe];
		}
		//Setting sprites for Units that are rapidfiring
		if(rapidfiring){
			atkframe+=.2;
			if(unit_type.equals("Rocket")||unit_type.equals("RPG")){
				atkframe = atkframe%4;
			}
			if(!unit_type.equals("RPG")){
				atkframe = atkframe%2;
			}
			curPic = atkList[(int)atkframe];
		}
		//choosing actions for unit this round of updates
		if(target!=null && !dead){	
			if(dist(target,this)<40 && !meleed){//unit is within cqc range of its target;
				if(!faction_type.equals("Imperial Guard")){
					melee();
				}
				else{
					rapidfire();
				}
			}
			//If they are not in range to nearest target, they move() until they are
			else if(dist(target,this)>range){
				move();
			}
			//If they're in range, they will fire, if there are not on cooldown
			else if(dist(target,this)<=range && !fired && !meleed){
				shoot();
			}
			//When they cannot fire, they move until they're ready to fire again
			else if(dist(target,this)<=range && !firing && !meleeing){
				move();
			}
		}
		//If way is clear, they will advance
		else if(!dead && !meleed && !fired){
			move();
		}
	}
	
	public void shoot(){
		Random rand = new Random();
		int hit = rand.nextInt(10);		//Units have a chance to miss their shots. Cant be 100% accurate
		if(hit<acmod){
			dealDam(target,rdmg);
		}
		curPic=atkList[(int)atkframe];
		fired=true;	//setting up cooldowns unitl Unit can shoot again
		firing=true;
		fireCD = new javax.swing.Timer(rof,this);	//timer that sets up the length of the fire cooldown
		fireCD.start();
		int dur = 1000;
		if(unit_type.equals("Flamer")){	//Rate of fire duration for the flame troop
			dur=3000;
		}
		fireDur = new javax.swing.Timer(dur,this);
		fireDur.start();
		atkframe=0;
		attack.play();	//play shooting sound
		
	}
	public void melee(){
		dealDam(target,mdmg);//melee cant miss
		meleed=true;
		meleeing=true;
		meleeDur = new javax.swing.Timer(1000,this);	//Duration of melee
		meleeDur.start();
		meleeframe=0;
		punch.play();			//Play the punching sound
	}
	//Imperial Guard can not melee, so within melee range they shoot twice as fast but with 1/2 the accuracy
	public void rapidfire(){
		Random rand = new Random();
		int hit = rand.nextInt(10);
		if(hit<(acmod/2)){
			dealDam(target,rdmg);
		}
		meleed=true;
		rapidfiring=true;
		meleeing=true;
		meleeDur = new javax.swing.Timer(rof/2,this);//still technically a "melee" action
		meleeDur.start();
		atkframe=0;
	}

	public void move(){
		x+= mvspd*dir;
		movframe+=0.25;
		if(unit_type.equals("Tank")){
			movframe=movframe%4;	
		}
		else{
			movframe=movframe%6;
		}
		curPic=moveList[(int)movframe];
		if(dir==LEFT && x>750){//checks if unit has made it off the battlefield
			clear=true;	//main game loop uses this to determing scoring points
		}
		if(dir==RIGHT && x<0){
			clear=true;
		}	
	}
	//Controls timers/cooldowns of different attack methods
	public void actionPerformed(ActionEvent e){
    	Object source = e.getSource();
    	//When cooldown is over, flags are reset;
    	if(source==fireCD){
    		fired=false;
    		fireCD.stop();
    	}
    	if(source==fireDur){
    		firing=false;
    		fireDur.stop();
    	}
    	if(source==meleeDur){
    		meleed=false;
    		meleeing=false;
    		rapidfiring=false;
    		meleeDur.stop();
    	}
	}
	  
}