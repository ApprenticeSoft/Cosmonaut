package com.cosmonaut.Bodies;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class Exit extends Obstacle{
	
	private Animation exitAnimation;
	public boolean open;
	public boolean heroContact;
	private boolean soundPlayed;
	private float animTime;
	private PointLight pointLight;
	private Color color;
	
	public Exit(final MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject) {
		super(game, world, camera, rectangleObject);
		animTime = 0;
		open = false;
		heroContact = false;
		soundPlayed = false;
		categoryBits = 0010;
		sound = game.assets.get("Sounds/Exit.ogg", Sound.class);
		exitAnimation = new Animation(0.04f, game.assets.get("Images/Animations/Exit_Animation.pack", TextureAtlas.class).findRegions("Exit_Animation"), Animation.PlayMode.NORMAL);
		
		create(world, camera, rectangleObject);
		
		body.getFixtureList().get(0).setSensor(true);
		body.getFixtureList().get(0).setUserData("Exit");
		body.setUserData("Exit");
		
		//Type
		if(rectangleObject.getProperties().get("Type") != null){
			if(rectangleObject.getProperties().get("Type").equals("End"))
				exitAnimation = new Animation(0.045f, game.assets.get("Images/Animations/Exit_End_Animation.pack", TextureAtlas.class).findRegions("Exit_End_Animation"), Animation.PlayMode.NORMAL);
		}
	}
	
	public Exit(final MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject, RayHandler rayHandler) {
		super(game, world, camera, rectangleObject);
		animTime = 0;
		open = false;
		heroContact = false;
		soundPlayed = false;
		categoryBits = 0010;
		sound = game.assets.get("Sounds/Exit.ogg", Sound.class);
		exitAnimation = new Animation(0.04f, game.assets.get("Images/Animations/Exit_Animation.pack", TextureAtlas.class).findRegions("Exit_Animation"), Animation.PlayMode.NORMAL);
		//pointLight = new PointLight(rayHandler, 40, new Color(0.8f, 0.8f, 0.8f, 1f), 2*width, 0, 0);
		
		create(world, camera, rectangleObject);
		
		body.getFixtureList().get(0).setSensor(true);
		body.getFixtureList().get(0).setUserData("Exit");
		body.setUserData("Exit");
		
		color = Pools.obtain(Color.class);	
		//Type
		if(rectangleObject.getProperties().get("Type") != null){
			if(rectangleObject.getProperties().get("Type").equals("End")){
				exitAnimation = new Animation(0.045f, game.assets.get("Images/Animations/Exit_End_Animation.pack", TextureAtlas.class).findRegions("Exit_End_Animation"), Animation.PlayMode.NORMAL);
				color.set(0.95f, 0.95f, 0.95f, 1f);
				pointLight = new PointLight(rayHandler, 40, color, 2.2f*width, 0, 0);
			}
		}
		else{
			color.set(0.8f, 0.8f, 0.8f, 1f);
			pointLight = new PointLight(rayHandler, 40, color, 2*width, 0, 0);
		}
		Pools.free(color);
		
		pointLight.setDistance(2*width);
		pointLight.setXray(true);
		pointLight.attachToBody(body, 0, 0);
	}
	
	public Exit(final MyGdxGame game){
		super(game);
		animTime = 0;
		open = false;
		heroContact = false;
		soundPlayed = false;
		categoryBits = 0010;
		sound = game.assets.get("Sounds/Exit.ogg", Sound.class);
		exitAnimation = new Animation(0.04f, game.assets.get("Images/Animations/Exit_Animation.pack", TextureAtlas.class).findRegions("Exit_Animation"), Animation.PlayMode.NORMAL);
		//pointLight = new PointLight(rayHandler, 40, new Color(0.8f, 0.8f, 0.8f, 1f), 2*width, 0, 0);
	}
	
	public void init(World world, OrthographicCamera camera, MapObject rectangleObject, RayHandler rayHandler){
		animTime = 0;
		open = false;
		heroContact = false;
		soundPlayed = false;
		categoryBits = 0010;
		create(world, camera, rectangleObject);
		
		body.getFixtureList().get(0).setSensor(true);
		body.getFixtureList().get(0).setUserData("Exit");
		body.setUserData("Exit");
		
		color = Pools.obtain(Color.class);	
		//Type
		if(rectangleObject.getProperties().get("Type") != null){
			if(rectangleObject.getProperties().get("Type").equals("End")){
				exitAnimation = new Animation(0.045f, game.assets.get("Images/Animations/Exit_End_Animation.pack", TextureAtlas.class).findRegions("Exit_End_Animation"), Animation.PlayMode.NORMAL);
				color.set(0.95f, 0.95f, 0.95f, 1f);
				pointLight = new PointLight(rayHandler, 40, color, 2.2f*width, 0, 0);
			}
		}
		else{
			color.set(0.8f, 0.8f, 0.8f, 1f);
			pointLight = new PointLight(rayHandler, 40, color, 2*width, 0, 0);
		}
		Pools.free(color);
		
		pointLight.setDistance(2*width);
		pointLight.setXray(true);
		pointLight.attachToBody(body, 0, 0);
	}
	
	public void active(){
		if(open)
			animTime += Gdx.graphics.getDeltaTime();
		
		if(exitAnimation.isAnimationFinished(animTime) && heroContact)
			GameConstants.LEVEL_FINISHED = true;	
		
		if(exitAnimation.getKeyFrameIndex(animTime) == 27 && !soundPlayed){
			sound.play();
			soundPlayed = true;
		}
	}
	
	@Override
	public void draw(SpriteBatch batch){		
		batch.setColor(1, 1, 1, 1);
		batch.draw(	exitAnimation.getKeyFrame(animTime), 
					this.body.getPosition().x - width, 
					this.body.getPosition().y - height,
					2 * width,
					2 * height);
	}
	
	public void dispose(){
		game.pools.free(this);
	}
}
