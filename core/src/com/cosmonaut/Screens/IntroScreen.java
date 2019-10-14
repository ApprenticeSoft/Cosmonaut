package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.Interlocutor;
import com.cosmonaut.Utils.TextBox;

public class IntroScreen implements Screen{

	final MyGdxGame game;
	private Stage stage;
	private Music musicIntro, spaceshipSound;
	private TextBox textBox;
	private Texture backgroundTexture;
	private float backgroundPosX = 0, spaceshipPosX = 0, spaceshipHeight, spaceshipWidth, alerteRougeTimer = 0, introTimer = 0;
	private Sound alarmSound, crashSound;
	private boolean alarmSoundPlay = false, musicPlay = false, crashSoundPlay = false, alarmText = false, vuePerso = false, transition = false;
	private Image transitionImage;
	private float transitionAlpha = 0, spaceshipSoundVolume = 0, musicIntroVolume = 0f, labelIntroAlpha = 0f;
	private int introStep = 1;
	private long soundId;
	private Label labelIntro;
	private LabelStyle labelStyle;
	private Vector3 colorFlicker;
	
	private Array<Interlocutor> interlocutors;
	
	/*
	 * Nouvelle version (18-02-2017)
	 */
	private Skin skin;
	private TextureAtlas atlas;
	private Texture map, textureTest;
	
	/*******************SHADERS**********************/
    String vertexShader;
    String fragmentShader;
    ShaderProgram flickerShaderProgram, alphaShaderProgram, colorReplacementProgram, vignetteProgram;
    float shaderTime = 0;
	
	public IntroScreen(final MyGdxGame game){
		this.game= game;
		Gdx.input.setCursorCatched(true);
		
		musicIntro = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Jazz.ogg"));
		musicIntro.setVolume(musicIntroVolume);
		//musicIntro.play();
		
		crashSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Crash.ogg"));
		
		alarmSound = game.assets.get("Sounds/Alarm.ogg", Sound.class);
		spaceshipSound = game.assets.get("Sounds/Background.ogg", Music.class);
		spaceshipSound.play();
		spaceshipSound.setLooping(true);
		spaceshipSound.setVolume(spaceshipSoundVolume);
		
		atlas = game.assets.get("Images/Intro/Images_Intro.pack", TextureAtlas.class);
		skin = new Skin(atlas);
		stage = new Stage();
		
		//Label
		labelStyle = new LabelStyle(game.assets.get("fontDialogue.ttf", BitmapFont.class), Color.WHITE);
		labelIntro = new Label(game.text.get("Somewhere"), labelStyle);
		labelIntro.setX(0.08f * Gdx.graphics.getWidth());
		labelIntro.setY(Gdx.graphics.getHeight() - labelIntro.getPrefHeight() - labelIntro.getX());
		labelIntro.setColor(1, 1, 1, labelIntroAlpha);
		stage.addActor(labelIntro);
		
		//Background
		backgroundTexture = new Texture(Gdx.files.internal("Images/Space.jpg"), true);
		backgroundTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.MipMapLinearNearest);
		
		//Spaceship
		//spaceshipHeight = 0.19f * skin.getRegion("Spaceship").getRegionHeight();
		spaceshipHeight = 0.32f * Gdx.graphics.getHeight();
		spaceshipWidth = spaceshipHeight * skin.getRegion("Vaisseau_Mere").getRegionWidth()/skin.getRegion("Vaisseau_Mere").getRegionHeight();
		spaceshipPosX = -spaceshipHeight * skin.getRegion("Vaisseau_Mere").getRegionWidth()/skin.getRegion("Vaisseau_Mere").getRegionHeight();
		
		//Transition
		transitionImage = new Image(game.skin.getDrawable("WhiteSquare"));
		transitionImage.setWidth(Gdx.graphics.getWidth());
		transitionImage.setHeight(Gdx.graphics.getHeight());
		transitionImage.setColor(0,0,0,1);
		transitionImage.setX(0);
		transitionImage.setY(0);
		transitionImage.addAction(Actions.alpha(transitionAlpha));
		stage.addActor(transitionImage);
					
		textBox = new TextBox(game, stage, "Texts/" + GameConstants.GAME_VERSION + "/" + Data.getLanguage() + "/Dialogue.txt", "\n", ";");
		textBox.setLabelPos(Gdx.graphics.getWidth()/2 - textBox.getTextBoxWidth()/2, 2*Gdx.graphics.getHeight()/3);
		
