package com.cosmonaut.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Bodies.Hero;
import com.cosmonaut.Screens.EndScreen;
import com.cosmonaut.Screens.GameScreen;
import com.cosmonaut.Screens.HomeScreen;
import com.cosmonaut.Screens.MainMenuScreen;
import com.cosmonaut.Screens.TutorialScreen;

public class HUD {
	
	final MyGdxGame game;
	private float posXOxygen, posYOxygen, widthOxygen, heightOxygen, outOfFuelAlpha, fuelAlpha, oxygenAlpha, buttonAlpha, testOxygenHeight, testOxygenWidth, testFuelWidth;
	private Hero hero;
	private LabelStyle hudLabelStyle;
	private Label outOfFuelLabel, oxygenLabel, fuelLabel;
	//private Image imageOxygenLevel, imageOxygenLevelBackground, imageFuelLevel, imageFuelLevelBackground;
	public String pressEscString;
	private boolean triggerInterstitial = false;
	private Vector2 posOxygen, posFuel;
	private Vector3 position, projectedPosition;
	private MyTiledTexture oxygenTexture;
	
	//Menu pause
	private UIWindow pauseWindow;
	private Button resumeButtonSpace, homeButtonSpace, restartButtonSpace, nextButtonSpace;
	private Label labelPause, labelRestart, labelResume, labelHome, labelNext;
	private LabelStyle spaceLabelStyleMenu;
	
	//Controles & Android buttons
	public Button buttonLeft, buttonRight, buttonJetPack, pauseButton, pauseButton2;
	
	//Fin de jeu
	private TextButtonStyle textButtonStyle;
	private TextButton endGameButton;

	public HUD(final MyGdxGame game, Stage stage, Skin skin, Hero hero){
		this.game = game;
		this.hero = hero;

		create(game, skin, hero);
		addToStage(stage);	
	}
	
	public HUD(final MyGdxGame game, Skin skin, Hero hero){
		this.game = game;
		this.hero = hero;
		create(game, skin, hero);
	}
	
