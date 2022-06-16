/*
 * aiGamePanel.java
 * This is the main game panel for a single player game. It manages the input of players and the ai
 * and draws what is going on during the game. It does all the updating and spawning of units during the game.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;
import java.util.*;
import java.io.*;
import java.awt.Graphics2D.*;
import java.applet.*;

public class aiGamePanel extends JPanel implements KeyListener, ActionListener{
	private static final int RIGHT = -1;
	private static final int LEFT = 1;
	
	private static final int IG = 2; //variables to determine what faction the ai will play as
	private static final int LM = 3;
	private static final int CM = 4;
	
	private int mode;//what faction ai will be
	
	private int rectx,recty;
	private boolean[] keys; //keyboard input
	private Lane[] lanes;	//the main Lanes
	
	private BattleLines mainFrame;
	
	//booleans for determing if a button has been pressed
	private boolean checkupR=false;
	private boolean checkdownR=false;
	private boolean checkupL=false;
	private boolean checkdownL=false;
	private boolean checkspace=false;
	private boolean checkenter=false;
	
	private Lane selectR;
	private Lane selectL;
	
	//indices for managing which lane is selected
	private int curLaneR;
	private int curLaneL;
	
	private UnitButton[] unitsL;
	private UnitButton[] unitsR;
	
	private boolean checkleftR=false;	
	private boolean checkrightR=false;
	private boolean checkleftL=false;
	private boolean checkrightL=false;
	
	private UnitButton bselectR;
	private UnitButton bselectL;
	
	//indices for managing which UnitButton is selected
	private int curButtonR;
	private int curButtonL;
	
	//lives for each team
	private int lifeL,lifeR;
	private int chargeL,chargeR;
	
	private boolean checkq=false;
	private boolean checkshift=false;
	private boolean Lcharge=false;
	private boolean Rcharge=false;
	
	private int[] stchoice;	//first choice
	private int[] ndchoice;	//second choice
	private int[] rdchoice; //third choice
	
	private JPanel exitPanel; //Panel for winScreen and exiting
	
	private ArrayList<Unit> deadUnits; 
	
	private Image GameMap, arrowLeft, arrowRight, carrowLeft, carrowRight;
	
	private JButton exitBtn;
	private JLabel gTimer, unitLabelL, unitLabelR, winScreen;

	private MainMenu gmm;
	
	private javax.swing.Timer gameTimer; //timer that ends game after time limit is reached
	private float secs; //tracks amount of seconds is left
	
	private AudioClip bgmusic;
		
	public aiGamePanel(BattleLines m, Faction leftf, Faction rightf){
		setLayout(null);
		keys = new boolean[KeyEvent.KEY_LAST+1];
		lanes = new Lane[8];
		unitsL = new UnitButton[6];
		unitsR = new UnitButton[6];
		mainFrame = m;
	    rectx = 0;
        recty = 200;
		setSize(750,630);
        addKeyListener(this);
        
        lifeL=37;
        lifeR=38;
        deadUnits = new ArrayList<Unit>();
        //Setting what faction the ai will play as
        if(rightf.getType().equals("Imperial Guard")){
        	mode=IG;
        	//these arrays contain what units are to be spawned at what time
        	stchoice = new int[]{0,2,3};
        	ndchoice = new int[]{4,0,2};
        	rdchoice = new int[]{5};
        }
        if(rightf.getType().equals("Loyal Marines")){
        	mode=LM;
        	stchoice = new int[]{3,4,2};
        	ndchoice = new int[]{1,4,5};
        	rdchoice = new int[]{0,5,3};
        }
        if(rightf.getType().equals("Chaos Marines")){
        	mode=CM;
        	stchoice = new int[]{1,5,2};
        	ndchoice = new int[]{1,3,5};
        	rdchoice = new int[]{0,1,4};
        }
        
        GameMap = new ImageIcon("Sprites/GameMap.png").getImage();
        arrowLeft = new ImageIcon("Sprites/ArrowLeft.png").getImage();
        arrowRight = new ImageIcon("Sprites/ArrowRight.png").getImage();
        carrowLeft = new ImageIcon("Sprites/ChargeLeft.png").getImage();
        carrowRight = new ImageIcon("Sprites/ChargeRight.png").getImage();
        
        bgmusic = Applet.newAudioClip(getClass().getResource("Sounds/Game.wav"));
        
        gameTimer = new javax.swing.Timer(300000, this);
        gameTimer.start();
        secs=300;
        //Generating lanes and their associated variables
        for(int i=0; i<8; i++){
        	lanes[i] = new Lane(i*50+200);
        }
        selectR=lanes[0];
        curLaneR=0;
        selectL=lanes[0];
        curLaneL=0;
        
        //Generating UnitButtons and assigning them their Unit
        for(int i=0; i<6; i++){
        	unitsL[i] = new UnitButton(10+55*i,20,leftf.getUnit(i));
        	unitsR[i] = new UnitButton(410+55*i,20,rightf.getUnit(i));
        }
        bselectR=unitsR[0];
        curButtonR=0;
        bselectL=unitsL[0];
        curButtonL=0;
        
        exitBtn = new JButton("Exit");
        exitBtn.addActionListener(this);
    	exitBtn.setSize(65,30);
    	exitBtn.setLocation(340,80);
       	add(exitBtn);
       	
       	//Timer that shows how much time is left in the game
       	gTimer = new JLabel(String.valueOf(secs));
		gTimer.setSize(200,50);
		gTimer.setLocation(352,20);
		gTimer.setForeground(Color.yellow);
		gTimer.setFont(new Font("Niagara Solid", Font.PLAIN, 50));
		add(gTimer);
		
		unitLabelL = new JLabel(unitsL[curButtonL].getUnit().getName());
		unitLabelL.setSize(200,50);
		unitLabelL.setLocation(10,65);
		unitLabelL.setForeground(Color.red);
		unitLabelL.setFont(new Font("Niagara Solid", Font.PLAIN, 30));
		add(unitLabelL);
		
		unitLabelR = new JLabel(unitsL[curButtonL].getUnit().getName());
		unitLabelR.setSize(200,50);
		unitLabelR.setLocation(410,65);
		unitLabelR.setForeground(Color.blue);
		unitLabelR.setFont(new Font("Niagara Solid", Font.PLAIN, 30));
		add(unitLabelR);
       
        bgmusic.loop();
       	setVisible(true);
        
	}
	
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.aistart();
    }
    
    public void actionPerformed(ActionEvent e){
    	Object source = e.getSource();
    	//If game is to be ended
    	if(source == gameTimer){
    		stopGame();
    	}
    	if (source == exitBtn ){
    		bgmusic.stop();
    		MainMenu gmm = new MainMenu(mainFrame);
    	}  	
	}
	
	public void stopGame(){ //Game is over
   		JLabel winScreen = new JLabel();
   		mainFrame.aistop();
   		
   		exitPanel = new JPanel();   //Panel where all of these things occur
   		exitPanel.setSize(500,120);
   		exitPanel.setLocation(125,250);
   		exitPanel.setLayout(null);
   		exitPanel.setBackground(new Color (0,0,255,126));

		if (lifeL > lifeR){//calculating who wins
			winScreen = new JLabel("Left Player Wins!");
		}
		else if (lifeL < lifeR){
			winScreen = new JLabel("Right Player Wins!");
		}
		else if (lifeL == lifeR){
			winScreen = new JLabel("Tie Game!");
		}
	
		winScreen.setForeground(new Color(255,255,0)); 
       	winScreen.setHorizontalAlignment(SwingConstants.CENTER);
       	winScreen.setSize(300,50);
       	winScreen.setLocation(100,20);
       	winScreen.setFont(new Font("Niagara Solid", Font.PLAIN, 50)); //Makes font bigger
       	exitPanel.add(winScreen);
       	
       	exitBtn.setLocation(220,75);//allows user to exit to main menu
       	exitPanel.add(exitBtn);
       	
       	
       	add(exitPanel, new Integer(2));
   		
   	}
   	
	public void keyUpdate(){//resests all the key flags
		if(!keys[KeyEvent.VK_UP]){
			checkupR=false;
		}
		if(!keys[KeyEvent.VK_DOWN]){
			checkdownR=false;
		}
		if(!keys[KeyEvent.VK_W]){
			checkupL=false;
		}
		if(!keys[KeyEvent.VK_S]){
			checkdownL=false;
		}
		if(!keys[KeyEvent.VK_A]){
			checkleftL=false;
		}
		if(!keys[KeyEvent.VK_D]){
			checkrightL=false;
		}
		if(!keys[KeyEvent.VK_Q]){
			checkq=false;
		}
		if(!keys[KeyEvent.VK_SPACE]){
			checkspace=false;
		}
	}
	
	public void move(){//where players use their keyboard inputs
	//allows players to select the lane they want and the unit button they want
		if(keys[KeyEvent.VK_W] && !checkupL ){
			if(curLaneL>0){
				checkupL=true;
				curLaneL-=1;
				selectL=lanes[curLaneL];
			}
		}
		if(keys[KeyEvent.VK_S] && !checkdownL ){
			if(curLaneL<7){
				checkdownL=true;
				curLaneL+=1;
				selectL=lanes[curLaneL];
			}
		}
		if(keys[KeyEvent.VK_A] && !checkleftL ){
			if(curButtonL>0){
				checkleftL=true;
				curButtonL-=1;
				bselectL=unitsL[curButtonL];
			}
			else{
				checkleftL=true;
				curButtonL=5;
				bselectL=unitsL[curButtonL];
			}
		}
		if(keys[KeyEvent.VK_D] && !checkrightL ){
			if(curButtonL<5){
				checkrightL=true;
				curButtonL+=1;
				bselectL=unitsL[curButtonL];
			}
			else{
				checkrightL=true;
				curButtonL=0;
				bselectL=unitsL[curButtonL];
			}
		}
		//Spawning Units
		if(keys[KeyEvent.VK_SPACE] && !checkspace && bselectL.getReady()){
			checkspace=true;
			spawn(selectL,bselectL.getUnit());

			for (UnitButton b : unitsL){
				b.unitSpawned();
			}
		}
		//Charging
		if(keys[KeyEvent.VK_Q] && !checkq && bselectL.getReady() && Lcharge){
			checkq=true;
			charge(bselectL.getUnit());
		
			for (UnitButton b : unitsL){
				b.unitSpawned();
			}	
		}
			//updates label to what the current selected unit is for each side
		unitLabelL.setText(bselectL.getUnit().getName());	
		unitLabelR.setText(bselectR.getUnit().getName());	
	}
	
	//updates the gameTimer and secs
	public void timerUpdate(){
		secs-=0.04;
		gTimer.setText(String.valueOf((int)secs));
		if(lifeL <= 0 || lifeR <= 0){//Win conditions
			stopGame();
		}
	}
	
	public void unitUpdate(){
		//ArayList that tracks which units have died or cleared the battlefield
		ArrayList<Unit> ucleared = new ArrayList<Unit>();
		ArrayList<Unit> newDead = new ArrayList<Unit>();
		//updates all units
		for(Lane l : lanes){
			l.update(ucleared,newDead);
		}
		//updates score if units have cleared
		for(Unit u : ucleared){
			if(u.getDir()==LEFT){
				lifeR--;
				lifeL++;
			}
			else{
				lifeR++;
				lifeL--;
			}
			u.getLane().removeU(u);
			//remove units that have cleared
		}
		//updates the master List of all dead units
		for(Unit u : newDead){
			deadUnits.add(u);
			if(u.getDir()==LEFT){
				chargeR++; // updates the requirements to have a charge
			}
			else{
				chargeL++;
			}
			if(chargeR>=20){
				Rcharge=true;
			}
			if(chargeL>=20){
				Lcharge=true;
			}
			u.getLane().removeU(u);
			//removes all dead units
		}
	}
	
	public void aiUpdate(){	//ai actions in the game
		aiLSelect();
		aiSpawn();
	}
	
	public void aiLSelect(){//lets ai select which lane to deploy in
		for(Lane l : lanes){
			if(l.getAdvan()>-5){//to avoid fighting hopeless fights
				if(l.getAdvan()<selectR.getAdvan()){//to select lane most in trouble
					selectR=l;
				}
			}
		}
	}
	
	public void aiSpawn(){//lets ai select which unit to deploy
		Random rand = new Random();
		//behaviour and units change depending on faction
		if(mode==IG){
			if(selectR.getAdvan()<=-1){//default condion
				bselectR=unitsR[1];
			}
			else if(selectR.getAdvan()<=5){//when ai has some control in lane
				int choice = rand.nextInt(3);//chooses random unit to spawn based on the pre-assgined behaviour array
				bselectR = unitsR[stchoice[choice]];
			}
			else if(selectR.getAdvan()<=10){//when ai is dominating in the lane
				int choice = rand.nextInt(3);
				bselectR = unitsR[ndchoice[choice]];
			}
			else if(selectR.getAdvan()>10){//spawns tank, tank beats everything
				bselectR=unitsR[5];
			}
		}
		if(mode==LM){
			if(selectR.getAdvan()<=-1){
				bselectR=unitsR[0];
			}
			else if(selectR.getAdvan()<=5){
				int choice = rand.nextInt(3);
				bselectR = unitsR[stchoice[choice]];
			}
			else if(selectR.getAdvan()<=10){
				int choice = rand.nextInt(3);
				bselectR = unitsR[ndchoice[choice]];
			}
			else if(selectR.getAdvan()>10){
				int choice = rand.nextInt(3);
				bselectR = unitsR[rdchoice[choice]];
			}
		}
		if(mode==CM){
			if(selectR.getAdvan()<=-1){
				bselectR=unitsR[0];
			}
			else if(selectR.getAdvan()<=5){
				int choice = rand.nextInt(3);
				bselectR = unitsR[stchoice[choice]];
			}
			else if(selectR.getAdvan()<=10){
				int choice = rand.nextInt(3);
				bselectR = unitsR[ndchoice[choice]];
			}
			else if(selectR.getAdvan()>10){
				int choice = rand.nextInt(3);
				bselectR = unitsR[rdchoice[choice]];
			}
		}
		//if selected unit is ready to spawn, it gets spawned
		if(bselectR.getReady()){
			if(Rcharge){//if charge is ready
				charge(bselectR.getUnit());
			}
			else{
				spawn(selectR,bselectR.getUnit());
				for (UnitButton b : unitsR){
					//reset all cooldowns for ai
					b.unitSpawned();
				}
			}
		}
	}
	
	public void spawn(Lane l, Unit u){ //spawns a unit to a specific lane
		if(u.getDir()==LEFT){
			u.setLane(l);
			l.addU(u);
		}
		else{
			u.setLane(l);
			l.addU(u);
		}
	}
	
	public void charge(Unit u){ 	//spawns a unit in each lane of the battlefield 
		if(u.getDir()==LEFT){
			Lcharge=false;
			chargeL=0;
		}
		else{
			Rcharge=false;
			chargeR=0;
		}
		for(Lane l : lanes){
			spawn(l,u.copy());
		}
	}
	
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    public void paintComponent(Graphics g){
    	g.drawImage(GameMap,0,0,this);
    	  
		g.drawImage(arrowRight,685,selectR.getLaneY(),this);
		if(Rcharge){//if charge availible
			g.drawImage(carrowRight,685,selectR.getLaneY(),this);
		}
		g.drawImage(arrowLeft,15,selectL.getLaneY(),this);
		if(Lcharge){
			g.drawImage(carrowLeft,15,selectL.getLaneY(),this);
		}
		
		//managing the score bar
		g.setColor(new Color (255,0,0,150));  
		g.fillRect(0,0,10*lifeL,5);
		
		g.setColor(new Color (0,0,255,150));  
		g.fillRect(750-10*lifeR,0,10*lifeR,5);
		
		//drawing the timer box
		g.setColor(new Color (139,69,19, 126));
		g.fillRect(340,20,65,50);
		
		
		//drawing the interface for unit buttons
		g.setColor(new Color(238,233,233));
		for(UnitButton b : unitsL){
			g.fillRect(b.getX(),b.getY(),50,50);
			g.drawImage(b.getUnit().getProf(),b.getX(),b.getY(),null);
		}
		for(UnitButton b : unitsR){
			g.fillRect(b.getX(),b.getY(),50,50);
			g.drawImage(b.getUnit().getProf(),b.getX(),b.getY(),null);
		}
		g.setColor(Color.red);
		//marking units that are ready for deployment
		for(UnitButton b : unitsL){
			if(b.getReady()){
				g.fillRect(b.getX(),b.getY(),50,50);
			}
		}
		for(UnitButton b : unitsR){
			if(b.getReady()){
				g.fillRect(b.getX(),b.getY(),50,50);
			}
		}
		for(UnitButton b : unitsL){
			g.drawImage(b.getUnit().getProf(),b.getX(),b.getY(),null);
		}
		for(UnitButton b : unitsR){
			g.drawImage(b.getUnit().getProf(),b.getX(),b.getY(),null);
		}
		g.setColor(Color.yellow);
		//drawing which unit is selected
		g.setColor(new Color (0,0,0,126));  
		g.fillRect(bselectR.getX(),bselectR.getY(),50,50);
		g.fillRect(bselectL.getX(),bselectL.getY(),50,50);
		//drawing dead
		for(Unit u: deadUnits){
			g.drawImage(u.getDeadPic(),u.getX(),u.getY(),null);
		}
		//drawing units  
		for(Lane l : lanes){
			for(Unit u : l.getUnitsL()){
				g.drawImage(u.getCurPic(),u.getX(),u.getY(),null);
			}
			for(Unit u : l.getUnitsR()){
					//this corrects the offsets that occur when blitting mirrored images
				if(u.getFiring()||u.getRapidFiring()){
					if(!u.getName().equals("Flamer") && !u.getName().equals("Tank")){
						g.drawImage(u.getCurPic(),u.getX()-20,u.getY(),null);	
					}
					if(u.getName().equals("Flamer")){
						g.drawImage(u.getCurPic(),u.getX()-120,u.getY(),null);
					}
					if(u.getName().equals("Tank")){
						g.drawImage(u.getCurPic(),u.getX()-40,u.getY(),null);
					}
				}
				else{
					g.drawImage(u.getCurPic(),u.getX(),u.getY(),null);
				}
			}
			
		}  
    }
}