		interlocutors = new Array<Interlocutor>();
		Interlocutor groundControl = new Interlocutor("Control", Gdx.graphics.getWidth()/20, 3*Gdx.graphics.getHeight()/4);
		Interlocutor tom = new Interlocutor("Tom", 19*Gdx.graphics.getWidth()/20 - Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/4);
		Interlocutor alarm = new Interlocutor("Alarm", Gdx.graphics.getWidth()/2 - textBox.getTextBoxWidth()/2, 2*Gdx.graphics.getHeight()/3);
		interlocutors.add(groundControl);
		interlocutors.add(tom);
		interlocutors.add(alarm);
		
		/*******************SHADERS**********************/
		ShaderProgram.pedantic = false;	//Important pour pouvoir modifier les variables uniformes
      	vertexShader = Gdx.files.internal("Shaders/PassThrough-vertex.glsl").readString();
      	fragmentShader = Gdx.files.internal("Shaders/Flicker-fragment.glsl").readString();
      	flickerShaderProgram = new ShaderProgram(vertexShader,fragmentShader);
      	System.out.println("Flicker Shader log : " + flickerShaderProgram.getLog());
      	game.batch.setShader(flickerShaderProgram);
      	game.batch.setColor(1, 1, 1, 1);

      	colorFlicker = Pools.obtain(Vector3.class).set(MathUtils.random(0.78f, 1),	MathUtils.random(0.12f, 0.51f),	MathUtils.random(0, 0.16f));
      	flickerShaderProgram.begin();
      	flickerShaderProgram.setUniformf("u_color", colorFlicker);
      	flickerShaderProgram.end();
      	
      	
      	alphaShaderProgram = new ShaderProgram(vertexShader,Gdx.files.internal("Shaders/AlphaMask-fragment.glsl").readString());
		map = new Texture(Gdx.files.internal("Images/SmokeMask.png")); 
		textureTest = new Texture(Gdx.files.internal("Images/Smoke.png"));  
      	System.out.println("Alpha Shader log : " + alphaShaderProgram.getLog());
      	
      	Vector3 colorBlack = Pools.obtain(Vector3.class).set(0, 0, 0);
      	colorReplacementProgram = new ShaderProgram(vertexShader,Gdx.files.internal("Shaders/ColorReplacement-fragment.glsl").readString()); 
      	colorReplacementProgram.begin();
      	colorReplacementProgram.setUniformf("u_output_color", colorBlack);
      	Pools.free(colorBlack);
      	System.out.println("Color Shader log : " + colorReplacementProgram.getLog());
   
      	vignetteProgram = new ShaderProgram(Gdx.files.internal("Shaders/Vignette-vertex.glsl").readString(), Gdx.files.internal("Shaders/Vignette-fragment.glsl").readString());
      	System.out.println("Vignette Shader log : " + vignetteProgram.getLog());

      	vignetteProgram.begin();
      	vignetteProgram.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      	vignetteProgram.setUniformf("u_PosX", 0.38f);
      	vignetteProgram.setUniformf("u_PosY", 0.5f);
      	vignetteProgram.setUniformf("outerRadius", 0.75f);
      	vignetteProgram.setUniformf("innerRadius", 0.12f);
      	vignetteProgram.setUniformf("intensity", 0.5f);
      	vignetteProgram.end();
      	