	public void create(final MyGdxGame game, Skin skin, Hero hero){
		outOfFuelAlpha = 0;
		fuelAlpha = 0;
		oxygenAlpha = 0;
		posFuel = Pools.obtain(Vector2.class).set(posXOxygen, posYOxygen - 2 * heightOxygen);
		posOxygen = Pools.obtain(Vector2.class).set(posXOxygen, posYOxygen);
		if(Data.getLanguage().equals("EN"))
			posXOxygen = new GlyphLayout(game.assets.get("fontHUD.ttf", BitmapFont.class), game.text.get("Oxygen").toUpperCase()).width + 2*Gdx.graphics.getWidth()/100;
		else
			posXOxygen = new GlyphLayout(game.assets.get("fontHUD.ttf", BitmapFont.class), game.text.get("Fuel").toUpperCase()).width + 2*Gdx.graphics.getWidth()/100;
		posYOxygen = 95 * Gdx.graphics.getHeight()/100;
		widthOxygen = Gdx.graphics.getWidth()/3;
		heightOxygen = Gdx.graphics.getHeight()/70;

		if(Gdx.app.getType() == ApplicationType.Desktop)
			pressEscString = game.text.get("PressEsc");
		else if(Gdx.app.getType() == ApplicationType.Android)
			pressEscString = game.text.get("PressPause");

		hudLabelStyle = new LabelStyle(game.assets.get("fontHUD.ttf", BitmapFont.class), Color.WHITE);
		
		outOfFuelLabel = new Label(pressEscString, hudLabelStyle);
		outOfFuelLabel.setX(Gdx.graphics.getWidth()/2 - new GlyphLayout(game.assets.get("fontHUD.ttf", BitmapFont.class), outOfFuelLabel.getText()).width/2);
		outOfFuelLabel.setY(Gdx.graphics.getHeight()/2 - new GlyphLayout(game.assets.get("fontHUD.ttf", BitmapFont.class), outOfFuelLabel.getText()).height/2);
		outOfFuelLabel.addAction(Actions.alpha(0));
		
		oxygenLabel = new Label(game.text.get("Oxygen").toUpperCase(), hudLabelStyle);
		oxygenLabel.setX(posXOxygen - new GlyphLayout(game.assets.get("fontHUD.ttf", BitmapFont.class), game.text.get("Oxygen").toUpperCase()).width - Gdx.graphics.getWidth()/100);
		oxygenLabel.setY(posYOxygen - new GlyphLayout(game.assets.get("fontHUD.ttf", BitmapFont.class), game.text.get("Oxygen").toUpperCase()).height/2);
		
		fuelLabel = new Label(game.text.get("Fuel").toUpperCase(), hudLabelStyle);
		fuelLabel.setX(posXOxygen - new GlyphLayout(game.assets.get("fontHUD.ttf", BitmapFont.class), game.text.get("Fuel").toUpperCase()).width - Gdx.graphics.getWidth()/100);
		fuelLabel.setY(posYOxygen - new GlyphLayout(game.assets.get("fontHUD.ttf", BitmapFont.class), game.text.get("Fuel").toUpperCase()).height/2 - 2 * heightOxygen);

		/*
		imageOxygenLevel = new Image(skin.getDrawable("WhiteSquare"));
		imageOxygenLevel.setColor(0,0,1,1);
		imageOxygenLevel.setWidth(widthOxygen);
		imageOxygenLevel.setHeight(heightOxygen);
		imageOxygenLevel.setX(posXOxygen);
		imageOxygenLevel.setY(posYOxygen);
		*/
		position = Pools.obtain(Vector3.class);
		projectedPosition = Pools.obtain(Vector3.class);
		oxygenTexture = new MyTiledTexture(game.assets.get("Images/Barre.png", Texture.class));
		testOxygenHeight = 0.011f * GameConstants.SCREEN_WIDTH;
		testOxygenWidth = (10 + (int)(Data.getOxygenLevel() * 1.2f)); 
		testFuelWidth = (10 + (int)(Data.getFuelLevel() * 1.2f));
		/*		
		imageOxygenLevelBackground = new Image(skin.getDrawable("WhiteSquare"));
		imageOxygenLevelBackground.setColor(0,0,0.35f,1);
		imageOxygenLevelBackground.setWidth(widthOxygen);
		imageOxygenLevelBackground.setHeight(heightOxygen);
		imageOxygenLevelBackground.setX(posXOxygen);
		imageOxygenLevelBackground.setY(posYOxygen);
		
		imageFuelLevel = new Image(skin.getDrawable("WhiteSquare"));
		imageFuelLevel.setColor(1,0,0,1);
		imageFuelLevel.setWidth(widthOxygen);
		imageFuelLevel.setHeight(heightOxygen);
		imageFuelLevel.setX(posXOxygen);
		imageFuelLevel.setY(posYOxygen - 2 * heightOxygen);
		
		imageFuelLevelBackground = new Image(skin.getDrawable("WhiteSquare"));
		imageFuelLevelBackground.setColor(0.35f,0,0,1);
		imageFuelLevelBackground.setWidth(widthOxygen);
		imageFuelLevelBackground.setHeight(heightOxygen);
		imageFuelLevelBackground.setX(posXOxygen);
		imageFuelLevelBackground.setY(posYOxygen - 2 * heightOxygen);
		*/
		/*
		stage.addActor(outOfFuelLabel);
		stage.addActor(oxygenLabel);
		stage.addActor(fuelLabel);
		stage.addActor(imageOxygenLevelBackground);
		stage.addActor(imageFuelLevelBackground);
		stage.addActor(imageOxygenLevel);
		stage.addActor(imageFuelLevel);
		*/
		
		Color colorSpace = Pools.obtain(Color.class);
		colorSpace.set(2/256f, 165/256f, 200/256f, 1);
		
		//Menu Pause-Lose-Win
		pauseWindow = new UIWindow(skin.getDrawable("MenuWindow"), 
								62*Gdx.graphics.getWidth()/100, 
								(62*Gdx.graphics.getWidth()/100) * skin.getRegion("MenuWindow").getRegionHeight()/skin.getRegion("MenuWindow").getRegionWidth());

		spaceLabelStyleMenu = new LabelStyle(game.assets.get("fontTable.ttf", BitmapFont.class), colorSpace);
		
		labelPause = new Label(game.text.get("Pause").toUpperCase(), spaceLabelStyleMenu);
		labelPause.setWrap(true);
		labelPause.setWidth(0.5f*pauseWindow.getWidth());
		labelPause.setAlignment(Align.center);

        resumeButtonSpace = new Button(skin.getDrawable("PlayButton"), skin.getDrawable("PlayButtonCheck"));     
        labelResume = new Label(game.text.get("Resume").toUpperCase(), spaceLabelStyleMenu);      
        labelResume.setTouchable(Touchable.disabled);
        restartButtonSpace = new Button(skin.getDrawable("RestartButton"), skin.getDrawable("RestartButtonCheck")); 
        labelRestart = new Label(game.text.get("Restart").toUpperCase(), spaceLabelStyleMenu);     
        labelRestart.setTouchable(Touchable.disabled);  
        homeButtonSpace = new Button(skin.getDrawable("HomeButton"), skin.getDrawable("HomeButtonCheck")); 
        labelHome = new Label(game.text.get("Home").toUpperCase(), spaceLabelStyleMenu);   
        labelHome.setTouchable(Touchable.disabled);    
        
        nextButtonSpace = new Button(skin.getDrawable("NextButton"), skin.getDrawable("NextButtonCheck"));  
        labelNext = new Label(game.text.get("Next").toUpperCase(), spaceLabelStyleMenu);      
        labelNext.setTouchable(Touchable.disabled);
		
		pauseWindow.addActorRelativeCentered(labelPause, 0.7f, 0.865f);
        pauseWindow.addActorRelative(resumeButtonSpace, 0.545f * pauseWindow.getWidth(), 0.545f * pauseWindow.getWidth() * skin.getRegion("PlayButtonCheck").getRegionHeight()/skin.getRegion("PlayButtonCheck").getRegionWidth(), 0.13f, 0.51f);
        pauseWindow.addActorRelative(restartButtonSpace, resumeButtonSpace.getWidth(), resumeButtonSpace.getHeight(), 0.23f, 0.31f);
        pauseWindow.addActorRelative(homeButtonSpace, resumeButtonSpace.getWidth(), resumeButtonSpace.getHeight(), 0.33f, 0.11f);
        pauseWindow.addActorRelative(nextButtonSpace, resumeButtonSpace.getWidth(), resumeButtonSpace.getHeight(), 0.13f, 0.51f);      
		pauseWindow.addActor(	labelResume, 
								resumeButtonSpace.getX() + 0.64f*resumeButtonSpace.getWidth() - labelResume.getPrefWidth()/2 - pauseWindow.getX(), 
								resumeButtonSpace.getY() + resumeButtonSpace.getHeight()/2 - labelResume.getPrefHeight()/2 - pauseWindow.getY());
		pauseWindow.addActor(	labelRestart, 
								restartButtonSpace.getX() + 0.64f*restartButtonSpace.getWidth() - labelRestart.getPrefWidth()/2 - pauseWindow.getX(), 
								restartButtonSpace.getY() + restartButtonSpace.getHeight()/2 - labelRestart.getPrefHeight()/2 - pauseWindow.getY());
		pauseWindow.addActor(	labelHome, 
								homeButtonSpace.getX() + 0.64f*homeButtonSpace.getWidth() - labelHome.getPrefWidth()/2 - pauseWindow.getX(), 
								homeButtonSpace.getY() + homeButtonSpace.getHeight()/2 - labelHome.getPrefHeight()/2 - pauseWindow.getY());
		pauseWindow.addActor(	labelNext, 
								nextButtonSpace.getX() + 0.64f*nextButtonSpace.getWidth() - labelNext.getPrefWidth()/2 - pauseWindow.getX(), 
								nextButtonSpace.getY() + nextButtonSpace.getHeight()/2 - labelNext.getPrefHeight()/2 - pauseWindow.getY());
		pauseWindow.setVisible(false);

		pauseWindow.alfaZero(0);
		
		//Game end
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = game.skin.getDrawable("GameFinishedWindow");
		textButtonStyle.down = game.skin.getDrawable("GameFinishedWindow");
		textButtonStyle.font = game.assets.get("fontMenu.ttf", BitmapFont.class);
		textButtonStyle.fontColor = colorSpace;
		textButtonStyle.downFontColor = Color.BLACK;
		
		endGameButton = new TextButton(game.text.get("Continue"), textButtonStyle);
		endGameButton.setVisible(false);
		endGameButton.addAction(Actions.alpha(0));
		endGameButton.setWidth(0.5f*Gdx.graphics.getWidth());
		endGameButton.setHeight(endGameButton.getWidth() * game.textureAtlas.findRegion("GameFinishedWindow").getRegionHeight() / game.textureAtlas.findRegion("GameFinishedWindow").getRegionWidth());
		endGameButton.setPosition(	Gdx.graphics.getWidth()/2 - endGameButton.getWidth()/2, 
									Gdx.graphics.getHeight()/2 - endGameButton.getHeight()/2);

		
		//Controles mobile
		System.out.println("Gdx.app.getType() = " + Gdx.app.getType());
		buttonLeft = new Button(skin.getDrawable("LeftButton"), skin.getDrawable("LeftButtonCheck"));
		buttonLeft.setWidth(GameConstants.CONTROL_BUTTONS_SIZE * Data.getControlSize());
		buttonLeft.setHeight(buttonLeft.getWidth() * skin.getRegion("LeftButton").getRegionHeight()/skin.getRegion("LeftButton").getRegionWidth());
		buttonLeft.setX(Gdx.graphics.getWidth()/100);
		buttonLeft.setY(Gdx.graphics.getWidth()/100);

		buttonRight = new Button(skin.getDrawable("RightButton"), skin.getDrawable("RightButtonCheck"));
		buttonRight.setWidth(buttonLeft.getWidth());
		buttonRight.setHeight(buttonLeft.getHeight());
		buttonRight.setX(buttonLeft.getX() + buttonLeft.getWidth() + 0.01f * Gdx.graphics.getWidth());
		buttonRight.setY(buttonLeft.getY());

		buttonJetPack = new Button(skin.getDrawable("JetPackButton"), skin.getDrawable("JetPackButtonCheck"));
		buttonJetPack.setWidth(buttonLeft.getWidth());
		buttonJetPack.setHeight(buttonLeft.getWidth());
		buttonJetPack.setX(Gdx.graphics.getWidth() - buttonJetPack.getWidth() - buttonLeft.getX());
		buttonJetPack.setY(buttonLeft.getY());
		
		pauseButton = new Button(skin.getDrawable("PauseButton"), skin.getDrawable("PauseButtonCheck"));
		pauseButton.setWidth(0.09f * Gdx.graphics.getHeight());
		pauseButton.setHeight(pauseButton.getWidth());
		pauseButton.setX(Gdx.graphics.getWidth() - pauseButton.getWidth() - 2*buttonLeft.getX());
		pauseButton.setY(Gdx.graphics.getHeight() - pauseButton.getWidth() - 2*buttonLeft.getX());

		pauseButton2 = new Button(skin.getDrawable("PauseButton"), skin.getDrawable("PauseButtonCheck"));
		pauseButton2.setWidth(0.09f * Gdx.graphics.getHeight());
		pauseButton2.setHeight(pauseButton.getWidth());
		pauseButton2.setX(Gdx.graphics.getWidth() - pauseButton.getWidth() - 2*buttonLeft.getX());
		pauseButton2.setY(Gdx.graphics.getHeight() - pauseButton.getWidth() - 2*buttonLeft.getX());
		pauseButton2.addAction(Actions.alpha(0));
		
		buttonLeft.addAction(Actions.alpha(Data.getControlOpacity()));
		buttonRight.addAction(Actions.alpha(Data.getControlOpacity()));
		buttonJetPack.addAction(Actions.alpha(Data.getControlOpacity()));
		
		Pools.free(colorSpace);
	}
	
