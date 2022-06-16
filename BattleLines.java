/*
 * BattleLines.java
 * Warhammer 40K : BattleLines
 *
 * By: Abbass Ayoub, Jack He
 * June 12,2015
 *
 * This is an RTS/lane-based game where a player can play against a friend or an AI 
 * in a battle of conquering great plains across the world of Warhammer. 
 *
 * Each player chooses a faction from 3 choices: Imperial Guard, Chaos Marines, and Loyal Marines.
 *
 * Each faction has different types of units with different stats- giving each Faction advantages
 * and disadvantages.
 *
 * The goal is to get as many of your units across the screen alive before time is up.
 * The player with the most life points at the end of the 5 minutes wins, or the player that gets
 * enough units across the field.
 *
 * **THIS IS WHERE THE GAME IS LAUNCHED**
 *
 * This class's biggest component is the loading in of the factions/units and their pictures.
 * It also contains getter and return methods called from the other files that accompany the game.
 * This class also holds the main timers that runs through the main game. 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;
import java.util.*;
import java.io.*;
import java.awt.Graphics2D.*;
import java.applet.*;


public class BattleLines extends JFrame implements ActionListener{
	javax.swing.Timer pTimer; 		//Player mode timer
	javax.swing.Timer aiTimer;  	//AI mode timer
	pGamePanel pgame;
	aiGamePanel aigame;
	MainMenu gmm;
	
	private static final int RIGHT = -1; //Direction depending on starting side
	private static final int LEFT = 1;
	
	private Faction[] factions;	//All the factions that can be played as
		
    public BattleLines() {
		super("BattleLines");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(750,630);
		gmm = new MainMenu(this);
		factions = new Faction[6];

		pTimer = new javax.swing.Timer(40, this);	 // trigger every 40 ms
		aiTimer = new javax.swing.Timer(40, this);
		
		//FILE I/O for generating the factions
		
		Scanner facfile = null;
		try{
			facfile = new Scanner(new File("GenFactions.txt"));
		}
		catch(IOException ex){
			System.out.println("Faction file not found.");
		}
		for(int i=0; i<3; i++){	//3 groups of 2 faction each, one left and one right
			String name = facfile.nextLine();
			factions[2*i] = new Faction(name+"L",name,LEFT);
			factions[2*i+1] = new Faction(name+"R",name,RIGHT);
			for(int x=0; x<6; x++){//adds the 6 units to each faction
				String[] line = facfile.nextLine().split(",");
				String uname = line[0];
				//Loading in all Sprites
				Image[] movelistL = new Image[6];
				Image[] movelistR = new Image[6];
				if(uname.equals("Tank")){	//different units have different amounts of move and fire sprites
					movelistL = new Image[4];
					movelistR = new Image[4];
					for(int b=1; b<5; b++){
						Image imL = new ImageIcon("Sprites/"+name+"/"+uname+"WalkL/"+String.valueOf(b)+".png").getImage();
						Image imR = new ImageIcon("Sprites/"+name+"/"+uname+"WalkR/"+String.valueOf(b)+".png").getImage();
						movelistL[b-1]= imL;
						movelistR[b-1]= imR;
					}
				}
				else{
					for(int a=1; a<7; a++){ // since Image files aren't named starting from 0
						Image imL = new ImageIcon("Sprites/"+name+"/"+uname+"WalkL/"+String.valueOf(a)+".png").getImage();
						Image imR = new ImageIcon("Sprites/"+name+"/"+uname+"WalkR/"+String.valueOf(a)+".png").getImage();
						movelistL[a-1]= imL;
						movelistR[a-1]= imR;	
					}
				}
				Image[] atklistL = new Image[2];
				Image[] atklistR = new Image[2];
				if(uname.equals("Flamer")){
					atklistL = new Image[5];
					atklistR = new Image[5];
					for(int c=1; c<6 ; c++){
						Image imL = new ImageIcon("Sprites/"+name+"/"+uname+"WalkL/fire"+String.valueOf(c)+".png").getImage();
						Image imR = new ImageIcon("Sprites/"+name+"/"+uname+"WalkR/fire"+String.valueOf(c)+".png").getImage();
						atklistL[c-1]= imL;
						atklistR[c-1]= imR;
					}
				}
				else if(uname.equals("Rocket")||uname.equals("RPG")){
					atklistL = new Image[4];
					atklistR = new Image[4];
					for(int c=1; c<5 ; c++){
						Image imL = new ImageIcon("Sprites/"+name+"/"+uname+"WalkL/fire"+String.valueOf(c)+".png").getImage();
						Image imR = new ImageIcon("Sprites/"+name+"/"+uname+"WalkR/fire"+String.valueOf(c)+".png").getImage();
						atklistL[c-1]= imL;
						atklistR[c-1]= imR;
					}
				}
				else{
					for(int d=1; d<3 ; d++){
						Image imL = new ImageIcon("Sprites/"+name+"/"+uname+"WalkL/fire"+String.valueOf(d)+".png").getImage();
						Image imR = new ImageIcon("Sprites/"+name+"/"+uname+"WalkR/fire"+String.valueOf(d)+".png").getImage();
						atklistL[d-1]= imL;
						atklistR[d-1]= imR;
					}
				}
				Image[] meleelistL = new Image[3];
				Image[] meleelistR = new Image[3];
				if(uname.equals("Chainsword")){
					meleelistL = new Image[4];
					meleelistR = new Image[4];
					for(int e=0; e<4 ; e++){
						Image imL = new ImageIcon("Sprites/"+name+"/"+uname+"MeleeL/"+String.valueOf(e)+".png").getImage();
						Image imR = new ImageIcon("Sprites/"+name+"/"+uname+"MeleeR/"+String.valueOf(e)+".png").getImage();
						meleelistL[e]= imL;
						meleelistR[e]= imR;
					}
				}
				else{
					for(int f=1; f<3 ; f++){
						Image imL = new ImageIcon("Sprites/"+name+"/MeleeL/"+String.valueOf(f)+".png").getImage();
						Image imR = new ImageIcon("Sprites/"+name+"/MeleeR/"+String.valueOf(f)+".png").getImage();
						meleelistL[f]= imL;
						meleelistR[f]= imR;
					}
				}
				Image deadPicL =  new ImageIcon("Sprites/"+name+"/"+uname+"WalkL/dead.png").getImage();
				Image deadPicR =  new ImageIcon("Sprites/"+name+"/"+uname+"WalkR/dead.png").getImage();
				Image profPicL =  new ImageIcon("Sprites/"+name+"/"+uname+"WalkL/0.png").getImage();
				Image profPicR =  new ImageIcon("Sprites/"+name+"/"+uname+"WalkR/0.png").getImage();
				int h = Integer.parseInt(line[1]);	//stats for units
				int rdmg = Integer.parseInt(line[2]);
				int mdmg = Integer.parseInt(line[3]); 
				int mvspd = Integer.parseInt(line[4]); 
				int range = Integer.parseInt(line[5]);
				int spawncd = Integer.parseInt(line[6]);
				int firecd = Integer.parseInt(line[7]);
				int acmod = Integer.parseInt(line[8]);
				AudioClip attack = Applet.newAudioClip(getClass().getResource("Sounds/"+uname+".wav"));
				AudioClip punch = Applet.newAudioClip(getClass().getResource("Sounds/Melee.wav"));
				 
				factions[2*i].addUnit(x,uname,movelistL,atklistL,meleelistL,deadPicL,profPicL,h,rdmg,mdmg,mvspd,range,spawncd,firecd,acmod,attack,punch);
				factions[2*i+1].addUnit(x,uname,movelistR,atklistR,meleelistR,deadPicR,profPicR,h,rdmg,mdmg,mvspd,range,spawncd,firecd,acmod,attack,punch);
			}
		}
		System.out.println("Loading Done");

		setResizable(false);
    }
    
	
	public void pstart(){
		pTimer.start();
	}
	public void pstop(){
		pTimer.stop();
	}
	public void aistart(){
		aiTimer.start();
	}
	public void aistop(){
		aiTimer.stop();
	}
	
	public Faction getFaction(int index){
		return factions[index];
	}
	
	//Where all actions of the game are updated (every 40 ms)
	public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();
		if(source==pTimer){
			pgame.timerUpdate();
			pgame.keyUpdate();
			pgame.unitUpdate();
			pgame.move();
			pgame.repaint();
		}
		if(source==aiTimer){
			aigame.timerUpdate();
			aigame.keyUpdate();
			aigame.unitUpdate();
			aigame.move();
			aigame.aiUpdate();
			aigame.repaint();
		}
	}
	
	//pmode is the Player vs Player mode of the game
	public void pmode(Faction left, Faction right){
		pgame = new pGamePanel(this,left,right);
		add(pgame);
		gmm.mute();
		gmm.setVisible(false);
	}
	
	//aimode is the player vs ai mode of te game
	public void aimode(Faction left, Faction right){
		aigame = new aiGamePanel(this,left,right);
		add(aigame);
		gmm.mute();
		gmm.setVisible(false);
	}
	
    public static void main(String[] arguments) {
		BattleLines frame = new BattleLines();		
    }
}