      	if(Gdx.app.getType() == ApplicationType.Android){
			game.assets.load("Images/Animations/Controls_Animation.pack", TextureAtlas.class);
			
			//if(GameConstants.SELECTED_LEVEL == 1 && !game.levelHandler.isLevelUnlocked(2)){
				game.assets.load("Images/Animations/Rotation_Control_Animation.pack", TextureAtlas.class);
				game.assets.load("Images/Animations/Jetpack_Control_Animation.pack", TextureAtlas.class);
			//}
		}
	}
		
	@Override
	public void render(float delta) {	
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
			dispose();
			game.setScreen(new MainMenuScreen(game));
		}
		
		game.assets.update();
		
		introTimer += Gdx.graphics.getDeltaTime();
		
		//If the 1st level is complete, don`t play the intro anymore
		/*
		if(game.levelHandler.isLevelUnlocked(2)){
			game.getScreen().dispose();
			if(GameConstants.SELECTED_LEVEL == 1 && !game.levelHandler.isLevelUnlocked(2))
				game.setScreen(new TutorialScreen(game));
			else
				game.setScreen(new GameScreen(game));
		}	
		*/
		//À enlever avant publication du jeu
		//game.getScreen().dispose();
		//game.setScreen(new TutorialScreen(game));
		
		/*******************Dialogue with timer********************/

	    textBox.dialogueTimer(interlocutors);		    
	    if(introStep == 1){
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			backgroundPosX -= 20 * Gdx.graphics.getDeltaTime();
			
			//Sons
			if(introTimer < 15){	
				if(introTimer > 7){
					labelIntro.setColor(1, 1, 1, labelIntroAlpha = MathUtils.clamp(labelIntroAlpha -= 0.5f * Gdx.graphics.getDeltaTime(), 0, 1));
					spaceshipSound.setVolume(spaceshipSoundVolume += 0.07f * Gdx.graphics.getDeltaTime());
					
				}
				else if(introTimer > 2){
					labelIntro.setColor(1, 1, 1, labelIntroAlpha = MathUtils.clamp(labelIntroAlpha += 0.5f * Gdx.graphics.getDeltaTime(), 0, 1));
				}
			}
			else if(introTimer > 25){
				if(!musicPlay){
					musicPlay = true;
					musicIntro.play();
				}
				
				if(musicIntroVolume < 0.6f)
					musicIntro.setVolume(musicIntroVolume += 0.05f * Gdx.graphics.getDeltaTime());
				
				spaceshipSound.setVolume(spaceshipSoundVolume -= 0.045f * Gdx.graphics.getDeltaTime());
			}
			
			//Images
		    game.batch.begin();	    
		    for(int i = 0; i < 3; i++){
			    game.batch.draw(backgroundTexture, 
			    				backgroundPosX + i * Gdx.graphics.getHeight()*backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
			    				0, 
			    				Gdx.graphics.getHeight() * backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
			    				Gdx.graphics.getHeight());
			}  
		    
		    game.batch.end();
		    //Vaisseau
		    if(!vuePerso){	
			    game.batch.begin();
			    if(introTimer > 9)
			    game.batch.draw(skin.getRegion("Vaisseau_Mere"), 
			    				spaceshipPosX += 28 * Gdx.graphics.getDeltaTime(), 
			    				0.4f*Gdx.graphics.getHeight() - 0.5f*spaceshipHeight, 
			    				spaceshipWidth, 
			    				spaceshipHeight);
			    game.batch.setShader(colorReplacementProgram);
			    game.batch.draw(skin.getRegion("Vaisseau_Survie"), 
			    				spaceshipPosX + 0.65f*spaceshipWidth, 
			    				0.4f*Gdx.graphics.getHeight() + 0.31f*spaceshipHeight, 
			    				0.25f*spaceshipHeight * skin.getRegion("Vaisseau_Survie").getRegionWidth()/skin.getRegion("Vaisseau_Survie").getRegionHeight(), 
			    				0.25f*spaceshipHeight);
			    game.batch.setShader(null);
			    game.batch.end();

			    if(introTimer > 50.2f){
			    	vuePerso = true;
			    	transitionImage.addAction(Actions.alpha(0, 0.5f));
			    }
			    else if(introTimer > 49){
			    	if(!transition){
			    		transitionImage.addAction(Actions.alpha(1, 1f));
			    		transition = true;
			    	}
			    }
		    }
		    //Personnage
		    else{
		  		map.bind(1);
		  		textureTest.bind(0);
		  		
		  		alphaShaderProgram.begin();
		  		alphaShaderProgram.setUniformf("u_time", shaderTime += Gdx.graphics.getDeltaTime());
		  		alphaShaderProgram.setUniformi("u_mask", 1);
		  		alphaShaderProgram.end();
		         
		  		colorFlicker.set(MathUtils.random(0.78f, 1), MathUtils.random(0.12f, 0.51f), MathUtils.random(0, 0.16f));
		    	flickerShaderProgram.begin();
		      	flickerShaderProgram.setUniformf("u_color", colorFlicker);
		      	flickerShaderProgram.end();
		      	
		    	game.batch.begin();
		      	//Mur avec hublot
		    	game.batch.setShader(vignetteProgram);
		    	game.batch.draw(skin.getRegion("Intro_Mur"),
		    					0, 
		    					Gdx.graphics.getHeight()/2 - 0.5f*Gdx.graphics.getWidth() * skin.getRegion("Intro_Mur").getRegionHeight()/skin.getRegion("Intro_Mur").getRegionWidth(), 
		    					Gdx.graphics.getWidth(), 
		    					Gdx.graphics.getWidth() * skin.getRegion("Intro_Mur").getRegionHeight()/skin.getRegion("Intro_Mur").getRegionWidth());
		    	//Fumée
		  		game.batch.setShader(alphaShaderProgram);
			    game.batch.draw(textureTest, 
	    						0.3968f*Gdx.graphics.getWidth(),  
			    				0.539f*Gdx.graphics.getHeight(), 
			    				0.05f*Gdx.graphics.getWidth(), 
			    				0.07f*Gdx.graphics.getWidth() * textureTest.getHeight()/textureTest.getWidth());
			    game.batch.setShader(flickerShaderProgram);
		    	//Personnage
		    	game.batch.draw(skin.getRegion("Intro_Personnage_Gauche"),
		    					0.42f * Gdx.graphics.getWidth() + 0.78f * Gdx.graphics.getHeight() * skin.getRegion("Intro_Personnage_Gauche").getRegionWidth()/skin.getRegion("Intro_Personnage_Gauche").getRegionHeight(), 
		    					0, 
		    					-0.78f * Gdx.graphics.getHeight() * skin.getRegion("Intro_Personnage_Gauche").getRegionWidth()/skin.getRegion("Intro_Personnage_Gauche").getRegionHeight(), 
		    					0.78f * Gdx.graphics.getHeight());	
		    	game.batch.end();
		    }

		    //Dialogue
		    if(!textBox.write && /*textBox.getTimer() > 27*/ musicIntro.getVolume() > 0.35f){
		    	textBox.setTimer(0);
		    	textBox.writeDialogue();
		    }
		    
		    if(textBox.dialogueFinished){
		    	transitionAlpha += Gdx.graphics.getDeltaTime();
		    	transitionImage.addAction(Actions.alpha(1, 1.2f));
		    	
		    	if(transitionAlpha > 2.5 && !crashSoundPlay){
		    		crashSoundPlay = true;
		    		crashSound.play();
		    	} 
		    	
		    	if(transitionAlpha > 8.5f){
		    		transitionAlpha = 1;
		    		introStep = 2;
		    		spaceshipSound.stop();
		    	}
		    }
	    }
	    else if(introStep == 2){
	    	//Arret de la musique d'ambiance
			musicIntro.stop();
			transitionImage.addAction(Actions.alpha(0));
			
	    	//Alarme image
			Gdx.gl.glClearColor((float)(1 + MathUtils.cos(alerteRougeTimer += 1.8f*Gdx.graphics.getDeltaTime()))/(4.0f*transitionAlpha), 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			//Alarm sound
			if(MathUtils.cos(alerteRougeTimer += 1.8f*Gdx.graphics.getDeltaTime()) > 0.9f){
				if(!alarmSoundPlay){
					alarmSoundPlay = true;
					soundId = alarmSound.play();
				}
			}
			else if(MathUtils.cos(alerteRougeTimer += 1.8f*Gdx.graphics.getDeltaTime()) < 0)
				alarmSoundPlay = false;
			
			if(!alarmText){
				alarmText = true;
				textBox.newTextFile("Texts/" + GameConstants.GAME_VERSION + "/" + Data.getLanguage() + "/Alarm.txt");
				textBox.setLabelPos(Gdx.graphics.getWidth()/2 - textBox.getTextBoxWidth()/2, 2*Gdx.graphics.getHeight()/3);
				textBox.setBaseTimeLimit(1.8f);
				textBox.setFactorTimeLimit(0.04f);
			}
			if(!textBox.write){
		    	textBox.setTimer(0);
		    	textBox.writeDialogue();
		    }
			
			//Atténuation de l'alarm/Transition
		    if(textBox.dialogueFinished){
		    	transitionAlpha += Gdx.graphics.getDeltaTime();
		    	alarmSound.setVolume(soundId, 1/transitionAlpha);
		    	
		    	if(transitionAlpha > 8f)
		    		introStep = 3;
		    }
		}	
	    else{
	    	if(game.assets.update()){
	    		Data.setIntroPlayed(true);
				game.getScreen().dispose();

				if(GameConstants.PLAY_INTRO)
					game.setScreen(new MainMenuScreen(game));
				else if(game.levelHandler.isLevelUnlocked(2))
					game.setScreen(new GameScreen(game));
				else
					game.setScreen(new TutorialScreen(game));
	
				game.assets.unload("Images/Intro/Images_Intro.pack");
	    	}	
	    }
	    
	    /**********************************************************/
	    stage.act();
	    stage.draw();	
	}

	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		Pools.free(colorFlicker);
		textBox.dispose();
		for(int i = 0; i < interlocutors.size; i++)
			interlocutors.get(i).dispose();
		
		musicIntro.dispose();
		alarmSound.dispose();
		spaceshipSound.dispose();
		stage.dispose();
		System.gc();
	}

}