	public void addToStage(Stage stage){
		stage.addActor(outOfFuelLabel);
		stage.addActor(oxygenLabel);
		stage.addActor(fuelLabel);
		//stage.addActor(imageOxygenLevelBackground);
		//stage.addActor(imageFuelLevelBackground);
		//stage.addActor(imageOxygenLevel);
		//stage.addActor(imageFuelLevel);
		
		if(GameConstants.GAME_CONTROLS == GameConstants.ANDROID_BUTTONS_CONTROLS){
			stage.addActor(buttonLeft);
			stage.addActor(buttonRight);
			stage.addActor(buttonJetPack);
			stage.addActor(pauseButton2);
			stage.addActor(pauseButton);
		}
		else if (GameConstants.GAME_CONTROLS == GameConstants.ANDROID_GESTURE_CONTROLS){
			stage.addActor(pauseButton2);
			stage.addActor(pauseButton);
		}

		stage.addActor(endGameButton);
		pauseWindow.addToStage(stage);
	}
	
	public void draw(MyCamera camera){
		testOxygenHeight = 0.011f * camera.viewportWidth;

		//Oxygène
		projectedPosition.set(posXOxygen, Gdx.graphics.getHeight() - posYOxygen, -widthOxygen * hero.getOxygenLevel()/GameConstants.MAX_OXYGEN);
		position.set(camera.unproject(projectedPosition));
		game.batch.setColor(0,0,0.35f,1);
		oxygenTexture.drawHorizontal(game.batch, position.x, position.y, testOxygenWidth * testOxygenHeight, testOxygenHeight);	
		game.batch.setColor(0,0,1,(1 + MathUtils.cos(oxygenAlpha))/2);
		oxygenTexture.drawHorizontal(game.batch, position.x, position.y, testOxygenWidth * testOxygenHeight * hero.getOxygenLevel()/GameConstants.MAX_OXYGEN, testOxygenHeight);
		
		//Fuel
		projectedPosition.set(posXOxygen, Gdx.graphics.getHeight() - (posYOxygen - 2 * heightOxygen), -widthOxygen * hero.getFuelLevel()/GameConstants.MAX_FUEL);
		position.set(camera.unproject(projectedPosition));	
		game.batch.setColor(0.35f,0,0,1);
		oxygenTexture.drawHorizontal(game.batch, position.x, position.y, testFuelWidth * testOxygenHeight, testOxygenHeight);
		game.batch.setColor(1,0,0,(1 + MathUtils.cos(fuelAlpha))/2);
		oxygenTexture.drawHorizontal(game.batch, position.x, position.y, testFuelWidth * testOxygenHeight * hero.getFuelLevel()/GameConstants.MAX_FUEL, testOxygenHeight);
		game.batch.setColor(1,1,1,1);
	}
	
