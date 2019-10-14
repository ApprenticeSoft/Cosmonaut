package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Bodies.CheckPoint;
import com.cosmonaut.Bodies.Dialogue;
import com.cosmonaut.Bodies.ItemSwitch;
import com.cosmonaut.Items.Item;
import com.cosmonaut.Items.Upgrade;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.TextBox;

public class TutorialScreen extends GameScreen{

	private TextBox tutoTextBox;
	private boolean /*tutorialOn = false,*/ fuelWarning = false, oxygenWarning = false, directionArrow = false;
	private int tutoStep = 1, previousTutoStep = 0;
	private float tutoCountDown;
	private Vector2 tutoCoordinate;
	private Animation rotationControlAnimation, jetpackControlAnimation, directionAnimation;;
	private float animationTime = 0, arrowAnimationTime = 0, spriteDirectionWidth, spriteDirectionHeight;
	
	//Touch controls
	public Image imageTouchSurface;
	public float touchSurfaceAlpha = 0f;
	
	public TutorialScreen(MyGdxGame game) {
		super(game);
        
    	if(Data.getGameControls() == 3){
    		hud.buttonJetPack.setVisible(false);
    		hud.buttonLeft.setVisible(false);
    		hud.buttonRight.setVisible(false);
    	}
    	else if(Data.getGameControls() == 4 || Data.getGameControls() == 12){
    		imageTouchSurface = new Image(game.skin.getDrawable("WhiteSquare"));
    		imageTouchSurface.setColor(1,1,1,touchSurfaceAlpha);
    		imageTouchSurface.setWidth(Gdx.graphics.getWidth()/2);
    		imageTouchSurface.setHeight(Gdx.graphics.getHeight());
    		imageTouchSurface.setX(0);
    		imageTouchSurface.setY(0);
    		imageTouchSurface.setTouchable(Touchable.disabled);

			stage.addActor(imageTouchSurface);
    	}
		
		tutoTextBox = new TextBox(game, stage, "Texts/" + GameConstants.GAME_VERSION + "/"  + Data.getLanguage() + "/Tutorial.txt", "\n", ";");
		tutoTextBox.setLabelPos(Gdx.graphics.getWidth()/2 - tutoTextBox.getTextBoxWidth()/2, Gdx.graphics.getHeight()/2 - tutoTextBox.getTextBoxHeight()/2);
        tutoTextBox.touchControl = false;
        tutoTextBox.timeControl = true;
        tutoCoordinate = Pools.obtain(Vector2.class).set(0,0);
        
        if(Gdx.app.getType() == ApplicationType.Android){
    		rotationControlAnimation = new Animation(0.035f, game.assets.get("Images/Animations/Rotation_Control_Animation.pack", TextureAtlas.class).findRegions("Rotation_Control"), Animation.PlayMode.LOOP);
    		jetpackControlAnimation = new Animation(0.04f, game.assets.get("Images/Animations/Jetpack_Control_Animation.pack", TextureAtlas.class).findRegions("Jetpack_Control"), Animation.PlayMode.LOOP);
        }
        
        directionAnimation = new Animation(0.04f, game.assets.get("Images/Animations/Fleche_Animation.pack", TextureAtlas.class).findRegions("Fleche_Animation"), Animation.PlayMode.NORMAL);

        spriteDirectionWidth = mapReader.hero.spriteWidth/2;
        spriteDirectionHeight = spriteDirectionWidth * directionAnimation.getKeyFrame(0, true).getRegionHeight() / directionAnimation.getKeyFrame(0, true).getRegionWidth();
        
	}
	
