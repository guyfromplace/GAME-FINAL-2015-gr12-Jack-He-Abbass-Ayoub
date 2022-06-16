/*
 * MainMenu.java
 * This is the Main Menu for the game. It allows the player to select the type of game
 * mode they want to play and choose what factions they want to play as.
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.awt.Graphics2D.*;
import java.applet.*;

public class MainMenu extends JFrame implements ActionListener{
	public static BattleLines MAIN_GAME;    //Calls the Gamepanel class from "BattleLines"
	JButton aiMode, pMode, tMode, eMode;
	Background bg;					    //Calls "Background" class for loading images
	FactionSelect fs;					//Calls "FactionSelect" for choosing factions
	Tutorial tl;
	
	private AudioClip menumusic;
	
	
	public MainMenu(BattleLines maingame){
		super("Welcome to BattleLines");
		MAIN_GAME= maingame;
		setLayout(null);
		//tl = new Tutorial(MAIN_GAME,this);
		
		menumusic = Applet.newAudioClip(getClass().getResource("Sounds/Menu.wav"));
		
		//JButtons for Main Menu Screen
		
		aiMode = new JButton("Single Player Mode");
		aiMode.addActionListener(this);
    	aiMode.setSize(200,30);
    	aiMode.setLocation(25, 435);
    	add(aiMode);
		
		pMode=new JButton("Two Player Mode");
		pMode.addActionListener(this);
    	pMode.setSize(200,30);
    	pMode.setLocation(25, 475);
    	add(pMode);
    	
    	tMode = new JButton("Tutorial");
		tMode.addActionListener(this);
    	tMode.setSize(200,30);
    	tMode.setLocation(25, 515);
    	add(tMode);
    	
    	eMode = new JButton("Exit Game");
		eMode.addActionListener(this);
    	eMode.setSize(200,30);
    	eMode.setLocation(25, 555);
    	add(eMode);
    	
    	bg = new Background(); //Background pic for mainmenu
    	add(bg);
    	
    	setSize(924,630);
    	menumusic.loop();
    	setVisible(true);
    	setResizable(false);
		
	}
	
	public void mute(){
		menumusic.stop();
	}
	public void reset(Tutorial ti){
		repaint(); 
	}
	
	//Controlling what each JButton on the MainMenu does
	public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();
		if(source== pMode){
			fs = new FactionSelect(MAIN_GAME, true); //Create FactionSelect only after pMode is selected
    		add(fs);
    		
    		aiMode.setVisible(false);						   
			pMode.setVisible(false);
			tMode.setVisible(false);
			eMode.setVisible(false);
			
			fs.setVisible(true);			
 
		}
		
		if (source == tMode){
			tl = new Tutorial(MAIN_GAME,this);
			add(tl);
			
			aiMode.setVisible(false);						   
			pMode.setVisible(false);
			tMode.setVisible(false);
			eMode.setVisible(false);

			tl.setVisible(true);
		}
		
		if (source == aiMode){          //Loads aiMode of the game
			fs = new FactionSelect(MAIN_GAME, false); //Create FactionSelect only after
    		add(fs);						   //Player mode is selected
			fs.setVisible(true);
			aiMode.setVisible(false);						   
			pMode.setVisible(false);
			tMode.setVisible(false);
			eMode.setVisible(false);									  
		}
		
		if (source == eMode){
			System.exit(0);
		}
	}
}

class Tutorial extends JPanel implements ActionListener{
	private BattleLines MAIN_GAME;
	private MainMenu gmm;
	private JButton exitBtn;
	private Image tut;		//Tutorial image
	
	public Tutorial(BattleLines MAIN_GAME, MainMenu gmm){
		super();
		this.MAIN_GAME = MAIN_GAME;
		this.gmm = gmm;
		setLayout(null);
		setSize(924,630);
		
		exitBtn = new JButton("Return to Main Menu");
        exitBtn.addActionListener(this);
    	exitBtn.setSize(200,30);
    	exitBtn.setLocation(445,565);
       	add(exitBtn);
	
		tut = new ImageIcon("Sprites/Tutorial.png").getImage(); //Image for tutorial screen
	}
	
	public void actionPerformed (ActionEvent e){
		Object source = e.getSource();
		
		if (source == exitBtn ){
    		gmm.remove(this);
    		gmm.repaint();
    		gmm.revalidate();
    		gmm.aiMode.setVisible(true);						   
			gmm.pMode.setVisible(true);
			gmm.tMode.setVisible(true);
			gmm.eMode.setVisible(true);
    	}
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.fillRect(0,0,924,630);
		g.drawImage(tut,0,0,null); 
		repaint();  		
    }	
}

/*
 *FactionSelect is the JPanel that appears after choosing a game mode. It allows the user to 
 *pick their desired faction to battle their enemy.
 *The game then generates a GamePanel with the selected factions
 */

class FactionSelect extends JPanel implements ActionListener{
	private BattleLines MAIN_GAME;   
	private JButton igl, cml, lml, igr, cmr, lmr, done, back;	//Jbuttons for factions: Imperial Guard, Chaos Marines, Loyal Marines
	private Image Fback;										//JButtons for navigating
	private JLabel fLeft, fRight, fTitle;
	Faction FactionLeft;
	Faction FactionRight;
	private MainMenu menu;
	private boolean flag; //flag to tell if aimode or pmode was selected in mainMenu