	public void update(){
		/*
		imageOxygenLevel.setWidth(widthOxygen * hero.getOxygenLevel()/GameConstants.MAX_OXYGEN);	
		imageFuelLevel.setWidth(widthOxygen * hero.getFuelLevel()/GameConstants.MAX_FUEL);
		
		if(hero.getFuelLevel() <= 0)
			imageFuelLevel.addAction(Actions.alpha(0));	
		else{
			imageFuelLevel.addAction(Actions.alpha((float)(1 + MathUtils.cos(fuelAlpha))/2));	
			outOfFuelAlpha = 0;
			outOfFuelLabel.addAction(Actions.alpha(outOfFuelAlpha));
			pauseButton2.addAction(Actions.alpha(outOfFuelAlpha));
		}
			
		if(hero.getOxygenLevel() <= 0)
			imageOxygenLevel.addAction(Actions.alpha(0));	
		else
			imageOxygenLevel.addAction(Actions.alpha((float)(1 + MathUtils.cos(oxygenAlpha))/2));
		*/	
		if(hero.getFuelLevel() <= 0)
			outOfFuel();
		else{	
			outOfFuelAlpha = 0;
			outOfFuelLabel.addAction(Actions.alpha(outOfFuelAlpha));
			pauseButton2.addAction(Actions.alpha(outOfFuelAlpha));
		}
		
		
		
		//Controles avec les boutons
		if(GameConstants.GAME_CONTROLS == GameConstants.ANDROID_BUTTONS_CONTROLS){
			if(!hero.isDead()){
				if(buttonLeft.isOver())
					hero.rotateClockwise();
				else if(buttonRight.isOver())
					hero.rotateCounterClockwise();
				else
					hero.stopRotating();		
				
				if(buttonJetPack.isOver() && hero.getFuelLevel() > 0)
					hero.jetpackOn();        
				else
					hero.jetpackOff();
			}
		}
		
		/*
		 * Test
		 */
		//fuelLabel.setText("" + ((float)Gdx.app.getJavaHeap()/1000000));
		//oxygenLabel.setText("" + ((float)Gdx.app.getNativeHeap()/1000000));
	}
	