	//@Override
	public void render(float delta) { 
		super.render(delta);
		
		if(!GameConstants.GAME_PAUSED){
			if(directionArrow){
				arrowAnimationTime += Gdx.graphics.getDeltaTime();
				if(arrowAnimationTime > 3)
					arrowAnimationTime = 0;
				
				game.batch.begin();
				game.batch.setColor(14/256f, 110/256f, 1, 0.6f);
				game.batch.draw(directionAnimation.getKeyFrame(arrowAnimationTime), 
								mapReader.hero.heroBody.getPosition().x - .45f*mapReader.hero.spriteWidth, 
								mapReader.hero.heroBody.getPosition().y - 0.25f*mapReader.hero.spriteHeight,
								.45f*mapReader.hero.spriteWidth, 
								0.25f*mapReader.hero.spriteHeight,
								spriteDirectionWidth, 
								spriteDirectionHeight,
								1,
								1,
								mapReader.hero.heroBody.getAngle()*MathUtils.radiansToDegrees);
				game.batch.setColor(1, 1, 1, 1);
				game.batch.end();
			}
			else
				arrowAnimationTime = 0;
		}
			
		if(Data.getGameControls() == 4){
			if(tutoStep == 3){
				animationTime += Gdx.graphics.getDeltaTime();
				game.batch.begin();
				game.batch.setColor(1, 1, 1, 1);
				game.batch.draw(rotationControlAnimation.getKeyFrame(animationTime), 
								camera.position.x - camera.viewportWidth/4 - GameConstants.MPP*rotationControlAnimation.getKeyFrame(0, true).getRegionWidth()/2, 
								camera.position.y, 
								0.18f*GameConstants.MPP*Gdx.graphics.getWidth(),
								0.18f*GameConstants.MPP*Gdx.graphics.getWidth() * rotationControlAnimation.getKeyFrame(0, true).getRegionHeight() / rotationControlAnimation.getKeyFrame(0, true).getRegionWidth());	 
		    	game.batch.end();
			}
			else if(tutoStep == 5){
				animationTime += Gdx.graphics.getDeltaTime();
				game.batch.begin();
				game.batch.setColor(1, 1, 1, 1);
				game.batch.draw(jetpackControlAnimation.getKeyFrame(animationTime), 
								camera.position.x + camera.viewportWidth/4 - GameConstants.MPP*jetpackControlAnimation.getKeyFrame(0, true).getRegionWidth()/2, 
								camera.position.y, 
								0.15f*GameConstants.MPP*Gdx.graphics.getWidth(),
								0.15f*GameConstants.MPP*Gdx.graphics.getWidth() * jetpackControlAnimation.getKeyFrame(0, true).getRegionHeight() / jetpackControlAnimation.getKeyFrame(0, true).getRegionWidth());	 
		    	game.batch.end();
			}
		}
		
		if(mapReader.hero.getFuelLevel() <= 0)
			tutoStep = 16;
		else if(mapReader.hero.getFuelLevel() <= GameConstants.MAX_FUEL/2){
			if(!fuelWarning){
				fuelWarning = true;
				tutoStep = 14;
			}
		}

		if(mapReader.hero.getOxygenLevel() <= 0)
			tutoStep = 16;
		else if(mapReader.hero.getOxygenLevel() <= GameConstants.MAX_OXYGEN/2)
			if(!oxygenWarning){
				oxygenWarning = true;
				tutoStep = 15;
			}

	
	}
	