	public FactionSelect(BattleLines MAIN_GAME, boolean flag){
		super();
		this.MAIN_GAME= MAIN_GAME;
		setLayout(null);
		setSize(924,630);
		this.flag = flag;;
		
		//JLabels for heading on the Jpanel
		
		JLabel fLeft = new JLabel("Left Faction");
		fLeft.setSize(200,30);
		fLeft.setLocation(85,150);
		fLeft.setForeground(Color.red);
		fLeft.setFont(new Font("Niagara Solid", Font.PLAIN, 30));
		add(fLeft);
		
		JLabel fRight = new JLabel("Right Faction");
		fRight.setSize(200,30);
		fRight.setLocation(285,150);
		fRight.setForeground(Color.red);
		fRight.setFont(new Font("Niagara Solid", Font.PLAIN, 30));
		add(fRight);
	
		JLabel fTitle = new JLabel("Choose Your Faction");
		fTitle.setSize(300,50);
		fTitle.setLocation(120,50);
		fTitle.setForeground(Color.yellow);
		fTitle.setFont(new Font("Niagara Solid", Font.PLAIN, 50));
		add(fTitle);
		
		//Jbuttons for choosing Factions
		
		igl = new JButton("Imperial Guard");
		igl.addActionListener(this);
		igl.setSize(200,30);
		igl.setLocation(25, 190);
		add(igl);
		
		cml = new JButton("Chaos Marines");
		cml.addActionListener(this);
		cml.setSize(200,30);
		cml.setLocation(25, 230);
		add(cml);
		
		lml = new JButton("Loyal Marines");
		lml.addActionListener(this);
		lml.setSize(200,30);
		lml.setLocation(25, 270);
		add(lml);
		
		igr = new JButton("Imperial Guard");
		igr.addActionListener(this);
		igr.setSize(200,30);
		igr.setLocation(230, 190);
		add(igr);
		
		cmr = new JButton("Chaos Marines");
		cmr.addActionListener(this);
		cmr.setSize(200,30);
		cmr.setLocation(230, 230);
		add(cmr);
		
		lmr = new JButton("Loyal Marines");
		lmr.addActionListener(this);
		lmr.setSize(200,30);
		lmr.setLocation(230, 270);
		add(lmr);
		
		//Button for entering battlefield (only after factions are chosen)
		
		done = new JButton("Enter The Battle Field");
		done.addActionListener(this);
		done.setSize(200,30);
		done.setLocation(125,320);
		add(done);

		
		Fback = new ImageIcon("Sprites/MenuPic2.png").getImage(); //Image for factionSelect JPanel
	}
	
	public void actionPerformed (ActionEvent e){
		Object source = e.getSource();

		if (source == igl){
			FactionLeft = MAIN_GAME.getFaction(0);				//Calls factions from the MainGame accordingly
			System.out.println("Left Faction: Imperial Guard");
		}
		
		if (source == lml){
			FactionLeft = MAIN_GAME.getFaction(2);
			System.out.println("Left Faction: Loyal Marines");
		}
		
		if (source == cml){
			FactionLeft = MAIN_GAME.getFaction(4);
			System.out.println("Left Faction: Chaos Marines");
		}
		
		if (source == igr){
			FactionRight = MAIN_GAME.getFaction(1);
			System.out.println("Right Faction: Imperial Guard");
		}
		
		if (source == lmr){
			FactionRight = MAIN_GAME.getFaction(3);
			System.out.println("Right Faction: Loyal Marines");
		}
		
		if (source == cmr){
			FactionRight = MAIN_GAME.getFaction(5);
			System.out.println("Right Faction: Chaos Marines");
		}
		
		if (source == done){
			if(FactionLeft!=null && FactionRight!=null && flag){
				MAIN_GAME.pmode(FactionLeft,FactionRight);
				MAIN_GAME.setVisible(true);
				setVisible(false);
			}
			else if(FactionLeft!=null && FactionRight!=null && !flag){
				MAIN_GAME.aimode(FactionLeft,FactionRight);
				MAIN_GAME.setVisible(true);
				setVisible(false);
			}
			else{
				System.out.println("Pick a Left Faction and a Right Faction");
			}
		}
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(Color.yellow);  //Adds yellow border to the JPanel
		g.fillRect(0,0,924,630);
		Graphics2D twoD = (Graphics2D)g;
		twoD.drawImage(Fback,10,10,null); 
		repaint();  		
    }
}

//Background class for loading and displaying background images
class Background extends JPanel implements ActionListener{
	private Image bg;
	
	public Background(){
		super();
		setSize (924,630);
		setLocation(0,0);
		
		bg = new ImageIcon("Sprites/MenuPic1.png").getImage();
		setOpaque(false);
		setVisible(true);
	}
	public void actionPerformed (ActionEvent e){
		Object source = e.getSource();
		repaint();	
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D twoD = (Graphics2D)g;
		twoD.drawImage(bg,0,0,null);
	}
}