	public void imageFuelPulse(){
		fuelAlpha += 4f * Gdx.graphics.getDeltaTime();		
	}
	
	public void imageOxygenPulse(){
		oxygenAlpha += 4f * Gdx.graphics.getDeltaTime();		
	}
	
	public void leftRightButtonPulse(){
		buttonAlpha += 4f * Gdx.graphics.getDeltaTime();
		buttonLeft.addAction(Actions.alpha((float)(1 + MathUtils.cos(buttonAlpha))/2));
		buttonRight.addAction(Actions.alpha((float)(1 + MathUtils.cos(buttonAlpha))/2));
	}
	
	public void jetpackButtonPulse(){
		buttonAlpha += 4f * Gdx.graphics.getDeltaTime();
		buttonJetPack.addAction(Actions.alpha((float)(1 + MathUtils.cos(buttonAlpha))/2));
	}
	
	public void resetAlpha(){
		fuelAlpha = 0;
		oxygenAlpha = 0;
	}
	
	public void win(){
		GameConstants.GAME_PAUSED = true;

		labelPause.setText(game.text.get("LevelCleared").toUpperCase());
		pauseWindow.setActorPositionRelative(labelPause, 0.7f, 0.865f);
		labelPause.setX(labelPause.getX() - labelPause.getWidth()/2);
		labelPause.setY(labelPause.getY() - labelPause.getHeight()/2);
		
		pauseWindow.setVisible(true);
		pauseWindow.alfaOne(0.25f);

		resumeButtonSpace.setVisible(false);
		labelResume.setVisible(false);
		labelRestart.setTouchable(Touchable.disabled);
		labelResume.setTouchable(Touchable.disabled);
		labelHome.setTouchable(Touchable.disabled);
		labelNext.setTouchable(Touchable.disabled);
	}
	