	@Override
	public void show() {
		inputMultiplexer.addProcessor(new GestureDetector(gestureListener));
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
		hud.buttonListener();
		
		world.setContactListener(new ContactListener(){
			@Override
			public void beginContact(Contact contact) {
				/*Fixture*/ fixtureA = contact.getFixtureA();
				/*Fixture*/ fixtureB = contact.getFixtureB();
			    
			    if(fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
			    	//Finish the level
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Exit")){
			    		mapReader.exit.open = true;
			    		mapReader.exit.heroContact = true;
			    	}    		
			    	else if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Exit")){
			    		mapReader.exit.open = true;
			    		mapReader.exit.heroContact = true;
			    	}    
				    
				    //Switch
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Switch")){
			    		for(ItemSwitch itemSwitch : game.switchs){
			    			if(itemSwitch.switchBody == fixtureB.getBody())
			    				itemSwitch.active(game.activableObstacles);
			    		}
			    	}
			    	else if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Switch")){
			    		for(ItemSwitch itemSwitch : game.switchs){
			    			if(itemSwitch.switchBody == fixtureA.getBody())
			    				itemSwitch.active(game.activableObstacles);
			    		}
			    	}
				    
			    	//Items
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Item")){
			    		for(Item item : game.items){
			    			if(item.body == fixtureB.getBody())
			    				item.activate();
			    		}
			    	}
			    	if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Item")){
			    		for(Item item : game.items){
			    			if(item.body == fixtureA.getBody())
			    				item.activate();
			    		}
			    	}
			    	
			    	//Dialogue
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Dialogue")){
				    	for(Dialogue dialogue : game.dialogues){
				    		if(dialogue.body.getFixtureList().get(0) == fixtureB){
				    			if(!dialogue.used)
				    				tutoStep = dialogue.getReferenceNumber();
				    			dialogue.activate();
				    		}
				    	}
					} 
			    	if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Dialogue")){
			    		for(Dialogue dialogue : game.dialogues){
				    		if(dialogue.body.getFixtureList().get(0) == fixtureA){
				    			if(!dialogue.used)
				    				tutoStep = dialogue.getReferenceNumber();
				    			dialogue.activate();
				    		}
				    	}
					}
			    	
			    	//Checkpoint
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Checkpoint")){
				    	for(CheckPoint checkpoint : game.checkpoints){
				    		if(checkpoint.body.getFixtureList().get(0) == fixtureB)
				    			checkpoint.activate();
				    	}
					} 
			    	if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Checkpoint")){
			    		for(CheckPoint checkpoint : game.checkpoints){
				    		if(checkpoint.body.getFixtureList().get(0) == fixtureB)
				    			checkpoint.activate();
				    	}
					}
				}  
			}

			@Override
			public void endContact(Contact contact) {
				/*Fixture*/ fixtureA = contact.getFixtureA();
				/*Fixture*/ fixtureB = contact.getFixtureB();
				
				if(fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
			    	//Finish the level
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Exit")){
			    		mapReader.exit.heroContact = false;
			    	}    		
			    	else if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Exit")){
			    		mapReader.exit.heroContact = false;
			    	}  
				}
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				/*Fixture*/ fixtureA = contact.getFixtureA();
				/*Fixture*/ fixtureB = contact.getFixtureB();
				
				if((fixtureA.getUserData() != null && fixtureA.getUserData().equals("Obstacle")) && (fixtureB.getUserData() != null && fixtureB.getUserData().equals("Obstacle"))) {
			    	contact.setEnabled(false);
				}		
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		});
	}
	
	@Override
	public void cameraActivity(){
		camera.displacement(mapReader.hero, mapReader, tiledMap);
		tutorial();
        camera.update();    
	}

	public void tutorial(){
		if(GameConstants.GAME_LOST || GameConstants.GAME_PAUSED)
			tutoTextBox.setVisible(false);
		else
			tutoTextBox.setVisible(true);
		
		if(tutoStep == 0){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				tutoTextBox.initiate();
				tutoTextBox.resizeBox();
				tutoTextBox.dialogueFinished();
				tutoTextBox.setBaseTimeLimit(1.2f);
				previousTutoStep = tutoStep;
				
				if(Data.getGameControls() == 3){							//Android touch controls
					hud.buttonJetPack.setTouchable(Touchable.enabled);
					hud.buttonLeft.setTouchable(Touchable.enabled);
					hud.buttonRight.setTouchable(Touchable.enabled);
				}
			}
			
			GameConstants.TUTORIAL = false;
			mapReader.hero.setFuelLevel(GameConstants.MAX_FUEL);
			mapReader.hero.setOxygenLevel(GameConstants.MAX_OXYGEN);
		}
		/*
		 * Text d'introduction
		 */
		else if(tutoStep == 1){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				previousTutoStep = tutoStep;
				tutoTextBox.setLabelPos(0.7f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.6f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
			}
			
			GameConstants.TUTORIAL = true;
			tutoTextBox.dialogueFinished = false;
			tutoTextBox.writeParagraph(1);
			if(tutoTextBox.dialogueFinished){
				tutoStep = 2;
				tutoTextBox.dialogueFinished = false;
			}
		}
		/*
		 * Test de la combinaison
		 */
		else if (tutoStep == 2){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				previousTutoStep = tutoStep;
			}
			
			tutoTextBox.dialogueFinished = false;
			tutoTextBox.writeLine(2,1);
			
			if(tutoTextBox.dialogueFinished){
				tutoStep = 3;
				tutoTextBox.dialogueFinished = false;
			}
		}	
		/*
		 * Test de rotation
		 */
		else if (tutoStep == 3){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				tutoCountDown = 0.9f;
				tutoTextBox.timeControl = false;
				tutoTextBox.touchControl = false;
				previousTutoStep = tutoStep;
			}
			
			if(Data.getGameControls() == 1){					//Keyboard controls
				tutoTextBox.dialogueFinished = false;
				tutoTextBox.writeLine(2,2);
				
				if(Gdx.input.isKeyPressed(Keys.W)){
					GameConstants.TUTORIAL = true;
					mapReader.hero.jetpackOff();
				}
				else if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.D)){
					tutoCountDown -= Gdx.graphics.getDeltaTime();
					GameConstants.TUTORIAL = false;
					
					if(tutoCountDown < 0){
						mapReader.hero.stopRotating();
						tutoTextBox.initiate();
						tutoTextBox.resizeBox();
						tutoTextBox.dialogueFinished();
						tutoStep = 4;
						tutoTextBox.dialogueFinished = false;
					}
				}
			}
			else if(Data.getGameControls() == 2){				//Game pad controls
				mapReader.hero.stopRotating();
				tutoTextBox.initiate();
				tutoTextBox.resizeBox();
				tutoTextBox.dialogueFinished();
				tutoStep = 4;
				tutoTextBox.dialogueFinished = false;
			}
			else if(Data.getGameControls() == 3){				//Android bouton controls
				tutoTextBox.dialogueFinished = false;
				tutoTextBox.writeLine(2,6);
				tutoTextBox.setLabelPos(hud.buttonRight.getX() + 1.2f*hud.buttonRight.getWidth(), hud.buttonRight.getY() + hud.buttonRight.getHeight()/2 + tutoTextBox.getTextBoxHeight()/2);
				hud.buttonLeft.setVisible(true);
				hud.buttonRight.setVisible(true);
				hud.leftRightButtonPulse();
				
				if(hud.buttonRight.isOver() || hud.buttonLeft.isOver()){
					tutoCountDown -= Gdx.graphics.getDeltaTime();
					GameConstants.TUTORIAL = false;
					
					if(tutoCountDown < 0){
						mapReader.hero.stopRotating();
						hud.buttonRight.setTouchable(Touchable.disabled);
						hud.buttonLeft.setTouchable(Touchable.disabled);
						hud.buttonLeft.addAction(Actions.alpha(Data.getControlOpacity()));
						hud.buttonRight.addAction(Actions.alpha(Data.getControlOpacity()));
						tutoTextBox.initiate();
						tutoTextBox.resizeBox();
						tutoTextBox.dialogueFinished();
						tutoStep = 4;
						tutoTextBox.dialogueFinished = false;
					}
				}
			}
			else if(Data.getGameControls() == 4){				//Android touch controls
				tutoTextBox.dialogueFinished = false;
				tutoTextBox.writeLine(2,8);
				tutoTextBox.setLabelPos(0.5f*Gdx.graphics.getWidth() + tutoTextBox.getTextBoxWidth()/20, 0.5f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				
				touchSurfaceAlpha += 5f * Gdx.graphics.getDeltaTime();
				imageTouchSurface.addAction(Actions.alpha((float)(1 + MathUtils.cos(touchSurfaceAlpha))/10));
				
				if(Gdx.input.isTouched() && Gdx.input.getX() < Gdx.graphics.getWidth()/2){
					GameConstants.TUTORIAL = false;
					
					if(mapReader.hero.isRotating)
						tutoCountDown -= Gdx.graphics.getDeltaTime();
					
					if(tutoCountDown < 0){
						mapReader.hero.stopRotating();
						imageTouchSurface.addAction(Actions.alpha(0));
						tutoTextBox.initiate();
						tutoTextBox.resizeBox();
						tutoTextBox.dialogueFinished();
						tutoStep = 4;
						tutoTextBox.dialogueFinished = false;
					}
				}
			}
		}
		/*
		 * Test de rotation réussi
		 */
		else if (tutoStep == 4){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				mapReader.hero.stopRotating();
				tutoTextBox.setLabelPos(0.7f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.6f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				GameConstants.TUTORIAL = true;
				tutoTextBox.timeControl = true;
				previousTutoStep = tutoStep;
			}
			
			tutoTextBox.dialogueFinished = false;
			tutoTextBox.writeLine(2,3);
			
			if(tutoTextBox.dialogueFinished){
				tutoStep = 5;
				tutoTextBox.dialogueFinished = false;
			}
		}	
		/*
		 * Test du jetpack
		 */
		else if (tutoStep == 5){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				tutoCountDown = 0.5f;
				tutoTextBox.timeControl = false;
				tutoTextBox.touchControl = false;
				previousTutoStep = tutoStep;
			}
			
			if(Data.getGameControls() == 1){							//Keyboard controls
				tutoTextBox.dialogueFinished = false;
				tutoTextBox.writeLine(2,4);
				
				if(Gdx.input.isKeyPressed(Keys.W)){
					tutoCountDown -= Gdx.graphics.getDeltaTime();
					GameConstants.TUTORIAL = false;
					
					if(tutoCountDown < 0){
						tutoTextBox.initiate();
						tutoTextBox.resizeBox();
						tutoTextBox.dialogueFinished();
						tutoStep = 6;
						tutoTextBox.dialogueFinished = false;
					}
				}
			}
			else if(Data.getGameControls() == 2){						//Game pad controls
				tutoTextBox.initiate();
				tutoTextBox.resizeBox();
				tutoTextBox.dialogueFinished();
				tutoStep = 6;
				tutoTextBox.dialogueFinished = false;
			}
			else if(Data.getGameControls() == 3){						//Android boutons controls
				tutoTextBox.dialogueFinished = false;
				tutoTextBox.writeLine(2,7);		
				tutoTextBox.setLabelPos(hud.buttonJetPack.getX() - tutoTextBox.getTextBoxWidth(), hud.buttonRight.getY() + hud.buttonRight.getHeight()/2 + tutoTextBox.getTextBoxHeight()/2);
				hud.buttonJetPack.setVisible(true);
				hud.buttonRight.setTouchable(Touchable.enabled);
				hud.buttonLeft.setTouchable(Touchable.enabled);
				hud.jetpackButtonPulse();
				
				if(hud.buttonJetPack.isOver()){
					tutoCountDown -= Gdx.graphics.getDeltaTime();
					GameConstants.TUTORIAL = false;
					
					if(tutoCountDown < 0){
						hud.buttonJetPack.setTouchable(Touchable.disabled);
						hud.buttonJetPack.addAction(Actions.alpha(Data.getControlOpacity()));
						tutoTextBox.initiate();
						tutoTextBox.resizeBox();
						tutoTextBox.dialogueFinished();
						tutoStep = 6;
						tutoTextBox.dialogueFinished = false;
					}
				}
			}
			else if(Data.getGameControls() == 4){						//Android touch controls
				tutoTextBox.dialogueFinished = false;
				tutoTextBox.writeLine(2,9);
				tutoTextBox.setLabelPos(0.5f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth(), 0.8f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				
				touchSurfaceAlpha += 5f * Gdx.graphics.getDeltaTime();
				imageTouchSurface.addAction(Actions.alpha((float)(1 + MathUtils.cos(touchSurfaceAlpha))/10));
				imageTouchSurface.setX(Gdx.graphics.getWidth()/2);
				
				if(Gdx.input.isTouched()){
					GameConstants.TUTORIAL = false;
					
					if(mapReader.hero.isJetPackActive)
						tutoCountDown -= Gdx.graphics.getDeltaTime();
					
					if(tutoCountDown < 0){
						mapReader.hero.stopRotating();
						imageTouchSurface.addAction(Actions.alpha(0));
						tutoTextBox.initiate();
						tutoTextBox.resizeBox();
						tutoTextBox.dialogueFinished();
						tutoStep = 6;
						tutoTextBox.dialogueFinished = false;
					}
				}
			}
			
			
			mapReader.hero.setFuelLevel(GameConstants.MAX_FUEL);
			mapReader.hero.setOxygenLevel(GameConstants.MAX_OXYGEN);
		}
		/*
		 * Test du jetpack réussi
		 */
		else if (tutoStep == 6){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				tutoTextBox.timeControl = true;
				previousTutoStep = tutoStep;
			}
			
			tutoTextBox.dialogueFinished = false;
			tutoTextBox.writeLine(2,5);
			
			if(tutoTextBox.dialogueFinished){
				tutoStep = 7;
				tutoTextBox.dialogueFinished = false;
				tutoTextBox.initiate();
			}
			
			mapReader.hero.setFuelLevel(GameConstants.MAX_FUEL);
			mapReader.hero.setOxygenLevel(GameConstants.MAX_OXYGEN);
		}
		/*
		 * Porte de sortie
		 */
		else if (tutoStep == 7){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				GameConstants.TUTORIAL = true;
				mapReader.hero.stopRotating();
				mapReader.hero.jetpackOff();
				tutoTextBox.setBaseTimeLimit(8);
				tutoTextBox.setLabelPos(0.6f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.22f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				tutoCountDown = 9f;
				tutoTextBox.timeControl = true;
				previousTutoStep = tutoStep;
				
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.disabled);
					hud.buttonLeft.setTouchable(Touchable.disabled);
					hud.buttonRight.setTouchable(Touchable.disabled);
				}
			}
			
			tutoCountDown -= Gdx.graphics.getDeltaTime();
			
			if(tutoCountDown > 3){
				tutoTextBox.writeParagraph(3);
				camera.positionToCoordinate(mapReader.exit.getX(),mapReader.exit.getY(), tiledMap);
			}
			else if(tutoCountDown > 0){
				camera.positionToCoordinate(mapReader.hero.getX(),mapReader.hero.getY(), tiledMap);
				mapReader.hero.heroBody.setTransform(mapReader.hero.getOrigine(), 0);
				tutoTextBox.initiate();
				tutoTextBox.resizeBox();
				tutoTextBox.dialogueFinished();
			}
			else{
				tutoStep = 8;
				tutoTextBox.dialogueFinished = false;
			}
			
			mapReader.hero.setFuelLevel(GameConstants.MAX_FUEL);
			mapReader.hero.setOxygenLevel(GameConstants.MAX_OXYGEN);
		}
		/*
		 * Interrupteur
		 */
		else if (tutoStep == 8){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				GameConstants.TUTORIAL = true;
				mapReader.hero.stopRotating();
				tutoTextBox.setBaseTimeLimit(7);
				tutoTextBox.setLabelPos(0.6f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.22f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				tutoCountDown = 7f;
				tutoTextBox.timeControl = true;
				previousTutoStep = tutoStep;
				
				for(Dialogue dialogue : game.dialogues){
					if(dialogue.getReferenceNumber() == 17){
						tutoCoordinate.set(dialogue.getX(), dialogue.getY());
						System.out.println("tutoCoordinate = " + tutoCoordinate);
					}
				}
			}
			
			tutoCountDown -= Gdx.graphics.getDeltaTime();
			
			if(tutoCountDown > 2.3f){
				tutoTextBox.writeParagraph(4);
				camera.positionToCoordinate(tutoCoordinate, tiledMap);
				camera.zoomIn();
			}
			else if(tutoCountDown > 0){
				camera.positionToCoordinate(mapReader.hero.getX(),mapReader.hero.getY(), tiledMap);
				if(camera.viewportWidth < GameConstants.SCREEN_WIDTH){
					camera.zoomOut();
				}
				tutoTextBox.initiate();
				tutoTextBox.resizeBox();
				tutoTextBox.dialogueFinished();
			}
			else{
				tutoStep = 0;
				tutoTextBox.dialogueFinished = false;
			
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.enabled);
					hud.buttonLeft.setTouchable(Touchable.enabled);
					hud.buttonRight.setTouchable(Touchable.enabled);
				}
			}
			
			mapReader.hero.setFuelLevel(GameConstants.MAX_FUEL);
			mapReader.hero.setOxygenLevel(GameConstants.MAX_OXYGEN);
		}
		/*
		 * Fleche direction
		 */
		else if (tutoStep == 17){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				tutoTextBox.initiate();
				tutoTextBox.resizeBox();
				tutoTextBox.dialogueFinished();
				tutoTextBox.setBaseTimeLimit(1.8f);
				tutoTextBox.setLabelPos(0.5f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.3f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				previousTutoStep = tutoStep;
				tutoCountDown = 10f;
				tutoTextBox.dialogueFinished = false;
			}
			
			tutoCountDown -= Gdx.graphics.getDeltaTime();
			
			if(tutoCountDown < 7){
				if(!tutoTextBox.dialogueFinished){
					camera.zoomIn();
		
					if(Data.getGameControls() == 3){
						hud.buttonJetPack.setTouchable(Touchable.disabled);
						hud.buttonLeft.setTouchable(Touchable.disabled);
						hud.buttonRight.setTouchable(Touchable.disabled);
					}
				}
				mapReader.hero.stopRotating();
				mapReader.hero.jetpackOff();
				GameConstants.BOX_STEP = 0;
				GameConstants.TUTORIAL = true;
				
				if(camera.viewportWidth <= GameConstants.SCREEN_WIDTH/2){
					tutoTextBox.writeParagraph(15);
					if(tutoTextBox.posLine == 1)
						if(tutoTextBox.posChar == 30)
							directionArrow = true;
				}
				
				if(tutoTextBox.dialogueFinished){
					if(camera.viewportWidth < GameConstants.SCREEN_WIDTH){
						camera.zoomOut();
					}
					else{
						GameConstants.BOX_STEP = 1/60f;
						tutoStep = 0;
						tutoTextBox.dialogueFinished = false;
						
						if(Data.getGameControls() == 3){
							hud.buttonJetPack.setTouchable(Touchable.enabled);
							hud.buttonLeft.setTouchable(Touchable.enabled);
							hud.buttonRight.setTouchable(Touchable.enabled);
						}
					}
				}			
			}			
		}
		/*
		 * Fin de l'oxygène et du fuel illimité
		 */
		else if (tutoStep == 9){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				tutoTextBox.initiate();
				tutoTextBox.resizeBox();
				tutoTextBox.dialogueFinished();
				tutoTextBox.setBaseTimeLimit(1.2f);
				tutoTextBox.setLabelPos(0.5f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.5f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				previousTutoStep = tutoStep;
			}
			
			GameConstants.TUTORIAL = false;
		}
		/*
		 * Annonce de la fin de l'oxygène et du fuel illimité
		 */
		else if (tutoStep == 10){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				mapReader.hero.stopRotating();
				mapReader.hero.jetpackOff();
				GameConstants.TUTORIAL = true;
				tutoTextBox.timeControl = true;
				GameConstants.BOX_STEP = 0;
				previousTutoStep = tutoStep;
						
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.disabled);
					hud.buttonLeft.setTouchable(Touchable.disabled);
					hud.buttonRight.setTouchable(Touchable.disabled);
				}
			}
			
			switch (tutoTextBox.posLine){
			case 1 : 
				tutoTextBox.setLabelPos(0.5f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.5f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				break;
			case 2 :
				tutoTextBox.setLabelPos(hud.getPosOxygen().x + 1.1f*hud.getWidthOxygen(), hud.getPosOxygen().y);
				tutoTextBox.setFactorTimeLimit(0.065f);
				hud.imageOxygenPulse();
				break;
			case 3 :
				tutoTextBox.setLabelPos(hud.getPosOxygen().x + 1.1f*hud.getWidthOxygen(), hud.getPosOxygen().y);
				tutoTextBox.setFactorTimeLimit(0.085f);
				hud.imageFuelPulse();
				break;
			case 4 :
				tutoTextBox.setLabelPos(0.5f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.5f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				tutoTextBox.setFactorTimeLimit(0.037f);
				hud.resetAlpha();
				break;
			}
			
			tutoTextBox.dialogueFinished = false;
			tutoTextBox.writeParagraph(5);
			
			if(tutoTextBox.dialogueFinished){
				GameConstants.BOX_STEP = 1/60f;
				tutoStep = 9;
				tutoTextBox.dialogueFinished = false;
				
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.enabled);
					hud.buttonLeft.setTouchable(Touchable.enabled);
					hud.buttonRight.setTouchable(Touchable.enabled);
				}
			}
		}
		/*
		 * Upgrade
		 */
		else if (tutoStep == 11){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				GameConstants.TUTORIAL = true;
				mapReader.hero.stopRotating();
				mapReader.hero.jetpackOff();
				tutoTextBox.setLabelPos(0.5f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.35f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				tutoCountDown = 2f;
				tutoTextBox.timeControl = true;
				tutoTextBox.dialogueFinished = false;
				GameConstants.BOX_STEP = 0;
				previousTutoStep = tutoStep;
				
				for(Item item : game.items){
					if(item.getClass().toString().equals("class com.cosmonaut.Items.Upgrade")){
						Upgrade upgrade = (Upgrade) item;
						if(upgrade.getID() == 1){
							tutoCoordinate.set(upgrade.getX(), upgrade.getY());
						}
					}
				}
				
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.disabled);
					hud.buttonLeft.setTouchable(Touchable.disabled);
					hud.buttonRight.setTouchable(Touchable.disabled);
				}
			}
			
			if(!GameConstants.UPGRADE_1){
				if(!tutoTextBox.dialogueFinished){
					tutoTextBox.writeParagraph(6);
					camera.positionToCoordinate(tutoCoordinate, tiledMap);
					camera.zoomIn();
				}			
				else{
					tutoCountDown -= Gdx.graphics.getDeltaTime();
					camera.positionToCoordinate(mapReader.hero.getX(),mapReader.hero.getY(), tiledMap);
					if(camera.viewportWidth < GameConstants.SCREEN_WIDTH){
						camera.zoomOut();
					}
					tutoTextBox.initiate();
					tutoTextBox.resizeBox();
					tutoTextBox.dialogueFinished();
					
					if(tutoCountDown < 0){
						GameConstants.BOX_STEP = 1/60f;
						tutoStep = 9;
						tutoTextBox.dialogueFinished = false;
						
						if(Data.getGameControls() == 3){
							hud.buttonJetPack.setTouchable(Touchable.enabled);
							hud.buttonLeft.setTouchable(Touchable.enabled);
							hud.buttonRight.setTouchable(Touchable.enabled);
						}
					}
				}
			}
			else{
				GameConstants.BOX_STEP = 1/60f;
				tutoStep = 9;
				tutoTextBox.dialogueFinished = false;
				
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.enabled);
					hud.buttonLeft.setTouchable(Touchable.enabled);
					hud.buttonRight.setTouchable(Touchable.enabled);
				}
			}
		}
		/*
		 * Conseil économie de fuel. Propulsion sur les paroies
		 */
		else if (tutoStep == 12){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				tutoTextBox.timeControl = true;
				tutoTextBox.touchControl = false;
				previousTutoStep = tutoStep;
				mapReader.hero.heroBody.setLinearVelocity(0,0);
				
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.disabled);
					hud.buttonLeft.setTouchable(Touchable.disabled);
					hud.buttonRight.setTouchable(Touchable.disabled);
				}
			}
			
			switch (tutoTextBox.posLine){
			case 1 : 
				tutoTextBox.setBaseTimeLimit(1.5f);
				break;
			case 2 :
				tutoTextBox.setBaseTimeLimit(50);
				break;
			}
			
			tutoTextBox.dialogueFinished = false;
			
			if(Data.getGameControls() == 1){					//Keyboard controls
				tutoTextBox.writeParagraph(7);
				
				if(tutoTextBox.dialogueFinished = true){
					if(Gdx.input.isKeyPressed(Keys.W)){
						GameConstants.TUTORIAL = true;
						mapReader.hero.jetpackOff();
					}
					else if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.D)){
						GameConstants.TUTORIAL = false;
	
						tutoTextBox.initiate();
						tutoTextBox.resizeBox();
						tutoTextBox.dialogueFinished();
						tutoStep = 9;
						tutoTextBox.dialogueFinished = false;	
					}
				}
			}
			else if(Data.getGameControls() == 2){				//Game pad controls
				
			}
			else if(Data.getGameControls() == 3){				//Android bouton controls
				tutoTextBox.writeParagraph(9);
						
				if(tutoTextBox.posLine == 2){
					hud.buttonLeft.setTouchable(Touchable.enabled);
					hud.buttonRight.setTouchable(Touchable.enabled);
					hud.leftRightButtonPulse();
					
					if(hud.buttonRight.isOver() || hud.buttonLeft.isOver()){
						GameConstants.TUTORIAL = false;
	
						tutoTextBox.initiate();
						tutoTextBox.resizeBox();
						tutoTextBox.dialogueFinished();
						tutoStep = 9;
						tutoTextBox.dialogueFinished = false;	
						hud.buttonJetPack.setTouchable(Touchable.enabled);
						hud.buttonLeft.addAction(Actions.alpha(Data.getControlOpacity()));
						hud.buttonRight.addAction(Actions.alpha(Data.getControlOpacity()));
					}
				}
			}
			else if(Data.getGameControls() == 4){				//Android touch controls
				tutoTextBox.writeParagraph(10);
				touchSurfaceAlpha += 5f * Gdx.graphics.getDeltaTime();
				imageTouchSurface.addAction(Actions.alpha((float)(1 + MathUtils.cos(touchSurfaceAlpha))/10));
				imageTouchSurface.setX(0);
						
				if(tutoTextBox.posLine == 2){
					
					if(Gdx.input.isTouched() && Gdx.input.getX() < Gdx.graphics.getWidth()/2){
						GameConstants.TUTORIAL = false;

						imageTouchSurface.addAction(Actions.alpha(0));
						tutoTextBox.initiate();
						tutoTextBox.resizeBox();
						tutoTextBox.dialogueFinished();
						tutoStep = 9;
						tutoTextBox.dialogueFinished = false;	
					}
				}
			}
		}
		/*
		 * Oxygen et fuel refill
		 */
		else if (tutoStep == 13){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				GameConstants.TUTORIAL = true;
				mapReader.hero.stopRotating();
				mapReader.hero.jetpackOff();
				tutoTextBox.setLabelPos(0.5f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.5f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				tutoCountDown = 1.5f;
				GameConstants.BOX_STEP = 0;
				tutoTextBox.timeControl = true;
				tutoTextBox.dialogueFinished = false;
				previousTutoStep = tutoStep;
				
				for(Dialogue dialogue : game.dialogues){
					if(dialogue.getReferenceNumber() == 20){
						tutoCoordinate.set(dialogue.getX(), dialogue.getY());
						System.out.println("tutoCoordinate = " + tutoCoordinate);
					}
				}
				
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.disabled);
					hud.buttonLeft.setTouchable(Touchable.disabled);
					hud.buttonRight.setTouchable(Touchable.disabled);
				}
			}
			
			if(!tutoTextBox.dialogueFinished){
				tutoTextBox.writeParagraph(8);
				camera.positionToCoordinate(tutoCoordinate, tiledMap);
				camera.zoomIn();
			}			
			else{
				tutoCountDown -= Gdx.graphics.getDeltaTime();
				camera.positionToCoordinate(mapReader.hero.getX(),mapReader.hero.getY(), tiledMap);
				if(camera.viewportWidth < GameConstants.SCREEN_WIDTH){
					camera.zoomOut();
				}
				tutoTextBox.initiate();
				tutoTextBox.resizeBox();
				tutoTextBox.dialogueFinished();
				
				if(tutoCountDown < 0){
					GameConstants.BOX_STEP = 1/60f;
					tutoStep = 9;
					tutoTextBox.dialogueFinished = false;
					
					if(Data.getGameControls() == 3){
						hud.buttonJetPack.setTouchable(Touchable.enabled);
						hud.buttonLeft.setTouchable(Touchable.enabled);
						hud.buttonRight.setTouchable(Touchable.enabled);
					}
				}
			}
		}
		/*
		 * Si le carburant est à moitié vide
		 */
		else if (tutoStep == 14){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				GameConstants.TUTORIAL = true;
				mapReader.hero.stopRotating();
				mapReader.hero.jetpackOff();
				tutoTextBox.setLabelPos(hud.getPosOxygen().x + 1.1f*hud.getWidthOxygen(), hud.getPosOxygen().y);
				tutoTextBox.setFactorTimeLimit(0.085f);
				GameConstants.BOX_STEP = 0;
				previousTutoStep = tutoStep;
				
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.disabled);
					hud.buttonLeft.setTouchable(Touchable.disabled);
					hud.buttonRight.setTouchable(Touchable.disabled);
				}
			}
			
			tutoTextBox.dialogueFinished = false;
			hud.imageFuelPulse();
			tutoTextBox.writeParagraph(11);
			if(tutoTextBox.dialogueFinished){
				hud.resetAlpha();
				tutoStep = 9;
				tutoTextBox.dialogueFinished = false;
				GameConstants.BOX_STEP = 1/60f;

				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.enabled);
					hud.buttonLeft.setTouchable(Touchable.enabled);
					hud.buttonRight.setTouchable(Touchable.enabled);
				}
			}
		}
		/*
		 * Si l'oxygène est à moitié vide
		 */
		else if (tutoStep == 15){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				GameConstants.TUTORIAL = true;
				mapReader.hero.stopRotating();
				mapReader.hero.jetpackOff();
				tutoTextBox.setLabelPos(hud.getPosOxygen().x + 1.1f*hud.getWidthOxygen(), hud.getPosOxygen().y);
				tutoTextBox.setFactorTimeLimit(0.085f);
				GameConstants.BOX_STEP = 0;
				previousTutoStep = tutoStep;
				
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.disabled);
					hud.buttonLeft.setTouchable(Touchable.disabled);
					hud.buttonRight.setTouchable(Touchable.disabled);
				}
			}
			
			tutoTextBox.dialogueFinished = false;
			hud.imageOxygenPulse();
			tutoTextBox.writeParagraph(12);
			if(tutoTextBox.dialogueFinished){
				hud.resetAlpha();
				tutoStep = 9;
				tutoTextBox.dialogueFinished = false;
				GameConstants.BOX_STEP = 1/60f;

				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.enabled);
					hud.buttonLeft.setTouchable(Touchable.enabled);
					hud.buttonRight.setTouchable(Touchable.enabled);
				}
			}
		}
		/*
		 * Si le fuel est vide
		 */
		else if (tutoStep == 16){
			if(previousTutoStep != tutoStep){
				System.out.println("tutoStep = " + tutoStep);
				GameConstants.TUTORIAL = true;
				mapReader.hero.stopRotating();
				mapReader.hero.jetpackOff();
				tutoTextBox.setLabelPos(0.5f*Gdx.graphics.getWidth() - tutoTextBox.getTextBoxWidth()/2, 0.5f*Gdx.graphics.getHeight() - tutoTextBox.getTextBoxHeight()/2);
				tutoTextBox.setFactorTimeLimit(0.085f);
				GameConstants.BOX_STEP = 0;
				previousTutoStep = tutoStep;
				
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.disabled);
					hud.buttonLeft.setTouchable(Touchable.disabled);
					hud.buttonRight.setTouchable(Touchable.disabled);
				}
			}
			
			tutoTextBox.dialogueFinished = false;
			if(mapReader.hero.getFuelLevel() <= 0)
				tutoTextBox.writeParagraph(13);
			else if(mapReader.hero.getOxygenLevel() <= 0)
					tutoTextBox.writeParagraph(14);
			
			if(tutoTextBox.dialogueFinished){
				hud.resetAlpha();
				tutoStep = 9;
				tutoTextBox.dialogueFinished = false;
				GameConstants.BOX_STEP = 1/60f;
				
				if(GameConstants.CHECKPOINT == 0)
					mapReader.hero.checkpointRestart(mapReader.hero.getOrigine());
				else
					for(CheckPoint checkpoint : game.checkpoints)
						if(GameConstants.CHECKPOINT == checkpoint.getReferenceNumber())
							mapReader.hero.checkpointRestart(checkpoint);
					
					
				if(Data.getGameControls() == 3){
					hud.buttonJetPack.setTouchable(Touchable.enabled);
					hud.buttonLeft.setTouchable(Touchable.enabled);
					hud.buttonRight.setTouchable(Touchable.enabled);
				}
			}
		}
	}

	public void dispose(){
		super.dispose();
		Pools.free(tutoCoordinate);
		
		tutoTextBox.dispose();
		
		if(Gdx.app.getType() == ApplicationType.Android){
			game.assets.unload("Images/Animations/Controls_Animation.pack");
			
			if(GameConstants.SELECTED_LEVEL == 1 && /*!*/game.levelHandler.isLevelUnlocked(2)){
				game.assets.unload("Images/Animations/Rotation_Control_Animation.pack");
				game.assets.unload("Images/Animations/Jetpack_Control_Animation.pack");
			}
		}
	}

}
