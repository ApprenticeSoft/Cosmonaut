package com.cosmonaut.Bodies;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class Hero {

	private final MyGdxGame game;
	public Body heroBody;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private PolygonShape heroShape;
	public float spriteWidth, spriteHeight;
	private float 	bodyWidth, bodyHeight,
					spriteElectrocutedWidth, spriteElectrocutedHeight,
					spriteSuffocateWidth, spriteSuffocateHeight,
					posXInit, posYInit,
					oxygenLevel, fuelLevel,
					gameOverDelay = 0;
	private TextureAtlas tomAtlas;
    private Animation /*tomIdle,*/ tomFly, tomElectrocuted, tomSuffocate;
    private Animation idleAnimations[];
	private int idleAnimation = 0, animationStep = 0;
    private Vector2 jetpackImpulse, suffocationImpulseDirection, suffocationImpulseCenter, origin;
	public boolean isJetPackActive;
	public boolean isRotating;
	private boolean dead = false;
	private boolean impulse = false;
    public Sound soundJetPack;
	private Sound soundImpact;
	private Sound soundElectrocution;
	
	//Test Box2DLight
	private ConeLight coneLight;
	
	//Test control
	private float previousPosX, posX;
	
	//Shader
    private ShaderProgram contourShader;
	
	
	public Hero(final MyGdxGame game, World world,  OrthographicCamera camera, TiledMap tiledMap){
		this.game = game;
		create(game, world, camera, tiledMap);
	}
	
	public Hero(final MyGdxGame game, World world,  OrthographicCamera camera, TiledMap tiledMap, RayHandler rayHandler){
		this.game = game;
		create(game, world, camera, tiledMap);

		Color color = Pools.obtain(Color.class).set(1f, 1f, 1f, 0.65f);	
        coneLight = new ConeLight(rayHandler, 110 * GameConstants.LIGHT_RAY_MULTIPLICATOR, color, 30, 0, 0, 0, 63);
        coneLight.attachToBody(heroBody, 0, .51f*bodyHeight, 35);   
        coneLight.setContactFilter((short) 0010, (short)-1000, (short)0001);
        
        /*
         * Éclairage du héro en cas de lumière ambiante trop faible
         */
        if(tiledMap.getProperties().get("Ambiant Light Min") != null){
        	if(Float.parseFloat(tiledMap.getProperties().get("Ambiant Light Min").toString()) < 0.2f){
            	float ambiantLight = 0.35f - Float.parseFloat(tiledMap.getProperties().get("Ambiant Light Min").toString());
        		color.set(1, 1, 1, ambiantLight);
            	PointLight pointLight = new PointLight(rayHandler, 50, color, 5, 0, 0);
            	pointLight.attachToBody(heroBody, 0, -0.5f*bodyHeight, 0);   
            	pointLight.setContactFilter((short) 0010, (short)-1000, (short)0001);
        	}
        }
        Pools.free(color);
	}
	
	public void create(final MyGdxGame game, World world,  OrthographicCamera camera, TiledMap tiledMap){
		oxygenLevel = GameConstants.MAX_OXYGEN;
		fuelLevel = GameConstants.MAX_FUEL;
		isRotating = false;

		jetpackImpulse = Pools.obtain(Vector2.class);
		suffocationImpulseDirection = Pools.obtain(Vector2.class);
		suffocationImpulseCenter = Pools.obtain(Vector2.class);
		origin = Pools.obtain(Vector2.class);
		
		soundJetPack = game.assets.get("Sounds/Jetpack.ogg", Sound.class);
		soundImpact = game.assets.get("Sounds/Impact.ogg", Sound.class);
		soundElectrocution = game.assets.get("Sounds/Electrocution.ogg", Sound.class);
		
		MapObjects personnages = (MapObjects)tiledMap.getLayers().get("Spawn").getObjects();

		bodyWidth = GameConstants.HERO_WIDTH;
		bodyHeight = GameConstants.HERO_HEIGHT;
		posXInit = (personnages.get("Tom").getProperties().get("x", float.class) + personnages.get("Tom").getProperties().get("width", float.class)/2) * GameConstants.MPP;
		posYInit = (personnages.get("Tom").getProperties().get("y", float.class) + 3*personnages.get("Tom").getProperties().get("height", float.class)/2) * GameConstants.MPP;
		
		heroShape = Pools.obtain(PolygonShape.class);
		heroShape.setAsBox(bodyWidth, bodyHeight/2);
		
		bodyDef = Pools.obtain(BodyDef.class);
		bodyDef.position.set(posXInit, posYInit);
        bodyDef.type = BodyType.DynamicBody; 
        
        heroBody = world.createBody(bodyDef);
        heroBody.setFixedRotation(false);
        	
        fixtureDef = Pools.obtain(FixtureDef.class);
        fixtureDef.shape = heroShape; 
        fixtureDef.density = (float)(GameConstants.DENSITY/(bodyWidth * bodyHeight));  
        fixtureDef.friction = 0.1f;  
        fixtureDef.restitution = 0.00f;  
        fixtureDef.filter.categoryBits = (short) 0001;
        fixtureDef.filter.groupIndex = (short) -1000;
        fixtureDef.isSensor = false;
        heroBody.createFixture(fixtureDef).setUserData("Tom"); 
        heroBody.setUserData("Tom");
        
        //Legs
        Vector2[] legCoordinates = {Pools.obtain(Vector2.class).set(-0.9f*bodyWidth,-bodyHeight/2),
					        		Pools.obtain(Vector2.class).set(bodyWidth,-bodyHeight/2),
					        		Pools.obtain(Vector2.class).set(bodyWidth,-3*bodyHeight/2),
					        		Pools.obtain(Vector2.class).set(.1f*bodyWidth,-3*bodyHeight/2)};
        PolygonShape legShape = Pools.obtain(PolygonShape.class);
        legShape.set(legCoordinates);
        fixtureDef.shape = legShape;
        heroBody.createFixture(fixtureDef).setUserData("Tom"); 
        
        //Animations
        if(Gdx.app.getType() == ApplicationType.Desktop){
        	tomAtlas = game.assets.get("Images/Animations/Tom_Animation_HD.pack", TextureAtlas.class);

            tomFly = new Animation(1/30f, tomAtlas.findRegions("Tom_Fly"), Animation.PlayMode.NORMAL);
            tomElectrocuted = new Animation(1/30f, tomAtlas.findRegions("Tom_Electrocuted"), Animation.PlayMode.LOOP);
            tomSuffocate = new Animation(1/30f, tomAtlas.findRegions("Tom_Suffocate"), Animation.PlayMode.NORMAL);
            
            idleAnimations = new Animation[3];
            idleAnimations[0] = new Animation(1/30f, tomAtlas.findRegions("Tom_Idle-1"), Animation.PlayMode.LOOP);
            idleAnimations[1] = new Animation(1/30f, tomAtlas.findRegions("Tom_Idle-2"), Animation.PlayMode.LOOP);
            idleAnimations[2] = new Animation(1/30f, tomAtlas.findRegions("Tom_Idle-3"), Animation.PlayMode.LOOP);
        }
        else{
        	tomAtlas = game.assets.get("Images/Animations/Tom_Animation.pack", TextureAtlas.class);
        	
            tomFly = new Animation(0.025f, tomAtlas.findRegions("Tom_Fly"), Animation.PlayMode.NORMAL);
            tomElectrocuted = new Animation(0.05f, tomAtlas.findRegions("Tom_Electrocuted"), Animation.PlayMode.LOOP);
            tomSuffocate = new Animation(0.06f, tomAtlas.findRegions("Tom_Suffocate"), Animation.PlayMode.NORMAL);
            
            idleAnimations = new Animation[3];
            idleAnimations[0] = new Animation(0.1f, tomAtlas.findRegions("Tom_Idle-1"), Animation.PlayMode.LOOP);
            idleAnimations[1] = new Animation(0.1f, tomAtlas.findRegions("Tom_Idle-2"), Animation.PlayMode.LOOP);
            idleAnimations[2] = new Animation(0.1f, tomAtlas.findRegions("Tom_Idle-3"), Animation.PlayMode.LOOP);
        }
            
        spriteHeight = 2 * bodyHeight;
        spriteWidth = spriteHeight * idleAnimations[0].getKeyFrame(0, true).getRegionWidth()  / idleAnimations[0].getKeyFrame(0, true).getRegionHeight();

        spriteElectrocutedWidth = spriteWidth * tomElectrocuted.getKeyFrame(0, true).getRegionWidth() / idleAnimations[0].getKeyFrame(0, true).getRegionWidth(); 
        spriteElectrocutedHeight = spriteHeight * tomElectrocuted.getKeyFrame(0, true).getRegionHeight() / idleAnimations[0].getKeyFrame(0, true).getRegionHeight();
        
        spriteSuffocateWidth = spriteWidth * tomSuffocate.getKeyFrame(0, true).getRegionWidth() / idleAnimations[0].getKeyFrame(0, true).getRegionWidth(); 
        spriteSuffocateHeight = spriteHeight * tomSuffocate.getKeyFrame(0, true).getRegionHeight() / idleAnimations[0].getKeyFrame(0, true).getRegionHeight();
      
        //Shader
        ShaderProgram.pedantic = false;	     
        contourShader = new ShaderProgram(	Gdx.files.internal("Shaders/Outline-vertex.glsl"),
						        		  	Gdx.files.internal("Shaders/Outline-fragment.glsl")
        );
          
        Vector2 shaderResolution = Pools.obtain(Vector2.class).set(1f / Gdx.graphics.getWidth(), 1f / Gdx.graphics.getHeight());
        Vector3 shaderColor = Pools.obtain(Vector3.class).set(14/256, 110/256, 1);
		contourShader.begin();
		contourShader.setUniformf("u_viewportInverse", shaderResolution);
		contourShader.setUniformf("u_offset", 3);
		contourShader.setUniformf("u_step", Math.min(1f, Gdx.graphics.getWidth() / 70f));
		contourShader.setUniformf("u_color", shaderColor);
		contourShader.end();
		Pools.free(shaderResolution);
		Pools.free(shaderColor);

        for(int i = 0; i < legCoordinates.length; i++)
    		Pools.free(legCoordinates[i]); 	
        Pools.free(legShape);
        Pools.free(heroShape);
        Pools.free(bodyDef);
        Pools.free(fixtureDef);
	}
	
	public void displacement(){	
		if(!dead){
			oxygenLevel -= Gdx.graphics.getDeltaTime();

			if(GameConstants.GAME_CONTROLS == GameConstants.ANDROID_GESTURE_CONTROLS)
				gestureControl();
			else if(GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_QWERTY)
				keyboardControl();
			else if(GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY)
				keyboardAZERTYControl();
			
			
			if(oxygenLevel < 0){
				oxygenLevel = 0;
				death(game.text.get("OutOfOxygen").toUpperCase());
			}
			if(fuelLevel < 0)
				fuelLevel = 0;	
		}
		/*
		 * Hero death
		 */
		else{	
			if(GameConstants.LOSE_MESSAGE.equals(game.text.get("Electrocuted").toUpperCase())){
				heroBody.setLinearVelocity(0, 0);

				if(gameOverDelay == 0)
					soundElectrocution.loop();
				else if(gameOverDelay > 2){
					GameConstants.GAME_LOST = true;
					soundElectrocution.stop();
					Data.setNbElectrocuted(Data.getNbElectrocuted() + 1);
				}
					
			}
			else if(GameConstants.LOSE_MESSAGE.equals(game.text.get("Crushed").toUpperCase())){
				GameConstants.GAME_LOST = true;
				Data.setNbCrushed(Data.getNbCrushed() + 1);
				impact();
			}
			else if(GameConstants.LOSE_MESSAGE.equals(game.text.get("OutOfOxygen").toUpperCase())){
				if(!impulse){
					impulse = true;

					suffocationImpulseDirection.set(MathUtils.random(-2.5f, 2.5f) * heroBody.getFixtureList().get(0).getDensity(), 
													MathUtils.random(-2.5f, 2.5f) * heroBody.getFixtureList().get(0).getDensity());
					suffocationImpulseCenter.set(	heroBody.getPosition().x + MathUtils.random(-0.9f * bodyWidth, 0.9f * bodyWidth), 
													heroBody.getPosition().y + MathUtils.random(-0.9f * bodyHeight, 0.9f * bodyHeight));
					
					heroBody.applyLinearImpulse(suffocationImpulseDirection, 
												suffocationImpulseCenter, 
												true);
					
					Data.setNbSuffocated(Data.getNbSuffocated() + 1);
				}
				
				if(gameOverDelay > 6.9f){
					GameConstants.GAME_LOST = true;
				}
			}
			
			gameOverDelay += Gdx.graphics.getDeltaTime();
		}
	}

	public void draw(SpriteBatch batch, float animTime){
		batch.setColor(1, 1, 1, 1);
		if(!dead){
			if(isJetPackActive)
				batch.draw(	tomFly.getKeyFrame(animTime), 
							heroBody.getPosition().x - bodyWidth, 
							heroBody.getPosition().y + bodyHeight/2 - spriteHeight, 
							bodyWidth,
							spriteHeight - bodyHeight/2,
							spriteWidth, 
							spriteHeight,
							1,
							1,
							heroBody.getAngle()*MathUtils.radiansToDegrees);
			else
				batch.draw(	idleAnimations[idleAnimation].getKeyFrame(animTime, true), 
							heroBody.getPosition().x - bodyWidth, 
							heroBody.getPosition().y + bodyHeight/2 - spriteHeight, 
							bodyWidth,
							spriteHeight - bodyHeight/2,
							spriteWidth, 
							spriteHeight,
							1,
							1,
							heroBody.getAngle()*MathUtils.radiansToDegrees);

			if(animationStep != (int)(GameConstants.ANIM_TIME/idleAnimations[0].getAnimationDuration())){
				idleAnimation = MathUtils.random(0, 2);
				animationStep = (int)(GameConstants.ANIM_TIME/idleAnimations[0].getAnimationDuration());
			}
			
		}
		else if(GameConstants.LOSE_MESSAGE.equals(game.text.get("Electrocuted").toUpperCase())){
			/*
			 * Contour bleuté
			 */
			game.batch.setShader(contourShader);
			batch.draw(	tomElectrocuted.getKeyFrame(animTime, true), 
						heroBody.getPosition().x - spriteElectrocutedWidth/2, 
						heroBody.getPosition().y - spriteElectrocutedHeight/2 - bodyHeight/2, 
						spriteElectrocutedWidth/2,
						spriteElectrocutedHeight/2 + bodyHeight/2,
						spriteElectrocutedWidth, 
						spriteElectrocutedHeight,
						1,
						1,
						heroBody.getAngle()*MathUtils.radiansToDegrees);
			game.batch.setShader(null);
			
			/*
			 * Dessin du cosmonaute
			 */
			batch.draw(	tomElectrocuted.getKeyFrame(animTime, true), 
						heroBody.getPosition().x - spriteElectrocutedWidth/2, 
						heroBody.getPosition().y - spriteElectrocutedHeight/2 - bodyHeight/2, 
						spriteElectrocutedWidth/2,
						spriteElectrocutedHeight/2 + bodyHeight/2,
						spriteElectrocutedWidth, 
						spriteElectrocutedHeight,
						1,
						1,
						heroBody.getAngle()*MathUtils.radiansToDegrees);
		}
		else if(GameConstants.LOSE_MESSAGE.equals(game.text.get("OutOfOxygen").toUpperCase())){
			batch.draw(	tomSuffocate.getKeyFrame(animTime), 
						heroBody.getPosition().x - spriteSuffocateWidth/2, 
						heroBody.getPosition().y - spriteSuffocateHeight/2 - bodyHeight/2, 
						spriteSuffocateWidth/2,
						spriteSuffocateHeight/2 + bodyHeight/2,
						spriteSuffocateWidth, 
						spriteSuffocateHeight,
						1,
						1,
						heroBody.getAngle()*MathUtils.radiansToDegrees);
		}
	}
	
	public void rotateClockwise(){
		isRotating = true;
		heroBody.setAngularVelocity(GameConstants.TOM_ROTATION);
	}
	
	public void rotateCounterClockwise(){
		isRotating = true;
		heroBody.setAngularVelocity(-GameConstants.TOM_ROTATION);
	}
	
	public void stopRotating(){
			heroBody.setAngularVelocity(0);
	}
	
	public float getX(){
		return heroBody.getPosition().x;
	}
	
	public float getOxygenLevel(){
		return oxygenLevel;
	}
	
	public void setOxygenLevel(float newOxygenLevel){
		oxygenLevel = newOxygenLevel;
	}
	
	public float getFuelLevel(){
		return fuelLevel;
	}
	
	public void setFuelLevel(float newFuelLevel){
		fuelLevel = newFuelLevel;
	}
	
	public float getY(){
		return heroBody.getPosition().y;
	}
	
	public Vector2 getOrigine(){
		return origin.set(posXInit, posYInit);
	}
	
	public void keyboardControl(){	
		if(Gdx.input.isKeyPressed(Keys.W) && fuelLevel > 0){
			jetpackOn();
        }
		else{
			jetpackOff();
		}
        
		if(Gdx.input.isKeyPressed(Keys.A))
			rotateClockwise();
		else if(Gdx.input.isKeyPressed(Keys.D))
			rotateCounterClockwise();	
		else
			stopRotating();
	}
	
	public void keyboardAZERTYControl(){	
		if(Gdx.input.isKeyPressed(Keys.Z) && fuelLevel > 0){
			jetpackOn();
        }
		else{
			jetpackOff();
		}
        
		if(Gdx.input.isKeyPressed(Keys.Q))
			rotateClockwise();
		else if(Gdx.input.isKeyPressed(Keys.D))
			rotateCounterClockwise();	
		else
			stopRotating();
	}
	
	public void gestureControl(){
		for(int i = 0; i<2; i++){
			if(Gdx.input.isTouched(i) && Gdx.input.getX(i) < Gdx.graphics.getWidth()/2){
				if(!isRotating){
					previousPosX = Gdx.input.getX(i);
					posX = Gdx.input.getX(i);
					isRotating = true;
				}
				else{
					posX = Gdx.input.getX(i);
					if(posX < previousPosX)
						rotateClockwise();
					else if(posX > previousPosX)
						rotateCounterClockwise();
					previousPosX = posX;
				}
			}
		}
		
		if((Gdx.input.isTouched(0) && Gdx.input.getX(0) < Gdx.graphics.getWidth()/2) ||(Gdx.input.isTouched(1) && Gdx.input.getX(1) < Gdx.graphics.getWidth()/2)){
			System.out.println("Rotate");
		}
		else{
			System.out.println("Don't rotate");
			stopRotating();
			isRotating = false;
		}
		
		if((Gdx.input.isTouched(0) && Gdx.input.getX(0) > Gdx.graphics.getWidth()/2) ||(Gdx.input.isTouched(1) && Gdx.input.getX(1) > Gdx.graphics.getWidth()/2))
			jetpackOn();
		else{
			jetpackOff();
		}
	}
	
	public void jetpackOn(){
		if(!isJetPackActive){
			GameConstants.ANIM_TIME = 0;
			isJetPackActive = true;
			soundJetPack.loop(0.5f);
		}
		jetpackImpulse.set(0, 60*(GameConstants.JETPACK_IMPULSE + 5*Data.getPowerLevel()) * Gdx.graphics.getDeltaTime());
		heroBody.applyForceToCenter(jetpackImpulse.rotate(heroBody.getAngle() * MathUtils.radiansToDegrees), true);
		fuelLevel -= Gdx.graphics.getDeltaTime() * GameConstants.FUEL_CONSUMPTION;
	}
	
	public void jetpackOff(){
		if(isJetPackActive){
			GameConstants.ANIM_TIME = 0;
			idleAnimation = MathUtils.random(0, 2);
			soundJetPack.stop();
			isJetPackActive = false;
		}
	}
	
	public void impact(){
		soundImpact.play(1, MathUtils.random(0.98f, 1.02f), 1);
	}
	
	public void soundPause(){
		if(soundJetPack != null)
			soundJetPack.pause();
	}
	
	public void soundResume(){
		if(soundJetPack != null)
			soundJetPack.resume();
	}
	
	public void death(String loseString){
		if(!dead){
			dead = true;
			GameConstants.ANIM_TIME = 0;
			soundJetPack.stop();
			GameConstants.LOSE_MESSAGE = loseString;
		}
	}
	
	public void checkpointRestart(float X, float Y){
		if(fuelLevel < GameConstants.MAX_FUEL/2)
			setFuelLevel(GameConstants.MAX_FUEL/2);
		if(oxygenLevel < GameConstants.MAX_OXYGEN/2)
			setOxygenLevel(GameConstants.MAX_OXYGEN/2);
		
		GameConstants.GAME_LOST = false;
		dead = false;
		heroBody.setLinearVelocity(0,0);	
		heroBody.setTransform(X, Y, 0);
	}
	
	public void checkpointRestart(Vector2 position){
		checkpointRestart(position.x, position.y);
	}
	
	public void checkpointRestart(CheckPoint checkPoint){
		checkpointRestart(checkPoint.getX(), checkPoint.getY());
	}
	
	public boolean isDead(){
		return dead;
	}

	public void dispose(){
		soundElectrocution.stop();
		soundJetPack.stop();
		Pools.free(jetpackImpulse);
		Pools.free(suffocationImpulseDirection);
		Pools.free(suffocationImpulseCenter);
		Pools.free(origin);
	}
}