	public void lose(){
		GameConstants.GAME_PAUSED = true;
		pauseButton.setTouchable(Touchable.disabled);

		labelPause.setText(GameConstants.LOSE_MESSAGE);
		pauseWindow.setActorPositionRelative(labelPause, 0.7f, 0.865f);
		labelPause.setX(labelPause.getX() - labelPause.getWidth()/2);
		labelPause.setY(labelPause.getY() - labelPause.getHeight()/2);

		restartButtonSpace.setY(restartButtonSpace.getY() + 0.07f * Gdx.graphics.getHeight());
		labelRestart.setY(labelRestart.getY() + 0.07f * Gdx.graphics.getHeight());
		homeButtonSpace.setY(homeButtonSpace.getY() + 0.07f * Gdx.graphics.getHeight());
		labelHome.setY(labelHome.getY() + 0.07f * Gdx.graphics.getHeight());	
		
		pauseWindow.setVisible(true);
		pauseWindow.alfaOne(0.25f);
		
		resumeButtonSpace.setVisible(false);
		labelResume.setVisible(false);
		nextButtonSpace.setVisible(false);
		labelNext.setVisible(false);
		labelRestart.setTouchable(Touchable.disabled);
		labelResume.setTouchable(Touchable.disabled);
		labelHome.setTouchable(Touchable.disabled);
		labelNext.setTouchable(Touchable.disabled);
		
		if(!triggerInterstitial){
			triggerInterstitial = true;
			GameConstants.INTERSTITIAL_TRIGGER--;
		}
	}
	
	public void pause(){
		GameConstants.GAME_PAUSED = true;

		pauseWindow.setVisible(true);
		pauseWindow.alfaOne(0.25f);

		nextButtonSpace.setVisible(false);
		labelNext.setVisible(false);
		labelRestart.setTouchable(Touchable.disabled);
		labelResume.setTouchable(Touchable.disabled);
		labelHome.setTouchable(Touchable.disabled);
		labelNext.setTouchable(Touchable.disabled);
	}
	
	public void resume(){
		System.out.println("RESUME");
		GameConstants.GAME_PAUSED = false;
		pauseWindow.setVisible(false);
		pauseWindow.alfaZero(0.25f);
	}
	
	public void gameComplete(){
		GameConstants.GAME_PAUSED = true;
		GameConstants.GAME_FINISHED = true;
		//endGameButton.setVisible(true);
		//endGameButton.addAction(Actions.alpha(1, 0.5f));
		//game.getScreen().dispose();
		//game.setScreen(new EndScreen(game));
	}
	
	public void outOfFuel(){
		outOfFuelAlpha += 4 * Gdx.graphics.getDeltaTime();		
		outOfFuelLabel.addAction(Actions.alpha((float)(1 + MathUtils.cos(outOfFuelAlpha))/2));	
		pauseButton2.addAction(Actions.alpha((float)(1 + MathUtils.cos(outOfFuelAlpha))/2));	
	}
	
	public void buttonListener(){
		resumeButtonSpace.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				resume();
			}
		});
		
		restartButtonSpace.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.getScreen().dispose();
				
				if(GameConstants.SELECTED_LEVEL == 1 && !game.levelHandler.isLevelUnlocked(2))
					game.setScreen(new TutorialScreen(game));
				else
					game.setScreen(new GameScreen(game));
			
				stopMusic();
			}
		});
		
		homeButtonSpace.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.getScreen().dispose();
				game.setScreen(new MainMenuScreen(game));
				
				stopMusic();
			}
		});
		
		nextButtonSpace.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				GameConstants.SELECTED_LEVEL++;
				try{
					if(GameConstants.SELECTED_LEVEL == 24){
			        	System.out.println("Dernier niveau");
						game.assets.load("Images/Fin/Images_Fin.pack", TextureAtlas.class);
						game.assets.finishLoading();
			        }
					game.getScreen().dispose();
					game.setScreen(new GameScreen(game));
				}catch(Exception e){
					game.getScreen().dispose();
					game.setScreen(new HomeScreen(game));
				}
			}
		});
		
		pauseButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				pause();
			}
		});
		
		endGameButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.getScreen().dispose();
				game.setScreen(new EndScreen(game));
			}
		});
	}
	
	public Vector2 getPosOxygen(){
		return posOxygen.set(posXOxygen, posYOxygen);
	}
	
	public float getWidthOxygen(){
		return widthOxygen;
	}
	
	public float getHeightOxygen(){
		return heightOxygen;
	}
	
	public Vector2 getPosFuel(){
		return posFuel.set(posXOxygen, posYOxygen - 2 * heightOxygen);
	}
	
	public void stopMusic(){		
		for(int i = 0; i < game.musics.size; i++){
			game.musics.get(i).stop();
			game.musics.removeIndex(i);
		}
	}
	
	public void dispose(){
		Pools.free(posFuel);
		Pools.free(posOxygen);
		Pools.free(position);
		Pools.free(projectedPosition);
	}
}
